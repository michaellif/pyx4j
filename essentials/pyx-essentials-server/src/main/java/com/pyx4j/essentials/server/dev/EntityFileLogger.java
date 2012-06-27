/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2012-06-27
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.dev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.IHaveServiceCallMarker;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.xml.XMLEntityWriter;
import com.pyx4j.essentials.server.xml.XMLStringWriter;
import com.pyx4j.log4j.LoggerConfig;

public class EntityFileLogger {

    private final static Logger log = LoggerFactory.getLogger(EntityFileLogger.class);

    private final static Map<File, Properties> fileNumbers = new Hashtable<File, Properties>();

    private static class DumpXMLEntityWriter extends XMLEntityWriter {

        public DumpXMLEntityWriter(XMLStringWriter xml) {
            super(xml);
        }

        @Override
        protected String getValueAsString(Object value) {
            if (value instanceof byte[]) {
                return "byte[" + ((byte[]) value).length + "] " + value.hashCode();
            } else {
                return super.getValueAsString(value);
            }
        }

    }

    private static enum DataType {

        Entity,

        EntityList
    }

    public static void log(String baseDirectory, String type, List<? extends IEntity> entList) {
        logAny(baseDirectory, type, entList, DataType.EntityList);
    }

    public static void log(String baseDirectory, String type, IEntity ent) {
        logAny(baseDirectory, type, ent, DataType.Entity);
    }

    @SuppressWarnings("unchecked")
    private static void logAny(String baseDirectory, String type, Object item, DataType dataType) {
        if (item == null) {
            return;
        }
        File dir;
        if (LoggerConfig.getContextName() != null) {
            dir = new File("logs", LoggerConfig.getContextName());
        } else {
            dir = new File("logs");
        }
        dir = new File(dir, baseDirectory);
        try {
            FileUtils.forceMkdir(dir);
        } catch (IOException e) {
            log.error("log write", e);
            return;
        }

        long id = getNextNumber(dir);
        NumberFormat nf = new DecimalFormat("0000");
        StringBuffer name = new StringBuffer(nf.format(id));
        name.append('-').append(type).append('-');
        String ext = ".log";
        {
            IEntity ent = null;
            switch (dataType) {
            case EntityList:
                name.append("list");
                if (!((List<?>) item).isEmpty()) {
                    ent = (IEntity) ((List<?>) item).get(0);
                }
                ext = ".xml";
                break;
            case Entity:
                ent = (IEntity) item;
                ext = ".xml";
                break;
            default:
                if (item != null) {
                    name.append(item.getClass().getSimpleName());
                    if (item instanceof IHaveServiceCallMarker) {
                        name.append('-').append(((IHaveServiceCallMarker) item).getServiceCallMarker().replace('\\', '_').replace('/', '_'));
                    }
                }
            }
            if (ent != null) {
                name.append(ent.getEntityMeta().getEntityClass().getSimpleName());
            }
        }
        name.append(ext);

        File f = new File(dir, name.toString());
        FileWriter w = null;
        try {
            w = new FileWriter(f);
            switch (dataType) {
            default:
                XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"));
                DumpXMLEntityWriter xmlWriter = new DumpXMLEntityWriter(xml);
                xmlWriter.setEmitAttachLevel(true);
                xmlWriter.setEmitLogTransient(false);
                xmlWriter.setEmitXmlTransient(true);
                if (item instanceof List) {
                    xml.start("list");
                    for (IEntity ent : (List<IEntity>) item) {
                        xmlWriter.write(ent);
                    }
                    xml.end();
                    w.write(xml.toString());
                } else if (item instanceof IEntity) {
                    xmlWriter.write((IEntity) item);
                    w.write(xml.toString());
                }
            }
            w.flush();
            log.debug("logged {} to file {}", type, f.getAbsolutePath());
        } catch (IOException e) {
            log.error("log write", e);
        } finally {
            com.pyx4j.gwt.server.IOUtils.closeQuietly(w);
        }
    }

    private static long getNextNumber(File dir) {
        File file = new File(dir, "log_number.properties");
        Properties props = fileNumbers.get(dir);

        if (props == null) {
            props = new Properties();
            try {
                props.load(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                log.error("File Not Found", e);
            } catch (IOException e) {
                log.error("IO Exception", e);
            }
            fileNumbers.put(dir, props);
        }

        String result = props.getProperty("number");
        if (result == null) {
            result = "0";
        }
        Long number = Long.valueOf(result);
        number += 1;
        props.put("number", number.toString());

        try {
            props.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            log.error("Error while saving  number", e);
            throw new Error(e.getLocalizedMessage());
        }

        return number;
    }
}
