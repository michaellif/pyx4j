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
 * Created on Jan 28, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.shared.GwtEvent;

public class RichTextArea extends com.google.gwt.user.client.ui.RichTextArea {
    private boolean ignoreBlur;

    public RichTextArea() {
        setStyleName(DefaultWidgetsTheme.StyleName.TextBox.name());
        getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        getElement().getStyle().setBorderWidth(1, Unit.PX);
        getElement().getStyle().setBackgroundColor("white");
        ignoreBlur = false;
    }

    /*
     * This method is used by containing component (ExtendedRichTextArea) to ignore blur events
     * triggered by toolbar buttons. This prevents from loosing text selections due to accessing
     * the text value by onEditiongStop() method via onBlur handler.
     */
    public void ignoreBlur(boolean ignore) {
        ignoreBlur = ignore;
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (!event.getAssociatedType().equals(BlurEvent.getType()) || !ignoreBlur) {
            super.fireEvent(event);
        }
    }
}
