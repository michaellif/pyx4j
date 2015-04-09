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
 * Created on 2013-02-20
 * @author vlads
 */
package com.pyx4j.essentials.server.dev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.gwt.server.IOUtils;

public class NumberInFile {

    private final static Logger log = LoggerFactory.getLogger(EntityFileLogger.class);

    private final static Map<File, Properties> fileNumbers = new Hashtable<File, Properties>();

    private final File file;

    public NumberInFile(File dir) {
        file = new File(dir, "log_number.properties");
    }

    public long getNextNumber() {
        return getNextNumber(file);
    }

    private static long getNextNumber(File file) {
        Properties props = fileNumbers.get(file);

        if (props == null) {
            props = new Properties();
            if (file.canRead()) {
                InputStream in = null;
                try {
                    props.load(in = new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    log.error("File Not Found", e);
                } catch (IOException e) {
                    log.error("IO Exception", e);
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
            fileNumbers.put(file, props);
        }

        String result = props.getProperty("number");
        if (result == null) {
            result = "0";
        }
        Long number = Long.valueOf(result);
        number += 1;
        props.put("number", number.toString());

        OutputStream out = null;
        try {
            file.getParentFile().mkdirs();
            props.store(out = new FileOutputStream(file), null);
        } catch (IOException e) {
            log.error("Error while saving  number", e);
            throw new Error(e.getLocalizedMessage());
        } finally {
            IOUtils.closeQuietly(out);
        }

        return number;
    }
}
