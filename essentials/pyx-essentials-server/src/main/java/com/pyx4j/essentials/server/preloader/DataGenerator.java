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
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.pyx4j.commons.FIFO;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;

public class DataGenerator {

    static String[] firstNames;

    static String[] lastNames;

    static List<AddressInfo> adresses;

    private static int areCodes[] = { 416, 905, 647 };

    public static enum GeneratorType {
        Boolean, FirstName, LastName, Letter, Name, Phone, Address, PostalCode, ZipCode,
    }

    private static class GeneratorContext {

        Random random = new Random();

        private Map<String, FIFO<Integer>> duplicates;

        private void setRandomSeed(long seed) {
            random.setSeed(seed);
            if (duplicates != null) {
                duplicates.clear();
            }
        }

        /**
         * Avoid creation of same values during data generation
         */
        int nextInt(int n, String duplicatesId, int resultsToRemeber) {
            if (duplicates == null) {
                duplicates = new HashMap<String, FIFO<Integer>>();
            }
            FIFO<Integer> fifo = duplicates.get(duplicatesId);
            if (fifo == null) {
                fifo = new FIFO<Integer>(resultsToRemeber);
                duplicates.put(duplicatesId, fifo);
            }

            // Avoid infinite loop
            for (int i = 0; i <= 10; i++) {
                int val = random.nextInt(n);
                if (!fifo.contains(val)) {
                    fifo.push(val);
                    return val;
                }
            }
            throw new Error("Can't generate unique value for Ids:" + duplicatesId);
        }

    }

    private static final ThreadLocal<GeneratorContext> generatorLocal = new ThreadLocal<GeneratorContext>() {
        @Override
        protected GeneratorContext initialValue() {
            return new GeneratorContext();
        }
    };

    public static void setRandomSeed(long seed) {
        generatorLocal.get().setRandomSeed(seed);
    }

    protected static Random random() {
        return generatorLocal.get().random;
    }

    public static int nextInt(int n, String duplicatesId, int resultsToRemeber) {
        return generatorLocal.get().nextInt(n, duplicatesId, resultsToRemeber);
    }

    public static String randomLetters(int count) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < count; i++) {
            b.append(randomLetter());
        }
        return b.toString();
    }

    public static char randomLetter() {
        return (char) (('A') + random().nextInt('Z' - 'A'));
    }

    private static String resourceFileName(String fileName) {
        return DataGenerator.class.getPackage().getName().replace('.', '/') + "/" + fileName;
    }

    public static String randomFirstName() {
        if (firstNames == null) {
            firstNames = CSVLoad.loadFile(resourceFileName("first-names.csv"), "Name");
        }
        return firstNames[nextInt(firstNames.length, "firstName", 20)];
    }

    public static String randomLastName() {
        if (lastNames == null) {
            lastNames = CSVLoad.loadFile(resourceFileName("last-names.csv"), "Name");
        }
        return lastNames[nextInt(lastNames.length, "lastName", 5)];
    }

    public static String randomName() {
        return randomFirstName() + " " + randomLastName();
    }

    public static AddressInfo randomAddressInfo() {
        if (adresses == null) {
            adresses = EntityCSVReciver.create(AddressInfo.class).loadFile(resourceFileName("postal_codes.csv"));
        }
        return adresses.get(nextInt(adresses.size(), "address", 10));
    }

    public static String randomAddress() {
        return randomAddressInfo().getStringView();
    }

    /**
     * Five random numbers
     */
    public static String randomZipCode() {
        return "" + random().nextInt(10) + random().nextInt(10) + random().nextInt(10) + random().nextInt(10) + random().nextInt(10);
    }

    private static final char[] canadianPostalFirtLetters = "ABCEGHJKLMNPRSTVXY".toCharArray();

    private static final char[] canadianPostalLetters = "ABCEGHJKLMNPRSTVWXYZ".toCharArray();

    /**
     * X0X 0X0 format
     */
    public static String randomPostalCode() {
        StringBuilder sb = new StringBuilder();

        sb.append(random(canadianPostalFirtLetters));
        sb.append(random().nextInt(10));
        sb.append(random(canadianPostalLetters));
        sb.append(" ");
        sb.append(random().nextInt(10));
        sb.append(random(canadianPostalLetters));
        sb.append(random().nextInt(10));

        return sb.toString();
    }

    public static String randomPhone() {
        return randomPhone(String.valueOf(areCodes[random().nextInt(areCodes.length)]));
    }

    public static String randomPhone(String areaCode) {
        DecimalFormat nf = new DecimalFormat("0000000");
        String unformatedPhone = areaCode + nf.format((random().nextInt(10000000)));
        return "(" + unformatedPhone.subSequence(0, 3) + ") " + unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10);
    }

    public static int randomInt() {
        return random().nextInt();
    }

    public static int randomInt(int n) {
        return random().nextInt(n);
    }

    public static double randomDouble(double max) {
        return max * random().nextDouble();
    }

    public static boolean randomBoolean() {
        return random().nextBoolean();
    }

    public static <E extends Enum<E>> E randomEnum(Class<E> elementType) {
        EnumSet<E> all = EnumSet.allOf(elementType);
        int r = random().nextInt(all.size());
        int n = 0;
        for (E en : all) {
            if (n == r) {
                return en;
            }
            n++;
        }
        return null;
    }

    public static <T> T random(List<T> list) {
        if (list.size() == 0) {
            return null;
        }
        int index = random().nextInt(list.size());
        return list.get(index);
    }

    public static <T> T random(T[] array) {
        if (array.length == 0) {
            return null;
        }
        int index = random().nextInt(array.length);
        return array[index];
    }

    public static <T> T random(T[] array, String duplicatesId, int resultsToRemeber) {
        if (array.length == 0) {
            return null;
        }
        int index = nextInt(array.length, duplicatesId, resultsToRemeber);
        return array[index];
    }

    private static char random(char[] array) {
        if (array.length == 0) {
            return '?';
        }
        int index = random().nextInt(array.length);
        return array[index];
    }

    public static LogicalDate randomDate(int month) {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, 2011);
        c.set(Calendar.MONTH, 1);
        c.set(Calendar.DATE, 1);

        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);

        c.add(Calendar.MONTH, (month > 0) ? random().nextInt(month) : -random().nextInt(-month));
        // DB does not store Milliseconds
        c.set(Calendar.MILLISECOND, 0);
        return new LogicalDate(c.getTime());
    }

    public static String random(GeneratorType type) {
        switch (type) {
        case Boolean:
            return String.valueOf(randomBoolean());
        case FirstName:
            return randomFirstName();
        case LastName:
            return randomLastName();
        case Name:
            return randomName();
        case Letter:
            return String.valueOf(randomLetter());
        case Phone:
            return randomPhone();
        case Address:
            return randomAddress();
        case PostalCode:
            return randomPostalCode();
        case ZipCode:
            return randomZipCode();
        default:
            throw new IllegalArgumentException();
        }
    }
}
