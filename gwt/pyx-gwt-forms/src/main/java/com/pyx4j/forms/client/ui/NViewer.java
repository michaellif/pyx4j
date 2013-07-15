/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on May 8, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;

public class NViewer<DATA> extends SimplePanel implements INativeComponent<DATA> {

    private final CViewer<DATA> cComponent;

    public NViewer(CViewer<DATA> cComponent) {
        this.cComponent = cComponent;
    }

    @Override
    public void setNativeValue(DATA value) {
        IsWidget widget = getCComponent().createContent(value);
        setWidget(widget);
    }

    @Override
    public DATA getNativeValue() throws ParseException {
        throw new IllegalStateException("getNativeValue() shouldn't be called in viewable mode");
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEditable(boolean editable) {
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public CViewer<DATA> getCComponent() {
        return cComponent;
    }

    @Override
    public void setViewable(boolean editable) {
    }

    @Override
    public boolean isViewable() {
        return false;
    }

    @Override
    public void setNavigationCommand(Command navigationCommand) {
    }

    @Override
    public void setDebugId(IDebugId debugId) {
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

}
