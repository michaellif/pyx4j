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
 * Created on Feb 11, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.entity.client.ui.flex;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;

public class CEntityFolderComponent<E extends IEntity> extends CComponent<NativeEntityFolder> {

    private final EntityFolderBinder<E> binder;

    public CEntityFolderComponent(EntityFolderBinder<E> binder) {
        this.binder = binder;
    }

    public CEntityFolderComponent(Class<E> clazz) {
        binder = new EntityFolderBinder<E>(clazz);
    }

    public void createLayout() {

    }

    public EntityFolderBinder<E> binder() {
        return binder;
    }

    public E proto() {
        return binder.proto();
    }

    public void bind(CEditableComponent<?, ?> component, IObject<?> member) {
        binder.bind(component, member);
    }

    @Override
    protected NativeEntityFolder initWidget() {
        return new NativeEntityFolder();
    }

    public void setWidget(Widget widget) {
        asWidget().setWidget(widget);
    }

}
