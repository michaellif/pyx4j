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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileScanner implements Scanner {

    JarFile jarFile;

    public JarFileScanner(File file) throws IOException {
        jarFile = new JarFile(file);
    }

    @Override
    public void close() {
        try {
            jarFile.close();
        } catch (IOException ignore) {
        }
    }

    @Override
    public Iterable<ScannerEntry> getEntries() {
        return new Iterable<ScannerEntry>() {

            @Override
            public Iterator<ScannerEntry> iterator() {
                return new JarEnumeration();
            }
        };

    }

    private class JarEnumeration implements Iterator<ScannerEntry> {

        Enumeration<JarEntry> jarEnum;

        JarEnumeration() {
            jarEnum = jarFile.entries();
        }

        @Override
        public boolean hasNext() {
            return jarEnum.hasMoreElements();
        }

        @Override
        public ScannerEntry next() {
            JarEntry jarEntry = jarEnum.nextElement();
            return new JarFileScannerEntry(jarFile, jarEntry);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
