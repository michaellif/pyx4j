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
 * Created on Jan 13, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.client.demo.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Custom1Option;

public class SignUpPanel extends VerticalPanel implements Custom1Option, CancelOption {

    SignUpPanel() {

        getElement().getStyle().setPadding(30, Unit.PX);

        CTextField nameTextField = new CTextField("Name");
        nameTextField.setMandatory(true);

        CTextField emailTextField = new CTextField("Email");

        CPasswordTextField password1TextField = new CPasswordTextField("Password (5-15 characters)");

        CPasswordTextField password2TextField = new CPasswordTextField("Re-type password");

        CComboBox<String> userType = new CComboBox<String>("I am a...");

        CComponent<?>[][] components = new CComponent[][] {

        { nameTextField, emailTextField },

        { password1TextField, password2TextField },

        { userType, null },

        };

        CForm form = new CForm(LabelAlignment.TOP);

        form.setComponents(components);
        add((Widget) form.initNativeComponent());

    }

    @Override
    public boolean onClickCancel() {
        return true;
    }

    @Override
    public boolean onClickCustom1() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public String custom1Text() {
        // TODO Auto-generated method stub
        return "Create&nbsp;Account";
    }
}