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
 * Created on Oct 24, 2011
 * @author vlads
 */
package com.pyx4j.i18n.gettext;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class POPrintForEclipse {

    public static enum PrintType {

        corrections,

        style,

        all
    }

    public static void main(String[] args) throws IOException {
        File file = new File("corrections.po");

        PrintType printType = PrintType.corrections;
        printType = PrintType.style;

        if (args != null) {
            if (args.length > 0) {
                printType = PrintType.valueOf(args[0]);
            }
            if (args.length > 1) {
                file = new File(args[1]);
            }
        }

        int count = 0;
        POFile po = new POFileReader().read(file);
        for (POEntry entry : po.entries) {

            switch (printType) {
            case corrections:
                if (entry.untranslated.equals(entry.translated) || entry.translated == null || entry.translated.length() == 0) {
                    continue;
                }
                System.out.println(entry.translated);
                break;
            case style:
                if (!needsStyleCorrection(entry)) {
                    continue;
                }
                System.out.println(entry.untranslated);
                break;
            case all:
                System.out.println(entry.untranslated);
                System.out.println(entry.translated);
                break;
            }

            for (String ref : entry.references) {
                System.out.println("\t" + toEclipse(ref));
            }
            System.out.println();
            count++;
        }

        System.out.println();
        System.out.println("Found " + count + " corrections in file " + file.getCanonicalPath());
    }

    private static Map<String, POEntry> exsisting = new HashMap<String, POEntry>();

    private static boolean needsStyleCorrection(POEntry entry) {
        String untranslated = entry.untranslated;
        String trim = untranslated.trim().replaceAll("\\s", " ").toLowerCase(Locale.ENGLISH);
        if (exsisting.containsKey(trim)) {
            POEntry entryDuplicate = exsisting.get(trim);
            System.out.println("--Duplicate entry");
            System.out.println(entryDuplicate.untranslated);
            for (String ref : entryDuplicate.references) {
                System.out.println("\t" + toEclipse(ref));
            }
            return true;
        }
        exsisting.put(trim, entry);
        if (!untranslated.equals(untranslated.trim())) {
            return true;
        }
        if (untranslated.endsWith(":") || (untranslated.endsWith(".") && !untranslated.endsWith("..."))) {
            return true;
        }
        return false;
    }

    private static String toEclipse(String ref) {
        String[] parts = ref.split(":");
        return " (" + parts[0].replace('/', '.') + ":" + parts[1] + ")";
    }
}
