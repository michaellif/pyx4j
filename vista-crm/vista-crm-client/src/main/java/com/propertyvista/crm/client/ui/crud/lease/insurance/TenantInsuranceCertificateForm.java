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
package com.propertyvista.crm.client.ui.crud.lease.insurance;

import java.math.BigDecimal;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateValidator;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.PropertyVistaIntegratedInsurance;
import com.propertyvista.domain.tenant.lease.Tenant;

public class TenantInsuranceCertificateForm<E extends InsuranceCertificate<?>> extends CForm<E> {

    final static I18n i18n = I18n.get(TenantInsuranceCertificateForm.class);

    public interface TenantOwnerClickHandler {

        void onTenantOwnerClicked(Tenant tenantId);

    }

    private BigDecimal minRequiredLiability;

    private final boolean displayTenantOwner;

    private final TenantOwnerClickHandler tenantOwnerClickHandler;

    private FormPanel contentPanel;

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
    protected IsWidget createContent() {
        contentPanel = new FormPanel(this); // TODO the only reason its a field is to set a proper caption for the insurance certificate folder

        if (displayTenantOwner) {
            CEntityLabel<Customer> comp = new CEntityLabel<Customer>();

            if (tenantOwnerClickHandler != null) {
                comp.setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        TenantInsuranceCertificateForm.this.tenantOwnerClickHandler.onTenantOwnerClicked(getValue().insurancePolicy().tenant()
                                .<Tenant> createIdentityStub());
                    }
                });
            }
            contentPanel.append(Location.Dual, proto().insurancePolicy().tenant(), comp).decorate().componentWidth(150);
        }

        contentPanel.append(Location.Dual, proto().insuranceProvider()).decorate().componentWidth(100);
        contentPanel.append(Location.Dual, proto().insuranceCertificateNumber()).decorate().componentWidth(200);
        contentPanel.append(Location.Dual, proto().liabilityCoverage()).decorate().componentWidth(200);
        contentPanel.append(Location.Dual, proto().inceptionDate()).decorate().componentWidth(100);
        contentPanel.append(Location.Dual, proto().expiryDate()).decorate().componentWidth(100);
        contentPanel.append(Location.Dual, proto().certificateDocs(), new InsuranceCertificateDocFolder());
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

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().liabilityCoverage()).addComponentValidator(new AbstractComponentValidator<BigDecimal>() {
            @Override
            public BasicValidationError isValid() {
                if (TenantInsuranceCertificateForm.this.minRequiredLiability != null && getComponent().getValue() != null
                        && getComponent().getValue().compareTo(minRequiredLiability) < 0) {
                    return new BasicValidationError(getComponent(), i18n.tr("The minimum required liability is {0,number,#,##0.00}", minRequiredLiability));
                }
                return null;
            }
        });
        get(proto().liabilityCoverage()).addComponentValidator(new AbstractComponentValidator<BigDecimal>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() != null && getComponent().getValue().compareTo(BigDecimal.ZERO) <= 0) {
                    return new BasicValidationError(getComponent(), i18n.tr("Please enter a positive value"));
                }
                return null;
            }
        });

        get(proto().inceptionDate()).addComponentValidator(new PastDateValidator());
        get(proto().expiryDate()).addComponentValidator(new FutureDateIncludeTodayValidator());
    }
}