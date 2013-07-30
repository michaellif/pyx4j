/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.portal.web.client.ui.residents.Edit;

public interface TenantSureCreditCardUpdateView extends Edit<InsurancePaymentMethod> {

    interface Presenter extends Edit.Presenter<InsurancePaymentMethod> {

        void onTenantAddressRequested();

        void onCCUpdateSuccessAcknowledged();

    }

    void reportCCUpdateSuccess();

    void setTenantAddress(AddressSimple tenantAddress);

    void setPreAuthorizedDebitAgreement(String agreementTextHml);

}
