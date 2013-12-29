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
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;

public class DetachedValuesTest extends InitializerTestBase {

    private void assertException(String message, Runnable run) {
        boolean accessed = false;
        try {
            run.run();
            accessed = true;
        } catch (RuntimeException e) {
        } catch (AssertionError e) {
        }

        if (accessed) {
            fail("Managed to access " + message);
        }
    }

    public void assertDepartmentFieldsAccess(final Employee emp) {

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

        emp.department().name().equals("somthingWrong");

        if (isJavaAssertEnabled()) {
            assertException("name().equals", new Runnable() {
                @Override
                public void run() {
                    Department dep = EntityFactory.create(Department.class);
                    dep.name().setValue("somthingResonable");
                    emp.department().name().equals(dep.name());
                }
            });
        }
    }

    public void testMethodsAccessDirect() {
        final Employee emp = EntityFactory.create(Employee.class);
        emp.department().setPrimaryKey(new Key(1));
        emp.department().setValueDetached();

        assertFalse("Can access isNull", emp.department().isNull());
        assertTrue("Can access isEmpty and is properly set", emp.department().isEmpty());

        assertDepartmentFieldsAccess(emp);
    }

    public void testMethodsAccessAttachedToStringMembers() {
        final Employee emp = EntityFactory.create(Employee.class);
        emp.department().setPrimaryKey(new Key(1));
        emp.department().name().setValue("somthing");
        emp.department().setAttachLevel(AttachLevel.ToStringMembers);

        assertEquals("AttachLevel", AttachLevel.ToStringMembers, emp.department().getAttachLevel());
        assertEquals("Value of isNull", false, emp.department().isNull());
        assertEquals("Value of isEmpty", false, emp.department().isEmpty());

        assertDepartmentFieldsAccess(emp);
    }

    public void testMethodsAccessIndirect() {
        final Employee emp = EntityFactory.create(Employee.class);
        emp.setValueDetached();

        assertEquals("AttachLevel", AttachLevel.Detached, emp.department().getAttachLevel());
        assertTrue("isValuesDetached", emp.department().isValueDetached());
        assertTrue("Can access isNull", emp.department().isNull());
        assertTrue("Can access isEmpty and is properly set", emp.department().isEmpty());

        assertDepartmentFieldsAccess(emp);

        if (isJavaAssertEnabled()) {
            assertException("department().equals", new Runnable() {
                @Override
                public void run() {
                    Department dep = EntityFactory.create(Department.class);
                    dep.id().setValue(new Key(2));
                    emp.department().equals(dep);
                }
            });
        }
    }

    public void testEntityMethods() {
        final Employee emp = EntityFactory.create(Employee.class);
        emp.department().setPrimaryKey(new Key(1));
        emp.department().setValueDetached();

        assertTrue("isValuesDetached", emp.department().isValueDetached());

        Department dptc = emp.department().duplicate();
        assertTrue("cloneEntity isValuesDetached", dptc.isValueDetached());

        Employee empc = emp.duplicate();
        assertTrue("parent.cloneEntity isValuesDetached", empc.department().isValueDetached());

        dptc = emp.department().duplicate(Department.class);
        assertTrue("cloneEntity isValuesDetached", dptc.isValueDetached());

        empc = emp.duplicate(Employee.class);
        assertTrue("parent.cloneEntity isValuesDetached", empc.department().isValueDetached());

    }

    public void testAssigments() {
        final Employee emp = EntityFactory.create(Employee.class);
        emp.setValueDetached();

        final Employee emp2 = EntityFactory.create(Employee.class);

        assertException("entity().set(detachedEntity{DetachedByParent})", new Runnable() {
            @Override
            public void run() {
                emp2.department().set(emp.department());
            }
        });
    }

}
