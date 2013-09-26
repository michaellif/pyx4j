/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.otherprovider.views;

import java.math.BigDecimal;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.tenantinsurance.TenantInsuranceCertificateForm;
import com.propertyvista.domain.tenant.insurance.InsuranceGeneric;
import com.propertyvista.portal.client.ui.residents.EditImpl;

public class TenantInsuranceByOtherProviderUpdateViewImpl extends EditImpl<InsuranceGeneric> implements TenantInsuranceByOtherProviderUpdateView {

    private static final I18n i18n = I18n.get(TenantInsuranceByOtherProviderUpdateViewImpl.class);

    public TenantInsuranceByOtherProviderUpdateViewImpl() {
        setForm(new TenantInsuranceCertificateForm<InsuranceGeneric>(InsuranceGeneric.class));
    }

    @Override
    public void reportSaveSuccess() {
        MessageDialog.info(i18n.tr("Your insurance details were saved successfully"));
    }

    @Override
    public void setMinRequiredLiability(BigDecimal minRequiredLiability) {
        ((TenantInsuranceCertificateForm<InsuranceGeneric>) getForm()).setMinRequiredLiability(minRequiredLiability);
    }
}
