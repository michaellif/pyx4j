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
import java.util.List;

import com.pyx4j.forms.client.gwt.NativeFlexForm;

public class CFlexForm extends CContainer {

    private final List<CComponent<?>> componentCollection = new ArrayList<CComponent<?>>();

    private NativeFlexForm nativeForm;

    public void addComponent(int index, CComponent<?> component) {
        componentCollection.add(index, component);
        component.setParent(this);
        if (getNativeComponent() != null) {
            getNativeComponent().layout();
        }
    }

    @Override
    public void addComponent(CComponent<?> component) {
        addComponent(componentCollection.size(), component);
    }

    @Override
    public Collection<CComponent<?>> getComponents() {
        return componentCollection;
    }

    @Override
    public NativeFlexForm getNativeComponent() {
        if (getParent() instanceof CFlexForm) {
            return (NativeFlexForm) getParent().getNativeComponent();
        } else {
            return nativeForm;
        }
    }

    @Override
    public NativeFlexForm initNativeComponent() {
        if (nativeForm == null) {
            nativeForm = new NativeFlexForm(this);
            nativeForm.layout();
            applyAccessibilityRules();
        }
        return nativeForm;

    }

}
