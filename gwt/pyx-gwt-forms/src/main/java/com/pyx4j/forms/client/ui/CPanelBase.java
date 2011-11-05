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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.Widget;

public abstract class CPanelBase<WIDGET_TYPE extends Widget & INativeEditableComponent<Void>> extends CContainer<Void, WIDGET_TYPE> {

    private final Collection<CComponent<?>> components = new ArrayList<CComponent<?>>();

    public CPanelBase() {
    }

    @Override
    public void addComponent(CComponent<?> component) {
        components.add(component);
        component.setParent(this);
    }

    @Override
    public Collection<CComponent<?>> getComponents() {
        return components;
    }

}
