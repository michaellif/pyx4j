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
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.crm.rpc.services.building.ac.CommunityEvents;
import com.propertyvista.crm.rpc.services.building.ac.ImportExport;
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
        //F general, details, units, add-ons, PC, contacts
        {
            List<Class<? extends IEntity>> entities = entities(BuildingDTO.class,//
                    Product.class, Service.class, Feature.class, Concession.class);

            grant(BuildingBasic, entities, READ);
            grant(BuildingFinancial, entities, READ);
            grant(BuildingAccounting, entities, READ);
            grant(BuildingProperty, entities, ALL);
            grant(BuildingMarketing, entities, ALL);
            grant(BuildingMechanicals, entities, READ);
            grant(BuildingAdministrator, entities, ALL);
            grant(BuildingLeasing, entities, READ);
        }

        {
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

        //G floorplans/general
        {
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

        // units
        {
            List<Class<? extends IEntity>> entities = entities(AptUnitDTO.class, AptUnitItem.class, AptUnitOccupancySegment.class);
            grant(BuildingBasic, entities, READ);
            grant(BuildingFinancial, entities, READ);
            grant(BuildingAccounting, entities, READ);
            grant(BuildingProperty, entities, READ);
            grant(BuildingMarketing, entities, ALL);
            grant(BuildingMechanicals, entities, READ);
            grant(BuildingAdministrator, entities, ALL);
            grant(BuildingLeasing, entities, READ);
        }
        //H  "floorplans/marketing, marketing"
        grant(BuildingBasic, Marketing.class, READ);
        grant(BuildingFinancial, Marketing.class, READ);
        grant(BuildingAccounting, Marketing.class, READ);
        grant(BuildingProperty, Marketing.class, READ);
        grant(BuildingMarketing, Marketing.class, ALL);
        grant(BuildingMechanicals, Marketing.class, READ);
        grant(BuildingAdministrator, Marketing.class, ALL);
        grant(BuildingLeasing, Marketing.class, READ);

        // mechanical
        {
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

        // add-ons
        {
            List<Class<? extends IEntity>> entities = entities(BuildingAddOns.class, ParkingDTO.class, LockerAreaDTO.class);
            //grant(BuildingBasic, entities, 0);
            //grant(BuildingFinancial, entities, 0);
            grant(BuildingAccounting, entities, READ);
            grant(BuildingProperty, entities, ALL);
            grant(BuildingMarketing, entities, ALL);
            grant(BuildingMechanicals, entities, READ);
            grant(BuildingAdministrator, entities, ALL);
            grant(BuildingLeasing, entities, READ);
        }

        //financial
        grant(BuildingFinancial, BuildingFinancial.class, READ);
        grant(BuildingAccounting, BuildingFinancial.class, READ);
        grant(BuildingAdministrator, BuildingFinancial.class, ALL);
        // see also VistaCrmFinancialAccessControlList  FinancialFull

        //billing cycles
        grant(BuildingFinancial, BillingCycleDTO.class, READ);
        grant(BuildingAccounting, BillingCycleDTO.class, READ);
        grant(BuildingAdministrator, BillingCycleDTO.class, ALL);
        // see also VistaCrmFinancialAccessControlList  FinancialFull

        // "actions/community events"
        grant(BuildingProperty, CommunityEvents.class);
        grant(BuildingMarketing, CommunityEvents.class);
        grant(BuildingMechanicals, CommunityEvents.class);
        grant(BuildingAdministrator, CommunityEvents.class);

        // "actions/import export"
        grant(BuildingProperty, ImportExport.class);
        grant(BuildingMarketing, ImportExport.class);
        grant(BuildingAdministrator, ImportExport.class);

        //landlords
        grant(BuildingBasic, LandlordDTO.class, READ);
        grant(BuildingFinancial, LandlordDTO.class, READ);
        grant(BuildingAccounting, LandlordDTO.class, READ);
        grant(BuildingProperty, LandlordDTO.class, READ);
        grant(BuildingMarketing, LandlordDTO.class, ALL);
        grant(BuildingMechanicals, LandlordDTO.class, READ);
        grant(BuildingAdministrator, LandlordDTO.class, ALL);
        grant(BuildingLeasing, LandlordDTO.class, READ);

    }
}
