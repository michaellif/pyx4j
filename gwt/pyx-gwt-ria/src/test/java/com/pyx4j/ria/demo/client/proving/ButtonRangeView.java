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
 * Created on May 16, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.ria.demo.client.proving;

import com.pyx4j.ria.client.ImageFactory;
import com.pyx4j.widgets.client.Button;

public class ButtonRangeView extends AbstractProvingView {

    public ButtonRangeView() {
        super("Button Range", ImageFactory.getImages().debugOn());
        super.setDescription("Test Buttons styles");

        createWidgetTestActionGroup("GWT Button", new com.google.gwt.user.client.ui.Button("Click Button"));

        createWidgetTestActionGroup("Button", new Button("Click Button"));

        createWidgetTestActionGroup("Toggle Button", new com.pyx4j.widgets.client.ToggleButton("Toggle"));
    }

}
