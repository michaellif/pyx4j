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
 * Created on 2011-06-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.config.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesConfiguration {

    private final String prefix;

    private final Map<String, String> properties;

    public PropertiesConfiguration(Map<String, String> properties) {
        this(null, properties);
    }

    public PropertiesConfiguration(String prefix, Map<String, String> properties) {
        this.prefix = prefix;
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getValue(String key) {
        return properties.get((prefix == null) ? key : prefix + "." + key);
    }

    public String getValue(String key, String defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public int getIntegerValue(String key, int defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        } else {
            return Integer.valueOf(value).intValue();
        }
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        } else {
            return Boolean.valueOf(value).booleanValue();
        }
    }

    public <T extends Enum<T>> T getEnumValue(String key, Class<T> enumClass, T defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        } else {
            return Enum.valueOf(enumClass, value);
        }
    }

    public static Map<String, String> loadProperties(File file) {
        Properties p = new Properties();
        FileReader reader = null;
        try {
            p.load(reader = new FileReader(file));
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Throwable e) {
            }
        }
        Map<String, String> m = new HashMap<String, String>();
        for (String key : p.stringPropertyNames()) {
            m.put(key, p.getProperty(key));
        }
        return m;
    }
}
