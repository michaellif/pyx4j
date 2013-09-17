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
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DirectoryScanner implements Scanner {

    private final File dir;

    private final String baseName;

    public DirectoryScanner(File dir) throws IOException {
        this.dir = dir;
        baseName = dir.getCanonicalPath();
    }

    @Override
    public Iterable<ScannerEntry> getEntries() {
        return new Iterable<ScannerEntry>() {

            @Override
            public Iterator<ScannerEntry> iterator() {
                return new DirectoryIterator(dir);
            }
        };

    }

    private class DirectoryIterator implements Iterator<ScannerEntry> {

        File[] files;

        int processing;

        Iterator<ScannerEntry> child = null;

        DirectoryIterator(File dir) {
            files = dir.listFiles();
            if (files == null) {
                throw new Error(dir.getAbsolutePath() + " path does not denote a directory");
            }
            processing = 0;
        }

        @Override
        public boolean hasNext() {
            return ((child != null) && (child.hasNext())) || (processing < files.length);
        }

        @Override
        public ScannerEntry next() {
            if (child != null) {
                try {
                    return child.next();
                } catch (NoSuchElementException e) {
                    child = null;
                }
            }
            if (processing >= files.length) {
                throw new NoSuchElementException();
            }
            File next = files[processing++];
            if (next.isDirectory()) {
                child = new DirectoryIterator(next);
            }
            return new FileScannerEntry(next, DirectoryScanner.this.baseName);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public void close() {

    }

}
