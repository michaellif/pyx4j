/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.security;

import static com.pyx4j.commons.HtmlUtils.h2;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.components.AnchorButton;

public class PasswordResetRequestResultViewImpl implements PasswordResetRequestResultView {

    private static final I18n i18n = I18n.get(PasswordResetRequestResultViewImpl.class);

    private Presenter presenter = null;

    private final HTML header;

    Widget viewWidget;

    public PasswordResetRequestResultViewImpl() {
        VerticalPanel main = new VerticalPanel();
        main.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        main.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        main.setWidth("100%");

        header = new HTML(h2(i18n.tr("A password reset link was sent to your e-mail address")));
        header.getElement().getStyle().setMargin(3, Unit.EM);
        header.getElement().getStyle().setProperty("textAlign", "center");
        main.add(header);

        AnchorButton loginButton = new AnchorButton(i18n.tr("return to log in screen"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                PasswordResetRequestResultViewImpl.this.presenter.goToLoginScreen();
            }
        });
        main.add(loginButton);

        viewWidget = main;
    }

    @Override
    public Widget asWidget() {
        return viewWidget;
    }

    @Override
    public void setPresetner(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(boolean resetSuccess) {
        String message = resetSuccess ? i18n.tr("A password reset link was sent to your e-mail address.") : i18n
                .tr("Failed to reset the password, make sure the email address you provided is correct.");
        header.setHTML(h2(message));
    }

}
