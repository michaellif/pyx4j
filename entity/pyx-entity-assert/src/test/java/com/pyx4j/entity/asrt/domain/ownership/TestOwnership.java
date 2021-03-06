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
 * Created on Feb 8, 2012
 * @author vlads
 */
package com.pyx4j.entity.asrt.domain.ownership;

import org.junit.Ignore;

import com.pyx4j.entity.core.EntityFactory;

import junit.framework.TestCase;

@Ignore
public class TestOwnership extends TestCase {

    public void testMissingOwnedAnnotation() {
        boolean created = false;
        try {
            EntityFactory.create(BO1Child.class);
            created = true;
        } catch (AssertionError ok) {
            System.out.println(ok);
        }
        if (created) {
            fail("Should not create object with missing @Owned");
        }
    }

    public void testMissingOwnedMember() {
        boolean created = false;
        try {
            EntityFactory.create(BO2Child.class);
            created = true;
        } catch (AssertionError ok) {
            System.out.println(ok);
        }
        if (created) {
            fail("Should not create object with missing value in Owner");
        }
    }

    public void testMissingOwnerAnnotation() {
        boolean created = false;
        try {
            EntityFactory.create(BO3Child.class);
            created = true;
        } catch (Throwable ok) {
        }
        if (created) {
            fail("Should not create object with missing @Owner");
        }
    }

}
