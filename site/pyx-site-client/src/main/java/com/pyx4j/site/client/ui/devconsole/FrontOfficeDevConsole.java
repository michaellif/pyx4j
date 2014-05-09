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
 * Created on Feb 11, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.devconsole;

import com.google.gwt.dom.client.Style.Unit;

import com.pyx4j.site.client.ui.layout.frontoffice.FrontOfficeLayoutPanel;
import com.pyx4j.widgets.client.Toolbar;

public class FrontOfficeDevConsole extends AbstractDevConsole {

    private final FrontOfficeLayoutPanel layoutPanel;

    public FrontOfficeDevConsole(final FrontOfficeLayoutPanel layoutPanel) {
        this.layoutPanel = layoutPanel;

        getElement().getStyle().setPadding(20, Unit.PX);

        Toolbar toolbar = new Toolbar();
        add(toolbar);
        toolbar.addItem(new SetMocksButton());
    }

    @Override
    void setMockValues() {
        setMockValues(layoutPanel);
    }

}
