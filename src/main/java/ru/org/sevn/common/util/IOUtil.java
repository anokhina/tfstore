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
package ru.org.sevn.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class IOUtil {

    public static boolean moveDirOverwrite(File srcDir, File destDir, boolean deleteSrc) throws FileNotFoundException, IOException {
        boolean ret = false;
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!srcDir.exists()) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        }
        if (!srcDir.isDirectory()) {
            throw new IOException("Source '" + srcDir + "' is not a directory");
        }
        
        if (destDir.exists()) {
            if (!destDir.isDirectory()) {
                throw new IOException("Source '" + destDir + "' is not a directory");
            }
            boolean removeSrc = true;
            for (File f : srcDir.listFiles()) {
                File f2 = new File(destDir, f.getName());
                if (f2.exists()) {
                    if (f.isDirectory()) {
                        if (f2.isDirectory()) {
                            boolean r = moveDirOverwrite(f, f2, deleteSrc);
                            if (!r) { removeSrc = false; }
                        } else {
                            removeSrc = false;
                        }
                    } else {
                        if (!f2.isDirectory()) {
                            Files.move(f.toPath(), f2.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            removeSrc = false;
                        }
                    }
                } else {
                    Files.move(f.toPath(), f2.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            if (removeSrc) {
                if (deleteSrc) {
                    Files.delete(srcDir.toPath());
                    ret = !srcDir.exists();
                } else {
                    ret = true;
                }
            }
        } else {
            destDir.getParentFile().mkdirs();
            return srcDir.renameTo(destDir);
        }
        return ret;
    }
}
