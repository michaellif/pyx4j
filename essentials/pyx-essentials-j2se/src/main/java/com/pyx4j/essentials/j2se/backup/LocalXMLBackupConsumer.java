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
 * Created on Sep 19, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.j2se.backup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Entity;

import com.pyx4j.essentials.rpc.admin.BackupEntityProperty;
import com.pyx4j.essentials.rpc.admin.BackupKey;
import com.pyx4j.essentials.server.report.XMLStringWriter;

public class LocalXMLBackupConsumer implements BackupConsumer {

    private static final Logger log = LoggerFactory.getLogger(LocalXMLBackupConsumer.class);

    private final File file;

    public long totalRecords;

    private final XMLStringWriter xml = new XMLStringWriter();

    public LocalXMLBackupConsumer(String fileName, boolean overrride, Date backupDate) {
        fileName = makeFileName(fileName, backupDate);
        file = new File(fileName);
        if (overrride && file.exists()) {
            file.delete();
        }
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new Error("Can't create backup destination directory");
            }
        }
    }

    protected String makeFileName(String fileName, Date backupDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat tf = new SimpleDateFormat("HH-mm");
        return fileName.replace("(date)", df.format(backupDate)).replace("(time)", tf.format(backupDate));
    }

    @Override
    public void start() {
        xml.start("Backup");
    }

    @Override
    public void save(Vector<HashMap<String, BackupEntityProperty>> records) {
        for (HashMap<String, BackupEntityProperty> record : records) {
            BackupEntityProperty key = record.get(Entity.KEY_RESERVED_PROPERTY);
            BackupKey keyValue = (BackupKey) key.getValue();
            xml.startIdented(keyValue.getKind());
            xml.write("id", keyValue.getId());

            for (Map.Entry<String, BackupEntityProperty> me : record.entrySet()) {
                String propertyName = me.getKey();
                if (propertyName.equals(Entity.KEY_RESERVED_PROPERTY)) {
                    continue;
                }
                BackupEntityProperty property = me.getValue();
                if (property.isIndexed()) {
                    // TODO
                } else {
                }
                xml.write(propertyName, property.getValue());
            }
            xml.endIdented(keyValue.getKind());
        }
        totalRecords += records.size();
    }

    @Override
    public void end() {
        xml.end("Backup");
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(xml.toString());
            writer.close();

            log.info("Saved {} records to {}", totalRecords, file.getAbsolutePath());
        } catch (IOException e) {
            log.error("error saving records to file {}", e);
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

}
