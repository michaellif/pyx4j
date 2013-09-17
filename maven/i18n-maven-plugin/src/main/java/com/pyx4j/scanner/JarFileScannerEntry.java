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
 * This classes are taken from Jour
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.scanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileScannerEntry implements ScannerEntry {

    JarFile jarFile;

    JarEntry jarEntry;

    public JarFileScannerEntry(JarFile jarFile, JarEntry jarEntry) {
        this.jarFile = jarFile;
        this.jarEntry = jarEntry;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return jarFile.getInputStream(this.jarEntry);
    }

    @Override
    public ScannerEntry getOrigin() {
        return this;
    }

    @Override
    public String getName() {
        return jarEntry.getName();
    }

    @Override
    public long getSize() {
        return jarEntry.getSize();
    }

    @Override
    public long getTime() {
        return jarEntry.getTime();
    }

    @Override
    public boolean isDirectory() {
        return jarEntry.isDirectory();
    }

}
