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
 * Created on May 25, 2010
 * @author michaellif
 * @version $Id: CEntityFormFolder.java 6523 2010-07-15 15:46:44Z michaellif $
 */
package com.pyx4j.entity.client.ui;

import com.google.gwt.resources.client.ImageResource;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CFormGroup;

public class CEntityFormGroup<E extends IEntity> extends CFormGroup<E> implements DelegatingEntityEditableComponent<E> {

    private final Class<E> entityClass;

    private boolean expended;

    public CEntityFormGroup(String title, Class<E> entityClass, EntityFormFactory<E> factory, ImageResource image) {
        this(title, entityClass, factory, true, image);
    }

    public CEntityFormGroup(String title, Class<E> entityClass, EntityFormFactory<E> factory) {
        this(title, entityClass, factory, true, null);
    }

    public CEntityFormGroup(String title, Class<E> entityClass, EntityFormFactory<E> factory, boolean expended) {
        this(title, entityClass, factory, expended, null);
    }

    public CEntityFormGroup(String title, Class<E> entityClass, EntityFormFactory<E> factory, boolean expended, ImageResource image) {
        super(factory);
        this.setTitleImage(image);
        this.setTitle(title);
        this.entityClass = entityClass;
        this.expended = expended;
    }

    // data type asserts.
    @Override
    public void populate(E value) {
        populateModel(null, value);

    }

    @Override
    public void populateModel(E orig, E value) {
        ((DelegatingEntityEditableComponent) getForm()).populateModel(null, value);
        getForm().setExpended(expended);
    }

}
