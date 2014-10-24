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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateValidator;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.PropertyVistaIntegratedInsurance;
import com.propertyvista.domain.tenant.lease.Tenant;

public class TenantInsuranceCertificateForm<E extends InsuranceCertificate<?>> extends CForm<E> {

    final static I18n i18n = I18n.get(TenantInsuranceCertificateForm.class);

    private BigDecimal minRequiredLiability;

    private final boolean displayTenantOwner;

    private FormPanel contentPanel;

    /**
     * @param displayTenantOwner
     *            display the owners name (if true then populated insurance certificated entity <b>must</b> have the tenant.customer.person() name)
     */
    public TenantInsuranceCertificateForm(Class<E> insuranceCertificateClass, boolean displayTenantOwner) {
        super(insuranceCertificateClass);
        this.minRequiredLiability = null;
        this.displayTenantOwner = displayTenantOwner;
    }

    @Override
    protected IsWidget createContent() {
        contentPanel = new FormPanel(this);

        if (displayTenantOwner) {
            contentPanel.append(Location.Dual, proto().insurancePolicy().tenant().customer().person().name(),
                    new NameEditor(i18n.tr("Owned By"), Tenant.class) {
                        @Override
                        public Key getLinkKey() {
                            return TenantInsuranceCertificateForm.this.getValue().insurancePolicy().tenant().getPrimaryKey();
                        }
                    });
        }
        contentPanel.append(Location.Left, proto().insuranceProvider()).decorate();
        contentPanel.append(Location.Left, proto().insuranceCertificateNumber()).decorate();
        contentPanel.append(Location.Left, proto().liabilityCoverage()).decorate().componentWidth(150);

        contentPanel.append(Location.Right, proto().inceptionDate()).decorate().componentWidth(100);
        contentPanel.append(Location.Right, proto().expiryDate()).decorate().componentWidth(100);

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