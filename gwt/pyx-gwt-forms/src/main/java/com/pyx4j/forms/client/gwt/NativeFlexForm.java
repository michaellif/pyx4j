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
 * Created on Apr 16, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.gwt;

import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CFlexForm;
import com.pyx4j.forms.client.ui.INativeComponent;

public class NativeFlexForm extends FlexTable implements INativeComponent {

    public NativeFlexForm(CFlexForm cFlexForm, List<CComponent<?>> componentCollection) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public CComponent<?> getCComponent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        // TODO Auto-generated method stub

    }

    public void layout() {
        // TODO Auto-generated method stub

    }

}
