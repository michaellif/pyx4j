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
 * Created on Sep 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.gettext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.objectweb.asm.tree.analysis.AnalyzerException;

import com.pyx4j.i18n.extractor.ConstantEntry;
import com.pyx4j.i18n.extractor.Extractor;

public class Verify {

    public static void main(String[] args) throws IOException, AnalyzerException {
        InputStream in;
        System.out.println("--i18n tests --");
        in = Verify.class.getResourceAsStream("/ut/MainClass.class");
        try {
            Extractor ce = new Extractor();
            ce.readClass(in);
            ce.complete();
            print(ce.getConstants());
        } finally {
            in.close();
        }

        in = Verify.class.getResourceAsStream("/ut/EnumTranslatable.class");
        try {
            Extractor ce = new Extractor();
            ce.readClass(in);
            ce.complete();
            print(ce.getConstants());
        } finally {
            in.close();
        }

        in = Verify.class.getResourceAsStream("/ut/EnumWithTranslations.class");
        try {
            Extractor ce = new Extractor();
            ce.readClass(in);
            ce.complete();
            print(ce.getConstants());
        } finally {
            in.close();
        }
    }

    private static void print(Collection<ConstantEntry> constants) {
        for (ConstantEntry entry : constants) {
            if (entry.comments != null) {
                for (String line : entry.comments) {
                    System.out.println("#. " + line);
                }
            }
            for (String line : entry.reference) {
                System.out.println("#: " + line);
            }
            System.out.println(entry.text);
        }
    }
}
