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

import java.text.ParseException;

import com.google.gwt.user.client.ui.HTML;

public class NativeCaption extends HTML implements INativeEditableComponent<String> {

    private final CCaption cComponent;

    public NativeCaption(CCaption cComponent) {
        this.cComponent = cComponent;
    }

    @Override
    public CCaption getCComponent() {
        return cComponent;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
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
    public void setNativeValue(String value) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getNativeValue() throws ParseException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setValid(boolean valid) {
        // TODO Auto-generated method stub

    }
}
