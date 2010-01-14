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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Custom1Option;

public class LogInPanel extends VerticalPanel implements Custom1Option, CancelOption {

    LogInPanel() {

        getElement().getStyle().setPadding(30, Unit.PX);
        getElement().getStyle().setPaddingRight(10, Unit.PX);

        CTextField emailTextField = new CTextField("Email");

        CPasswordTextField passwordTextField = new CPasswordTextField("Password");

        CHyperlink forgotPassword = new CHyperlink(null, new Command() {

            @Override
            public void execute() {
                System.out.println("forgotPassword");
            }
        });
        forgotPassword.setValue("Forgot Password?");

        CComponent<?>[][] components = new CComponent[][] {

        { emailTextField },

        { passwordTextField },

        { forgotPassword },

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
        return "Log&nbsp;In";
    }
}