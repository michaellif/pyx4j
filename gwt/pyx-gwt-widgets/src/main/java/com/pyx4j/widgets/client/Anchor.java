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
 * Created on Aug 9, 2010
 * @author michaellif
 */
package com.pyx4j.widgets.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.gwt.commons.ui.HasStyle;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class Anchor extends com.google.gwt.user.client.ui.Anchor implements IFocusWidget, HasStyle {

    public static final String DEFAULT_HREF = "javascript:;";

    public Anchor(String text) {
        this(text, false, DEFAULT_HREF);
    }

    public Anchor(String text, final Command command) {
        this(text, false, DEFAULT_HREF);
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                command.execute();
            }
        });
    }

    public Anchor(String text, boolean asHTML) {
        this(text, asHTML, DEFAULT_HREF);
    }

    public Anchor(String text, boolean asHTML, String href) {
        super(text, asHTML, href);
        setStylePrimaryName(getElement(), WidgetsTheme.StyleName.Anchor.name());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            removeStyleDependentName(WidgetsTheme.StyleDependent.disabled.name());
        } else {
            addStyleDependentName(WidgetsTheme.StyleDependent.disabled.name());
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (isEnabled()) {
            super.onBrowserEvent(event);
        } else {
            event.stopPropagation();
        }
    };

    @Override
    public HandlerRegistration addClickHandler(final ClickHandler handler) {
        ClickHandler wrapper = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                handler.onClick(event);
                event.preventDefault();
            }
        };
        return super.addClickHandler(wrapper);
    }

    @Override
    public void setEditable(boolean editable) {
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        ensureDebugId(debugId.debugId());
    }
}
