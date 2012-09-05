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

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.rpc.customization.ISettingsPersistenceService;

public abstract class SettingsPersistenceService<E extends IEntity> implements ISettingsPersistenceService<E> {

    @Override
    public void list(AsyncCallback<Vector<String>> callback, E proto) {
        Vector<String> result = new Vector<String>();
        Iterator<String> i = new CustomizationPersistenceHelper<E>().list(proto).iterator();
        while (i.hasNext()) {
            result.add(i.next());
        }
        callback.onSuccess(result);
    }

    @Override
    public void save(AsyncCallback<VoidSerializable> callback, String id, E entity) {

        new CustomizationPersistenceHelper<E>().save(id, entity);
        callback.onSuccess(null);
    }

    @Override
    public void load(AsyncCallback<IEntity> callback, String id, E proto) {
        callback.onSuccess(new CustomizationPersistenceHelper<E>().load(id, proto));
    }

}
