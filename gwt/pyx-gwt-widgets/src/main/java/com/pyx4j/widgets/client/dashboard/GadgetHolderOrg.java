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
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class GadgetHolderOrg extends SimplePanel {

    public static final int SPACING = 10;

    private final HTML gadget;

    public GadgetHolderOrg(String title, String background, String border) {
        getElement().getStyle().setProperty("WebkitBoxSizing", "border-box");
        getElement().getStyle().setProperty("MozBoxSizing", "border-box");
        getElement().getStyle().setProperty("boxSizing", "border-box");
        getElement().getStyle().setMargin(SPACING, Unit.PX);

        getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        getElement().getStyle().setBorderWidth(3, Unit.PX);
        getElement().getStyle().setBorderColor(border);

        gadget = new HTML(title);
        gadget.getElement().getStyle().setBackgroundColor(background);
        gadget.setHeight(Random.nextInt(5) + 1 + "em");

        setWidget(gadget);

    }

    public Widget getDragHandler() {
        return gadget;
    }

}
