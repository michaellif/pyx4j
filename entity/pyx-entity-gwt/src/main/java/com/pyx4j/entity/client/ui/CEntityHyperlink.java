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
 * Created on Apr 28, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.pyx4j.gwt.commons.History;
import com.google.gwt.user.client.ui.Anchor;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.INativeEditableComponent;

public class CEntityHyperlink extends CEditableComponent<IEntity> {

    private NativeEntityHyperlink nativeLink;

    private IEntity value;

    private boolean wordWrap = false;

    private final String uriPrefix;

    public CEntityHyperlink(String title, String uriPrefix) {
        super(title);
        this.uriPrefix = uriPrefix;
    }

    @Override
    public NativeEntityHyperlink getNativeComponent() {
        return nativeLink;
    }

    @Override
    public NativeEntityHyperlink initNativeComponent() {
        if (nativeLink == null) {
            nativeLink = new NativeEntityHyperlink(this);
            applyAccessibilityRules();
        }
        return nativeLink;
    }

    @Override
    public void setValue(IEntity value) {
        this.value = value;
        if (nativeLink != null) {
            nativeLink.setNativeValue(value);
        }
    }

    @Override
    public IEntity getValue() {
        return value;
    }

    public void setWordWrap(boolean wrap) {
        if (nativeLink != null) {
            nativeLink.setWordWrap(wrap);
        }
        wordWrap = wrap;
    }

    public boolean isWordWrap() {
        return wordWrap;
    }

    class NativeEntityHyperlink extends Anchor implements INativeEditableComponent<IEntity> {

        private final Command comand;

        private final CEntityHyperlink cHyperlink;

        private boolean enabled;

        public NativeEntityHyperlink(CEntityHyperlink hyperlink) {
            super("&nbsp;", true);
            this.cHyperlink = hyperlink;
            setTabIndex(hyperlink.getTabIndex());

            comand = new Command() {

                @Override
                public void execute() {
                    History.newItem(uriPrefix + cHyperlink.getValue().getPrimaryKey());
                }
            };

            addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if (isEnabled() && NativeEntityHyperlink.this.comand != null) {
                        NativeEntityHyperlink.this.comand.execute();
                    }
                }

            });

            addKeyUpHandler(new KeyUpHandler() {
                public void onKeyUp(KeyUpEvent event) {
                    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && isEnabled() && NativeEntityHyperlink.this.comand != null) {
                        NativeEntityHyperlink.this.comand.execute();
                    }
                }
            });

            setEnabled(cHyperlink.isEnabled());
        }

        public CComponent<?> getCComponent() {
            return cHyperlink;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
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
        }

        @Override
        public void setNativeValue(IEntity value) {
            setHTML("&nbsp;" + ((value == null) ? "" : value.getStringView()));
        }

    }
}