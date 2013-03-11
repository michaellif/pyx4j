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
package com.propertyvista.field.client;

import com.google.gwt.resources.client.ImageResource;

import com.pyx4j.site.client.AppPlaceEntityMapper;

import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.field.client.resources.FieldImages;
import com.propertyvista.field.rpc.FieldSiteMap;

public class FieldEntityMapper {

    private static ImageResource DEFAULT_IMAGE = FieldImages.INSTANCE.blank();

    public static void init() {

        AppPlaceEntityMapper.register(Building.class, FieldSiteMap.Properties.Building.class, DEFAULT_IMAGE);
        AppPlaceEntityMapper.register(AptUnit.class, FieldSiteMap.Properties.Unit.class, DEFAULT_IMAGE);
        AppPlaceEntityMapper.register(AptUnitItem.class, FieldSiteMap.Properties.UnitItem.class, DEFAULT_IMAGE);

        AppPlaceEntityMapper.register(Tenant.class, FieldSiteMap.Tenants.Tenant.class, DEFAULT_IMAGE);
        AppPlaceEntityMapper.register(Guarantor.class, FieldSiteMap.Tenants.Guarantor.class, DEFAULT_IMAGE);

        AppPlaceEntityMapper.register(Lease.class, FieldSiteMap.Tenants.Lease.class, DEFAULT_IMAGE);
        AppPlaceEntityMapper.register(LeaseTerm.class, FieldSiteMap.Tenants.LeaseTerm.class, DEFAULT_IMAGE);
        AppPlaceEntityMapper.register(MaintenanceRequest.class, FieldSiteMap.Tenants.MaintenanceRequest.class, DEFAULT_IMAGE);

    }
}
