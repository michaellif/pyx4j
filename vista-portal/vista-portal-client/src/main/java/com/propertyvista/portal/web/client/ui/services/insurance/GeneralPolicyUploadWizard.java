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
package com.propertyvista.portal.web.client.ui.services.insurance;

import java.math.BigDecimal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.gwt.shared.FileURLBuilder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.GeneralInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.InsuranceCertificateScanUploadService;
import com.propertyvista.portal.web.client.ui.CPortalEntityWizard;
import com.propertyvista.portal.web.client.ui.util.decorators.FormWidgetDecoratorBuilder;

public class GeneralPolicyUploadWizard extends CPortalEntityWizard<GeneralInsurancePolicyDTO> {

    private final static I18n i18n = I18n.get(GeneralPolicyUploadWizard.class);

    public interface TenantOwnerClickHandler {

        void onTenantOwnerClicked(Tenant tenantId);

    }

    private BigDecimal minRequiredLiability;

    private final boolean displayTenantOwner;

    private final TenantOwnerClickHandler tenantOwnerClickHandler;

    /**
     * @param displayTenantOwner
     *            display the owners name (if true then populated insurance certificated entity <b>must</b> have the tenant.customer.person() name)
     * @param tenantOwnerClickHandler
     *            a handler for tenantOwner click (if not null will render tenant's name as a hyperlink that execs this handler on click)
     */
    public GeneralPolicyUploadWizard(GeneralPolicyUploadWizardView view, boolean displayTenantOwner, TenantOwnerClickHandler tenantOwnerClickHandler) {
        super(GeneralInsurancePolicyDTO.class, view, i18n.tr("Insurance Certificate"), i18n.tr("Submit"), ThemeColor.contrast3);
        this.minRequiredLiability = null;
        this.displayTenantOwner = displayTenantOwner;
        this.tenantOwnerClickHandler = tenantOwnerClickHandler;

        addStep(createDetailsStep());
    }

    public GeneralPolicyUploadWizard(GeneralPolicyUploadWizardView view) {
        this(view, false, null);

    }

    public BasicFlexFormPanel createDetailsStep() {
        BasicFlexFormPanel contentPanel = new BasicFlexFormPanel(); // TODO the only reason its a field is to set a proper caption for the insurance certificate folder
        int row = -1;
        if (displayTenantOwner) {
            CEntityLabel<Customer> comp = new CEntityLabel<Customer>(i18n.tr("Tenant"));
            if (tenantOwnerClickHandler != null) {
                comp.setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        //TODO     UploadSertificateWizard.this.tenantOwnerClickHandler.onTenantOwnerClicked(getValue().tenant().<Tenant> createIdentityStub());
                    }
                });
            }
            //TODO    contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().tenant(), comp), 150).build());
        }

        contentPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().certificate().insuranceProvider()), 150).build());
        contentPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().certificate().insuranceCertificateNumber()), 150).build());
        contentPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().certificate().liabilityCoverage()), 150).build());
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
        contentPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().certificate().inceptionDate()), 150).build());

        get(proto().certificate().inceptionDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null && value.compareTo(new LogicalDate()) > 0) {
                    return new ValidationError(component, i18n.tr("Please provide a date less than or equal of today"));
                }
                return null;
            }
        });
        contentPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().certificate().expiryDate()), 150).build());
        get(proto().certificate().expiryDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null && value.compareTo(new LogicalDate()) < 0) {
                    return new ValidationError(component, i18n.tr("Please provide a date greater than or equal of today"));
                }
                return null;
            }
        });

        contentPanel.setWidget(
                ++row,
                0,
                new FormWidgetDecoratorBuilder(inject(proto().certificate().certificateScan(),
                        new CFile<InsuranceCertificateScan>(GWT.<InsuranceCertificateScanUploadService> create(InsuranceCertificateScanUploadService.class),
                                new FileURLBuilder<InsuranceCertificateScan>() {

                                    @Override
                                    public String getUrl(InsuranceCertificateScan file) {
                                        return MediaUtils.createInsuranceCertificateScanUrl(file);
                                    }
                                })), 200).build());

        get(proto().certificate().certificateScan()).setNote(i18n.tr("Attach Scanned Insurance Certificate (picture, PDF or archive file)"));

        return contentPanel;
    }

    public void setMinRequiredLiability(BigDecimal minRequiredLiability) {
        this.minRequiredLiability = minRequiredLiability;
    }

}