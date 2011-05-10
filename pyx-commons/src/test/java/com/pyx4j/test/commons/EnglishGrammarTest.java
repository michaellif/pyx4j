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
 * Created on 2011-05-10
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.test.commons;

import junit.framework.TestCase;

import com.pyx4j.commons.EnglishGrammar;

public class EnglishGrammarTest extends TestCase {

    public void testCapitalize() {
        assertEquals("Abc", EnglishGrammar.capitalize("Abc"));
        assertEquals("Abc", EnglishGrammar.capitalize("abc"));
        assertEquals("ABC", EnglishGrammar.capitalize("ABC"));
        assertEquals("Abc Def", EnglishGrammar.capitalize("AbcDef"));
        assertEquals("Abc Def", EnglishGrammar.capitalize("Abc_def"));
        assertEquals("Abc DEF", EnglishGrammar.capitalize("AbcDEF"));
    }
}
