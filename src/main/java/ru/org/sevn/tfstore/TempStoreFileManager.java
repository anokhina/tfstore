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
package ru.org.sevn.tfstore;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import ru.org.sevn.common.solr.SolrIndexer;

public class TempStoreFileManager extends AbstractStoreFileManager {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    
    public TempStoreFileManager(File dir, SolrIndexer indexer) {
        super(dir, indexer);
    }

    @Override
    protected Path makeRelativePath(FileInfo file) {
        Path fileRelPath = super.makeRelativePath(file);
        Path ret = Paths.get(sdf.format(file.getDateOff()));
        return ret.resolve(fileRelPath);
    }
    
    @Override
    protected String getStoreIdName() {
        return "tmp";
    }
}