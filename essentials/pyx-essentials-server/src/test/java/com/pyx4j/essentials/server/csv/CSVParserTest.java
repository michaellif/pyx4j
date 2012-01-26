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
 * Created on Jan 26, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.csv;

import java.util.Arrays;

import junit.framework.TestCase;

import com.pyx4j.commons.ConverterUtils;

public class CSVParserTest extends TestCase {

    private void assertLine(String line, String... expected) {
        CSVParser parser = new CSVParser();
        String[] results = parser.parse(line);
        String resultsStr = ConverterUtils.convertStringCollection(Arrays.asList(results), "|");
        assertEquals("[" + line + "] pars results.length {" + resultsStr + "}", expected.length, results.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals("[" + line + "] pars results[" + i + "] {" + resultsStr + "}", expected[i], results[i]);
        }
    }

    public void testEmbeddedCommas() {
        assertLine("E350,\"Super, luxurious truck", "E350", "Super, luxurious truck");
    }

    /**
     * We simulate behavior of MS Excel, see file "sample.csv"
     */
    public void testEmbeddedDoubleQuote() {
        assertLine("A,\"B\"C,D", "A", "BC", "D");
        assertLine("A,\"B\"C\",D", "A", "BC\"", "D");
        assertLine("A,\"B\"\"C\",D", "A", "B\"C", "D");
        assertLine("E350,\"Super, \"\"luxurious\"\" truck\"", "E350", "Super, \"luxurious\" truck");
    }
}
