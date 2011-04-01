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
 * Created on 2011-04-01
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import junit.framework.TestCase;

import com.pyx4j.entity.server.domain.GraphItem;
import com.pyx4j.entity.server.domain.bidir.Child;
import com.pyx4j.entity.server.domain.bidir.Master;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityGraph;

public class RpcEntityServiceFilterTest extends TestCase {

    public void testBidirectionalRelationshipFilter() {
        Master m = EntityFactory.create(Master.class);
        m.name().setValue("public1");
        m.rpcTransientName().setValue("secret1");
        Child c = EntityFactory.create(Child.class);
        c.name().setValue("public2");
        c.serverSideName().setValue("secret2");
        m.child().set(c);

        Master rpcMaster = (Master) new RpcEntityServiceFilter().filterOutgoing(null, m);

        assertEquals("Not filtered master", "public1", rpcMaster.name().getValue());
        assertEquals("Not filtered child", "public2", rpcMaster.child().name().getValue());
        assertNull("filtered master", rpcMaster.rpcTransientName().getValue());
        assertNull("filtered child", rpcMaster.child().serverSideName().getValue());
    }

    public void testDuplicateValuesHack() {

        GraphItem rootEntity = EntityFactory.create(GraphItem.class);
        rootEntity.rpcTransientName().setValue("secret");

        GraphItem child1 = EntityFactory.create(GraphItem.class);
        child1.setPrimaryKey(1L);
        child1.rpcTransientName().setValue("secret");

        GraphItem child2 = EntityFactory.create(GraphItem.class);
        child2.setPrimaryKey(1L);
        child2.rpcTransientName().setValue("secret");

        rootEntity.child1().set(child1);
        rootEntity.child2().set(child2);
        rootEntity.childrenSet().add(child1);
        rootEntity.childrenList().add(child1);
        rootEntity.childrenList().add(child2);

        GraphItem child3 = EntityFactory.create(GraphItem.class);
        child3.rpcTransientName().setValue("secret");
        child3.childrenSet().add(rootEntity);
        rootEntity.childrenList().add(child3);

        GraphItem rpcFiltered = (GraphItem) new RpcEntityServiceFilter().filterOutgoing(null, rootEntity);
        EntityGraph.applyRecursivelyAllObjects(rpcFiltered, new EntityGraph.ApplyMethod() {
            @Override
            public void apply(IEntity entity) {
                if (entity instanceof GraphItem) {
                    assertNull("filtered", ((GraphItem) entity).rpcTransientName().getValue());
                }
            }
        });
    }
}
