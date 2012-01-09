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
package com.pyx4j.entity.client;

import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.INativeComponent;

public class NativeEntityPanel<E extends IObject<?>> extends SimplePanel implements INativeComponent<E> {

    public NativeEntityPanel() {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public CComponent<?, ?> getCComponent() {
        return null;
    }

    @Override
    public void setEditable(boolean editable) {
        if (editable) {
            asWidget().removeStyleDependentName(DefaultEntityFolderTheme.StyleDependent.readOnly.name());
        } else {
            asWidget().addStyleDependentName(DefaultEntityFolderTheme.StyleDependent.readOnly.name());
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            asWidget().removeStyleDependentName(DefaultEntityFolderTheme.StyleDependent.readOnly.name());
        } else {
            asWidget().addStyleDependentName(DefaultEntityFolderTheme.StyleDependent.readOnly.name());
        }

    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public void setNativeValue(E value) {
    }

    @Override
    public void onPropertyChange(PropertyChangeEvent event) {
    }

    @Override
    public E getNativeValue() {
        return null;
    }

}
