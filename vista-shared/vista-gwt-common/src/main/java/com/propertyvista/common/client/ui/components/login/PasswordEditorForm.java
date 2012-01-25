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
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;

public abstract class PasswordEditorForm extends CEntityDecoratableEditor<PasswordChangeRequest> {

    private static final I18n i18n = I18n.get(PasswordEditorForm.class);

    public enum Type {
        CHANGE, RESET
    }

    private final Type type;

    private final HTML userNameLabel;

    public PasswordEditorForm(Type type) {
        super(PasswordChangeRequest.class);
        setWidth("30em");
        this.type = type;
        this.userNameLabel = new HTML();
    }

    protected abstract void onSubmitPasswordChange();

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

        if (type.equals(Type.CHANGE)) {
            main.setWidget(++row, 0, userNameLabel);
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().currentPassword())).componentWidth(15).labelWidth(15).build());
        }
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().newPassword())).componentWidth(15).labelWidth(15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().newPasswordConfirm())).componentWidth(15).labelWidth(15).build());
        if (type.equals(Type.CHANGE)) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().requireChangePasswordOnNextSignIn())).componentWidth(15).labelWidth(15).build());
        }

        Button submitButton = new Button(i18n.tr("Submit"));
        submitButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        submitButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onSubmitPasswordChange();
            }
        });

        main.setWidget(++row, 0, submitButton);
        main.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        main.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingTop(1, Unit.EM);

        return main;
    }

    public void setUserName(String userName) {
        userNameLabel.setHTML(new SafeHtmlBuilder().appendEscaped(i18n.tr("Change password for {0}", userName)).toSafeHtml());
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        if (type.equals(Type.CHANGE)) {
            get(proto().currentPassword()).setVisible(isSelfAdmin());
            get(proto().requireChangePasswordOnNextSignIn()).setVisible(!isSelfAdmin());
            userNameLabel.setVisible(!isSelfAdmin());
        }

    }

    private boolean isSelfAdmin() {
        return getValue().userPk().isNull() || EqualsHelper.equals(getValue().userPk().getValue(), ClientContext.getUserVisit().getPrincipalPrimaryKey());
    }

}