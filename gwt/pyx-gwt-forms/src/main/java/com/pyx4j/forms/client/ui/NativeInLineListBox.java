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

import java.util.List;

import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.forms.client.ui.CListBox.ListBoxDisplayProperties;

public class NativeInLineListBox<E> extends NativeListSelectionComposite<E> {

    private final CListBox<E> cListBox;

    public NativeInLineListBox(final CListBox<E> cListBox, ListBoxDisplayProperties properties) {
        super(properties);
        this.cListBox = cListBox;

        cListBox.retriveOptions(new CListBox.AsyncOptionsReadyCallback<E>() {
            @Override
            public void onOptionsReady(List<E> opt) {
                setOptions(opt);
            }
        });

    }

    @Override
    public CComponent<?> getCComponent() {
        return cListBox;
    }

    @Override
    public String getItemName(E item) {
        return cListBox.getItemName(item);
    }

    @Override
    public void onNativeValueChange(List<E> values) {
        cListBox.setValue(values);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAccessKey(char key) {
        // TODO Auto-generated method stub

    }

}
