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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.AbstractAccessAdapter;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.INativeEditableComponent;

public class CEntityHyperlink extends CEditableComponent<IEntity, NativeEntityHyperlink> {

    private NativeEntityHyperlink nativeLink;

    private boolean wordWrap = false;

    private final String uriPrefix;

    public CEntityHyperlink(String title, String uriPrefix) {
        super(title);
        this.uriPrefix = uriPrefix;

        this.addAccessAdapter(new AbstractAccessAdapter() {

            @Override
            public boolean isEnabled(CComponent<?> component) {
                return !isValueEmpty();
            }

        });
    }

    @Override
    protected NativeEntityHyperlink initWidget() {
        return new NativeEntityHyperlink(this);
    }

    @Override
    public void setValue(IEntity value) {
        super.setValue(value);
        applyAccessibilityRules();
    }

    @Override
    public void populate(IEntity value) {
        super.populate(value);
        applyAccessibilityRules();
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || getValue().isNull();
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

    public String getItemName(IEntity value) {
        return ((value == null) ? "" : value.getStringView());
    }

    protected String getEntityHistoryToken(IEntity value) {
        return uriPrefix + value.getPrimaryKey();
    }

}

class NativeEntityHyperlink extends Anchor implements INativeEditableComponent<IEntity> {

    private final Command comand;

    private final CEntityHyperlink cHyperlink;

    public NativeEntityHyperlink(final CEntityHyperlink hyperlink) {
        super("&nbsp;", true);
        this.cHyperlink = hyperlink;
        setTabIndex(hyperlink.getTabIndex());

        comand = new Command() {

            @Override
            public void execute() {
                History.newItem(hyperlink.getEntityHistoryToken(cHyperlink.getValue()));
            }
        };

        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (isEnabled() && NativeEntityHyperlink.this.comand != null) {
                    NativeEntityHyperlink.this.comand.execute();
                }
            }

        });

        addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && isEnabled() && NativeEntityHyperlink.this.comand != null) {
                    NativeEntityHyperlink.this.comand.execute();
                }
            }
        });
    }

    @Override
    public CComponent<?> getCComponent() {
        return cHyperlink;
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
        setHTML("&nbsp;" + cHyperlink.getItemName(value) + "&nbsp;");
    }

}