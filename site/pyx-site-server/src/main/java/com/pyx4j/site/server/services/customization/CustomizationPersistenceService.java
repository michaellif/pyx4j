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
 * Created on Sep 5, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.server.services.customization;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.rpc.customization.ICustomizationPersistenceService;
import com.pyx4j.site.shared.domain.cusomization.CustomizationHolder;

public abstract class CustomizationPersistenceService<E extends IEntity> implements ICustomizationPersistenceService<E> {

    private final Class<? extends CustomizationHolder> customizationHolderTableClass;

    public <H extends CustomizationHolder> CustomizationPersistenceService(Class<H> customizationHolderTableClass) {
        this.customizationHolderTableClass = customizationHolderTableClass;
    }

    @Override
    public void list(AsyncCallback<Vector<String>> callback, E proto) {
        Vector<String> result = new Vector<String>();
        Iterator<String> i = new CustomizationPersistenceHelper<E>(customizationHolderTableClass).list(proto).iterator();
        while (i.hasNext()) {
            result.add(i.next());
        }
        callback.onSuccess(result);
    }

    @Override
    public void save(AsyncCallback<VoidSerializable> callback, String id, E entity, boolean allowOverwrite) {
        new CustomizationPersistenceHelper<E>(customizationHolderTableClass).save(id, entity, allowOverwrite);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void load(AsyncCallback<E> callback, String id, E proto) {
        callback.onSuccess(new CustomizationPersistenceHelper<E>(customizationHolderTableClass).load(id, proto));
    }

    @Override
    public void delete(com.google.gwt.user.client.rpc.AsyncCallback<VoidSerializable> callback, String id, E proto) {
        new CustomizationPersistenceHelper<E>(customizationHolderTableClass).delete(id, proto);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

}
