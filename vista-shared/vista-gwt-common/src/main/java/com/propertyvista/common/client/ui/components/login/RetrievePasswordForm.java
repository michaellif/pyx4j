/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.login;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;

public class RetrievePasswordForm extends CEntityEditor<PasswordRetrievalRequest> {

    private static I18n i18n = I18n.get(RetrievePasswordForm.class);

    private final String caption;

    private final Command newCommand;

    public RetrievePasswordForm(String caption, Command newCommand) {
        super(PasswordRetrievalRequest.class);
        this.caption = caption;
        this.newCommand = newCommand;
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
        FlowPanel main = new FlowPanel();
        HTML header = new HTML(HtmlUtils.h2(caption));
        header.getElement().getStyle().setMarginBottom(3, Unit.EM);
        header.getElement().getStyle().setProperty("textAlign", "center");

        main.add(header);
        main.add(new LoginPanelWidgetDecorator(inject(proto().email())));
        main.add(new HTML());
        main.add(new LoginPanelWidgetDecorator(inject(proto().captcha())));

        Button retrievePasswordButton = new Button(i18n.tr("Retrieve Password"));
        // retrievePasswordButton.ensureDebugId(VistaFormsDebugId.Auth_RetrivePassword.toString());
        retrievePasswordButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                newCommand.execute();
            }

        });

        retrievePasswordButton.getElement().getStyle().setMarginLeft(9, Unit.EM);
        retrievePasswordButton.getElement().getStyle().setMarginRight(1, Unit.EM);
        retrievePasswordButton.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        main.add(retrievePasswordButton);

        main.getElement().getStyle().setMarginTop(1, Unit.EM);
        main.getElement().getStyle().setMarginBottom(1, Unit.EM);

        return main;
    }
}