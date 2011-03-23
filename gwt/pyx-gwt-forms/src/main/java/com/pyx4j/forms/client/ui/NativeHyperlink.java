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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;

import com.pyx4j.widgets.client.style.CSSClass;

public class NativeHyperlink<E> extends Anchor implements INativeReference<E> {

    private boolean enabled;

    private Command comand;

    private final CAbstractHyperlink<E> cHyperlink;

    private final NativeReferenceDelegate<E> delegate;

    public NativeHyperlink(CAbstractHyperlink<E> hyperlink, Command comand) {
        super(hyperlink.getValue() != null ? hyperlink.getValue().toString() : null);
        delegate = new NativeReferenceDelegate<E>(this);

        this.cHyperlink = hyperlink;
        setStyleName(CSSClass.pyx4j_Hyperlink.name());

        setTabIndex(hyperlink.getTabIndex());
        setCommand(comand);

        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (isEnabled() && NativeHyperlink.this.comand != null) {
                    NativeHyperlink.this.comand.execute();
                }
            }

        });

        addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && isEnabled() && NativeHyperlink.this.comand != null) {
                    NativeHyperlink.this.comand.execute();
                }
            }
        });

        setEnabled(cHyperlink.isEnabled());
    }

    @Override
    public CAbstractHyperlink<E> getCComponent() {
        return cHyperlink;
    }

    public void setCommand(Command comand) {
        this.comand = comand;
    }

    public Command getCommand() {
        return comand;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            addStyleDependentName("enabled");
        } else {
            removeStyleDependentName("enabled");
        }
    }

    @Override
    public void setWordWrap(boolean wrap) {
        getElement().getStyle().setProperty("whiteSpace", wrap ? "normal" : "nowrap");
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
        delegate.setNativeValue(value);
    }

    @Override
    public void setValid(boolean valid) {
        // do nothing - actually it's valid always...
    }
}
