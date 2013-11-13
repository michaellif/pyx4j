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
package com.propertyvista.portal.resident.ui.dashboard;

import com.pyx4j.site.client.IsView;

import com.propertyvista.portal.rpc.portal.resident.dto.ResidentSummaryDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceSummaryDTO;

public interface MainDashboardView extends IsView {

    interface DashboardPresenter {

        void payNow();

        void setAutopay();

        void buyTenantSure();

        void addThirdPartyTenantInsuranceCertificate();

        void createMaintenanceRequest();

    }

    void setPresenter(DashboardPresenter presenter);

    void populateProfileGadget(ResidentSummaryDTO profileSummary);

    void populateBillingGadget(BillingSummaryDTO billingSummary);

    void populateInsuranceGadget(InsuranceStatusDTO insuranceStatus);

    void populateMaintenanceGadget(MaintenanceSummaryDTO maintenanceSummary);

}
