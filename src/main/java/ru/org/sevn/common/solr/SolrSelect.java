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

import java.io.IOException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrSelect {
    //TODO space replacement
    //+ - && || ! ( ) { } [ ] ^ " ~ * ? : \
    public static final String SPECIAL_CHARACTERS_STR = "\\,+,-,&&,||,!,(,),{,},[,],^,\",~,*,?,:,/, ";
    public static final String[] SPECIAL_CHARACTERS = SPECIAL_CHARACTERS_STR.split(",");
    public static final String[] SPECIAL_CHARACTERS_REPL = "\\\\,\\+,\\-,\\&&,\\||,\\!,\\(,\\),\\{,\\},\\[,\\],\\^,\\\",\\~,\\*,\\?,\\:,\\/,\\ ".split(",");
    
    public static String toQueryNamedParam(final String k, final String s) {
        if (s == null || s.length() == 0) return null;
        StringBuilder sb = new StringBuilder();
        sb.append(k).append(":").append(s).append("");
        return sb.toString();
    }
    public static String toQueryNamedParamEscape(final String k, final Object s) {
        if (s == null || s.toString().trim().length() == 0) return null;
        StringBuilder sb = new StringBuilder();
        sb.append(k).append(":").append(toQueryParam(s.toString())).append("");
        return sb.toString();
    }
    public static String toQueryParam(final String s) {
        String rs = s;
        if ( s != null ) {
            for(int i = 0; i < SPECIAL_CHARACTERS.length; i++) {
                rs = rs.replace(SPECIAL_CHARACTERS[i], SPECIAL_CHARACTERS_REPL[i]);
            }
        }
        return rs;
    }
    
    public static interface SolrDocumentProcessor {
        void process(SolrDocument d, int size, int i);
    }
    
    public static class PrintSolrDocumentProcessor implements SolrDocumentProcessor {

        @Override
        public void process(SolrDocument d, int size, int i) {
            System.out.println("" + d.toString());
        }
        
    }
    
    public static void findSolrDocument(SolrDocumentProcessor proc, SolrClient solrClient, String q, int start, int retMaxSize) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery(q).setStart(start).setRows(retMaxSize);
        //query.setQuery(version.getSong_name());
        //query.addFilterQuery("cat:electronics","store:amazon.com");
        //query.setFields("id","price","merchant","cat","store");
        //query.set("defType", "edismax");

        QueryResponse response = solrClient.query(query);
        SolrDocumentList results = response.getResults();
        int size = Math.min(results.size(), retMaxSize);
        for (int i = 0; i < size; ++i) {
            SolrDocument doc = results.get(i);
            proc.process(doc, size, i);
        }
    }
    
    //http://www.solrtutorial.com/solr-query-syntax.html
    public static enum LogicOp {
        AND, OR
    }
    
    public static StringBuilder groupItSolrQuery(StringBuilder sb, boolean groupIt) {
        if (groupIt) {
            sb.insert(0, "( ");
        }
        sb.append(sb);
        if (groupIt) {
            sb.append(" )");
        }
        return sb;
    }
    public static StringBuilder appendSolrQuery(StringBuilder sb, LogicOp op, String query, boolean groupIt) {
        if (groupIt) {
            sb.insert(0, "( ");
        }
        sb.append(" ").append(op.toString()).append(" ").append(query).append(" ");
        if (groupIt) {
            sb.append(" )");
        }
        return sb;
    }
    
}
