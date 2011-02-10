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

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.EditableComponentFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

public class CEntityEditableComponent<E extends IEntity, T extends Widget> extends CEditableComponent<E, NativeEntityEditor<E, T>> {

    private final EntityBinder<E> binder;

    public static <T extends IEntity, Y extends Widget> CEntityEditableComponent<T, Y> create(Class<T> clazz, Y content, EditableComponentFactory factory) {
        return new CEntityEditableComponent<T, Y>(clazz, content, factory);
    }

    public CEntityEditableComponent(Class<E> clazz, T content, EditableComponentFactory factory) {
        binder = new EntityBinder<E>(clazz, factory);
        asWidget().setWidget(content);
        createLayout();
    }

    public void createLayout() {

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

    public <Y> CEditableComponent<Y, ?> create(IObject<Y> member) {
        return binder.create(member);
    }

    @Override
    protected NativeEntityEditor<E, T> initWidget() {
        return new NativeEntityEditor<E, T>();
    }

    public T content() {
        return asWidget().content();
    }

}
