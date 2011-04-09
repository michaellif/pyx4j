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

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;

public class DataGenerator {

    static String[] firstNames;

    static String[] lastNames;

    static List<AddressInfo> adresses;

    protected static Random random = new Random();

    private static int areCodes[] = { 416, 905, 647 };

    public static void setRandomSeed(long seed) {
        random.setSeed(seed);
    }

    public static String randomLetters(int count) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < count; i++) {
            b.append(randomLetter());
        }
        return b.toString();
    }

    public static char randomLetter() {
        return (char) (('A') + random.nextInt('Z' - 'A'));
    }

    private static String resourceFileName(String fileName) {
        return DataGenerator.class.getPackage().getName().replace('.', '/') + "/" + fileName;
    }

    public static String randomFirstName() {
        if (firstNames == null) {
            firstNames = CSVLoad.loadFile(resourceFileName("first-names.csv"), "Name");
        }
        return firstNames[random.nextInt(firstNames.length)];
    }

    public static String randomLastName() {
        if (lastNames == null) {
            lastNames = CSVLoad.loadFile(resourceFileName("last-names.csv"), "Name");
        }
        return lastNames[random.nextInt(lastNames.length)];
    }

    public static String randomName() {
        return randomFirstName() + " " + randomLastName();
    }

    public static AddressInfo randomAddressInfo() {
        if (adresses == null) {
            adresses = EntityCSVReciver.create(AddressInfo.class).loadFile(resourceFileName("postal_codes.csv"));
        }
        return adresses.get(random.nextInt(adresses.size()));
    }

    /**
     * Five random numbers
     */
    public static String randomZipCode() {
        return "" + random.nextInt(10) + random.nextInt(10) + random.nextInt(10) + random.nextInt(10) + random.nextInt(10);
    }

    /**
     * X0X 0X0 format
     */
    public static String randomPostalCode() {
        StringBuilder sb = new StringBuilder();

        sb.append(randomLetter());
        sb.append(random.nextInt(10));
        sb.append(randomLetter());
        sb.append(" ");
        sb.append(random.nextInt(10));
        sb.append(randomLetter());
        sb.append(random.nextInt(10));

        return sb.toString();
    }

    public static String randomPhone() {
        return randomPhone(String.valueOf(areCodes[random.nextInt(areCodes.length)]));
    }

    public static String randomPhone(String areaCode) {
        DecimalFormat nf = new DecimalFormat("0000000");
        String unformatedPhone = areaCode + nf.format((random.nextInt(10000000)));
        return unformatedPhone.subSequence(0, 3) + "-" + unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10);
    }

    public static int randomInt(int n) {
        return random.nextInt(n);
    }

    public static <E extends Enum<E>> E randomEnum(Class<E> elementType) {
        EnumSet<E> all = EnumSet.allOf(elementType);
        int r = random.nextInt(all.size());
        int n = 0;
        for (E en : all) {
            if (n == r) {
                return en;
            }
            n++;
        }
        return null;
    }

    public static Date randomDate(int month) {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.MONTH, (month > 0) ? random.nextInt(month) : -random.nextInt(-month));
        return c.getTime();
    }
}
