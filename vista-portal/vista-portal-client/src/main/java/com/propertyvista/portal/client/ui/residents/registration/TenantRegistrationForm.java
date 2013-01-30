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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

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
        userDataPanel.add(center(new WatermarkDecoratorBuilder<CTextFieldBase<?, ?>>((CTextFieldBase<?, ?>) inject(proto().firstName())).build()));
        userDataPanel.add(center(new WatermarkDecoratorBuilder<CTextFieldBase<?, ?>>((CTextFieldBase<?, ?>) inject(proto().lastName())).build()));
        userDataPanel.add(center(new WatermarkDecoratorBuilder<CTextFieldBase<?, ?>>((CTextFieldBase<?, ?>) inject(proto().secuirtyCode())).build()));
        userDataPanel.add(center(new WatermarkDecoratorBuilder<CTextFieldBase<?, ?>>((CTextFieldBase<?, ?>) inject(proto().email())).build()));
        userDataPanel.add(center(new WatermarkDecoratorBuilder<CTextFieldBase<?, ?>>((CTextFieldBase<?, ?>) inject(proto().password())).build()));
        userDataPanel.add(center(new WatermarkDecoratorBuilder<CTextFieldBase<?, ?>>((CTextFieldBase<?, ?>) inject(proto().passwordConfirm())).build()));

        contentPanel.add(center(userDataPanel));

        get(proto().passwordConfirm()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String, ?> component, String value) {
                if (!get(proto().password()).getValue().equals(value)) {
                    new ValidationError(component, i18n.tr(""));
                }
                return null;
            }
        });
        get(proto().password()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().password())));

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
