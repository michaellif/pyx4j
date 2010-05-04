/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-04-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.preloader;

import java.util.Random;

import com.pyx4j.essentials.server.csv.CSVLoad;

public class DataGenerator {

    static String[] firstNames;

    static String[] lastNames;

    public static String randomLetters(int count) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < count; i++) {
            b.append(randomLetter());
        }
        return b.toString();
    }

    public static char randomLetter() {
        return (char) (('A') + new Random().nextInt('Z' - 'A'));
    }

    private static String resourceFileName(String fileName) {
        return DataGenerator.class.getPackage().getName().replace('.', '/') + "/" + fileName;
    }

    public static String randomFirstName() {
        if (firstNames == null) {
            firstNames = CSVLoad.loadFile(resourceFileName("first-names.csv"), "name");
        }
        return firstNames[new Random().nextInt(firstNames.length)];
    }

    public static String randomLastName() {
        if (lastNames == null) {
            lastNames = CSVLoad.loadFile(resourceFileName("last-names.csv"), "name");
        }
        return lastNames[new Random().nextInt(lastNames.length)];
    }

    public static String randomName() {
        return randomFirstName() + " " + randomLastName();
    }
}
