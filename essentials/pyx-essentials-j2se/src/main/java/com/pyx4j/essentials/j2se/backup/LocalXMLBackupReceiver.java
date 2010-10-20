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
 * Created on Oct 19, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.j2se.backup;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.appengine.api.datastore.Entity;

import com.pyx4j.essentials.rpc.admin.BackupEntityProperty;
import com.pyx4j.essentials.rpc.admin.BackupKey;
import com.pyx4j.essentials.rpc.admin.BackupRecordsResponse;
import com.pyx4j.essentials.rpc.admin.BackupRequest;
import com.pyx4j.gwt.server.IOUtils;

public class LocalXMLBackupReceiver extends AbstractBackupReceiver {

    private static final Logger log = LoggerFactory.getLogger(LocalXMLBackupReceiver.class);

    private final File file;

    private final Map<String, List<HashMap<String, BackupEntityProperty>>> data;

    public LocalXMLBackupReceiver(String fileName) {
        file = new File(fileName);
        if (!file.exists()) {
            throw new Error("File not found " + file.getAbsolutePath());
        }
        data = new HashMap<String, List<HashMap<String, BackupEntityProperty>>>();
    }

    @Override
    public void start() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(null);
            Document xmlDoc;
            // Read zip if necessary
            if (file.getName().endsWith(".zip")) {
                ZipFile zipFile = new ZipFile(file);
                InputStream zipin = null;
                try {
                    String name = file.getName().substring(0, file.getName().length() - 4);
                    ZipEntry entry = null;
                    if (name.endsWith(".xml")) {
                        entry = zipFile.getEntry(name);
                    }
                    if (entry == null) {
                        // if not found 
                        Enumeration<? extends ZipEntry> en = zipFile.entries();
                        while (en.hasMoreElements()) {
                            ZipEntry ze = en.nextElement();
                            if (ze.isDirectory()) {
                                continue;
                            } else if (ze.getName().endsWith(".xml")) {
                                entry = ze;
                                break;
                            }
                        }
                        if (entry == null) {
                            throw new RuntimeException("XML file not found in zip");
                        }
                    }
                    log.info("Reading file {}/{}", file.getAbsolutePath(), entry.getName());
                    xmlDoc = builder.parse(zipin = zipFile.getInputStream(entry));
                } finally {
                    IOUtils.closeQuietly(zipin);
                    zipFile.close();
                }
            } else {
                log.info("Reading file {}", file.getAbsolutePath());
                xmlDoc = builder.parse(file);
            }
            // Read the context
            NodeList list = xmlDoc.getElementsByTagName("Backup");
            if (list.getLength() == 0) {
                throw new RuntimeException("Invalid XML root");
            }
            Node rootNode = list.item(0);
            NodeList nodeList = rootNode.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    parsEntityNode((Element) node);
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException("Error parsing XML", e);
        }
    }

    private void parsEntityNode(Element node) {
        String kind = node.getNodeName();
        HashMap<String, BackupEntityProperty> entityRecord = new HashMap<String, BackupEntityProperty>();
        entityRecord.put(Entity.KEY_RESERVED_PROPERTY, new BackupEntityProperty(new BackupKey(kind, Long.valueOf(node.getAttribute("id"))), false));

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node valueNode = nodeList.item(i);
            if (valueNode instanceof Element) {
                entityRecord.put(valueNode.getNodeName(), parsValueNode((Element) valueNode));
            }
        }

        List<HashMap<String, BackupEntityProperty>> entityList = data.get(kind);
        if (entityList == null) {
            entityList = new Vector<HashMap<String, BackupEntityProperty>>();
            data.put(kind, entityList);
        }
        entityList.add(entityRecord);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private BackupEntityProperty parsValueNode(Element valueNode) {
        Serializable value = XMLBackupUtils.valueOf(valueNode.getTextContent(), valueNode.getAttribute("type"));
        if (value instanceof Collection) {
            NodeList nodeList = valueNode.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node itemNode = nodeList.item(i);
                if ((itemNode instanceof Element) && ("item".equals(itemNode.getNodeName()))) {
                    ((Collection) value).add(XMLBackupUtils.valueOf(itemNode.getTextContent(), ((Element) itemNode).getAttribute("type")));
                }
            }
        }
        return new BackupEntityProperty(value, "Y".equals(valueNode.getAttribute("idx")));
    }

    @Override
    public void end() {
    }

    @Override
    protected BackupRecordsResponse get(BackupRequest request) {
        BackupRecordsResponse response = new BackupRecordsResponse();

        List<HashMap<String, BackupEntityProperty>> entityList = data.get(request.getEntityKind());
        if (entityList == null) {
            return response;
        }

        ListIterator<HashMap<String, BackupEntityProperty>> iterator;
        if (request.getEncodedCursorRefference() != null) {
            iterator = entityList.listIterator(Integer.valueOf(request.getEncodedCursorRefference()));
        } else {
            iterator = entityList.listIterator();
        }
        while (iterator.hasNext()) {
            HashMap<String, BackupEntityProperty> entityRecord = iterator.next();
            response.addRecord(entityRecord);
            if (response.size() >= request.getResponceSize()) {
                response.setEncodedCursorRefference(String.valueOf(iterator.nextIndex()));
                break;
            }
        }

        return response;
    }

}
