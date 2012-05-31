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
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;

public class Anchor extends com.google.gwt.user.client.ui.Anchor {

    public static final String DEFAULT_HREF = "javascript:;";

    public Anchor(String text) {
        this(text, false, DEFAULT_HREF);
    }

    public Anchor(String text, ClickHandler handler) {
        this(text, false, DEFAULT_HREF);
        addClickHandler(handler);
    }

    public Anchor(String text, boolean asHTML, String href) {
        super(text, asHTML, href);
        setStylePrimaryName(getElement(), DefaultWidgetsTheme.StyleName.Anchor.name());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.disabled.name());
        } else {
            addStyleDependentName(DefaultWidgetsTheme.StyleDependent.disabled.name());
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

}
