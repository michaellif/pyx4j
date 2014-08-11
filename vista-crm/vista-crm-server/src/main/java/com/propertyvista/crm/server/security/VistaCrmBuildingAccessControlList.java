/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 6, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import static com.propertyvista.domain.security.VistaCrmBehavior.BuildingAccounting;
import static com.propertyvista.domain.security.VistaCrmBehavior.BuildingAdministrator;
import static com.propertyvista.domain.security.VistaCrmBehavior.BuildingBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.BuildingFinancial;
import static com.propertyvista.domain.security.VistaCrmBehavior.BuildingLeasing;
import static com.propertyvista.domain.security.VistaCrmBehavior.BuildingMarketing;
import static com.propertyvista.domain.security.VistaCrmBehavior.BuildingMechanicals;
import static com.propertyvista.domain.security.VistaCrmBehavior.BuildingProperty;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.crm.rpc.services.MediaUploadBuildingService;
import com.propertyvista.crm.rpc.services.MediaUploadFloorplanService;
import com.propertyvista.crm.rpc.services.UpdateUploadService;
import com.propertyvista.crm.rpc.services.billing.BillingCycleBillListService;
import com.propertyvista.crm.rpc.services.billing.BillingCycleCrudService;
import com.propertyvista.crm.rpc.services.billing.BillingCycleLeaseListService;
import com.propertyvista.crm.rpc.services.building.ac.CommunityEvents;
import com.propertyvista.crm.rpc.services.building.ac.ImportExport;
import com.propertyvista.crm.rpc.services.building.ac.UpdateUnitAvailability;
import com.propertyvista.crm.rpc.services.importer.ExportBuildingDataDownloadService;
import com.propertyvista.crm.rpc.services.importer.ImportBuildingDataService;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.property.asset.building.BuildingAddOns;
import com.propertyvista.domain.property.asset.building.BuildingFinancial;
import com.propertyvista.domain.property.asset.building.BuildingMechanical;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ComplexDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.LandlordDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;

class VistaCrmBuildingAccessControlList extends UIAclBuilder {

