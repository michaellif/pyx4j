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
import java.util.HashMap;

public class DevelopmentProfile {

    public static final String DEV_STORAGE = "dev.storage.properties";

    public static final String BRANCH_PROFILE = "development-branch.profile";

    public static File getDevelopmentProfileDirectory() {
        File currentDir = new File(".").getAbsoluteFile();
        File profileDir = currentDir;
        while (new File(profileDir, "pom.xml").exists()) {
            profileDir = profileDir.getParentFile();
            if (new File(profileDir, BRANCH_PROFILE + ".template").exists() || new File(profileDir, BRANCH_PROFILE).exists()) {
                return profileDir;
            }
        }
        return currentDir;
    }

    private PropertiesConfiguration devStorage;

    private static class SingletonHolder {
        public static final DevelopmentProfile INSTANCE = new DevelopmentProfile();
    }

    static DevelopmentProfile instance() {
        return SingletonHolder.INSTANCE;
    }

    private DevelopmentProfile() {
        File file = new File(getDevelopmentProfileDirectory(), DEV_STORAGE);
        if (file.canRead()) {
            devStorage = new PropertiesConfiguration(null, PropertiesConfiguration.loadProperties(file));
        } else {
            devStorage = new PropertiesConfiguration(new HashMap<String, String>());
        }
    }

    /**
     * Persistable build/dev environment storage.
     */
    public static PropertiesConfiguration getDevStorage() {
        return instance().devStorage;
    }

    public static void saveDevStorage() {
        PropertiesConfiguration.saveProperties(new File(getDevelopmentProfileDirectory(), DEV_STORAGE), getDevStorage().getProperties());
    }
}
