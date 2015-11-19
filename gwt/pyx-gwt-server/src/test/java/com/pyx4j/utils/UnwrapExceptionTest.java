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
 * Created on Nov 10, 2015
 * @author vlads
 */
package com.pyx4j.utils;

import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.utils.ErrorMessagesBuilder.ErrorMessageOptions;

public class UnwrapExceptionTest {

    @Test
    public void testMessages() {
        {
            ErrorMessageOptions options = new ErrorMessageOptions();
            options.skipPackage = false;

            Assert.assertEquals("Error formating", "java.lang.Error: Hi1\njava.lang.NullPointerException",
                    ErrorMessagesBuilder.getAllErrorMessages(new Error("Hi1", new NullPointerException()), options));

            Assert.assertEquals("Error formating", "java.lang.RuntimeException\n...",
                    ErrorMessagesBuilder.getAllErrorMessages(new RuntimeException(new RuntimeException()), options));
        }

        {
            ErrorMessageOptions options = new ErrorMessageOptions();
            options.skipPackage = true;

            Assert.assertEquals("Error formating", "Error: Hi1\nNullPointerException",
                    ErrorMessagesBuilder.getAllErrorMessages(new Error("Hi1", new NullPointerException()), options));

            Assert.assertEquals("Error formating", "RuntimeException\n...",
                    ErrorMessagesBuilder.getAllErrorMessages(new RuntimeException(new RuntimeException()), options));
        }
    }

    private void makeException() {
        throw new RuntimeException("mssage1");
    }

    @Test
    public void testStackTrace() {
        Error e = null;
        try {
            makeException();
        } catch (Throwable t) {
            e = new Error("Hi1", t);
        }

        String trace = ExceptionUtils.forceSafeStackTrace(e);
        Assert.assertTrue("has message", trace.contains("mssage1"));
        Assert.assertTrue("has function", trace.contains("makeException"));
    }
}
