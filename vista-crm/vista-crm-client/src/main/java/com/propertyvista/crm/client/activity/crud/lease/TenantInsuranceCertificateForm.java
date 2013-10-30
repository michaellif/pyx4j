/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 4, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease;

import java.math.BigDecimal;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.PropertyVistaIntegratedInsurance;
import com.propertyvista.domain.tenant.lease.Tenant;

public class TenantInsuranceCertificateForm<E extends InsuranceCertificate> extends CEntityForm<E> {

    final static I18n i18n = I18n.get(TenantInsuranceCertificateForm.class);

    public interface TenantOwnerClickHandler {

        void onTenantOwnerClicked(Tenant tenantId);

    }

    private BigDecimal minRequiredLiability;

    private final boolean displayTenantOwner;

    private final TenantOwnerClickHandler tenantOwnerClickHandler;

    private BasicFlexFormPanel contentPanel;

    /**
     * @param displayTenantOwner
     *            display the owners name (if true then populated insurance certificated entity <b>must</b> have the tenant.customer.person() name)
     * @param tenantOwnerClickHandler
     *            a handler for tenantOwner click (if not null will render tenant's name as a hyperlink that execs this handler on click)
     */
    public TenantInsuranceCertificateForm(Class<E> insuranceCertificateClass, boolean displayTenantOwner, TenantOwnerClickHandler tenantOwnerClickHandler) {
        super(insuranceCertificateClass);
        this.minRequiredLiability = null;
        this.displayTenantOwner = displayTenantOwner;
        this.tenantOwnerClickHandler = tenantOwnerClickHandler;
    }

    public TenantInsuranceCertificateForm(Class<E> insuranceCertificateClass) {
        this(insuranceCertificateClass, false, null);
    }

    @Override
    public IsWidget createContent() {
        contentPanel = new BasicFlexFormPanel(); // TODO the only reason its a field is to set a proper caption for the insurance certificate folder
        int row = -1;
        if (displayTenantOwner) {
            CEntityLabel<Customer> comp = new CEntityLabel<Customer>(i18n.tr("Tenant"));
            if (tenantOwnerClickHandler != null) {
                comp.setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        TenantInsuranceCertificateForm.this.tenantOwnerClickHandler.onTenantOwnerClicked(getValue().insurancePolicy().tenant()
                                .<Tenant> createIdentityStub());
                    }
                });
            }
            contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().insurancePolicy().tenant(), comp), 15, true).build());
        }

        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().insuranceProvider()), 10, true).build());
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().insuranceCertificateNumber()), 20, true).build());
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().liabilityCoverage()), 20, true).build());
        get(proto().liabilityCoverage()).addValueValidator(new EditableValueValidator<BigDecimal>() {
            @Override
            public ValidationError isValid(CComponent<BigDecimal> component, BigDecimal value) {
                if (TenantInsuranceCertificateForm.this.minRequiredLiability != null && value != null && value.compareTo(minRequiredLiability) < 0) {
                    return new ValidationError(component, i18n.tr("The minimum required liability is {0,number,#,##0.00}", minRequiredLiability));
                }
                return null;
            }
        });
        get(proto().liabilityCoverage()).addValueValidator(new EditableValueValidator<BigDecimal>() {
            @Override
            public ValidationError isValid(CComponent<BigDecimal> component, BigDecimal value) {
                if (value != null && value.compareTo(BigDecimal.ZERO) <= 0) {
                    return new ValidationError(component, i18n.tr("Please enter a positive value"));
                }
                return null;
            }
        });
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().inceptionDate()), 10, true).build());

        get(proto().inceptionDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null && value.compareTo(new LogicalDate()) > 0) {
                    return new ValidationError(component, i18n.tr("Please provide a date less than or equal of today"));
                }
                return null;
            }
        });
        contentPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().expiryDate()), 10, true).build());
        get(proto().expiryDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null && value.compareTo(new LogicalDate()) < 0) {
                    return new ValidationError(component, i18n.tr("Please provide a date greater than or equal of today"));
                }
                return null;
            }
        });

        contentPanel.setWidget(++row, 0, 2, inject(proto().certificateDocs(), new InsuranceCertificateDocFolder()));
        return contentPanel;
    }

    public void setMinRequiredLiability(BigDecimal minRequiredLiability) {
        this.minRequiredLiability = minRequiredLiability;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        setViewable(getValue() instanceof PropertyVistaIntegratedInsurance); // TODO this should not be controlled by the form itstelf
    }

}