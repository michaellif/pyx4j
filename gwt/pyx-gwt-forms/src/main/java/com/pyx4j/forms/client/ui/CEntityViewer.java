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
package com.pyx4j.forms.client.ui;

import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.validators.ValidationResults;

public abstract class CEntityViewer<E extends IObject<?>> extends CEntityContainer<E> {

    @Override
    protected void onValuePropagation(E value, boolean fireEvent, boolean populate) {
        super.onValuePropagation(value, fireEvent, populate);
        setContent(createContent(value));
    };

    public abstract IsWidget createContent(E value);

    protected void setContent(IsWidget widget) {
        asWidget();
        getWidget().setWidget(widget);
    }

    @Override
    public final IsWidget createContent() {
        return null;
    }

    @Override
    public Collection<? extends CComponent<?, ?>> getComponents() {
        return null;
    }

    @Override
    public ValidationResults getValidationResults() {
        return new ValidationResults();
    }

    @Override
    protected void setComponentsValue(E value, boolean fireEvent, boolean populate) {
    }

}
