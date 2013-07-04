/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.domain.dto.financial.FinancialSummaryDTO;
import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;
import com.propertyvista.portal.rpc.portal.services.resident.DashboardService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressRetriever;
import com.propertyvista.shared.config.VistaFeatures;

public class DashboardServiceImpl implements DashboardService {

    @Override
    public void retrieveTenantDashboard(AsyncCallback<TenantDashboardDTO> callback) {
        TenantDashboardDTO dashboard = EntityFactory.create(TenantDashboardDTO.class);

        LeaseTermTenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenantInLease.leaseTermV());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit().floorplan());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit().building());

        dashboard.profileInfo().tenantName().setValue(tenantInLease.leaseParticipant().customer().person().name().getStringView());
        dashboard.profileInfo().floorplanName().set(tenantInLease.leaseTermV().holder().lease().unit().floorplan().marketingName());
        dashboard.profileInfo().tenantAddress().setValue(AddressRetriever.getLeaseParticipantCurrentAddress(tenantInLease).getStringView());

        dashboard.billSummary().set(BillSummaryServiceImpl.retrieve());

        final List<MaintenanceRequestDTO> mrList = new ArrayList<MaintenanceRequestDTO>();
        new MaintenanceServiceImpl().listOpenIssues(new AsyncCallback<Vector<MaintenanceRequestDTO>>() {
            @Override
            public void onSuccess(Vector<MaintenanceRequestDTO> arg0) {
                mrList.addAll(arg0);
            }

            @Override
            public void onFailure(Throwable arg0) {
            }
        });
        dashboard.maintanances().addAll(mrList);

        // TODO review this: (i think tenant insurance status can be used for other countries as well but the problem is with TenantSure
        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            dashboard.tenantInsuranceStatus().set(
                    ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceStatus(
                            TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().<Tenant> createIdentityStub()));

        }

        // fill stuff for the new web portal
        dashboard.profileInfo().tenantName().setValue(tenantInLease.leaseParticipant().customer().person().name().getStringView());
        dashboard.profileInfo().tenantAddress().setValue(AddressRetriever.getLeaseParticipantCurrentAddress(tenantInLease).getStringView());
        dashboard.profileInfo().floorplanName().set(tenantInLease.leaseTermV().holder().lease().unit().floorplan().marketingName());

        FinancialSummaryDTO billingSummary = BillSummaryServiceImpl.retrieve();
        dashboard.billingInfo().currentBalance().setValue(billingSummary.currentBalance().getValue());
        if (!VistaFeatures.instance().yardiIntegration()) {
            Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(tenantInLease.leaseTermV().holder().lease());
            dashboard.billingInfo().dueDate().setValue(bill.dueDate().getValue());
        }

        dashboard.maintenanceInfo();

        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            dashboard
                    .residentServicesInfo()
                    .tenantInsuranceStatus()
                    .set(ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceStatus(
                            TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().<Tenant> createIdentityStub()));

        }

        callback.onSuccess(dashboard);
    }
}
