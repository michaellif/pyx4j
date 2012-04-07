/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 6, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client;

import com.pyx4j.site.client.AppPlaceEntityMapper;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.EquifaxResult;

public class CrmEntityMapper {

    public static void init() {
        AppPlaceEntityMapper.register(Complex.class, CrmSiteMap.Properties.Complex.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Building.class, CrmSiteMap.Properties.Building.class, CrmImages.INSTANCE.propertiesNormal());
        AppPlaceEntityMapper.register(Floorplan.class, CrmSiteMap.Properties.Floorplan.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(AptUnit.class, CrmSiteMap.Properties.Unit.class, CrmImages.INSTANCE.unitsNormal());
        AppPlaceEntityMapper.register(LockerArea.class, CrmSiteMap.Properties.LockerArea.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Parking.class, CrmSiteMap.Properties.Parking.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Roof.class, CrmSiteMap.Properties.Roof.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Tenant.class, CrmSiteMap.Tenants.Tenant.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Guarantor.class, CrmSiteMap.Tenants.Guarantor.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(Lease.class, CrmSiteMap.Tenants.Lease.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(EquifaxResult.class, CrmSiteMap.Tenants.EquifaxResult.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(MaintenanceRequest.class, CrmSiteMap.Tenants.MaintenanceRequest.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(GlCode.class, CrmSiteMap.Settings.GlCodeCategory.class, CrmImages.INSTANCE.arrowGreyLeft());
        AppPlaceEntityMapper.register(ProductItem.class, CrmSiteMap.Properties.Service.class, CrmImages.INSTANCE.arrowGreyLeft());
    }

}
