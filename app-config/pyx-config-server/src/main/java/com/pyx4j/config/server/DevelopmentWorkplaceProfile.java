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
import java.util.Collections;

/**
 * Persistable Development settings changeable by developer.
 */
public abstract class DevelopmentWorkplaceProfile {

    public static final String WORKPLACE_PROFILE = "development-workplace.profile";

    private static File getDevelopmentProfileDirectory(String appName) {
        File currentDir = new File(".").getAbsoluteFile();
        File rootDir = currentDir;
        while (new File(rootDir, "pom.xml").exists()) {
            rootDir = rootDir.getParentFile();
            File dir = new File(rootDir, appName);
            if (new File(dir, WORKPLACE_PROFILE).exists() // Configuration file Exists
                    || new File(rootDir, WORKPLACE_PROFILE).exists()) { // Configuration template
                return dir;
            }
        }
        return new File(currentDir, appName);
    }

    private final File directory;

    private PropertiesConfiguration configProperties;

    protected DevelopmentWorkplaceProfile(String appName) {
        directory = getDevelopmentProfileDirectory(appName);
        File file = new File(directory, WORKPLACE_PROFILE);
        if (file.canRead()) {
            configProperties = new PropertiesConfiguration(null, PropertiesConfiguration.loadProperties(file));
        } else {
            configProperties = new PropertiesConfiguration(Collections.<String, String> emptyMap());
        }
    }

    public File getDirectory() {
        return directory;
    }

    public PropertiesConfiguration getProperties() {
        return configProperties;
    }

}
