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
 * Created on May 11, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.resources.client.ImageResource;

public class ToggleButton extends Button {

    private boolean active = false;

    public ToggleButton(ImageResource imageResource) {
        this(imageResource, null);
    }

    public ToggleButton(String text) {
        this(null, text);
    }

    public ToggleButton(ImageResource imageResource, final String text) {
        super(new ToggleButtonFacesHandler(), imageResource, text);
    }

    public boolean isActive() {
        return active;
    }

    public void toggleActive() {
        this.fireEvent(new ClickEvent() {
        });
    }

    static class ToggleButtonFacesHandler extends ButtonFacesHandler implements ClickHandler {

        public ToggleButtonFacesHandler() {
        }

        @Override
        public void onMouseOver(MouseOverEvent event) {
            super.onMouseOver(event);
            ToggleButton button = (ToggleButton) getButton();
            if (isEnabled() && button.active) {
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
                button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.pushed.name());
            }
        }

        @Override
        public void onMouseOut(MouseOutEvent event) {
            super.onMouseOut(event);
            ToggleButton button = (ToggleButton) getButton();
            if (isEnabled() && button.active) {
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
                button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.pushed.name());
            }

        }

        @Override
        public void onClick(ClickEvent event) {
            ToggleButton button = (ToggleButton) getButton();
            button.active = !button.active;
            if (button.active) {
                button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.pushed.name());
            } else {
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.pushed.name());
            }
        }
    }
}
