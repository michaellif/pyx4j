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
 * Created on 2011-03-18
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.dev;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.DeepReflectionToStringBuilder;
import org.apache.commons.lang.builder.DeepToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.IHaveServiceCallMarker;
import com.pyx4j.commons.IStringView;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.xml.XMLEntityWriter;
import com.pyx4j.entity.xml.XMLStringWriter;
import com.pyx4j.log4j.LoggerConfig;

public class DataDump {

    private final static Logger log = LoggerFactory.getLogger(DataDump.class);

    private static long debugCount = 0;

    public static final String defaultDirectory = "dump-entity";

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

        EntityList,

        SerializableObject
    }

    public static void dump(String type, List<? extends IEntity> entList) {
        dumpAny(defaultDirectory, type, entList, DataType.EntityList);
    }

    public static void dump(String type, IEntity ent) {
        dumpAny(defaultDirectory, type, ent, DataType.Entity);
    }

    public static void dump(String type, Serializable object) {
        dumpAny(defaultDirectory, type, object, DataType.SerializableObject);
    }

    public static void dumpToDirectory(String baseDirectory, String type, IEntity ent) {
        dumpAny(baseDirectory, type, ent, DataType.Entity);
    }

    public static IStringView xmlStringView(final IEntity ent) {
        return new IStringView() {
            @Override
            public String getStringView() {
                return toXmlString(ent);
            }

        };
    }

    public static String toXmlString(IEntity ent) {
        XMLStringWriter xml = new XMLStringWriter(StandardCharsets.UTF_8);
        DumpXMLEntityWriter xmlWriter = new DumpXMLEntityWriter(xml);
        xmlWriter.setEmitAttachLevel(true);
        xmlWriter.setEmitLogTransient(false);
        xmlWriter.setEmitXmlTransient(true);
        xmlWriter.setEmitIdentityHashCode(true);
        xmlWriter.write(ent);
        return xml.toString();
    }

    @SuppressWarnings("unchecked")
    private static void dumpAny(String baseDirectory, String type, Object item, DataType dataType) {
        if ((item == null) || (!ServerSideConfiguration.instance().isDevelopmentBehavior())) {
            return;
        }
        long id = ++debugCount;
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
        File dir;
        if (LoggerConfig.getContextName() != null) {
            dir = new File("logs", LoggerConfig.getContextName());
        } else {
            dir = new File("logs");
        }
        dir = new File(dir, baseDirectory);
        try {
            FileUtils.forceMkdir(dir);
            if (id == 1) {
                FileUtils.cleanDirectory(dir);
            }
        } catch (IOException e) {
            log.error("debug write", e);
            return;
        }
        File f = new File(dir, name.toString());
        FileWriter w = null;
        try {
            w = new FileWriter(f);
            switch (dataType) {
            case SerializableObject:
                try {
                    w.write(DeepReflectionToStringBuilder.toString(item, DeepToStringStyle.STYLE_NO_IDENTITY));
                } finally {
                    DeepReflectionToStringBuilder.removeThreadLocale();
                }
                break;
            default:
                XMLStringWriter xml = new XMLStringWriter(StandardCharsets.UTF_8);
                DumpXMLEntityWriter xmlWriter = new DumpXMLEntityWriter(xml);
                xmlWriter.setEmitAttachLevel(true);
                xmlWriter.setEmitLogTransient(false);
                xmlWriter.setEmitXmlTransient(true);
                xmlWriter.setEmitIdentityHashCode(true);
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
            log.debug("dumped value to file {}", f.getAbsolutePath());
        } catch (IOException e) {
            log.error("debug write", e);
        } finally {
            com.pyx4j.gwt.server.IOUtils.closeQuietly(w);
        }
    }
}
