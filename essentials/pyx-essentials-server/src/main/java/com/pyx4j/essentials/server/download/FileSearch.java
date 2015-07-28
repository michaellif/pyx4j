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
 * Created on May 25, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server.download;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.gwt.server.DateUtils;

public class FileSearch {

    public static List<File> searchFile(final FileSearchFilter filter, File dir, final FileSearchProgressCallback callback) {
        final Calendar fromTimeC = new GregorianCalendar();
        if (filter.getFromTime() != null) {
            DateUtils.setTime(fromTimeC, filter.getFromTime());
        }

        final Calendar toTimeC = new GregorianCalendar();
        if (filter.getToTime() != null) {
            DateUtils.setTime(toTimeC, filter.getToTime());
        }

        List<File> allFiles = new ArrayList<>();
        final Queue<File> dirs = new LinkedList<>();
        dirs.add(dir);
        while (!dirs.isEmpty()) {
            File cdir = dirs.poll();
            File[] files = cdir.listFiles(new FileFilter() {

                @Override
                public boolean accept(File file) {
                    if (filter.isRecursive() && file.isDirectory()) {
                        dirs.add(file);
                    }

                    Date fileDate = new Date(file.lastModified());
                    Calendar fileTimeC = new GregorianCalendar();
                    DateUtils.setTime(fileTimeC, new Time(fileDate.getTime()));

                    if (filter.getFromDateTime() != null) {
                        if (fileDate.before(filter.getFromDateTime())) {
                            return false;
                        }
                    } else if (filter.getFromTime() != null) {
                        if (fileTimeC.before(fromTimeC)) {
                            return false;
                        }
                    }

                    if (filter.getToDateTime() != null) {
                        if (fileDate.after(filter.getToDateTime())) {
                            return false;
                        }
                    } else if (filter.getToTime() != null) {
                        if (fileTimeC.after(toTimeC)) {
                            return false;
                        }
                    }

                    if (file.isDirectory()) {
                        return !CommonsStringUtils.isStringSet(filter.getText());
                    } else if (CommonsStringUtils.isStringSet(filter.getText())) {
                        return contains(file, filter.getText());
                    } else {
                        return true;
                    }
                }
            });

            allFiles.addAll(Arrays.asList(files));
        }

        return allFiles;

    }

    public static List<File> searchFileNio(final FileSearchFilter filter, final Path baseDir, final FileSearchProgressCallback callback) throws IOException {
        final Calendar fromTimeC = new GregorianCalendar();
        if (filter.getFromTime() != null) {
            DateUtils.setTime(fromTimeC, filter.getFromTime());
        }

        final Calendar toTimeC = new GregorianCalendar();
        if (filter.getToTime() != null) {
            DateUtils.setTime(toTimeC, filter.getToTime());
        }

        final boolean doTextSearch = CommonsStringUtils.isStringSet(filter.getText());

        final List<File> allFiles = new ArrayList<>();

        Files.walkFileTree(baseDir, EnumSet.of(FileVisitOption.FOLLOW_LINKS), 20, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (callback != null) {
                    callback.onVisitDirectory();
                }

                if ((!doTextSearch) && (!baseDir.equals(dir))) {
                    allFiles.add(dir.toFile());
                }

                if (filter.isRecursive()) {
                    return FileVisitResult.CONTINUE;
                } else if (baseDir.equals(dir)) {
                    return FileVisitResult.CONTINUE;
                } else {
                    return FileVisitResult.SKIP_SUBTREE;
                }
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!fileDateMatches(attrs)) {
                    return FileVisitResult.CONTINUE;
                }

                if (doTextSearch) {
                    if (contains(file, filter.getText())) {
                        allFiles.add(file.toFile());
                    }
                } else {
                    allFiles.add(file.toFile());
                }
                return FileVisitResult.CONTINUE;
            }

            private boolean fileDateMatches(BasicFileAttributes attrs) {
                Date fileDate = new Date(attrs.lastModifiedTime().toMillis());
                Calendar fileTimeC = new GregorianCalendar();
                DateUtils.setTime(fileTimeC, new Time(fileDate.getTime()));

                if (filter.getFromDateTime() != null) {
                    if (fileDate.before(filter.getFromDateTime())) {
                        return false;
                    }
                } else if (filter.getFromTime() != null) {
                    if (fileTimeC.before(fromTimeC)) {
                        return false;
                    }
                }

                if (filter.getToDateTime() != null) {
                    if (fileDate.after(filter.getToDateTime())) {
                        return false;
                    }
                } else if (filter.getToTime() != null) {
                    if (fileTimeC.after(toTimeC)) {
                        return false;
                    }
                }

                return true;
            }

        });

        return allFiles;
    }

    private static boolean contains(File file, String text) {
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            return false;
        }
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(text)) {
                    return true;
                }
            }
        } finally {
            scanner.close();
        }
        return false;
    }

    private static boolean contains(Path file, String text) throws IOException {
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            return false;
        }
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(text)) {
                    return true;
                }
            }
        } finally {
            scanner.close();
        }
        return false;
    }
}
