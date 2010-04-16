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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.ui.FlexTable;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CFlexForm;
import com.pyx4j.forms.client.ui.INativeComponent;

public class NativeFlexForm extends FlexTable implements INativeComponent {

    private static final Logger log = LoggerFactory.getLogger(NativeFlexForm.class);

    private final CFlexForm form;

    public NativeFlexForm(CFlexForm form) {
        this.form = form;
    }

    @Override
    public CComponent<?> getCComponent() {
        return form;
    }

    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    public void layout() {
        // TODO Auto-generated method stub

    }

}
