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
 * Created on Oct 11, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.gettext;

import java.io.IOException;
import java.util.Collection;

import org.objectweb.asm.tree.analysis.AnalyzerException;

import ut.annotations.ChildToTranslate;
import ut.annotations.I18nCaption;
import ut.annotations.SuperNotToTranslate;

import com.pyx4j.i18n.extractor.ConstantEntry;
import com.pyx4j.i18n.extractor.Extractor;

public class VerifyAddotations {

    public static void main(String[] args) throws IOException, AnalyzerException {
        System.out.println("--i18n tests --");
        Extractor ce = new Extractor();
        ce.readClass(ChildToTranslate.class);
        ce.readClass(SuperNotToTranslate.class);
        ce.readClass(I18nCaption.class);
        ce.complete();
        print(ce.getConstants());
    }

    private static void print(Collection<ConstantEntry> constants) {
        for (ConstantEntry entry : constants) {
            for (String line : entry.reference) {
                System.out.println("#: " + line);
            }
            System.out.println(entry.text);
        }
    }
}
