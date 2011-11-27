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
 * Created on 2011-03-22
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.ui.IsWidget;

public abstract class CViewer<E> extends CFocusComponent<E, NativeViewer<E>> {

    public CViewer() {
        super();
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void setEditable(boolean editable) {
        // do nothing - not editable...
    }

    @Override
    public void onEditingStart() {
        // do nothing - not editable...
    }

    @Override
    public void onEditingStop() {
        // do nothing - not editable...
    }

    public abstract IsWidget createContent(E value);

    @Override
    protected NativeViewer<E> createWidget() {
        return new NativeViewer<E>(this);
    }

}
