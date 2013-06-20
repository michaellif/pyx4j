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
 * @version $Id$
 */
package com.propertyvista.field.client.ui.login;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.login.AbstractLoginViewImpl;
import com.propertyvista.common.client.ui.components.login.LoginView;
import com.propertyvista.domain.DemoData;

public class LoginViewImpl extends AbstractLoginViewImpl implements LoginView {

    private static final I18n i18n = I18n.get(LoginViewImpl.class);

    public LoginViewImpl() {
        super(i18n.tr(""));
    }

    @Override
    protected void createContent() {
        int row = -1;

        setWidget(++row, 0, form);
    }

    @Override
    protected List<DevLoginData> devLoginValues() {
        return Arrays.asList(//@formatter:off
                new DevLoginData(DemoData.UserType.PM, 'Q'),
                new DevLoginData(DemoData.UserType.EMP, 'E')
        );//@formatter:on
    }

    @Override
    public void setDevLogin(List<? extends DevLoginCredentials> devLoginData, String appModeName) {
        // TODO Auto-generated method stub

    }

    public interface LoginHtmlTemplates extends SafeHtmlTemplates {

        public static LoginHtmlTemplates TEMPLATES = GWT.create(LoginHtmlTemplates.class);

        @Template("<div class=\"{0}\"><div style=\"width:0px; height:100%; border-width:1px; border-style:inset;\"/></div>")
        SafeHtml orLineSeparator(String style);
    }
}
