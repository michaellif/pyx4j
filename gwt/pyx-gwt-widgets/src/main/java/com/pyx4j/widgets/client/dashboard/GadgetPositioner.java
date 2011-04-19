/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Apr 19, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.widgets.client.dashboard;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.SimplePanel;

public class GadgetPositioner extends SimplePanel {

    public GadgetPositioner(int width, int height) {
        getElement().getStyle().setProperty("WebkitBoxSizing", "border-box");
        getElement().getStyle().setProperty("MozBoxSizing", "border-box");
        getElement().getStyle().setProperty("boxSizing", "border-box");
        getElement().getStyle().setPadding(Dashboard.SPACING, Unit.PX);
        getElement().getStyle().setZIndex(100);

        setPixelSize(width, height);

        SimplePanel positionerBorder = new SimplePanel();
        positionerBorder.setHeight("100%");
        positionerBorder.getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
        positionerBorder.getElement().getStyle().setBorderWidth(1, Unit.PX);
        positionerBorder.getElement().getStyle().setBorderColor("#555");
        setWidget(positionerBorder);
    }

}
