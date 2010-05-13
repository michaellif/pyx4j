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
 * Created on 2010-05-13
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.test.commons;

import java.text.MessageFormat;

import junit.framework.TestCase;

//import com.pyx4j.gwt.emul.java.text.MessageFormat;

public class MessageFormatTest extends TestCase {

    private void assertMessageFormat(String expected, String pattern, Object... arguments) {
        String result = MessageFormat.format(pattern, arguments);
        assertEquals(expected, result);

    }

    public void testReplacements() {
        assertMessageFormat("A, B, C", "{0}, {1}, {2}", "A", "B", "C");
        assertMessageFormat("A, B, A", "{0}, {1}, {0}", "A", "B", "C");
        assertMessageFormat("A, null, C", "{0}, {1}, {2}", "A", null, "C");

        assertMessageFormat("{0}", "'{0}'", "A");
    }

}
