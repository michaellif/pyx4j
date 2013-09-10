/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 30, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.web.services.mock;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPayInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPaySummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.FinancialDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.dto.MainDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.dto.ServicesDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.dto.TenantProfileDTO;
import com.propertyvista.portal.rpc.portal.web.services.DashboardService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class DashboardServiceMockImpl implements DashboardService {

    @Override
    public void retrieveMainDashboard(AsyncCallback<MainDashboardDTO> callback) {
        MainDashboardDTO dashboard = EntityFactory.create(MainDashboardDTO.class);

        populateProfileInfo(dashboard.profileInfo());

        populateBillingSummary(dashboard.billingSummary());

        callback.onSuccess(dashboard);
    }

    @Override
    public void retrieveFinancialDashboard(AsyncCallback<FinancialDashboardDTO> callback) {
        FinancialDashboardDTO dashboard = EntityFactory.create(FinancialDashboardDTO.class);

        populateBillingSummary(dashboard.billingSummary());

        populateAutoPaySummary(dashboard.autoPaySummary());

        callback.onSuccess(dashboard);
    }

    @Override
    public void retrieveServicesDashboard(AsyncCallback<ServicesDashboardDTO> callback) {
        ServicesDashboardDTO dashboard = EntityFactory.create(ServicesDashboardDTO.class);
        callback.onSuccess(dashboard);
    }

    private static void populateProfileInfo(TenantProfileDTO profileInfo) {
        LeaseTermTenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
        profileInfo.tenantName().setValue(tenantInLease.leaseParticipant().customer().person().name().getStringView());
        profileInfo.floorplanName().setValue("3 bedroom + den");
        profileInfo.tenantAddress().setValue("8 Milky Way Drive, Burlington, ON");
    }

    private static void populateBillingSummary(BillingSummaryDTO billingSummary) {
        billingSummary.currentBalance().setValue(new BigDecimal("1300.00"));
        billingSummary.dueDate().setValue(new LogicalDate(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000));
    }

    private static void populateAutoPaySummary(AutoPaySummaryDTO autoPaySummary) {

        {
            AutoPayInfoDTO autoPay = EntityFactory.create(AutoPayInfoDTO.class);
            autoPay.amount().setValue(new BigDecimal("1100.00"));
            autoPaySummary.currentAutoPayments().add(autoPay);
        }

        autoPaySummary.currentAutoPayDate().setValue(new LogicalDate(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000));
        autoPaySummary.nextAutoPayDate().setValue(new LogicalDate(System.currentTimeMillis() + 35 * 24 * 60 * 60 * 1000));
        autoPaySummary.modificationsAllowed().setValue(true);
    }

    @Override
    public void deletePreauthorizedPayment(AsyncCallback<VoidSerializable> defaultAsyncCallback, PreauthorizedPayment itemId) {
        // TODO Auto-generated method stub

    }

}
