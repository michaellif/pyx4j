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
 * Created on Feb 2, 2010
 * @author vlads
 */
package com.pyx4j.entity.test.shared.criterion;

import java.io.Serializable;

import junit.framework.TestCase;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.test.shared.domain.City;
import com.pyx4j.entity.test.shared.domain.Province;

public class DOEqualsTest extends TestCase {

    public void testPropertyCriterion() {
        City c = EntityFactory.create(City.class);
        PropertyCriterion d1 = new PropertyCriterion(c.name(), Restriction.EQUAL, "1");
        PropertyCriterion d2 = new PropertyCriterion(c.name(), Restriction.EQUAL, "1");
        assertTrue("DO are the same", d1.equals(d2));
        assertEquals("DO are the same", d1.hashCode(), d2.hashCode());

        d2 = new PropertyCriterion(c.name(), Restriction.EQUAL, "2");
        assertFalse("DO are different", d1.equals(d2));
        assertTrue("DO are different", d1.hashCode() != d2.hashCode());

        d2 = new PropertyCriterion(c.instanceValueClass(), Restriction.EQUAL, "1");
        assertFalse("DO are different", d1.equals(d2));
        assertTrue("DO are different", d1.hashCode() != d2.hashCode());

        d2 = new PropertyCriterion(c.name(), Restriction.GREATER_THAN, "1");
        assertFalse("DO are different", d1.equals(d2));
        assertTrue("DO are different", d1.hashCode() != d2.hashCode());

        d2 = new PropertyCriterion((Path) null, Restriction.EQUAL, (Serializable) null);

        assertFalse("DO are different", d1.equals(d2));
        assertFalse("DO are different", d2.equals(d1));
        assertTrue("DO are different", d1.hashCode() != d2.hashCode());
    }

    public void testEntityCriteria() {
        EntityQueryCriteria<City> d1 = EntityQueryCriteria.create(City.class);
        {
            EntityQueryCriteria<City> d2 = EntityQueryCriteria.create(City.class);
            assertTrue("DO are the same", d1.equals(d2));
            assertEquals("DO are the same", d1.hashCode(), d2.hashCode());

            d1.add(PropertyCriterion.eq(d1.proto().name(), "1"));
            d2.add(PropertyCriterion.eq(d2.proto().name(), "1"));
            assertTrue("DO are the same", d1.equals(d2));
            assertEquals("DO are the same", d1.hashCode(), d2.hashCode());

            d2.add(PropertyCriterion.eq(d2.proto().name(), "2"));
            assertFalse("DO are different", d1.equals(d2));
            assertTrue("DO are different", d1.hashCode() != d2.hashCode());
        }

        {
            EntityQueryCriteria<Province> d2 = EntityQueryCriteria.create(Province.class);
            assertFalse("DO are different", d1.equals(d2));
        }
    }
}
