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
 * Created on 2011-02-09
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

/**
 * This component represents root IEntity of the shown object tree
 */
public abstract class CEntityForm<E extends IEntity> extends CEntityEditableComponent<E> {

    protected final IEditableComponentFactory factory;

    public CEntityForm(Class<E> rootClass) {
        this(rootClass, new EntityFormComponentFactory());
    }

    public CEntityForm(Class<E> rootClass, IEditableComponentFactory factory) {
        super(new EntityFormBinder<E>(rootClass));
        this.factory = factory;
    }

    public void initialize() {
        initContent();
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        return factory.create(member);
    }

    @Override
    public void populate(E value) {
        if (value == null) {

            @SuppressWarnings("unchecked")
            E newEntity = (E) EntityFactory.create(proto().getValueClass());

            createNewEntity(newEntity, new DefaultAsyncCallback<E>() {

                @Override
                public void onSuccess(E result) {
                    CEntityForm.super.populate(result);
                }

            });
        } else {
            super.populate(value);
        }
    }

    /**
     * Implementation to override new Entity creation. No need to call
     * super.createNewEntity().
     * 
     * @param newEntity
     * @param callback
     */
    protected void createNewEntity(E newEntity, AsyncCallback<E> callback) {
        callback.onSuccess(newEntity);
    }

}
