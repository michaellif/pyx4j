/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.services.dashboard;

import com.pyx4j.site.client.IsView;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.InsuranceStatusDTO;

public interface ServicesDashboardView extends IsView {

    public interface ServicesDashboardPresenter {

        // Tenant Insurance Related        

        // when no tenant insurance
        void getTenantSure();

        void addThirdPartyTenantInsuranceCertificate();

        // Tenant Sure Managment Related
        void updateCreditCardDetails();

        void cancelTenantSure();

        /** reverts cancelation */
        void reinstate();

        void viewFaq();

        void viewAboutTenantSure();

        /** email can be null: then tenant's default email should be used */
        void sendCertificate(String email);

        // Insurance by other provide related
        void updateThirdPartyTenantInsuranceCeritificate();

    }

    void setPresenter(ServicesDashboardPresenter presenter);

    void populateInsuranceGadget(InsuranceStatusDTO insuranceStatus);

}
