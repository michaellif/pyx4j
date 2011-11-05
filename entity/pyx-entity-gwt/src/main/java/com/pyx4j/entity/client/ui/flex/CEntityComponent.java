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
 * Created on May 31, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CButton;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CContainer;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.IAccessAdapter;

public abstract class CEntityComponent<E extends IObject<?>> extends CContainer<E, NativeEntityPanel<E>> implements IEditableComponentFactory, IAccessAdapter {

    private CEntityComponent<?> bindParent;

    @Override
    public boolean isEnabled(CComponent<?> component) {
        if (component instanceof CButton) {
            return isEditable() && isEnabled();
        } else {
            return isEnabled();
        }
    }

    @Override
    public boolean isEditable(CComponent<?> component) {
        return isEditable();
    }

    @Override
    public boolean isVisible(CComponent<?> component) {
        return isVisible();
    }

    @Override
    public boolean isVisited() {
        return true;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        assert (bindParent != null) : "Flex Component " + this.getClass().getName() + "is not bound";
        return bindParent.create(member);
    }

    public void onBound(CEntityComponent<?> parent) {
        assert (bindParent == null) : "Flex Component " + this.getClass().getName() + " is already bound to " + bindParent;
        bindParent = parent;
    }

    public boolean isBound() {
        return bindParent != null;
    }

    public void addValidations() {

    }

}
