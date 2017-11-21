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
import ru.org.sevn.common.mime.Mime;

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
    
    public SolrIndexer(String collection) {
        this(DEFAULT_URL, collection);
    }
    
    public SolrIndexer(String url, String collection) {
        solrClient = new HttpSolrClient.Builder(url + "/" + collection).build();
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
    
    public void commitAsync(Consumer<Throwable> result) {
        executorService.submit( () -> {
            try {
                getSolrClient().commit();
            } catch (SolrServerException | IOException ex) {
                Logger.getLogger(SolrIndexer.class.getName()).log(Level.SEVERE, null, ex);
                if (result != null) {
                    result.accept(ex);
                }
            }
        });
    }
    public void addDocAsync(String wpath, String path, String content, 
            String contentType, String title, Consumer<Throwable> result) {
        
            executorService.submit(() -> {
                Throwable res;
                try {
                    if (title == null) {

                        res = addDoc(makeUpdateRequest(wpath, path, content, contentType, path, null));
                    } else {
                        res = addDoc(makeUpdateRequest(wpath, path, content, contentType, title, null));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SolrIndexer.class.getName()).log(Level.SEVERE, null, ex);
                    res = ex;
                }

                if (res != null && result != null) {
                    result.accept(res);
                }
        });
    }
    
    public Throwable addDoc(String wpath, String path, File fl, String title, 
            HashMap<String, Object> attributes) {
        
        Throwable res = null;
        try {
            if (title == null) {
                title = fl.getName();
            }
            if (fl.isDirectory()) {
                {
                    HashMap<String, Object> attributes2index = new HashMap<>(attributes);
                    addFileAttributes(fl, attributes2index);
                    res = addDoc(makeDir(wpath, path, fl.getName(), attributes2index));
                    if (res != null) {
                        return res;
                    }
                }
                for (File f : fl.listFiles()) {
                    res = addDoc(wpath, Paths.get(path).resolve(f.getName()).toString(), f, null, new HashMap<>(attributes));
                    if (res != null) {
                        return res;
                    }
                }
            } else {
                ContentStreamUpdateRequest req = makeUpdateRequest(wpath, path, fl, title, attributes);
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
    
    public void addDocAsync(String wpath, String path, File fl, String title,
            HashMap<String, Object> attributes,
            Consumer<Throwable> result) {
        
            executorService.submit(() -> {
                Throwable res = addDoc(wpath, path, fl, title, attributes);
                if (/*res != null &&*/ result != null) {
                    result.accept(res);
                }
        });
    }
    
    public Throwable addHtml(String wpath, String path, String content, String title, Consumer<Throwable> result) {
        Throwable res;
        if (title == null) {
            res = addDoc(makeHtml(wpath, path, content, path, null));
        } else {
            res = addDoc(makeHtml(wpath, path, content, title, null));
        }
        return res;
    }
    
    public void addHtmlAsync(String wpath, String path, String content, 
            String title, Consumer<Throwable> result) {
        
        executorService.submit(() -> {
            Throwable res = addHtml(wpath, path, content, title, result);
            if (res != null && result != null) {
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
    public static final String HTML_PATH = "path_s";
    public static final String HTML_WPATH = "wpath_s";
    public static final String HTML_TITLE = "title_s";
    
    public static final String LITERALS_PREFIX = "literal.";
    
    //https://wiki.apache.org/solr/ExtractingRequestHandler
    //https://wiki.apache.org/solr/ContentStreamUpdateRequestExample
    public static ContentStreamUpdateRequest makeUpdateRequest(
            String wpath, String path, String content, String contentType, 
            String title, HashMap<String, Object> attributes) throws IOException {
        
        //if (contentType == null) return null;
        ContentStreamBase cs = new ContentStreamBase.StringStream(content);
        cs.setContentType(contentType);
        return makeUpdateRequest(wpath, path, cs, title, attributes);
    }
    public static void addFileAttributes(File fl, HashMap<String, Object> attributes) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(fl.toPath(), BasicFileAttributes.class);
        attributes.put("file_size_l", attr.size());
        attributes.put("file_isSymbolicLink_b", attr.isSymbolicLink());
        attributes.put("file_isRegularFile_b", attr.isRegularFile());
        attributes.put("file_isOther_b", attr.isOther());
        attributes.put("file_isDirectory_b", attr.isDirectory());
        attributes.put("file_creationTime_s", attr.creationTime().toInstant().toString());
        attributes.put("file_lastAccessTime_s", attr.lastAccessTime().toInstant().toString());
        attributes.put("file_lastModifiedTime_s", attr.lastModifiedTime().toInstant().toString());
    }
    public static ContentStreamUpdateRequest makeUpdateRequest(
            String wpath, String path, File fl, String title, 
            HashMap<String, Object> attributes) throws IOException {
        
        //String contentType = null;
        //contentType = Mime.getMimeTypeFile(fl);
        addFileAttributes(fl, attributes);
        
        //if (contentType == null) return null; //TODO 
        ContentStreamBase cs = new ContentStreamBase.FileStream(fl);
        //cs.setContentType(contentType);
        return makeUpdateRequest(wpath, path, cs, title, attributes);
    }
    public static ContentStreamUpdateRequest makeUpdateRequest(
            String wpath, String path, ContentStream cstream, String title, 
            HashMap<String, Object> attributes) throws IOException {
        
        ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract");

        up.addContentStream(cstream);
        
        String id = makeId(wpath, path);
        System.out.println(id);

        up.setParam(LITERALS_PREFIX + "id", id);
        up.setParam(LITERALS_PREFIX + HTML_PATH, path);
        up.setParam(LITERALS_PREFIX + HTML_WPATH, wpath);
        up.setParam(LITERALS_PREFIX + HTML_TITLE, title);
        
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

    public static SolrInputDocument makeDir(String wpath, String path, 
            String title, HashMap<String, Object> attributes) {
        
        //System.out.println("index>>*>"+wpath + ":" + path);
        SolrInputDocument doc = new SolrInputDocument();
        
        doc.addField(HTML_PATH, path);
        doc.addField(HTML_WPATH, wpath);
        doc.addField("id", makeId(wpath, path));
        doc.addField(HTML_TITLE, title);
        if (attributes != null) attributes.forEach((k, v) -> { 
            switch(k) {
                case HTML_PATH:
                case HTML_WPATH:
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
    
    public static SolrInputDocument makeHtml(String wpath, String path, String content, String title, HashMap<String, Object> attributes) {
        //System.out.println("index>>*>"+wpath + ":" + path);
        SolrInputDocument doc = new SolrInputDocument();
        
        doc.addField(HTML_PATH, path);
        doc.addField(HTML_WPATH, wpath);
        doc.addField("id", makeId(wpath, path));
        doc.addField(HTML_TITLE, title);
        doc.addField("_text_", content);
        if (attributes != null) attributes.forEach((k, v) -> { 
            switch(k) {
                case HTML_PATH:
                case HTML_WPATH:
                case "_text_":
                    break;
                    default: doc.addField(k, v);
            }
        });
        return doc;
    }
    
    public void addDocumentsCommit(Collection<SolrInputDocument> docs) throws SolrServerException, IOException {
        if (docs.size() > 0) {
            solrClient.add(docs);
            solrClient.commit();
        }
    }
    
    public SolrDocumentList findHtml(String wpath, String str, int rows) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery();
        query.setFields(HTML_WPATH, HTML_PATH, HTML_TITLE);
        query.setQuery(str);
        query.addFilterQuery(HTML_WPATH + ":" + wpath);
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
