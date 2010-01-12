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
package com.pyx4j.forms.client.gwt;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.impl.FocusImpl;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.INativeFocusComponent;

public class NativeHyperlink extends Hyperlink implements INativeFocusComponent {

    private static final FocusImpl impl = FocusImpl.getFocusImplForWidget();

    private Command comand;

    private final CHyperlink cHyperlink;

    private boolean enabled;

    public NativeHyperlink(CHyperlink hyperlink, Command comand) {
        this.cHyperlink = hyperlink;

        setText(hyperlink.getValue());
        setTabIndex(hyperlink.getTabIndex());
        setCommand(comand);

        addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (isEnabled() && NativeHyperlink.this.comand != null) {
                    NativeHyperlink.this.comand.execute();
                }
            }

        });

        addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && isEnabled() && NativeHyperlink.this.comand != null) {
                    NativeHyperlink.this.comand.execute();
                }
            }
        });

        setEnabled(cHyperlink.isEnabled());
    }

    public CComponent<?> getCComponent() {
        return cHyperlink;
    }

    public void setCommand(Command comand) {
        this.comand = comand;
    }

    public Command getCommand() {
        return comand;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setWordWrap(boolean wrap) {
        getElement().getStyle().setProperty("whiteSpace", wrap ? "normal" : "nowrap");
    }

    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return addDomHandler(handler, FocusEvent.getType());
    }

    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return addDomHandler(handler, BlurEvent.getType());
    }

    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return addDomHandler(handler, KeyUpEvent.getType());
    }

    public int getTabIndex() {
        return impl.getTabIndex(getElement());
    }

    public void setFocus(boolean focused) {
        if (focused) {
            impl.focus(getElement());
        } else {
            impl.blur(getElement());
        }
    }

    public void setTabIndex(int index) {
        impl.setTabIndex(getElement(), index);
    }

    @Override
    protected void setElement(com.google.gwt.user.client.Element elem) {
        super.setElement(elem);

        // Accessibility: setting tab index to be 0 by default, ensuring element
        // appears in tab sequence. Note that this call will not interfere with
        // any calls made to FocusWidget.setTabIndex(int) by user code, because
        // FocusWidget.setTabIndex(int) cannot be called until setElement(elem)
        // has been called.
        setTabIndex(0);
    }
}
