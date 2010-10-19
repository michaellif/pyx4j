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
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        BackupUtils.ensureDirectoryExists(file);
    }

    protected String makeFileName(String fileName, Date backupDate) {
        return BackupUtils.makeFileName(fileName, backupDate);
    }

    @Override
    public void start() {
        xml.startIdented("Backup");
    }

    @Override
    public void save(Vector<HashMap<String, BackupEntityProperty>> records) {
        for (HashMap<String, BackupEntityProperty> record : records) {
            BackupEntityProperty key = record.get(Entity.KEY_RESERVED_PROPERTY);
            BackupKey keyValue = (BackupKey) key.getValue();

            Map<String, String> entityAttributes = new LinkedHashMap<String, String>();
            entityAttributes.put("id", String.valueOf(keyValue.getId()));

            xml.startIdented(keyValue.getKind(), entityAttributes);

            for (Map.Entry<String, BackupEntityProperty> me : record.entrySet()) {
                String propertyName = me.getKey();
                if (propertyName.equals(Entity.KEY_RESERVED_PROPERTY)) {
                    continue;
                }
                Map<String, String> attributes = new LinkedHashMap<String, String>();
                BackupEntityProperty property = me.getValue();
                Serializable value = property.getValue();
                attributes.put("type", XMLBackupUtils.getValueType(value));
                if (property.isIndexed()) {
                    attributes.put("idx", "Y");
                }
                if (value instanceof Collection) {
                    xml.startIdented(propertyName, attributes);
                    for (Object item : (Collection<?>) value) {
                        Map<String, String> itemAttributes = new HashMap<String, String>();
                        itemAttributes.put("type", XMLBackupUtils.getValueType(item));
                        xml.write("item", itemAttributes, XMLBackupUtils.getValueAsString(item));
                    }
                    xml.endIdented(propertyName);
                } else {
                    xml.write(propertyName, attributes, XMLBackupUtils.getValueAsString(value));
                }
            }
            xml.endIdented(keyValue.getKind());
        }
        totalRecords += records.size();
    }

    @Override
    public void end() {
        xml.endIdented("Backup");
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
