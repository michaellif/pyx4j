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
 * Created on Sep 9, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;

public class DetachedValuesTest extends InitializerTestCase {

    private void assertException(String message, Runnable run) {
        boolean accessed = false;
        try {
            run.run();
            accessed = true;
        } catch (RuntimeException e) {
        }

        if (accessed) {
            fail("Managed to access " + message);
        }
    }

    public void testMethodsAccess() {
        final Employee emp = EntityFactory.create(Employee.class);
        emp.department().setPrimaryKey(new Key(1));
        emp.department().setValuesDetached();

        assertFalse("Can access isNull", emp.department().isNull());
        assertTrue("Can access isEmpty and is properly set", emp.department().isEmpty());
        assertNotNull("Can access toString", emp.department().toString());
        assertFalse("Can access equals", emp.department().equals(null));
        assertNotNull("Can access member name()", emp.department().name().getFieldName());
        assertNotNull("Can access member employees()", emp.department().employees().getFieldName());

        assertTrue("Can access isInstanceOf", emp.department().isInstanceOf(Department.class));
        assertTrue("Can access isAssignableFrom", emp.department().isAssignableFrom(Department.class));

        assertException("name().getValue", new Runnable() {
            @Override
            public void run() {
                emp.department().name().getValue();
            }
        });

        assertException("id().getValue", new Runnable() {
            @Override
            public void run() {
                emp.department().id().getValue();
            }
        });

        assertException("name().setValue", new Runnable() {
            @Override
            public void run() {
                emp.department().name().setValue("10");
            }
        });

        assertException("name().setValue(null)", new Runnable() {
            @Override
            public void run() {
                emp.department().name().setValue(null);
            }
        });

        assertException("name().getStringView", new Runnable() {
            @Override
            public void run() {
                emp.department().name().getStringView();
            }
        });
    }
}
