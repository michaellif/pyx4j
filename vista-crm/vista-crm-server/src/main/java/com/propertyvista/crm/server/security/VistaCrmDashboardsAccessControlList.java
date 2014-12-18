/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 25, 2014
 * @author vlads
 */
package com.propertyvista.crm.server.security;

import static com.propertyvista.domain.security.VistaCrmBehavior.DashboardsGadgetsBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.DashboardsGadgetsFull;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.crm.rpc.services.dashboard.GadgetMetadataService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ApplicationsGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.BuildingResidentInsuranceListService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.CollectionsGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.DelinquentLeaseListService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeadsAndRentalsGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeaseExpirationGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.MaintenanceGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.NoticesGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentRecordsGadgetListService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentRecordsSummaryGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitAvailabilityStatusListService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitAvailabilitySummaryGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitTurnoverAnalysisGadgetService;
import com.propertyvista.crm.server.security.access.DashboardDatasetAccessRule;
import com.propertyvista.crm.server.security.access.DashboardOwnerInstanceAccess;
import com.propertyvista.crm.server.security.access.DashboardUserInstanceAccess;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;

class VistaCrmDashboardsAccessControlList extends UIAclBuilder {

    VistaCrmDashboardsAccessControlList() {

        // - Dashboard:
        // we want owners (dashboard creator) to have full access to dashboards they own, and other users only read-only access and only for shared.
        grant(DashboardsGadgetsBasic, new EntityPermission(DashboardMetadata.class, new DashboardOwnerInstanceAccess(), ALL));
        grant(DashboardsGadgetsBasic, new EntityPermission(DashboardMetadata.class, new DashboardUserInstanceAccess(), READ));
        grant(DashboardsGadgetsBasic, new DashboardDatasetAccessRule(), DashboardMetadata.class);

        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(DashboardMetadataService.class));
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(DashboardMetadataCrudService.class));
        grant(DashboardsGadgetsBasic, DashboardMetadata.class, ALL);

        // - Gadgets:
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(GadgetMetadataService.class));

        // TODO define correct behaviors
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(ApplicationsGadgetService.class));
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(ArrearsGadgetService.class));
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(ArrearsReportService.class));
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(UnitTurnoverAnalysisGadgetService.class));
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(UnitAvailabilitySummaryGadgetService.class));

        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(UnitAvailabilityStatusListService.class));
        grant(DashboardsGadgetsBasic, new EntityPermission(UnitAvailabilityStatus.class, EntityPermission.READ));

        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(CollectionsGadgetService.class));
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(DelinquentLeaseListService.class));
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(LeadsAndRentalsGadgetService.class));
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(LeaseExpirationGadgetService.class));
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(MaintenanceGadgetService.class));
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(NoticesGadgetService.class));
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(PaymentRecordsSummaryGadgetService.class));
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(PaymentRecordsGadgetListService.class));
        grant(DashboardsGadgetsBasic, new IServiceExecutePermission(BuildingResidentInsuranceListService.class));

        grant(DashboardsGadgetsFull, DashboardsGadgetsBasic);
    }
}
