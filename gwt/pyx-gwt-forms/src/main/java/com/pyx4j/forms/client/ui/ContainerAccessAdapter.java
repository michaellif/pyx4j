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

    private CContainer<?> container;

    private boolean inheritEnabled = true;

    private boolean inheritEditable = true;

    private boolean inheritVisible = true;

    private boolean inheritViewable = true;

    public ContainerAccessAdapter() {
    }

    public void setContainer(CContainer<?> container) {
        this.container = container;
    }

    @Override
    public Boolean isEnabled() {
        if (!inheritEnabled || container == null) {
            return null;
        } else {
            return container.isEnabled();
        }
    }

    @Override
    public Boolean isEditable() {
        if (!inheritEditable || container == null) {
            return null;
        } else {
            return container.isEditable();
        }
    }

    @Override
    public Boolean isVisible() {
        if (!inheritVisible || container == null) {
            return null;
        } else {
            return container.isVisible();
        }
    }

    @Override
    public Boolean isViewable() {
        if (!inheritViewable || container == null) {
            return null;
        } else {
            return container.isViewable();
        }
    }

    public void inheritEnabled(boolean flag) {
        inheritEnabled = flag;
    }

    public void inheritEditable(boolean flag) {
        inheritEditable = flag;
    }

    public void inheritVisible(boolean flag) {
        inheritVisible = flag;
    }

    public void inheritViewable(boolean flag) {
        inheritViewable = flag;
    }

}
