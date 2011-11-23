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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;

public class NativeButton extends com.google.gwt.user.client.ui.Button implements INativeFocusComponent<Void> {

    private Command comand;

    private final CButton button;

    public NativeButton(CButton button, String html, Command comand) {
        super(html);
        setCommand(comand);
        this.button = button;
        this.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (NativeButton.this.comand != null) {
                    NativeButton.this.comand.execute();
                }
            }
        });
        setTabIndex(button.getTabIndex());
        setEnabled(button.isEnabled());
        ensureDebugId("Button." + html);
    }

    public void setCommand(Command comand) {
        this.comand = comand;
    }

    public Command getCommand() {
        return comand;
    }

    @Override
    public CButton getCComponent() {
        return button;
    }

    @Override
    public void setEditable(boolean editable) {
        super.setEnabled(editable);
    }

    @Override
    public boolean isEditable() {
        return isEnabled();
    }

    @Override
    public void setNativeValue(Void value) {
        // TODO Auto-generated method stub

    }

    @Override
    public Void getNativeValue() throws ParseException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setValid(boolean valid) {
        // TODO Auto-generated method stub

    }
}
