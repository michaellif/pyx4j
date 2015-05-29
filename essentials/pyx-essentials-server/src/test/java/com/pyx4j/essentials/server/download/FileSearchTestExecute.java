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
import java.util.List;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.gwt.server.DateUtils;

public class FileSearchTestExecute {

    /*
     * Tests:
     * Regular HDD | Local Net | IP Network (WiFi)
     * 
     * STD java:
     * Fill text search: 01min 14sec
     * + Date Boundaries: 24sec | 40sec | 04min 58sec
     * + Time Boundaries: 721msec
     * 
     * Use NIO version:
     * Fill text search: 01min 13sec
     * + Date Boundaries: 21sec | 40sec | 03min 36sec
     * + Time Boundaries: 403msec
     */

    public static void main(String[] args) throws Exception {

        boolean useNIO = false;
        boolean useDateBoundaries = true;
        boolean useTimeBoundaries = true;

        FileSearchFilter filter = new FileSearchFilter();

        //filter.setText("2015-05-22 23:29:20,284");
        filter.setRecursive(true);

        if (useDateBoundaries) {
            filter.setFromDate(DateUtils.detectDateformat("2015-05-22"));
            filter.setToDate(DateUtils.detectDateformat("2015-05-22"));
        }

        if (useTimeBoundaries) {
            filter.setFromTime("23:00");
            filter.setToTime("23:59");
        }

        File dir;
        dir = new File("E:/oradata/amx-prod/2015-may-21,22");
        //dir = new File("//VLADS-I7B/test-logs");
        //dir = new File("//10.1.1.152/test-logs/2015-05-22");

        System.out.println("use NIO             :" + useNIO);
        System.out.println("use Date Boundaries :" + useDateBoundaries);
        System.out.println("use Time Boundaries :" + useTimeBoundaries);

        System.out.println("searching ...");

        long start = System.currentTimeMillis();

        List<File> found;
        if (useNIO) {
            found = FileSearch.searchFileNio(filter, dir.toPath(), null);
        } else {
            found = FileSearch.searchFile(filter, dir, null);
        }

        System.out.println("found " + found.size() + " file(s)");
        if (found.size() == 1) {
            System.out.println("found " + found.get(0));
        }

        System.out.println("Search duration " + TimeUtils.secSince(start));

    }
}
