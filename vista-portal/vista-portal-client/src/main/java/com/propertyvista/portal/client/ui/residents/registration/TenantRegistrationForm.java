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
package com.propertyvista.portal.client.ui.residents.registration;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.client.themes.LandingPagesTheme;
import com.propertyvista.portal.client.ui.residents.decorators.WatermarkDecoratorBuilder;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationDTO;

public class TenantRegistrationForm extends CEntityDecoratableForm<SelfRegistrationDTO> {

    private static final I18n i18n = I18n.get(TenantRegistrationForm.class);

    private CSimpleEntityComboBox<SelfRegistrationBuildingDTO> buildingComboBox;

    public TenantRegistrationForm() {
        super(SelfRegistrationDTO.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IsWidget createContent() {
        FlowPanel contentPanel = new FlowPanel();

        buildingComboBox = ((CSimpleEntityComboBox<SelfRegistrationBuildingDTO>) inject(proto().building(),
                new CSimpleEntityComboBox<SelfRegistrationBuildingDTO>()));

        FlowPanel selectBuildingPanel = new FlowPanel();

        Label buildingFieldLabel = new Label();
        buildingFieldLabel.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        buildingFieldLabel.setText(i18n.tr("Select your building:"));
        selectBuildingPanel.add(buildingFieldLabel);
        selectBuildingPanel.add(new DecoratorBuilder(buildingComboBox).componentWidth(20).labelWidth(0).customLabel("").useLabelSemicolon(false)
                .mandatoryMarker(false).build());

        contentPanel.add(center(selectBuildingPanel));

        FlowPanel userDataPanel = new FlowPanel();
        userDataPanel.getElement().getStyle().setMarginTop(20, Unit.PX);

        FlowPanel userDataLabelHolder = new FlowPanel();
        SimplePanel tooltipImageHolder = new SimplePanel();
        tooltipImageHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        tooltipImageHolder.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        Image tooltipImage = new Image(ImageFactory.getImages().formTooltipInfo());
        tooltipImage
                .setTitle(i18n
                        .tr("The Security Code is a secure identifier that is provided by your Property Manager specifically for you. You should have received this code by mail. Don't have a Security Code? To get your own unique access code, please contact the Property Manager directly"));

        tooltipImageHolder.add(tooltipImage);

        Label userDataLabel = new Label();
        userDataLabel.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        userDataLabel.getElement().getStyle().setDisplay(Display.INLINE);
        userDataLabel.setText(i18n.tr("Please fill in your name, email address, and the security code:"));

        userDataLabelHolder.add(userDataLabel);
        userDataLabelHolder.add(tooltipImageHolder);

        userDataPanel.add(center(userDataLabelHolder));
        userDataPanel.add(center(new WatermarkDecoratorBuilder<CTextFieldBase<?, ?>>((CTextFieldBase<?, ?>) inject(proto().firstName())).build()));
        userDataPanel.add(center(new WatermarkDecoratorBuilder<CTextFieldBase<?, ?>>((CTextFieldBase<?, ?>) inject(proto().lastName())).build()));
        userDataPanel.add(center(new WatermarkDecoratorBuilder<CTextFieldBase<?, ?>>((CTextFieldBase<?, ?>) inject(proto().email())).build()));
        userDataPanel.add(center(new WatermarkDecoratorBuilder<CTextFieldBase<?, ?>>((CTextFieldBase<?, ?>) inject(proto().secuirtyCode())).build()));
        contentPanel.add(center(userDataPanel));

        FlowPanel definePasswordPanel = new FlowPanel();
        definePasswordPanel.getElement().getStyle().setMarginTop(20, Unit.PX);
        Label definePasswordLabel = new Label();
        definePasswordLabel.setStyleName(DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());
        definePasswordLabel.setText(i18n.tr("Set up your password:"));
        definePasswordPanel.add(definePasswordLabel);
        definePasswordPanel.add(center(new WatermarkDecoratorBuilder<CTextFieldBase<?, ?>>((CTextFieldBase<?, ?>) inject(proto().password())).watermark("")
                .build()));
        definePasswordPanel.add(center(new WatermarkDecoratorBuilder<CTextFieldBase<?, ?>>((CTextFieldBase<?, ?>) inject(proto().passwordConfirm())).watermark(
                "").build()));

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

        return contentPanel;
    }

    public void setBuildingOptions(List<SelfRegistrationBuildingDTO> buildings) {
        buildingComboBox.setOptions(buildings);
    }

    private Widget center(IsWidget w) {
        w.asWidget().addStyleName(LandingPagesTheme.StyleName.LandingInputField.name());
        return w.asWidget();
    }

}
