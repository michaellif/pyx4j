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
package com.propertyvista.portal.web.client.ui.dashboard;

import com.pyx4j.site.client.IsView;

import com.propertyvista.portal.rpc.portal.web.dto.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.MaintenanceSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.TenantProfileSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.InsuranceStatusDTO;

public interface MainDashboardView extends IsView {

    interface DashboardPresenter {

        void viewCurrentBill();

        void payNow();

        void setAutopay();

        void buyTenantSure();

        void addThirdPartyTenantInsuranceCertificate();

    }

    void setPresenter(DashboardPresenter presenter);

    void populateProfileGadget(TenantProfileSummaryDTO profileSummary);

    void populateBillingGadget(BillingSummaryDTO billingSummary);

    void populateInsuranceGadget(InsuranceStatusDTO insuranceStatus);

    void populateMaintenanceGadget(MaintenanceSummaryDTO maintenanceSummary);

}
