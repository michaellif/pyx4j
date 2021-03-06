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
 * Created on Jan 17, 2012
 * @author michaellif
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;

public class RevalidationTrigger<E> implements ValueChangeHandler<E>, PropertyChangeHandler {

    private final CComponent<?> targetComponent;

    public RevalidationTrigger(CComponent<?> targetComponent) {
        this.targetComponent = targetComponent;
    }

    @Override
    public void onValueChange(ValueChangeEvent<E> event) {
        targetComponent.revalidate();
    }

    @Override
    public void onPropertyChange(PropertyChangeEvent event) {
        if (event.isEventOfType(PropertyName.enabled, PropertyName.editable, PropertyName.visible, PropertyName.viewable, PropertyName.valid,
                PropertyName.editingInProgress)) {
            targetComponent.revalidate();
        }
    }
}
