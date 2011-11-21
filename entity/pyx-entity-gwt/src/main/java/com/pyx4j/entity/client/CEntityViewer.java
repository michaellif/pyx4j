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
 * Created on Jun 1, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client;

import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.ValidationResults;

public abstract class CEntityViewer<E extends IEntity> extends CEntityContainer<E> {

    @Override
    public void populate(E value) {
        super.populate(value);
        setContent(createContent(value));
    }

    @Override
    protected NativeEntityPanel<E> createWidget() {
        return new NativeEntityPanel<E>();
    }

    public abstract IsWidget createContent(E value);

    protected void setContent(IsWidget widget) {
        asWidget().setWidget(widget);
    }

    @Override
    public IsWidget createContent() {
        return null;
    }

    @Override
    public Collection<? extends CComponent<?, ?>> getComponents() {
        return null;
    }

    @Override
    public ValidationResults getValidationResults() {
        return null;
    }

    @Override
    public void addComponent(CComponent<?, ?> component) {
        // TODO Auto-generated method stub

    }
}
