/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.onlinepayment;

import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.wizard.IWizardView;
import com.pyx4j.site.client.ui.wizard.WizardForm;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.wizard.common.BusinessInformationForm;
import com.propertyvista.crm.client.ui.wizard.common.PersonalInformationForm;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;
import com.propertyvista.dto.OnlinePaymentSetupDTO;
import com.propertyvista.dto.OnlinePaymentSetupDTO.PropertyAccountInfo;

public class OnlinePaymentWizardForm extends WizardForm<OnlinePaymentSetupDTO> {

    private static final I18n i18n = I18n.get(OnlinePaymentWizardForm.class);

    public static class PropertyAccountInfoForm extends CEntityDecoratableForm<OnlinePaymentSetupDTO.PropertyAccountInfo> {

        public PropertyAccountInfoForm() {
            super(PropertyAccountInfo.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel panel = new FormFlexPanel();
            int row = -1;
            panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().averageMonthlyRent())).build());
            panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numberOfRentedUnits())).build());
            panel.setWidget(++row, 0, new HTML("&nbsp;"));
            panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().transitNumber())).build());
            int irow = row; // save the row that will hold the image with the cheque guide
            panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().institutionNumber())).build());
            panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().accountNumber())).build());

            panel.setWidget(irow, 1, new Image(VistaImages.INSTANCE.canadianChequeGuide()));
            panel.getFlexCellFormatter().setRowSpan(irow, 1, 3);
            return panel;
        }
    }

    public static class PropertyAccountInfoFolder extends VistaBoxFolder<OnlinePaymentSetupDTO.PropertyAccountInfo> {

        public PropertyAccountInfoFolder() {
            super(PropertyAccountInfo.class);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof PropertyAccountInfo) {
                return new PropertyAccountInfoForm();
            }
            return super.create(member);
        }

    }

    public OnlinePaymentWizardForm(IWizardView<OnlinePaymentSetupDTO> view) {
        super(OnlinePaymentSetupDTO.class, view);
        addStep(createPricingStep(i18n.tr("Pricing")));
        addStep(createBusinessInfoStep(i18n.tr("Business Information")));
        addStep(createPersonalInfoStep(i18n.tr("Personal Information")));
        addStep(createPropertyAndBankingStep(i18n.tr("Property and Banking")));
        addStep(createConfirmationStep(i18n.tr("Confirmation")));
    }

    public void setPaymentFees(AbstractPaymentFees paymentFees) {
        // TODO Auto-generated method stub
    }

    private FormFlexPanel createPricingStep(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = 0;
        main.setH1(++row, 0, 1, i18n.tr("Pricing Information for Online Payments"));
        main.setWidget(++row, 0, new OnlinePaymentPricingTab());
        return main;
    }

    private FormFlexPanel createBusinessInfoStep(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Business Information"));
        main.setWidget(++row, 0, inject(proto().businessInformation(), new BusinessInformationForm()));
        return main;
    }

    private FormFlexPanel createPersonalInfoStep(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Personal Information"));
        main.setWidget(++row, 0, inject(proto().personalInformation(), new PersonalInformationForm()));
        return main;
    }

    private FormFlexPanel createPropertyAndBankingStep(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Property and Banking"));
        main.setWidget(++row, 0, inject(proto().propertyAccounts(), new PropertyAccountInfoFolder()));
        get(proto().propertyAccounts()).addValueValidator(new EditableValueValidator<List<PropertyAccountInfo>>() {
            @Override
            public ValidationError isValid(CComponent<List<PropertyAccountInfo>, ?> component, List<PropertyAccountInfo> value) {
                if (value != null && value.size() < 1) {
                    return new ValidationError(component, i18n.tr("At least one property account is required"));
                } else {
                    return null;
                }
            }
        });
        return main;
    }

    private FormFlexPanel createConfirmationStep(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Confirmation"));
        return main;
    }

}
