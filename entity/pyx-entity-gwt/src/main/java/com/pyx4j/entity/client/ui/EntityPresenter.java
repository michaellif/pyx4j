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
 * Created on Feb 8, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.entity.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.forms.client.ui.CEditableComponent;

public class EntityPresenter<E extends IEntity> {

    private static final Logger log = LoggerFactory.getLogger(CEntityForm.class);

    private final E metaEntity;

    private E origEntity;

    private E editableEntity;

    private final HashMap<CEditableComponent<?, ?>, Path> binding = new HashMap<CEditableComponent<?, ?>, Path>();

    public EntityPresenter(Class<E> clazz) {
        metaEntity = EntityFactory.create(clazz);
    }

    public E meta() {
        return metaEntity;
    }

    @SuppressWarnings("unchecked")
    public <T extends IEntity> CEditableComponent<T, ?> get(T member) {
        return (CEditableComponent<T, ?>) get((IObject<?>) member);
    }

    @SuppressWarnings("unchecked")
    public <T> CEditableComponent<T, ?> get(IObject<T> member) {
        return getRaw(member);
    }

    @SuppressWarnings("rawtypes")
    public <T> CEditableComponent getRaw(IObject<T> member) {
        Path memberPath = member.getPath();
        for (Map.Entry<CEditableComponent<?, ?>, Path> me : binding.entrySet()) {
            if (me.getValue().equals(memberPath)) {
                return me.getKey();
            }
        }
        throw new IndexOutOfBoundsException("Memeber " + member.getFieldName() + " is not bound");
    }

    public boolean contains(IObject<?> member) {
        Path memberPath = member.getPath();
        for (Map.Entry<CEditableComponent<?, ?>, Path> me : binding.entrySet()) {
            if (me.getValue().equals(memberPath)) {
                return true;
            }
        }
        return false;
    }

    public void bind(CEditableComponent<?, ?> component, Path path) {
        binding.put(component, path);
    }

    @SuppressWarnings("unchecked")
    public void populate(E entity) {
        this.origEntity = entity;
        if (entity != null) {
            log.debug("populate {}", entity);
            this.editableEntity = (E) entity.cloneEntity();
        } else {
            this.editableEntity = EntityFactory.create((Class<E>) meta().getObjectClass());
        }
        populateComponents();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void populateComponents() {
        for (CEditableComponent component : binding.keySet()) {
            Path memberPath = binding.get(component);
            IObject<?> m = editableEntity.getMember(memberPath);
            if (component instanceof DelegatingEntityEditableComponent) {
                ((DelegatingEntityEditableComponent) component).populateModel(null, m);
            } else if ((m instanceof IEntity) || (m instanceof ICollection)) {
                component.populate(m);
            } else {
                try {
                    component.populate(m.getValue());
                } catch (ClassCastException e) {
                    // TODO Auto-generated catch block
                    log.error("Error", e);
                    throw new ClassCastException("property " + memberPath + " ValueClass:" + m.getMeta().getValueClass() + " Error:" + e.getMessage());
                }
            }
        }
    }

    public E getValue() {
        return editableEntity;
    }

    public E getOrigValue() {
        return origEntity;
    }

    public String getTitle() {
        return editableEntity.getStringView();
    }
}
