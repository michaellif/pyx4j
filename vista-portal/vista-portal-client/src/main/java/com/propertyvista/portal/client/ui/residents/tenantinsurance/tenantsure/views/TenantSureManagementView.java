/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceStatusDetailedDTO;

public interface TenantSureManagementView extends IsWidget {

    public interface Presenter {

        void updateCreditCardDetails();

        void cancelTenantSure();

        void viewFaq();

        void viewAboutTenantSure();

        void sendDocumentation(String email);

    }

    void setPresenter(Presenter presenter);

    void populate(TenantSureTenantInsuranceStatusDetailedDTO detailedStatus);

    void reportUpdateCreditCardUpdate(String errorMessage);

    void reportCancelFailure(String errorMessage);

    void reportSendDocumentatioinSuccess();

}
