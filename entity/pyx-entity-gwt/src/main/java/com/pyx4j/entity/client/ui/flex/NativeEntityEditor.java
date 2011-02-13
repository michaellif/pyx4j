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
package com.pyx4j.entity.client.ui.flex;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.INativeEditableComponent;

public class NativeEntityEditor<E> extends SimplePanel implements INativeEditableComponent<E> {

    public NativeEntityEditor() {
        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
    }

    @Override
    public void setTabIndex(int tabIndex) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFocus(boolean focused) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setEnabled(boolean enabled) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public CComponent<?> getCComponent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setEditable(boolean editable) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isEditable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setNativeValue(E value) {
        // TODO Auto-generated method stub

    }

}
