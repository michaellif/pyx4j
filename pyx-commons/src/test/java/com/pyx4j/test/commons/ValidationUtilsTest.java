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
 * Created on Aug 27, 2013
 * @author stanp
 * @version $Id$
 */
package com.pyx4j.test.commons;

import junit.framework.TestCase;

import com.pyx4j.commons.ValidationUtils;

public class ValidationUtilsTest extends TestCase {

    // see http://blogs.msdn.com/b/testing123/archive/2009/02/05/email-address-test-cases.aspx
    public void testEmail() {
        assertTrue("Valid Email: Valid email", ValidationUtils.isValidEmail("email@domain.com"));
        assertTrue("Valid Email: Valid email - mixed case in all parts", ValidationUtils.isValidEmail("EMAiL@doMAIN.CoM"));
        assertTrue("Valid Email: Email contains dot in the address field", ValidationUtils.isValidEmail("firstname.lastname@domain.com"));
        assertTrue("Valid Email: Email contains dot with subdomain", ValidationUtils.isValidEmail("email@subdomain.domain.com"));
        assertTrue("Valid Email: Plus sign is considered valid character", ValidationUtils.isValidEmail("firstname+lastname@domain.com"));
        assertTrue("Valid Email: Domain is valid IP address", ValidationUtils.isValidEmail("email@123.123.123.123"));
//        assertTrue("Valid Email: Square bracket around IP address is considered valid", ValidationUtils.isValidEmail("email@[123.123.123.123]"));
//        assertTrue("Valid Email: Quotes around email is considered valid", ValidationUtils.isValidEmail("\"email\"@domain.com"));
        assertTrue("Valid Email: Digits in address are valid", ValidationUtils.isValidEmail("1234567890@domain.com"));
        assertTrue("Valid Email: Dash in domain name is valid", ValidationUtils.isValidEmail("email@domain-one.com"));
        assertTrue("Valid Email: Underscore in the address field is valid", ValidationUtils.isValidEmail("_______@domain.com"));
        assertTrue("Valid Email: .name is valid Top Level Domain name", ValidationUtils.isValidEmail("email@domain.name"));
        assertTrue("Valid Email: Dot in Top Level Domain name is valid", ValidationUtils.isValidEmail("email@domain.co.jp"));
        assertTrue("Valid Email: Dash in address field is valid", ValidationUtils.isValidEmail("firstname-lastname@domain.com"));

        assertFalse("Invalid Email: Missing @ sign and domain", ValidationUtils.isValidEmail("plainaddress"));
        assertFalse("Invalid Email: Garbage", ValidationUtils.isValidEmail("#@%^%#$@#$@#.com"));
        assertFalse("Invalid Email: Missing username", ValidationUtils.isValidEmail("@domain.com"));
        assertFalse("Invalid Email: Encoded html within email is invalid", ValidationUtils.isValidEmail("Joe Smith <email@domain.com>"));
        assertFalse("Invalid Email: Missing @", ValidationUtils.isValidEmail("email.domain.com"));
        assertFalse("Invalid Email: Two @ sign", ValidationUtils.isValidEmail("email@domain@domain.com"));
        assertFalse("Invalid Email: Leading dot in address is not allowed", ValidationUtils.isValidEmail(".email@domain.com"));
        assertFalse("Invalid Email: Trailing dot in address is not allowed", ValidationUtils.isValidEmail("email.@domain.com"));
        assertFalse("Invalid Email: Multiple dots", ValidationUtils.isValidEmail("email..email@domain.com"));
        assertFalse("Invalid Email: Text followed email is not allowed", ValidationUtils.isValidEmail("email@domain.com (Joe Smith)"));
        assertFalse("Invalid Email: Missing top level domain (.com/.net/.org/etc)", ValidationUtils.isValidEmail("email@domain"));
        assertFalse("Invalid Email: Leading dash in front of domain is invalid", ValidationUtils.isValidEmail("email@-domain.com"));
//        assertFalse("Invalid Email: .web is not a valid top level domain", ValidationUtils.isValidEmail("email@domain.web"));
//        assertFalse("Invalid Email: Invalid IP format", ValidationUtils.isValidEmail("email@111.222.333.44444"));
        assertFalse("Invalid Email: Multiple dot in the domain portion is invalid", ValidationUtils.isValidEmail("email@domain..com"));
    }
}
