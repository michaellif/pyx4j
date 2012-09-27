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
 * Created on Oct 7, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.extractor;

import java.util.Collection;

import junit.framework.TestCase;
import ut.MainClass;
import ut.annotations.ChildToTranslate;
import ut.annotations.I18nCaption;
import ut.annotations.SuperNotToTranslate;

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.i18n.annotations.I18nAnnotation;

public class AnnotationsExtractorTest extends TestCase {

    public void testAnnotations() throws Exception {
        Extractor ce = new Extractor();
        ce.readClass(ChildToTranslate.class);
        ce.readClass(SuperNotToTranslate.class);
        ce.readClass(I18nCaption.class);
        ce.readClass(MainClass.class);
        ce.complete();

        //print(ce.getConstants());

        Collection<String> extracted = ce.getConstantsText();

        assertFalse(extracted.contains("notExtracted"));
        assertFalse(extracted.contains("Not Extracted"));
        assertFalse(extracted.contains(EnglishGrammar.capitalize(SuperNotToTranslate.class.getSimpleName())));

        assertTrue(extracted.contains(EnglishGrammar.capitalize(ChildToTranslate.class.getSimpleName())));
        assertTrue(extracted.contains(EnglishGrammar.capitalize("extractedAsIs")));
        assertTrue(extracted.contains(EnglishGrammar.capitalize("extractedAsIsWithDescription")));
        assertTrue(extracted.contains(ChildToTranslate.DESCRIPTION1));
        assertTrue(extracted.contains(ChildToTranslate.NAME2));

        assertFalse(extracted.contains(I18nAnnotation.DEFAULT_VALUE));
        assertFalse(extracted.contains(""));
        assertFalse(extracted.contains(EnglishGrammar.capitalize("extractedNoNameWithDescription")));

        assertTrue(extracted.contains("Constructor"));
        assertTrue(extracted.contains("Constructor with java format"));

        for (ConstantEntry entry : ce.getConstants()) {
            if (entry.text.equals("method with java format")) {
                assertTrue(entry.javaFormatFlag);
            } else if (entry.text.equals("Constructor with java format")) {
                assertTrue(entry.javaFormatFlag);
            }
        }
    }

    static void print(Collection<ConstantEntry> constants) {
        for (ConstantEntry entry : constants) {
            for (String line : entry.reference) {
                System.out.println("#: " + line);
            }
            System.out.println(entry.text);
        }
    }
}
