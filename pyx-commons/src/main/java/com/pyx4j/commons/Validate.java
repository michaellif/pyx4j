/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Oct 23, 2013
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

import java.util.Collection;

public class Validate {

    public static void isTrue(Boolean expression, String messageFormat, Object... arguments) {
        if (!expression) {
            throw new Error(SimpleMessageFormat.format("expected [true]\n actual: [{0}]\n", expression) + SimpleMessageFormat.format(messageFormat, arguments));
        }
    }

    public static void notNull(Object object, String messageFormat, Object... arguments) {
        if (object == null) {
            throw new Error(SimpleMessageFormat.format("expected [null]\n actual: [{0}]\n", object) + SimpleMessageFormat.format(messageFormat, arguments));
        }
    }

    static public void isEquals(Object expected, Object actual, String messageFormat, Object... arguments) {
        if (!EqualsHelper.equals(expected, actual)) {
            throw new Error(SimpleMessageFormat.format("expected [{0}]\n actual: [{1}]\n", expected, actual)
                    + SimpleMessageFormat.format(messageFormat, arguments));
        }
    }

    static public <T> void contains(Collection<T> expected, T actual, String messageFormat, Object... arguments) {
        if (!expected.contains(actual)) {
            throw new Error(SimpleMessageFormat.format("expected [{0}]\n actual: [{1}]\n", expected, actual)
                    + SimpleMessageFormat.format(messageFormat, arguments));
        }
    }
}
