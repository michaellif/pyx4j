/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 14, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.login;

import static com.pyx4j.commons.HtmlUtils.h2;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationRequest;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;

public class LoginForm extends CEntityEditor<AuthenticationRequest> {

    private static I18n i18n = I18n.get(LoginForm.class);

    private final String caption;

    private final Command loginCommand;

    private final Command retreivePasswordCommand;

    public LoginForm(String caption, Command loginCommand, Command retreivePasswordCommand) {
        super(AuthenticationRequest.class);
        this.caption = caption;
        this.loginCommand = loginCommand;
        this.retreivePasswordCommand = retreivePasswordCommand;
        setWidth("30em");

    }

    @Override
    protected void onWidgetCreated() {
        super.onWidgetCreated();
        asWidget().setStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name(), true);
        asWidget().getElement().getStyle().setMarginTop(5, Unit.PCT);
        asWidget().getElement().getStyle().setMarginBottom(5, Unit.PCT);

    }

    @Override
    public IsWidget createContent() {
        HTML header = new HTML(h2(caption));
        header.getElement().getStyle().setMarginBottom(3, Unit.EM);
        header.getElement().getStyle().setProperty("textAlign", "center");

        FlowPanel main = new FlowPanel();
        main.add(header);

        if (ApplicationMode.isDevelopment()) {
            FlowPanel devMessagePanel = new FlowPanel();
            devMessagePanel.add(new HTML("This application is running in <B>DEVELOPMENT</B> mode."));
            devMessagePanel.add(new HTML("Press <i>Ctrl+Q</i> to login"));
            devMessagePanel.getElement().getStyle().setProperty("textAlign", "center");
            devMessagePanel.getElement().getStyle().setMarginBottom(3, Unit.EM);
            main.add(devMessagePanel);
        }

        main.add(new LoginPanelWidgetDecorator(inject(proto().email())));
        main.add(new HTML());
        main.add(new LoginPanelWidgetDecorator(inject(proto().password())));
        main.add(new HTML());
        main.add(new LoginPanelWidgetDecorator(inject(proto().captcha())));

        Button loginButton = new Button(i18n.tr("Login"));
        loginButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        loginButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                loginCommand.execute();
            }
        });

        loginButton.getElement().getStyle().setMarginLeft(9, Unit.EM);
        loginButton.getElement().getStyle().setMarginRight(1, Unit.EM);
        loginButton.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        main.add(loginButton);

        if (retreivePasswordCommand != null) {
            CHyperlink forgotPassword = new CHyperlink(null, retreivePasswordCommand);
            forgotPassword.setValue(i18n.tr("Retrieve Password"));
            main.add(forgotPassword);
        }

        return main;
    }
}