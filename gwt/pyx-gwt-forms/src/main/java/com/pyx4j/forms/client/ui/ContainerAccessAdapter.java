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

public class ContainerAccessAdapter implements IAccessAdapter {

    private final CContainer container;

    public ContainerAccessAdapter(CContainer container) {
        this.container = container;
    }

    @Override
    public boolean isEnabled(CComponent<?, ?> component) {
        if (component instanceof CButton) {
            return container.isEditable() && container.isEnabled();
        } else {
            return container.isEnabled();
        }
    }

    @Override
    public boolean isEditable(CComponent<?, ?> component) {
        return container.isEditable();
    }

    @Override
    public boolean isVisible(CComponent<?, ?> component) {
        return container.isVisible();
    }

}
