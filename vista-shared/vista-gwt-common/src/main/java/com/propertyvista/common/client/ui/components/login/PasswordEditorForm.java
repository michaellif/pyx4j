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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;

public class PasswordEditorForm extends CEntityDecoratableEditor<PasswordChangeRequest> {

    private final static I18n i18n = I18n.get(PasswordEditorForm.class);

    public enum Type {
        CHANGE, RESET
    }

    private final Type type;

    public PasswordEditorForm(Type type) {
        super(PasswordChangeRequest.class);
        this.type = type;
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

        if (type.equals(Type.CHANGE)) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().currentPassword())).componentWidth(15).labelWidth(15).build());
            main.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        }
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().newPassword())).componentWidth(15).labelWidth(15).build());
        main.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().newPasswordConfirm())).componentWidth(15).labelWidth(15).build());
        get(proto().newPasswordConfirm()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationFailure isValid(CComponent<String, ?> component, String value) {
                if (value == null || !value.equals(get(proto().newPassword()).getValue())) {
                    return new ValidationFailure(i18n.tr("The passwords don't match."));
                } else {
                    return null;
                }
            }
        });
        main.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        if (type.equals(Type.CHANGE)) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().requireChangePasswordOnNextSignIn())).componentWidth(15).labelWidth(15).build());
            main.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        }

        return main;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        if (type.equals(Type.CHANGE)) {
            get(proto().currentPassword()).setVisible(isSelfAdmin());
            get(proto().requireChangePasswordOnNextSignIn()).setVisible(!isSelfAdmin());
        }

    }

    private boolean isSelfAdmin() {
        return getValue().userPk().isNull() || EqualsHelper.equals(getValue().userPk().getValue(), ClientContext.getUserVisit().getPrincipalPrimaryKey());
    }

}