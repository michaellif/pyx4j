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
 * Created on Jan 22, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.forms.client.gwt.NativeVerticalPanel;

public class CVerticalPanel extends CPanelBase<NativeVerticalPanel> {

    @Override
    protected NativeVerticalPanel initWidget() {

        NativeVerticalPanel nativePanel = new NativeVerticalPanel(this);

        for (CComponent<?> component : getComponents()) {
            nativePanel.add(component.asWidget(), component.getConstraints());
        }
        applyAccessibilityRules();

        return nativePanel;
    }
}
