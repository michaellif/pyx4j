/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Jul 23, 2015
 * @author vlads
 */
package com.pyx4j.config.server;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.pyx4j.commons.Consts;

/**
 * Persistable Development settings changeable by application.
 */
public abstract class DevelopmentProfile {

    public interface ValueProvider<T> {

        T getValue();
    }

    public static final String DEV_STORAGE = "dev.storage.properties";

    private final File directory;

    private final File file;

    private final PropertiesConfiguration devStorage;

    protected DevelopmentProfile(String appName) {
        directory = new File(System.getProperty("user.home"), appName);
        file = new File(directory, DEV_STORAGE);
        if (file.canRead()) {
            devStorage = new PropertiesConfiguration(null, PropertiesConfiguration.loadProperties(file));
        } else {
            devStorage = new PropertiesConfiguration(new HashMap<String, String>());
        }
    }

    public File getDirectory() {
        return directory;
    }

    /**
     * Persistable build/dev environment storage.
     */
    public PropertiesConfiguration getDevStorage() {
        return devStorage;
    }

    public void saveDevStorage() {
        directory.mkdirs();
        PropertiesConfiguration.saveProperties(file, getDevStorage().getProperties());
    }

    public boolean getCacheBooleanValue(String key, int checkEveyMinutes, ValueProvider<Boolean> valueProvider) {
        boolean value = getDevStorage().getBooleanValue(key, false);
        String checked = getDevStorage().getValue(key + ".checked", null);

        boolean needToCheck = false;
        if (checked != null) {
            try {
                Date checkedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(checked);
                needToCheck = (Math.abs(checkedDate.getTime() - System.currentTimeMillis()) > checkEveyMinutes * Consts.MIN2MSEC);
            } catch (ParseException e) {
                needToCheck = true;
            }
        } else {
            needToCheck = true;
        }

        if (needToCheck) {
            value = valueProvider.getValue();
            getDevStorage().getProperties().put(key, String.valueOf(value));
            getDevStorage().getProperties().put(key + ".checked", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            saveDevStorage();
        }

        return value;
    }
}
