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
 * Created on Apr 21, 2015
 * @author vlads
 */
package com.pyx4j.entity.test.server;

import org.apache.commons.beanutils.PropertyUtils;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.test.shared.InitializerTestBase;
import com.pyx4j.entity.test.shared.domain.inherit.override.O1Concrete1;
import com.pyx4j.entity.test.shared.domain.inherit.override.O2Concrete1;

public class PojoEntityMemberOverrideTest extends InitializerTestBase {

    public void testOveloadedMemeberAccess() throws ReflectiveOperationException {
        // See if we can create it
        ServerEntityFactory.getPojoClass(O1Concrete1.class);

        {
            O1Concrete1 o1c1 = EntityFactory.create(O1Concrete1.class);
            O2Concrete1 o2c1 = EntityFactory.create(O2Concrete1.class);
            o1c1.o2Base().set(o2c1);

            IPojo<O1Concrete1> o1c1Pojo = ServerEntityFactory.getPojo(o1c1);

            @SuppressWarnings("unchecked")
            IPojo<O2Concrete1> o2c1Pojo = (IPojo<O2Concrete1>) PropertyUtils.getProperty(o1c1Pojo, "o2Base");

            assertTrue("same entity", o2c1.equals(o2c1Pojo.getEntityValue()));
        }

    }
}
