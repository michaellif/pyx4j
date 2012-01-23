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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;

public class NewPasswordForm extends CEntityDecoratableEditor<PasswordChangeRequest> {

    private static final I18n i18n = I18n.get(NewPasswordForm.class);

    public enum ConversationType {
        RESET, CHANGE
    }

    private HTML header;

    private final Command retreiveCommand;

    @Deprecated
    /**
     * Deprecated: no need for the "caption" arg.
     */
    public NewPasswordForm(String caption, Command retreiveCommand) {
        super(PasswordChangeRequest.class);
        this.retreiveCommand = retreiveCommand;
        setWidth("30em");
    }

    public NewPasswordForm(Command retreiveCommand) {
        super(PasswordChangeRequest.class);
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
        FormFlexPanel main = new FormFlexPanel();
        main.setWidth("100%");
        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().currentPassword())).componentWidth(15).labelWidth(15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().newPassword())).componentWidth(15).labelWidth(15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().newPasswordConfirm())).componentWidth(15).labelWidth(15).build());

        Button newPasswordButton = new Button(i18n.tr("Submit"));
        newPasswordButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        newPasswordButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                retreiveCommand.execute();
            }

        });

        main.setWidget(++row, 0, newPasswordButton);
        main.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        return main;
    }

    public void setConversationType(ConversationType type) {

        switch (type) {
        case CHANGE:
            get(proto().currentPassword()).setVisible(true);
            break;
        case RESET:
            get(proto().currentPassword()).setVisible(false);
            break;
        }
    }
}