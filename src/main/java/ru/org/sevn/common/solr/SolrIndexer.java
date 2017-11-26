/*
 * Copyright 2017 Veronica Anokhina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.org.sevn.common.solr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.ContentStreamBase;
import ru.org.sevn.tfstore.StoreLogger;

public class SolrIndexer {
    /*
https://plus.google.com/collection/cP5YV
    
unzip solr.zip
cd solr
bin\solr start -p 8983
bin\solr create -c www

check it with 
http://localhost:8983/solr/admin/cores?action=STATUS

solr-6.6.0\server\solr\www\conf\managed-schema

from solr home
bin\solr stop -p 8983﻿    
    
    solr-6.6.0\server\solr\www\conf\managed-schema 
    */
    public static String DEFAULT_URL = "http://localhost:8983/solr";
    private final SolrClient solrClient;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    
    private final StoreLogger logger;

    public StoreLogger getLogger() {
        return logger;
    }
    
    public SolrIndexer(String collection, StoreLogger logger) {
        this(DEFAULT_URL, collection, logger);
    }
    
    public SolrIndexer(String url, String collection, StoreLogger logger) {
        solrClient = new HttpSolrClient.Builder(url + "/" + collection).build();
        this.logger = logger;
        //deleteAll();
    }
    
    public boolean isAlive() {
        try {        
            SolrPingResponse resp = solrClient.ping();
            return (resp.getResponse() != null);
        } catch (SolrServerException | IOException ex) {
            Logger.getLogger(SolrIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public Throwable deleteAll() {
        try {
            solrClient.deleteByQuery("*:*");
            solrClient.commit();
        } catch (SolrServerException | IOException ex) {
            Logger.getLogger(SolrIndexer.class.getName()).log(Level.SEVERE, null, ex);
            return ex;
        }
        return null;
    }
    public void query(String s, int start, int retMaxSize) {
        try {
            SolrSelect.findSolrDocument(new SolrSelect.PrintSolrDocumentProcessor(), solrClient, s, null, start, retMaxSize);
        } catch (SolrServerException ex) {
            Logger.getLogger(SolrIndexer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SolrIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private final AtomicLong counterNeedCommit = new AtomicLong();
    private final LinkedBlockingQueue<Consumer<Throwable>> onCommit = new LinkedBlockingQueue();
    private static final int MAX_COMMIT_CNT = 5;

    public void commitAsync(Consumer<Throwable> result) {
        onCommit.add(result);
        if (counterNeedCommit.get() > MAX_COMMIT_CNT || counterNeedCommit.get() == 0) {
            commitNow();
        } else {
            executorService.submit( () -> {
                commitNow();
            });
        }
    }
    
    private void commitNow() {
        synchronized(onCommit) {
            Throwable err = null;
            try {
                if (counterNeedCommit.get() > 0) {
                    System.out.println("ABOUT COMMIT");
                    getSolrClient().commit();
                    counterNeedCommit.set(0);
                }
            } catch (SolrServerException | IOException ex) {
                Logger.getLogger(SolrIndexer.class.getName()).log(Level.SEVERE, null, ex);
                err = ex;
            }
            Consumer<Throwable> result;
            while ( (result = onCommit.poll()) != null ) {
                if (result != null) {
                    result.accept(err);
                }
            }
        }
    }
    
    public Throwable addDoc(String wpath, String ch, String uuidpath, String path, File fl, String title, 
            HashMap<String, Object> attributes) {
        
        Throwable res = null;
        try {
            String fullTitle = Paths.get(ch, uuidpath).relativize(Paths.get(path)).toString();
            if (title == null) {
                title = fl.getName();
            }
            if (fl.isDirectory()) {
                {
                    HashMap<String, Object> attributes2index = new HashMap<>(attributes);
                    addFileAttributes(fl, attributes2index);
                    res = addDoc(makeDir(wpath, ch, uuidpath, path, fl.getName(), fullTitle, attributes2index));
                    if (res != null) {
                        return res;
                    }
                }
                for (File f : fl.listFiles()) {
                    res = addDoc(wpath, ch, uuidpath, Paths.get(path).resolve(f.getName()).toString(), f, null, new HashMap<>(attributes));
                    if (res != null) {
                        return res;
                    }
                }
            } else {
                ContentStreamUpdateRequest req = makeUpdateRequest(wpath, ch, uuidpath, path, fl, title, fullTitle, attributes);
                if (req != null) {
                    res = addDoc(req);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SolrIndexer.class.getName()).log(Level.SEVERE, null, ex);
            res = ex;
        }
        return res;
    }
    
    public void addDocAsync(String wpath, String ch, String uuidpath, String path, File fl, String title,
            HashMap<String, Object> attributes,
            Consumer<Throwable> result) {
        
            System.out.println("SCHEDULE>" + fl.getAbsolutePath());
            executorService.submit(() -> {
                System.out.println("SCH----->" + fl.getAbsolutePath());
                Throwable res = addDoc(wpath, ch, uuidpath, path, fl, title, attributes);
                long zzz = counterNeedCommit.incrementAndGet();;
                System.out.println("SCH----->" + zzz);
                
                if (result != null) {
                    result.accept(res);
                }
        });
    }
    
    public Throwable addDoc(ContentStreamUpdateRequest ur) {
        try {
            solrClient.request(ur);
        } catch (SolrServerException | IOException ex) {
            Logger.getLogger(SolrIndexer.class.getName()).log(Level.SEVERE, null, ex);
            return ex;
        }
        return null;
    }
    
    public Throwable addDoc(SolrInputDocument doc) {
        try {
            solrClient.add(doc);
        } catch (SolrServerException | IOException ex) {
            Logger.getLogger(SolrIndexer.class.getName()).log(Level.SEVERE, null, ex);
            return ex;
        }
        return null;
    }
    
    //see solr\example\example-DIH\solr\db\conf\managed-schema 
    public static final String DOC_PATH = "path_s";
    public static final String DOC_WPATH = "wpath_s";
    public static final String DOC_UUID = "uuid_s";
    public static final String DOC_PART = "part_s";
    public static final String DOC_TITLE = "title_s";
    public static final String DOC_FULL_TITLE = "full_title_s";
    public static final String DOC_TAGS = "tags_ss";
    public static final String FILE_LASTMODIFIEDTIME = "file_lastModifiedTime_s";
    
    public static final String LITERALS_PREFIX = "literal.";
    
    //https://wiki.apache.org/solr/ExtractingRequestHandler
    //https://wiki.apache.org/solr/ContentStreamUpdateRequestExample
    public static void addFileAttributes(File fl, HashMap<String, Object> attributes) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(fl.toPath(), BasicFileAttributes.class);
        attributes.put("file_size_l", attr.size());
        attributes.put("file_isSymbolicLink_b", attr.isSymbolicLink());
        attributes.put("file_isRegularFile_b", attr.isRegularFile());
        attributes.put("file_isOther_b", attr.isOther());
        attributes.put("file_isDirectory_b", attr.isDirectory());
        attributes.put("file_creationTime_s", attr.creationTime().toInstant().toString());
        attributes.put("file_lastAccessTime_s", attr.lastAccessTime().toInstant().toString());
        attributes.put(FILE_LASTMODIFIEDTIME, attr.lastModifiedTime().toInstant().toString());
    }
    public static ContentStreamUpdateRequest makeUpdateRequest(
            String wpath, String ch, String uuidpath, String path, File fl, String title, String fullTitle,
            HashMap<String, Object> attributes) throws IOException {
        
        addFileAttributes(fl, attributes);
        
        ContentStreamBase cs = new ContentStreamBase.FileStream(fl);
        return makeUpdateRequest(wpath, ch, uuidpath, path, cs, title, fullTitle, attributes);
    }
    public static ContentStreamUpdateRequest makeUpdateRequest(
            String wpath, String ch, String uuidpath, String path, ContentStream cstream, String title, String fullTitle,
            HashMap<String, Object> attributes) throws IOException {
        
        ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract");

        up.addContentStream(cstream);
        
        String id = makeId(wpath, path);
        System.out.println("makeUpdateRequest>"+id);

        up.setParam(LITERALS_PREFIX + "id", id);
        up.setParam(LITERALS_PREFIX + DOC_PATH, path);
        up.setParam(LITERALS_PREFIX + DOC_WPATH, wpath);
        up.setParam(LITERALS_PREFIX + DOC_PART, ch);
        up.setParam(LITERALS_PREFIX + DOC_UUID, uuidpath);
        up.setParam(LITERALS_PREFIX + DOC_TITLE, title);
        up.setParam(LITERALS_PREFIX + DOC_FULL_TITLE, fullTitle);
        
        for (String k : attributes.keySet()) {
            up.setParam(LITERALS_PREFIX + k, attributes.get(k).toString());
        }

        //up.setParam("uprefix", "ignored_");
        up.setParam("uprefix", "attr_");
        up.setParam("fmap.content_type", "content_type_s");
        up.setParam("fmap.content", "_text_");// TODO ? without _s
    
        up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
        return up;
    }

    //TODO
    public static SolrInputDocument makeDir(String wpath, String ch, String uuidpath, String path, 
            String title, String fullTitle, HashMap<String, Object> attributes) {
        
        //System.out.println("index>>*>"+wpath + ":" + path);
        SolrInputDocument doc = new SolrInputDocument();
        
        doc.addField(DOC_PATH, path);
        doc.addField(DOC_WPATH, wpath);
        doc.addField(DOC_PART, ch);
        doc.addField(DOC_UUID, uuidpath);
        doc.addField("id", makeId(wpath, path));
        doc.addField(DOC_TITLE, title);
        doc.addField(DOC_FULL_TITLE, fullTitle);
        if (attributes != null) attributes.forEach((k, v) -> { 
            switch(k) {
                case DOC_PATH:
                case DOC_WPATH:
                case "_text_":
                    break;
                    default: doc.addField(k, v);
            }
        });
        return doc;
    }
    
    private static String makeId(String wpath, String path) {
        return Paths.get(wpath, path).toString();
    }
    
    public void addDocumentsCommit(Collection<SolrInputDocument> docs) throws SolrServerException, IOException {
        if (docs.size() > 0) {
            solrClient.add(docs);
            solrClient.commit();
        }
    }
    
    public SolrDocumentList findHtml(String wpath, String str, int rows) throws SolrServerException, IOException {
        //TODO change it
        SolrQuery query = new SolrQuery();
        query.setFields(DOC_WPATH, DOC_PATH, DOC_TITLE);
        query.setQuery(str);
        query.addFilterQuery(DOC_WPATH + ":" + wpath);
        query.setStart(0);
        query.setRows(rows);
        QueryResponse response = solrClient.query(query);
        SolrDocumentList results = response.getResults();
        //results.get(0).get("path");
        return results;
    }

    public SolrClient getSolrClient() {
        return solrClient;
    }
}
