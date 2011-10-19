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
package com.pyx4j.entity.client.ui.flex.editor;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.CEntityComponent;
import com.pyx4j.entity.client.ui.flex.CEntityContainer;
import com.pyx4j.entity.client.ui.flex.EntityBinder;
import com.pyx4j.entity.client.ui.flex.EntityFormComponentFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;

//TODO to be renamed to CEntityForm
public abstract class CEntityEditor<E extends IEntity> extends CEntityContainer<E> {

    private static final Logger log = LoggerFactory.getLogger(CEntityEditor.class);

    protected IEditableComponentFactory factory;

    private final EntityBinder<E> binder;

    public CEntityEditor(Class<E> clazz) {
        this(clazz, new EntityFormComponentFactory());
    }

    public CEntityEditor(Class<E> clazz, IEditableComponentFactory factory) {
        binder = new EntityBinder<E>(clazz, this);
        this.factory = factory;
    }

    public EntityBinder<E> binder() {
        return binder;
    }

    public E proto() {
        return binder.proto();
    }

    @Override
    public void populate(E value) {
        binder.populate(value);
        super.populate(binder.getValue());
    }

    @Override
    public void setValue(E value) {
        binder.setValue(value);
        super.setValue(binder.getValue());
    }

    @Override
    public E getValue() {
        return binder.getValue();
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (isBound()) {
            return super.create(member);
        } else {
            return factory.create(member);
        }
    }

    @Override
    public Collection<? extends CEditableComponent<?, ?>> getComponents() {
        return binder.getComponents();
    }

    @Override
    public ValidationResults getValidationResults() {
        return getAllValidationResults();
    }

    public final <T> void bind(CEditableComponent<T, ?> component, IObject<?> member) {
        binder.bind(component, member);
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        super.setDebugId(debugId);
        binder.setComponentsDebugId(this.getDebugId());
    }

    @Override
    public void onBound(CEntityComponent<?, ?> parent) {
        super.onBound(parent);
        ((SimplePanel) getContainer()).setWidget(createContent());
        initContent();
    }

    public final CEditableComponent<?, ?> inject(IObject<?> member) {
        CEditableComponent<?, ?> comp = create(member);
        bind(comp, member);
        return comp;
    }

    public final CEditableComponent<?, ?> inject(IObject<?> member, CEditableComponent<?, ?> comp) {
        bind(comp, member);
        return comp;
    }

    @SuppressWarnings("unchecked")
    public <T extends IEntity> CEditableComponent<T, ?> get(T member) {
        return (CEditableComponent<T, ?>) binder.get((IObject<?>) member);
    }

    public <T> CEditableComponent<T, ?> get(IObject<T> member) {
        return binder.get(member);
    }

    public CEditableComponent<?, ?> getRaw(IObject<?> member) {
        return binder.get(member);
    }

    public void setWidget(IsWidget widget) {
        asWidget().setWidget(widget);
    }

}
