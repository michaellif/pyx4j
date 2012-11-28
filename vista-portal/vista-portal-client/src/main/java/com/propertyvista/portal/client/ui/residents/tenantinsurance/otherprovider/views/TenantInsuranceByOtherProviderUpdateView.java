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

import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.portal.client.ui.residents.View;

public interface TenantInsuranceByOtherProviderUpdateView extends View<InsuranceCertificate> {

    interface Presenter extends View.Presenter<InsuranceCertificate> {

    }

    /** can be <code>null</code> if unlimited */
    void setMinRequiredLiability(BigDecimal minRequiredLiability);

    void reportSaveSuccess();

}
