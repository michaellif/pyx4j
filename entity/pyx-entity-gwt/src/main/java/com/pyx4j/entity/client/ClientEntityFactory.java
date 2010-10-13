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
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.impl.IEntityFactoryImpl;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class ClientEntityFactory implements IEntityFactoryImpl {

    private static IEntityFactoryImpl singleFactory = null;

    /**
     * Call this function in client code before compiler reached RPC RemoteService to
     * ensure IEntityImplementations are serialized by RPC.
     */
    public static void ensureIEntityImplementations() {
        if (!GWT.isScript()) {
            // Hosted mode initialization needs to call GWT.create(..); 
            try {
                EntityFactory.create(null);
            } catch (NullPointerException ignore) {
            }
        } else if (singleFactory == null) {
            synchronized (ClientEntityFactory.class) {
                singleFactory = GWT.create(IEntityFactoryImpl.class);
            }
        }
    }

    @Override
    public <T extends IEntity> T create(Class<T> clazz, IObject<?> parent, String fieldName) {
        if (singleFactory == null) {
            synchronized (ClientEntityFactory.class) {
                singleFactory = GWT.create(IEntityFactoryImpl.class);
            }
        }
        return singleFactory.create(clazz, parent, fieldName);
    }

    @Override
    public EntityMeta createEntityMeta(Class<? extends IEntity> clazz) {
        if (singleFactory == null) {
            synchronized (ClientEntityFactory.class) {
                singleFactory = GWT.create(IEntityFactoryImpl.class);
            }
        }
        return singleFactory.createEntityMeta(clazz);
    }
}
