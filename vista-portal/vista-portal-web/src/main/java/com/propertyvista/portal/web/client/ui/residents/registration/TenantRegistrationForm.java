/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.registration;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationDTO;
import com.propertyvista.portal.rpc.shared.EntityValidationException;
import com.propertyvista.portal.rpc.shared.EntityValidationException.MemberValidationError;
import com.propertyvista.portal.web.client.themes.LandingPagesTheme;
import com.propertyvista.portal.web.client.ui.util.decorators.LoginDecoratorBuilder;

public class TenantRegistrationForm extends CEntityDecoratableForm<SelfRegistrationDTO> {

    private static final I18n i18n = I18n.get(TenantRegistrationForm.class);

    private CBuildingSuggestBox buildingSelector;

    private EntityValidationException entityValidationError;

    public TenantRegistrationForm() {
        super(SelfRegistrationDTO.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IsWidget createContent() {
        FlowPanel contentPanel = new FlowPanel();

        FlowPanel userDataPanel = new FlowPanel();
        userDataPanel.getElement().getStyle().setMarginTop(20, Unit.PX);

        buildingSelector = ((CBuildingSuggestBox) inject(proto().building(), new CBuildingSuggestBox()));
        buildingSelector.setWatermark(i18n.tr("Your building's address"));
        buildingSelector.setNote(i18n.tr("Search by typing your building's street, postal code, province etc..."));
        userDataPanel.add(center(new LoginDecoratorBuilder(buildingSelector, false).customLabel(i18n.tr("Select your building")).build()));

        Label userDataLabel = new Label();
        userDataLabel.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        userDataLabel.getElement().getStyle().setDisplay(Display.INLINE);
        userDataLabel.setText(i18n.tr("Enter your first, middle and last name the way it is spelled in your lease agreement"));
        userDataPanel.getElement().getStyle().setMarginBottom(20, Unit.PX);

        userDataPanel.add(center(userDataLabel));
        userDataPanel.add(center(new LoginDecoratorBuilder(inject(proto().firstName()), true).build()));
        userDataPanel.add(center(new LoginDecoratorBuilder(inject(proto().middleName()), true).build()));
        userDataPanel.add(center(new LoginDecoratorBuilder(inject(proto().lastName()), true).build()));

        CTextFieldBase<?, ?> emailField;
        Widget w = center(new LoginDecoratorBuilder(emailField = (CTextFieldBase<?, ?>) inject(proto().email()), true).build());
        w.getElement().getStyle().setMarginTop(20, Unit.PX);
        userDataPanel.add(w);
        emailField.setNote(i18n.tr("Please note: your email will be your user name"));

        CTextFieldBase<?, ?> securityCodeField;
        userDataPanel.add(center(new LoginDecoratorBuilder(securityCodeField = (CTextFieldBase<?, ?>) inject(proto().securityCode()), true).build()));
        securityCodeField.setNote(i18n.tr("The Security Code is a secure identifier that is provided by your Property Manager specifically for you."));
        securityCodeField
                .setTooltip(i18n
                        .tr("You should have received Security Code by mail. Don't have a Security Code? To get your own unique access code, please contact the Property Manager directly."));

        contentPanel.add(center(userDataPanel));

        FlowPanel definePasswordPanel = new FlowPanel();
        definePasswordPanel.getElement().getStyle().setMarginTop(20, Unit.PX);

        definePasswordPanel.add(center(new LoginDecoratorBuilder(inject(proto().password()), false).customLabel(i18n.tr("Set up your password")).build()));

        definePasswordPanel.add(center(new LoginDecoratorBuilder(inject(proto().passwordConfirm()), false).build()));

        contentPanel.add(center(definePasswordPanel));

        get(proto().passwordConfirm()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String, ?> component, String confirmPassword) {
                String password = (get(proto().password())).getValue();
                if ((password == null & confirmPassword != null) | (password != null & confirmPassword == null) || (!password.equals(confirmPassword))) {
                    return new ValidationError(component, i18n.tr("Passwords don't match"));
                }
                return null;
            }
        });
        get(proto().password()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().passwordConfirm())));

        for (String memberName : proto().getEntityMeta().getMemberNames()) {
            final IObject<?> member = proto().getMember(memberName);
            CComponent<?, ?> boundMember = null;
            try {
                boundMember = get(member);
            } catch (Throwable e) {
                // just skip the unbound member
            }
            if (boundMember != null) {
                boundMember.addValueValidator(new EditableValueValidator() {
                    @Override
                    public ValidationError isValid(CComponent component, Object value) {
                        if (TenantRegistrationForm.this.entityValidationError != null) {
                            for (MemberValidationError memberValidationError : TenantRegistrationForm.this.entityValidationError.getErrors()) {
                                if (memberValidationError.getMember().getPath().equals(member.getPath())) {
                                    return new ValidationError(component, memberValidationError.getMessage());
                                }
                            }
                        }
                        return null;
                    }
                });
            }
        }

        return contentPanel;
    }

    public void setBuildingOptions(List<SelfRegistrationBuildingDTO> buildings) {
        buildingSelector.setOptions(buildings);
    }

    public void setEntityValidationError(EntityValidationException caught) {
        this.entityValidationError = caught;
        setUnconditionalValidationErrorRendering(true);
        revalidate();
    }

    private Widget center(IsWidget w) {
        w.asWidget().addStyleName(LandingPagesTheme.StyleName.LandingInputField.name());
        return w.asWidget();
    }

}
