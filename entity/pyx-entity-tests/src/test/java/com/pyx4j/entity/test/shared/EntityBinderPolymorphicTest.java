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
 * Created on May 13, 2015
 * @author vlads
 */
package com.pyx4j.entity.test.shared;

import org.junit.Assert;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.utils.BindingContext;
import com.pyx4j.entity.shared.utils.BindingContext.BindingType;
import com.pyx4j.entity.shared.utils.SimpleEntityBinder;
import com.pyx4j.entity.test.shared.domain.inherit.binder.B1sub1;
import com.pyx4j.entity.test.shared.domain.inherit.binder.B1sub1TO;
import com.pyx4j.entity.test.shared.domain.inherit.binder.B1superHolder;
import com.pyx4j.entity.test.shared.domain.inherit.binder.B1superHolderTO;
import com.pyx4j.unit.shared.UniqueInteger;

public class EntityBinderPolymorphicTest extends InitializerTestBase {

    public static class PolymorphicMemberBinder extends SimpleEntityBinder<B1superHolder, B1superHolderTO> {

        protected PolymorphicMemberBinder() {
            super(B1superHolder.class, B1superHolderTO.class);
        }

        @Override
        protected void bind() {
            bind(toProto.nameB1Holder(), boProto.nameB1Holder());
            bind(toProto.item(), boProto.item(), new EntityDtoBinderTest.PluralPolymorphicBinder());
        }

    }

    public void testPolymorphicMemberBinder() {
        B1sub1 boItem = EntityFactory.create(B1sub1.class);
        boItem.nameB1sub1().setValue(UniqueInteger.getInstance("B1sub1").nextIdAsString());

        B1superHolder bo = EntityFactory.create(B1superHolder.class);
        bo.nameB1Holder().setValue(UniqueInteger.getInstance("B1superHolder").nextIdAsString());
        boItem.holder().set(bo);
        bo.item().set(boItem);

        B1superHolderTO to = new PolymorphicMemberBinder().createTO(bo, new BindingContext(BindingType.List));

        Assert.assertEquals("Item Proper instance", B1sub1TO.class, to.item().getInstanceValueClass());
        Assert.assertEquals("Holder Proper instance", B1superHolderTO.class, to.item().holder().getInstanceValueClass());
        Assert.assertEquals("Holder", to, to.item().holder());

        Assert.assertEquals("nameB1sub1", boItem.nameB1sub1().getValue(), to.item().<B1sub1TO> cast().nameB1sub1().getValue());

        Assert.assertEquals("nameB1Holder", bo.nameB1Holder().getValue(), to.item().holder().nameB1Holder().getValue());

    }
}
