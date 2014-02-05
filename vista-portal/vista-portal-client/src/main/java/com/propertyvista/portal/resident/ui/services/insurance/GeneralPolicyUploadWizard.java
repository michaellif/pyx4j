/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import java.math.BigDecimal;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.GeneralInsurancePolicyDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class GeneralPolicyUploadWizard extends CPortalEntityWizard<GeneralInsurancePolicyDTO> {

    private final static I18n i18n = I18n.get(GeneralPolicyUploadWizard.class);

    public interface TenantOwnerClickHandler {

        void onTenantOwnerClicked(Tenant tenantId);

    }

    private BigDecimal minRequiredLiability;

    /**
     * @param displayTenantOwner
     *            display the owners name (if true then populated insurance certificated entity <b>must</b> have the tenant.customer.person() name)
     * @param tenantOwnerClickHandler
     *            a handler for tenantOwner click (if not null will render tenant's name as a hyperlink that execs this handler on click)
     */
    public GeneralPolicyUploadWizard(GeneralPolicyUploadWizardView view) {
        super(GeneralInsurancePolicyDTO.class, view, i18n.tr("Insurance Certificate"), i18n.tr("Submit"), ThemeColor.contrast3);
        this.minRequiredLiability = null;

        addStep(createDetailsStep());
    }

    public BasicFlexFormPanel createDetailsStep() {
        BasicFlexFormPanel contentPanel = new BasicFlexFormPanel();
        int row = -1;

        contentPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().certificate().insuranceProvider()), 150).mockValue("Insurance Provider")
                .build());
        contentPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().certificate().insuranceCertificateNumber()), 150).mockValue("ABC123")
                .build());
        contentPanel.setWidget(++row, 0,
                new FormWidgetDecoratorBuilder(inject(proto().certificate().liabilityCoverage()), 150).mockValue(new BigDecimal("222.33")).build());
        contentPanel.setWidget(
                ++row,
                0,
                new FormWidgetDecoratorBuilder(inject(proto().certificate().inceptionDate()), 150).mockValue(
                        new LogicalDate(System.currentTimeMillis() - (long) 182 * 24 * 60 * 60 * 1000)).build());

        contentPanel.setWidget(
                ++row,
                0,
                new FormWidgetDecoratorBuilder(inject(proto().certificate().expiryDate()), 150).mockValue(
                        new LogicalDate(System.currentTimeMillis() + (long) 182 * 24 * 60 * 60 * 1000)).build());

        contentPanel.setH1(++row, 0, 1, "Attach Scanned Insurance Certificate Documents");
        contentPanel.setWidget(++row, 0, inject(proto().certificate().certificateDocs(), new CertificateScanFolder()));

        return contentPanel;
    }

    public void setMinRequiredLiability(BigDecimal minRequiredLiability) {
        this.minRequiredLiability = minRequiredLiability;
    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().certificate().liabilityCoverage()).addValueValidator(new EditableValueValidator<BigDecimal>() {
            @Override
            public ValidationError isValid(CComponent<BigDecimal> component, BigDecimal value) {
                if (GeneralPolicyUploadWizard.this.minRequiredLiability != null && value != null && value.compareTo(minRequiredLiability) < 0) {
                    return new ValidationError(component, i18n.tr("The minimum required liability is {0,number,#,##0.00}", minRequiredLiability));
                }
                return null;
            }
        });
        get(proto().certificate().liabilityCoverage()).addValueValidator(new EditableValueValidator<BigDecimal>() {
            @Override
            public ValidationError isValid(CComponent<BigDecimal> component, BigDecimal value) {
                if (value != null && value.compareTo(BigDecimal.ZERO) <= 0) {
                    return new ValidationError(component, i18n.tr("Please enter a positive value"));
                }
                return null;
            }
        });
        get(proto().certificate().inceptionDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null && value.compareTo(new LogicalDate()) > 0) {
                    return new ValidationError(component, i18n.tr("Please provide a date less than or equal of today"));
                }
                return null;
            }
        });
        get(proto().certificate().expiryDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null && value.compareTo(new LogicalDate()) < 0) {
                    return new ValidationError(component, i18n.tr("Please provide a date greater than or equal of today"));
                }
                return null;
            }
        });
        get(proto().certificate().certificateDocs()).addValueValidator(new EditableValueValidator<List<InsuranceCertificateScan>>() {
            @Override
            public ValidationError isValid(CComponent<List<InsuranceCertificateScan>> component, List<InsuranceCertificateScan> value) {
                if (value != null && value.isEmpty()) {
                    return new ValidationError(component, i18n.tr("Please upload a scan of your insurance certificate"));
                }
                return null;
            }
        });
    }
}