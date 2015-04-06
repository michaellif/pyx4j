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
 * Created on Jan 2, 2015
 * @author vlads
 */
package com.pyx4j.i18n.gettext;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.junit.Assert;

public class POFileWriterWrapTest extends TestCase {

    private static final String cr = System.getProperty("line.separator").toString();

    private void assertWrap(String expected, String incomming) {
        POFileWriter poWriter = new POFileWriter();
        poWriter.pageWidth = 79;
        poWriter.wrapLines = true;
        StringWriter b = new StringWriter();
        poWriter.writeString(new PrintWriter(b), incomming, "msgid ".length());
        Assert.assertEquals(expected.replace(cr, "\n"), b.toString().replace(cr, "\n"));
    }

    public void testMultiLineOutput() {
        assertWrap("\"\"\n" //
                + "\"Here is an example of how one might continue a very long string\\n\"\n" //
                + "\"for the common case the string represents multi-line output.\\n\"\n", //
                "Here is an example of how one might continue a very long string\nfor the common case the string represents multi-line output.\n");
    }

    public void testSingleLineWithCRAtTheEnd() {
        assertWrap(//
                "\"Here is an example of how one might continue a very long string\\n\"\n", //
                "Here is an example of how one might continue a very long string\n");
    }
}
