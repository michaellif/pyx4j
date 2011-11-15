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
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.ui.Image;

public class ToggleButton extends Button {

    private boolean checked = false;

    public ToggleButton(Image image) {
        this(image, null);
    }

    public ToggleButton(String text) {
        this(null, text);
    }

    public ToggleButton(Image image, final String text) {
        super(new ToggleButtonFacesHandler(), image, text);
    }

    public boolean isChecked() {
        return checked;
    }

    public void toggleChecked() {
        this.fireEvent(new ClickEvent() {
        });
    }

    static class ToggleButtonFacesHandler extends ButtonFacesHandler implements ClickHandler {

        private ToggleButton button;

        public ToggleButtonFacesHandler() {
        }

        @Override
        public void init(Button button) {
            this.button = (ToggleButton) button;
            button.addMouseOverHandler(this);
            button.addMouseOutHandler(this);
            button.addMouseDownHandler(this);
            button.addMouseUpHandler(this);
            button.addClickHandler(this);
        }

        @Override
        public void onMouseOver(MouseOverEvent event) {
            if (button.checked) {
                button.removeStyleDependentName("hover");
                button.removeStyleDependentName("checked");
                button.addStyleDependentName("pushed");
            } else {
                button.removeStyleDependentName("pushed");
                button.addStyleDependentName("hover");
            }

        }

        @Override
        public void onMouseOut(MouseOutEvent event) {
            if (button.checked) {
                button.removeStyleDependentName("pushed");
                button.removeStyleDependentName("hover");
                button.addStyleDependentName("checked");
            } else {
                button.removeStyleDependentName("hover");
                button.removeStyleDependentName("pushed");
            }

        }

        @Override
        public void onMouseDown(MouseDownEvent event) {
            button.removeStyleDependentName("hover");
            button.addStyleDependentName("pushed");
        }

        @Override
        public void onMouseUp(MouseUpEvent event) {
            button.removeStyleDependentName("pushed");
            button.addStyleDependentName("hover");
        }

        @Override
        public void onClick(ClickEvent event) {
            button.checked = !button.checked;
            if (button.checked) {
                button.addStyleDependentName("checked");
            } else {
                button.removeStyleDependentName("checked");
            }
        }
    }
}
