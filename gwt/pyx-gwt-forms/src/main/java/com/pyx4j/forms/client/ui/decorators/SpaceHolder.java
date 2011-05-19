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
 * Created on Feb 10, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.decorators;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SpaceHolder extends SimplePanel {

    private final String width;

    private HTML spaceHolder;

    public SpaceHolder(double value, Unit unit) {
        this.width = value + unit.getType();
        clear();
    }

    public SpaceHolder(String width) {
        this.width = width;
        clear();
    }

    @Override
    public void setWidget(Widget w) {
        super.setWidget(w);
        w.setWidth(width);
    }

    @Override
    public void clear() {
        super.clear();
        if (spaceHolder == null) {
            spaceHolder = new HTML("&nbsp;");

        }
        setWidget(spaceHolder);
    }
}