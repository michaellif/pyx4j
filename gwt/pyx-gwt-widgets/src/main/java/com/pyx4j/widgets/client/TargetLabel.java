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
 * Created on 2011-04-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusWidget;

public class TargetLabel extends com.google.gwt.user.client.ui.Label {

    public TargetLabel() {
        super();
    }

    public TargetLabel(String text) {
        super(text);
    }

    public TargetLabel(String text, FocusWidget target) {
        super(text);
        setTarget(target);
    }

    public void setTarget(final FocusWidget target) {
        this.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                (target).setFocus(true);
            }
        });
    }
}
