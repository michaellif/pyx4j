/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Sep 26, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.j2se.backup;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class BackupUtils {

    static void ensureDirectoryExists(File file) {
        File dir = file.getAbsoluteFile().getParentFile();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new Error("Can't create backup destination directory");
            }
        }
        if (!dir.isDirectory()) {
            throw new Error("File " + dir.getAbsolutePath() + " exists and is not a directory.");
        }
    }

    static String makeFileName(String fileName, Date backupDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat tf = new SimpleDateFormat("HH-mm");
        return fileName.replace("(date)", df.format(backupDate)).replace("(time)", tf.format(backupDate));
    }
}
