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
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;

public class NewPasswordForm extends CEntityEditor<PasswordChangeRequest> {

    private static I18n i18n = I18n.get(NewPasswordForm.class);

    public enum ConversationType {
        RESET, CHANGE
    }

    private HTML header;

    private final String caption;

    private final Command retreiveCommand;

    public NewPasswordForm(String caption, Command retreiveCommand) {
        super(PasswordChangeRequest.class);
        this.caption = caption;
        this.retreiveCommand = retreiveCommand;
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
        header = new HTML();
        header.getElement().getStyle().setMarginBottom(3, Unit.EM);
        header.getElement().getStyle().setProperty("textAlign", "center");

        main.add(header);
        main.add(new HTML());
        main.add(new LoginPanelWidgetDecorator(inject(proto().currentPassword())));
        main.add(new HTML());
        main.add(new LoginPanelWidgetDecorator(inject(proto().newPassword())));
        main.add(new HTML());
        main.add(new LoginPanelWidgetDecorator(inject(proto().newPassword2())));

        Button newPasswordButton = new Button(caption);
        newPasswordButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        newPasswordButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                retreiveCommand.execute();
            }

        });

        newPasswordButton.getElement().getStyle().setMarginLeft(9, Unit.EM);
        newPasswordButton.getElement().getStyle().setMarginRight(1, Unit.EM);
        newPasswordButton.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        main.add(newPasswordButton);

        main.getElement().getStyle().setMarginTop(1, Unit.EM);
        main.getElement().getStyle().setMarginBottom(1, Unit.EM);

        setWidget(main);
        return main;
    }

    public void setConversationType(ConversationType type) {

        switch (type) {
        case CHANGE:
            header.setHTML(HtmlUtils.h2(i18n.tr("Change Password")));
            get(proto().currentPassword()).setVisible(true);
            break;
        case RESET:
            header.setHTML(HtmlUtils.h2(i18n.tr("Create Password")));
            get(proto().currentPassword()).setVisible(false);
            break;
        }
    }
}