    VistaCrmBuildingAccessControlList() {
        { // 
            List<Class<? extends IEntity>> entities = entities(ComplexDTO.class);

            grant(BuildingBasic, entities, READ);
            grant(BuildingFinancial, entities, READ);
            grant(BuildingAccounting, entities, READ);
            grant(BuildingProperty, entities, READ);
            grant(BuildingMarketing, entities, READ | UPDATE);
            grant(BuildingMechanicals, entities, READ);
            grant(BuildingAdministrator, entities, ALL);
            grant(BuildingLeasing, entities, READ | UPDATE);
        }
        { //F
            List<Class<? extends IEntity>> entities = entities(BuildingDTO.class);

            grant(BuildingBasic, entities, READ);
            grant(BuildingFinancial, entities, READ);
            grant(BuildingAccounting, entities, READ);
            grant(BuildingProperty, entities, ALL);
            grant(BuildingMarketing, entities, ALL);
            grant(BuildingMechanicals, entities, READ);
            grant(BuildingAdministrator, entities, ALL);
            grant(BuildingLeasing, entities, READ);
        }
        { // G
            List<Class<? extends IEntity>> entities = entities(Product.class, Service.class, Feature.class, Concession.class);

            grant(BuildingBasic, entities, READ);
            grant(BuildingFinancial, entities, READ);
            grant(BuildingAccounting, entities, READ);
            grant(BuildingProperty, entities, ALL);
            grant(BuildingMarketing, entities, ALL);
            grant(BuildingMechanicals, entities, READ);
            grant(BuildingAdministrator, entities, ALL);
            grant(BuildingLeasing, entities, READ);
        }
        { // H
            List<Class<? extends IEntity>> entities = entities(FloorplanDTO.class);

            grant(BuildingBasic, entities, READ);
            grant(BuildingFinancial, entities, READ);
            grant(BuildingAccounting, entities, READ);
            grant(BuildingProperty, entities, ALL);
            grant(BuildingMarketing, entities, ALL);
            grant(BuildingMechanicals, entities, READ);
            grant(BuildingAdministrator, entities, ALL);
            grant(BuildingLeasing, entities, READ);
        }
        { // I
            List<Class<? extends IEntity>> entities = entities(Marketing.class);

            grant(BuildingBasic, entities, READ);
            grant(BuildingFinancial, entities, READ);
            grant(BuildingAccounting, entities, READ);
            grant(BuildingProperty, entities, READ);
            grant(BuildingMarketing, entities, READ | UPDATE);
            grant(BuildingMechanicals, entities, READ);
            grant(BuildingAdministrator, entities, READ | UPDATE);
            grant(BuildingLeasing, entities, READ);

            grant(BuildingMarketing, new IServiceExecutePermission(MediaUploadBuildingService.class));
            grant(BuildingMarketing, new IServiceExecutePermission(MediaUploadFloorplanService.class));

            grant(BuildingAdministrator, new IServiceExecutePermission(MediaUploadBuildingService.class));
            grant(BuildingAdministrator, new IServiceExecutePermission(MediaUploadFloorplanService.class));
        }
        { // J
            List<Class<? extends IEntity>> entities = entities(AptUnitDTO.class, AptUnitItem.class, AptUnitOccupancySegment.class);

            grant(BuildingBasic, entities, READ);
            grant(BuildingFinancial, entities, READ);
            grant(BuildingAccounting, entities, READ);
            grant(BuildingProperty, entities, ALL);
            grant(BuildingMarketing, entities, ALL);
            grant(BuildingMechanicals, entities, READ);
            grant(BuildingAdministrator, entities, ALL);
            grant(BuildingLeasing, entities, READ);
        }

        { // K
            List<Class<? extends IEntity>> entities = entities(BuildingMechanical.class, BoilerDTO.class, ElevatorDTO.class, RoofDTO.class);

            grant(BuildingBasic, entities, READ);
            grant(BuildingFinancial, entities, READ);
            grant(BuildingAccounting, entities, READ);
            grant(BuildingProperty, entities, READ);
            //grant(BuildingMarketing, entities, 0);
            grant(BuildingMechanicals, entities, ALL);
            grant(BuildingAdministrator, entities, ALL);
            //grant(BuildingLeasing, entities, 0);
        }
        { // L,M
            List<Class<? extends IEntity>> entities = entities(BuildingFinancial.class);

            grant(BuildingFinancial, entities, READ);
            grant(BuildingAccounting, entities, READ);
            grant(BuildingAdministrator, entities, ALL);

            grant(BuildingAccounting, BuildingMerchantAccount.class, READ | UPDATE);
            grant(BuildingAdministrator, BuildingMerchantAccount.class, READ | UPDATE);
            // see also VistaCrmFinancialAccessControlList  FinancialFull
        }
        { // N
            List<Class<? extends IEntity>> entities = entities(BuildingAddOns.class, ParkingDTO.class, LockerAreaDTO.class);

            grant(BuildingAccounting, entities, READ);
            grant(BuildingProperty, entities, ALL);
            grant(BuildingMarketing, entities, ALL);
            grant(BuildingMechanicals, entities, READ);
            grant(BuildingAdministrator, entities, ALL);
            grant(BuildingLeasing, entities, READ);
        }
        { // O
            List<Class<? extends IEntity>> entities = entities(BillingCycleDTO.class);

            grant(BuildingFinancial, entities, READ);
            grant(BuildingAccounting, entities, READ);
            grant(BuildingAdministrator, entities, ALL);
            // see also VistaCrmFinancialAccessControlList  FinancialFull
            grant(BuildingFinancial, BuildingAccounting, BuildingAdministrator, new IServiceExecutePermission(BillingCycleCrudService.class));
            grant(BuildingFinancial, BuildingAccounting, BuildingAdministrator, new IServiceExecutePermission(BillingCycleBillListService.class));
            grant(BuildingFinancial, BuildingAccounting, BuildingAdministrator, new IServiceExecutePermission(BillingCycleLeaseListService.class));
        }

        { // P
            grant(BuildingProperty, CommunityEvents.class);
            grant(BuildingMarketing, CommunityEvents.class);
            grant(BuildingMechanicals, CommunityEvents.class);
            grant(BuildingAdministrator, CommunityEvents.class);
        }
        { // Q
            grant(BuildingProperty, BuildingMarketing, BuildingAdministrator, ImportExport.class);

            grant(BuildingProperty, BuildingMarketing, BuildingAdministrator, new IServiceExecutePermission(UpdateUploadService.class));
            grant(BuildingProperty, BuildingMarketing, BuildingAdministrator, new IServiceExecutePermission(ImportBuildingDataService.class));
            grant(BuildingProperty, BuildingMarketing, BuildingAdministrator, new IServiceExecutePermission(ExportBuildingDataDownloadService.class));
        }
        { // R
            grant(BuildingProperty, UpdateUnitAvailability.class);
            grant(BuildingMarketing, UpdateUnitAvailability.class);
            grant(BuildingMechanicals, UpdateUnitAvailability.class);
            grant(BuildingAdministrator, UpdateUnitAvailability.class);
            grant(BuildingLeasing, UpdateUnitAvailability.class);
        }

        { // S
            List<Class<? extends IEntity>> entities = entities(LandlordDTO.class);
            grant(BuildingBasic, entities, READ);
            grant(BuildingFinancial, entities, READ);
            grant(BuildingAccounting, entities, READ);
            grant(BuildingProperty, entities, ALL);
            grant(BuildingMarketing, entities, READ | UPDATE);
            grant(BuildingMechanicals, entities, READ);
            grant(BuildingAdministrator, entities, ALL);
            grant(BuildingLeasing, entities, READ);
        }
    }
}
