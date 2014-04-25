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
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.GeneralInsurancePolicyDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

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

        addStep(createDetailsStep(), i18n.tr("General"));
    }

    public BasicFlexFormPanel createDetailsStep() {
        BasicFlexFormPanel contentPanel = new BasicFlexFormPanel();
        int row = -1;

        contentPanel.setWidget(++row, 0, inject(proto().certificate().insuranceProvider(), new FieldDecoratorBuilder(150).build()));
        contentPanel.setWidget(++row, 0, inject(proto().certificate().insuranceCertificateNumber(), new FieldDecoratorBuilder(150).build()));
        contentPanel.setWidget(++row, 0, inject(proto().certificate().liabilityCoverage(), new FieldDecoratorBuilder(150).build()));
        contentPanel.setWidget(++row, 0, inject(proto().certificate().inceptionDate(), new FieldDecoratorBuilder(150).build()));

        contentPanel.setWidget(++row, 0, inject(proto().certificate().expiryDate(), new FieldDecoratorBuilder(150).build()));

        contentPanel.setH1(++row, 0, 1, "Attach Scanned Insurance Certificate Documents");
        contentPanel.setWidget(++row, 0, inject(proto().certificate().certificateDocs(), new CertificateScanFolder()));

        return contentPanel;
    }

    @Override
    public void generateMockData() {
        get(proto().certificate().insuranceProvider()).setMockValue("Insurance Provider");
        get(proto().certificate().insuranceCertificateNumber()).setMockValue("ABC123");
        get(proto().certificate().liabilityCoverage()).setMockValue(new BigDecimal("222.33"));
        get(proto().certificate().inceptionDate()).setMockValue(new LogicalDate(System.currentTimeMillis() - (long) 182 * 24 * 60 * 60 * 1000));
        get(proto().certificate().expiryDate()).setMockValue(new LogicalDate(System.currentTimeMillis() + (long) 182 * 24 * 60 * 60 * 1000));
    }

    public void setMinRequiredLiability(BigDecimal minRequiredLiability) {
        this.minRequiredLiability = minRequiredLiability;
    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().certificate().liabilityCoverage()).addComponentValidator(new AbstractComponentValidator<BigDecimal>() {
            @Override
            public FieldValidationError isValid() {
                if (GeneralPolicyUploadWizard.this.minRequiredLiability != null && getComponent().getValue() != null
                        && getComponent().getValue().compareTo(minRequiredLiability) < 0) {
                    return new FieldValidationError(getComponent(), i18n.tr("The minimum required liability is {0,number,#,##0.00}", minRequiredLiability));
                }
                return null;
            }
        });
        get(proto().certificate().liabilityCoverage()).addComponentValidator(new AbstractComponentValidator<BigDecimal>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null && getComponent().getValue().compareTo(BigDecimal.ZERO) <= 0) {
                    return new FieldValidationError(getComponent(), i18n.tr("Please enter a positive value"));
                }
                return null;
            }
        });
        get(proto().certificate().inceptionDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null && getComponent().getValue().compareTo(new LogicalDate()) > 0) {
                    return new FieldValidationError(getComponent(), i18n.tr("Please provide a date less than or equal of today"));
                }
                return null;
            }
        });
        get(proto().certificate().expiryDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null && getComponent().getValue().compareTo(new LogicalDate()) < 0) {
                    return new FieldValidationError(getComponent(), i18n.tr("Please provide a date greater than or equal of today"));
                }
                return null;
            }
        });
        get(proto().certificate().certificateDocs()).addComponentValidator(new AbstractComponentValidator<List<InsuranceCertificateScan>>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null && getComponent().getValue().isEmpty()) {
                    return new FieldValidationError(getComponent(), i18n.tr("Please upload a scan of your insurance certificate"));
                }
                return null;
            }
        });
    }
}