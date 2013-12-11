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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.l1;

import java.math.BigDecimal;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;
import com.pyx4j.site.client.ui.prime.wizard.WizardForm;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
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

public class L1FormDataReviewWizardForm extends WizardForm<L1FormDataReviewWizardDTO> {

    private static final I18n i18n = I18n.get(L1FormDataReviewWizardForm.class);

    private Button editToggleButton;

    public L1FormDataReviewWizardForm(IWizard<? extends IEntity> view) {
        super(L1FormDataReviewWizardDTO.class, view);
        addStep(createRentalUnitAddressStep());
        addStep(createRelatedFilesStep());
        addStep(createTenantsStep());
        addStep(createReasonForApplicationStep());
        addStep(createDetailsOfLandlordsClaimStep());
        addStep(createLandlordContactInfoStep());
        addStep(createAgentsSignatureStep());
        addStep(createPaymentAndSchedulingStep());
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

    private TwoColumnFlexFormPanel createRentalUnitAddressStep() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Rental Unit Address"));
        int row = -1;
        editToggleButton = new Button("");
        editToggleButton.setCommand(new Command() {
            @Override
            public void execute() {
                toggleRentalUnitAddressEdit();
            }
        });
        panel.setWidget(++row, 0, editToggleButton);
        panel.setWidget(++row, 0, inject(proto().formData().rentalUnitInfo(), new LtbRentalUnitAddressForm()));
        return panel;
    }

    private TwoColumnFlexFormPanel createRelatedFilesStep() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Related Files"));
        int row = -1;
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().formData().relatedApplicationFileNumber1())).labelWidth("20em").build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().formData().relatedApplicationFileNumber2())).labelWidth("20em").build());
        return panel;
    }

    private TwoColumnFlexFormPanel createTenantsStep() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Tenants"));
        int row = -1;
        panel.setH1(++row, 0, 2, i18n.tr("Tenants"));
        panel.setWidget(++row, 0, 2, inject(proto().formData().tenants(), new L1TenantInfoFolder()));
        panel.setH1(++row, 0, 2, i18n.tr("Contact Information (fill in mailing address only if different from rental unit address from step 1)"));
        panel.setWidget(++row, 0, 2, inject(proto().formData().tenantContactInfo(), new L1TenantContactInfoForm()));
        return panel;
    }

    private TwoColumnFlexFormPanel createReasonForApplicationStep() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Reason for Application"));
        int row = -1;
        panel.setWidget(++row, 0, inject(proto().formData().reasonForApplication(), new L1ReasonForApplicationForm()));
        return panel;
    }

    private TwoColumnFlexFormPanel createDetailsOfLandlordsClaimStep() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Details of Landlord's Claim"));
        int row = -1;
        panel.setH1(++row, 0, 2, i18n.tr(i18n.tr("Rent Owing")));
        panel.setWidget(++row, 0, 2, inject(proto().formData().owedRent(), new LtbOwedRentForm() {
            @Override
            public void onTotalUpdated() {
                updateTotalOwed();
            }
        }));
        panel.setH1(++row, 0, 2, i18n.tr(i18n.tr("NSF Check Charges")));
        panel.setWidget(++row, 0, 2, inject(proto().formData().owedNsfCharges(), new L1OwedNsfChargesForm() {
            @Override
            public void onTotalUpdated() {
                updateTotalOwed();
            }
        }));
        panel.setH1(++row, 0, 2, i18n.tr(i18n.tr("Summary")));
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().formData().owedSummary().applicationFillingFee())).build());
        get(proto().formData().owedSummary().applicationFillingFee()).setViewable(true);
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().formData().owedSummary().total())).build());
        get(proto().formData().owedSummary().total()).setViewable(true);

        return panel;
    }

    private TwoColumnFlexFormPanel createLandlordContactInfoStep() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Landlord(s) Details"));
        int row = -1;
        panel.setWidget(++row, 0, 2, inject(proto().formData().landlordsContactInfos(), new L1LandlordsContactInfoFolder()));
        return panel;
    }

    private TwoColumnFlexFormPanel createAgentsSignatureStep() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Signature"));
        int row = -1;
        panel.setH1(++row, 0, 2,
                i18n.tr("If the person who signs this application is an agent or an officer of a corporation, you must provide the following information:"));
        panel.setWidget(++row, 0, 2, inject(proto().formData().agentContactInfo(), new LtbAgentContactInfoForm()));
        panel.setH1(++row, 0, 2, i18n.tr("Signature:"));
        panel.setWidget(++row, 0, 2, inject(proto().formData().signatureData(), new L1SignatureDataForm()));
        return panel;
    }

    private TwoColumnFlexFormPanel createPaymentAndSchedulingStep() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Payment and Scheduling"));
        int row = -1;
        panel.setWidget(++row, 0, 2, inject(proto().formData().scheduleAndPayment(), new L1ScheduleAndPaymentForm()));
        return panel;
    }

    private void updateTotalOwed() {
        BigDecimal rent = getValue().formData().owedRent().totalRentOwing().getValue(BigDecimal.ZERO);
        BigDecimal nsf = getValue().formData().owedNsfCharges().nsfTotalChargeOwed().getValue(BigDecimal.ZERO);
        BigDecimal applicationFillingFee = getValue().formData().owedSummary().applicationFillingFee().getValue();

        BigDecimal total = rent.add(nsf).add(applicationFillingFee);
        get(proto().formData().owedSummary().total()).setValue(total);
    }
}
