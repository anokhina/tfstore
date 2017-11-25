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

public interface StoreFileManager {

    public static long MAX_SIZE = 25025314816L - 1L;

    //120M
    public static long MAX_SIZE1 = 120*1024*1024L;
    
    enum Errors {
        TOO_BIG, EXISTS, SOLR, FATAL
    }
    
    Errors addFileIn(FileInfo file);
    
    public default Errors addFile(FileInfo fi) {
        if (fi.getFileSize() >= MAX_SIZE1) {
            return Errors.TOO_BIG;
        }
        return addFileIn(fi);
    }
}
