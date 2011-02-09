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

import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CTextComponent;

public class EntityPresenter<E extends IEntity> {

    private static final Logger log = LoggerFactory.getLogger(CEntityForm.class);

    private final E entityPrototype;

    private final EditableComponentFactory factory;

    private E origEntity;

    private E editableEntity;

    private final HashMap<CEditableComponent<?, ?>, Path> binding = new HashMap<CEditableComponent<?, ?>, Path>();

    public static <T extends IEntity> EntityPresenter<T> create(EditableComponentFactory factory, Class<T> clazz) {
        return new EntityPresenter<T>(factory, clazz);
    }

    public EntityPresenter(EditableComponentFactory factory, Class<E> clazz) {
        this.factory = factory;
        entityPrototype = EntityFactory.getEntityPrototype(clazz);
    }

    public E proto() {
        return entityPrototype;
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

    public <T> CEditableComponent<T, ?> create(IObject<T> member) {
        @SuppressWarnings("unchecked")
        CEditableComponent<T, ?> component = (CEditableComponent<T, ?>) factory.create(member);
        bind(component, member);
        return component;
    }

    public void bind(CEditableComponent<?, ?> component, IObject<?> member) {
        applyAttributes(component, member);
        binding.put(component, member.getPath());
    }

    protected void applyAttributes(CEditableComponent<?, ?> component, IObject<?> member) {
        MemberMeta mm = member.getMeta();
        if (mm.isValidatorAnnotationPresent(NotNull.class)) {
            component.setMandatory(true);
        }
        if (String.class == mm.getValueClass()) {
            ((CTextComponent<?, ?>) component).setMaxLength(mm.getStringLength());
            if (mm.getDescription() != null) {
                ((CTextComponent<?, ?>) component).setWatermark(mm.getWatermark());
            }
        }
        if (mm.getDescription() != null) {
            component.setToolTip(mm.getDescription());
        }
        component.setTitle(mm.getCaption());
        component.setDebugId(member.getPath());
    }

    @SuppressWarnings("unchecked")
    public void populate(E entity) {
        this.origEntity = entity;
        if (entity != null) {
            log.debug("populate {}", entity);
            this.editableEntity = (E) entity.cloneEntity();
        } else {
            this.editableEntity = EntityFactory.create((Class<E>) proto().getObjectClass());
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
