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

import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;

public class NativeViewer<E> extends SimplePanel implements INativeComponent<E> {

    private final CViewer<E> cComponent;

    public NativeViewer(CViewer<E> cComponent) {
        this.cComponent = cComponent;
    }

    @Override
    public CViewer<E> getCComponent() {
        return cComponent;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        // do nothing - actually it's not enabled...
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void setEditable(boolean editable) {
        // do nothing - actually it's not editable...
    }

    @Override
    public void setNativeValue(E value) {
        setWidget(cComponent.createContent(value));
    }

    @Override
    public E getNativeValue() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setReadOnly(boolean readOnly) {
        // do nothing - actually it's always read-only ...
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        DomDebug.attachedWidget();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        DomDebug.detachWidget();
    }

    @Override
    public void setViewable(boolean editable) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isViewable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        // TODO Auto-generated method stub

    }

}
