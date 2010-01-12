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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CLayoutConstraints;

public class GWTStyleAdapter {

    public static void setLayoutConstraints(Widget widget, CLayoutConstraints layoutConstraints) {
        if (layoutConstraints == null) {
            return;
        }
        if (layoutConstraints.padding != null) {
            String padding = layoutConstraints.padding.top + "px " + layoutConstraints.padding.right + "px " + layoutConstraints.padding.bottom + "px "
                    + layoutConstraints.padding.left + "px ";
            DOM.setStyleAttribute(widget.getElement(), "padding", padding);
        }
        if (layoutConstraints.margin != null) {
            String margin = layoutConstraints.margin.top + "px " + layoutConstraints.margin.right + "px " + layoutConstraints.margin.bottom + "px "
                    + layoutConstraints.margin.left + "px ";
            DOM.setStyleAttribute(widget.getElement(), "margin", margin);
        }
        if (layoutConstraints.stretch != null) {
            switch (layoutConstraints.stretch) {
            case VERTICAL:
                widget.setHeight("100%");
                break;
            case HORIZONTAL:
                widget.setWidth("100%");
                break;
            case BOTH:
                widget.setHeight("100%");
                widget.setWidth("100%");
                break;
            }
        }

        if (layoutConstraints.anchor != null) {
            switch (layoutConstraints.anchor) {
            case LOWER_RIGHT:

                break;
            }

        }
    }
}
