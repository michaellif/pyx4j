/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-22
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.tools.legal.l1;

import java.math.BigDecimal;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.wizard.IPrimeWizardView;
import com.pyx4j.site.client.backoffice.ui.prime.wizard.PrimeWizardForm;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.tools.legal.l1.forms.L1LandlordsContactInfoFolder;
import com.propertyvista.crm.client.ui.tools.legal.l1.forms.L1OwedNsfChargesForm;
import com.propertyvista.crm.client.ui.tools.legal.l1.forms.L1ReasonForApplicationForm;
import com.propertyvista.crm.client.ui.tools.legal.l1.forms.L1ScheduleAndPaymentForm;
import com.propertyvista.crm.client.ui.tools.legal.l1.forms.L1SignatureDataForm;
import com.propertyvista.crm.client.ui.tools.legal.l1.forms.L1TenantContactInfoForm;
import com.propertyvista.crm.client.ui.tools.legal.l1.forms.L1TenantInfoFolder;
import com.propertyvista.crm.client.ui.tools.legal.l1.forms.LtbAgentContactInfoForm;
import com.propertyvista.crm.client.ui.tools.legal.l1.forms.LtbOwedRentForm;
import com.propertyvista.crm.client.ui.tools.legal.l1.forms.LtbRentalUnitAddressForm;
import com.propertyvista.crm.rpc.dto.legal.l1.L1FormDataReviewWizardDTO;

public class L1FormDataReviewWizardForm extends PrimeWizardForm<L1FormDataReviewWizardDTO> {

    private static final I18n i18n = I18n.get(L1FormDataReviewWizardForm.class);

    private Button editToggleButton;

    public L1FormDataReviewWizardForm(IPrimeWizardView<? extends IEntity> view) {
        super(L1FormDataReviewWizardDTO.class, view);
        addStep(createRentalUnitAddressStep().asWidget(), i18n.tr("Rental Unit Address"));
        addStep(createRelatedFilesStep().asWidget(), i18n.tr("Related Files"));
        addStep(createTenantsStep().asWidget(), i18n.tr("Tenants"));
        addStep(createReasonForApplicationStep().asWidget(), i18n.tr("Reason for Application"));
        addStep(createDetailsOfLandlordsClaimStep().asWidget(), i18n.tr("Details of Landlord's Claim"));
        addStep(createLandlordContactInfoStep().asWidget(), i18n.tr("Landlord(s) Details"));
        addStep(createAgentsSignatureStep().asWidget(), i18n.tr("Signature"));
        addStep(createPaymentAndSchedulingStep().asWidget(), i18n.tr("Payment and Scheduling"));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        setRentalUnitAddressEdit(false);
    }

    private void toggleRentalUnitAddressEdit() {
        setRentalUnitAddressEdit(!get(proto().formData().rentalUnitInfo()).isEditable());
    }

    private void setRentalUnitAddressEdit(boolean isEditable) {
        get(proto().formData().rentalUnitInfo()).setEditable(isEditable);
        get(proto().formData().rentalUnitInfo()).setViewable(!isEditable);
        editToggleButton.setTextLabel(isEditable ? i18n.tr("Accept") : i18n.tr("Change"));
    }

    private FormPanel createRentalUnitAddressStep() {
        FormPanel formPanel = new FormPanel(this);
        editToggleButton = new Button("");
        editToggleButton.setCommand(new Command() {
            @Override
            public void execute() {
                toggleRentalUnitAddressEdit();
            }
        });
        formPanel.append(Location.Left, editToggleButton);
        formPanel.append(Location.Left, proto().formData().rentalUnitInfo(), new LtbRentalUnitAddressForm());
        return formPanel;
    }

    private FormPanel createRelatedFilesStep() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().formData().relatedApplicationFileNumber1()).decorate().labelWidth("20em");
        formPanel.append(Location.Left, proto().formData().relatedApplicationFileNumber2()).decorate().labelWidth("20em");
        return formPanel;
    }

    private FormPanel createTenantsStep() {
        FormPanel panel = new FormPanel(this);
        panel.h1(i18n.tr("Tenants"));
        panel.append(Location.Dual, proto().formData().tenants(), new L1TenantInfoFolder());
        panel.h1(i18n.tr("Contact Information (fill in mailing address only if different from rental unit address from step 1)"));
        panel.append(Location.Dual, proto().formData().tenantContactInfo(), new L1TenantContactInfoForm());
        return panel;
    }

    private FormPanel createReasonForApplicationStep() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().formData().reasonForApplication(), new L1ReasonForApplicationForm());
        return formPanel;
    }

    private FormPanel createDetailsOfLandlordsClaimStep() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.h1(i18n.tr(i18n.tr("Rent Owing")));
        formPanel.append(Location.Dual, proto().formData().owedRent(), new LtbOwedRentForm() {
            @Override
            public void onTotalUpdated() {
                updateTotalOwed();
            }
        });
        formPanel.h1(i18n.tr(i18n.tr("NSF Check Charges")));
        formPanel.append(Location.Dual, proto().formData().owedNsfCharges(), new L1OwedNsfChargesForm() {
            @Override
            public void onTotalUpdated() {
                updateTotalOwed();
            }
        });
        formPanel.h1(i18n.tr(i18n.tr("Summary")));
        formPanel.append(Location.Dual, proto().formData().owedSummary().applicationFillingFee()).decorate();
        get(proto().formData().owedSummary().applicationFillingFee()).setViewable(true);
        formPanel.append(Location.Dual, proto().formData().owedSummary().total()).decorate();
        get(proto().formData().owedSummary().total()).setViewable(true);

        return formPanel;
    }

    private FormPanel createLandlordContactInfoStep() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().formData().landlordsContactInfos(), new L1LandlordsContactInfoFolder());
        return formPanel;
    }

    private FormPanel createAgentsSignatureStep() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.h1(i18n
                .tr("If the person who signs this application is an agent or an officer of a corporation, you must provide the following information:"));
        formPanel.append(Location.Dual, proto().formData().agentContactInfo(), new LtbAgentContactInfoForm());
        formPanel.h1(i18n.tr("Signature:"));
        formPanel.append(Location.Dual, proto().formData().signatureData(), new L1SignatureDataForm());
        return formPanel;
    }

    private FormPanel createPaymentAndSchedulingStep() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().formData().scheduleAndPayment(), new L1ScheduleAndPaymentForm());
        return formPanel;
    }

    private void updateTotalOwed() {
        BigDecimal rent = getValue().formData().owedRent().totalRentOwing().getValue(BigDecimal.ZERO);
        BigDecimal nsf = getValue().formData().owedNsfCharges().nsfTotalChargeOwed().getValue(BigDecimal.ZERO);
        BigDecimal applicationFillingFee = getValue().formData().owedSummary().applicationFillingFee().getValue();

        BigDecimal total = rent.add(nsf).add(applicationFillingFee);
        get(proto().formData().owedSummary().total()).setValue(total);
    }
}
