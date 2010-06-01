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
 * @version $Id$
 */
package com.pyx4j.entity.client.ui;

import java.util.Iterator;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CFormFolder;

public class CEntityFormFolder<E extends IEntity> extends CFormFolder<E> implements DelegatingEntityEditableComponent<List<E>> {

    private final Class<E> entityClass;

    public CEntityFormFolder(String title, Class<E> entityClass, EntityFormFactory<E> factory) {
        super(factory);
        this.setTitle(title);
        this.entityClass = entityClass;
    }

    // data type asserts.
    @Override
    public void populate(List<E> value) {
        assert (value == null || value instanceof IList);
        super.populate(value);
    }

    @Override
    public void populateModel(List<E> orig, List<E> value) {

        //hack to create Forms array, TODO remove this. think better way to create forms
        populate(value);

        if (value != null) {
            Iterator<CForm> formIt = getForms().iterator();
            Iterator<E> valueIt = value.iterator();
            while (formIt.hasNext()) {
                CForm f = formIt.next();
                E v = valueIt.next();
                if (f instanceof DelegatingEntityEditableComponent) {
                    ((DelegatingEntityEditableComponent) f).populateModel(null, v);
                }
            }
        }
    }

    @Override
    public void addItem(E value) {
        if (value == null) {
            value = EntityFactory.create(entityClass);
        }
        super.addItem(value);

        // Hack again
        ((DelegatingEntityEditableComponent) getForms().get(getForms().size() - 1)).populateModel(null, value);

    }
}
