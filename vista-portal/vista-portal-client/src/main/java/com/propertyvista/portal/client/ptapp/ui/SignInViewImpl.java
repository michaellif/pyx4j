/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.essentials.client.BaseLogInPanel;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;

public class SignInViewImpl extends VerticalPanel implements SignInView {

    private static I18n i18n = I18nFactory.getI18n(SignInViewImpl.class);

    private Presenter presenter;

    public SignInViewImpl() {

        CTextField username = new CTextField("Username");

        CPasswordTextField password = new CPasswordTextField("Password");

        CCheckBox keepMeSigned = new CCheckBox("Keep me logged in");

        CHyperlink forgotPassword = new CHyperlink(null, new Command() {

            @Override
            public void execute() {
                // TODO Auto-generated method stub

            }
        });
        forgotPassword.setValue(i18n.tr("Did you forget your password?"));

        CComponent<?>[][] components = new CComponent[][] {

        { username },

        { password },

        { keepMeSigned },

        { forgotPassword },

        };

        Widget searchForm = CForm.createDecoratedFormWidget(LabelAlignment.TOP, components, "Sign-in");
        add(searchForm);

        Button viewButton = new Button("Sign-in");
        viewButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        viewButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
            }

        });
        viewButton.getElement().getStyle().setProperty("margin", "3px 20px 3px 8px");
        add(viewButton);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
