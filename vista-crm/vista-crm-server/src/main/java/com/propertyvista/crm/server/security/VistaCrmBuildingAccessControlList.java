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

import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.services.building.ac.CommunityEvents;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.property.asset.building.BuildingFinancial;
import com.propertyvista.domain.property.asset.building.BuildingMechanical;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.FloorplanDTO;

class VistaCrmBuildingAccessControlList extends UIAclBuilder {

    VistaCrmBuildingAccessControlList() {
        //F general, details, units, add-ons, PC, contacts
        grant(BuildingBasic, BuildingDTO.class, AptUnitDTO.class, READ);
        grant(BuildingFinancial, BuildingDTO.class, AptUnitDTO.class, READ);
        grant(BuildingAccounting, BuildingDTO.class, AptUnitDTO.class, READ);
        grant(BuildingProperty, BuildingDTO.class, AptUnitDTO.class, ALL);
        grant(BuildingMarketing, BuildingDTO.class, AptUnitDTO.class, ALL);
        grant(BuildingMechanicals, BuildingDTO.class, AptUnitDTO.class, READ);
        grant(BuildingAdministrator, BuildingDTO.class, AptUnitDTO.class, ALL);
        grant(BuildingLeasing, BuildingDTO.class, AptUnitDTO.class, READ);

        //G floorplans/general
        grant(BuildingBasic, FloorplanDTO.class, READ);
        grant(BuildingFinancial, FloorplanDTO.class, READ);
        grant(BuildingAccounting, FloorplanDTO.class, READ);
        grant(BuildingProperty, FloorplanDTO.class, ALL);
        grant(BuildingMarketing, FloorplanDTO.class, ALL);
        grant(BuildingMechanicals, FloorplanDTO.class, READ);
        grant(BuildingAdministrator, FloorplanDTO.class, ALL);
        grant(BuildingLeasing, FloorplanDTO.class, READ);

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
        grant(BuildingBasic, BuildingMechanical.class, READ);
        grant(BuildingFinancial, BuildingMechanical.class, READ);
        grant(BuildingAccounting, BuildingMechanical.class, READ);
        grant(BuildingProperty, BuildingMechanical.class, READ);
        //grant(BuildingMarketing, BuildingMechanical.class, 0);
        grant(BuildingMechanicals, BuildingMechanical.class, ALL);
        grant(BuildingAdministrator, BuildingMechanical.class, ALL);
        //grant(BuildingLeasing, BuildingMechanical.class, 0);

        //financial
        grant(BuildingFinancial, BuildingFinancial.class, READ);
        grant(BuildingAccounting, BuildingFinancial.class, READ);
        grant(BuildingAdministrator, BuildingFinancial.class, ALL);

        //
        grant(BuildingProperty, CommunityEvents.class);
        grant(BuildingMarketing, CommunityEvents.class);
        grant(BuildingMechanicals, CommunityEvents.class);
        grant(BuildingAdministrator, CommunityEvents.class);

    }
}
