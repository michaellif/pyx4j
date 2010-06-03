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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        populateModel(null, value);

    }

    @Override
    public void populateModel(List<E> orig, List<E> value) {
        assert (value == null || value instanceof IList);
        LinkedHashMap<E, CForm> oldMap = new LinkedHashMap<E, CForm>(getFormsMap());
        getFormsMap().clear();
        if (value != null) {
            for (E item : value) {
                CForm form = null;
                if (oldMap.containsKey(item)) {
                    form = oldMap.get(item);
                } else {
                    form = createForm();
                }
                ((DelegatingEntityEditableComponent) form).populateModel(null, item);
                getFormsMap().put(item, form);
            }
        }
        super.populate(value);
    }

    @Override
    public void addItem() {
        E item = EntityFactory.create(entityClass);
        getValue().add(item);
        CForm form = createForm();
        ((DelegatingEntityEditableComponent) form).populateModel(null, item);
        getFormsMap().put(item, form);

        setNativeComponentValue(getValue());

    }

    @Override
    public void removeItem(CForm cForm) {
        Map<E, CForm> map = getFormsMap();
        for (E value : map.keySet()) {
            if (cForm.equals(map.get(value))) {
                getValue().remove(value);
                getFormsMap().remove(value);
                setNativeComponentValue(getValue());
                return;
            }
        }

    }

    @Override
    public void moveItem(CForm cForm, boolean up) {
        Map<E, CForm> map = getFormsMap();
        for (E value : map.keySet()) {
            if (cForm.equals(map.get(value))) {
                int indexBefore = getValue().indexOf(value);
                int indexAfter = indexBefore + (up ? -1 : +1);
                if (indexAfter < 0 || indexAfter > getValue().size()) {
                    return;
                }
                getValue().remove(indexBefore);
                getValue().add(indexAfter, value);
                Map<E, CForm> oldMap = new HashMap<E, CForm>(getFormsMap());
                getFormsMap().clear();
                for (E item : getValue()) {
                    getFormsMap().put(item, oldMap.get(item));
                }
                setNativeComponentValue(getValue());
                return;
            }
        }
    }

}
