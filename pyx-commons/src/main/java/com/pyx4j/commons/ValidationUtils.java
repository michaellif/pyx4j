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
 * Created on Apr 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

public class ValidationUtils {

    // see http://en.wikipedia.org/wiki/Postal_codes_in_Canada#Number_of_possible_postal_codes
    public static boolean isCanadianPostalCodeValid(String value) {
        return value.toUpperCase().matches("^[ABCEGHJKLMNPRSTVXY]{1}\\d{1}[A-Z]{1} *\\d{1}[A-Z]{1}\\d{1}$") && !value.toUpperCase().matches(".*[DFIOQU].*");
    }

    public static boolean isUSZipCodeValid(String value) {
        return value.matches("^\\d{5}(-\\d{4})?$");
    }

    public static boolean isCorrectUrl(String value) {
        return value.matches("^\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    }

    public static boolean urlHasProtocol(String url) {
        return url.matches("^\\b(https?|ftp|file)://{1}.*");
    }
}
