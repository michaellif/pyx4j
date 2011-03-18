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
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.report.XMLStringWriter;
import com.pyx4j.essentials.server.xml.XMLEntityConverter;
import com.pyx4j.log4j.LoggerConfig;

public class DataDump {

    private final static Logger log = LoggerFactory.getLogger(DataDump.class);

    private static long debugCount = 0;

    public static void dump(String type, IEntity ent) {
        if ((ent == null) || (!ServerSideConfiguration.instance().isDevelopmentBehavior())) {
            return;
        }
        long id = ++debugCount;
        NumberFormat nf = new DecimalFormat("0000");
        StringBuffer name = new StringBuffer(nf.format(id));
        name.append('-').append(type);
        name.append('-').append(ent.getEntityMeta().getEntityClass().getSimpleName());
        name.append(".xml");
        File dir;
        if (LoggerConfig.getContextName() != null) {
            dir = new File("logs", LoggerConfig.getContextName());
        } else {
            dir = new File("logs");
        }
        dir = new File(dir, "dump-entity");
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
            XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"));
            XMLEntityConverter.write(xml, ent);
            w.write(xml.toString());
            w.flush();
            log.debug("dumped value to file", f.getAbsolutePath());
        } catch (IOException e) {
            log.error("debug write", e);
        } finally {
            com.pyx4j.gwt.server.IOUtils.closeQuietly(w);
        }
    }
}
