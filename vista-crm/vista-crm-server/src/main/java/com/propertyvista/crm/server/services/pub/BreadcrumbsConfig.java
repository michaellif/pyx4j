/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 4, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.pub;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ComplexDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.GuarantorDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.MasterApplicationDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.PetsDTO;
import com.propertyvista.dto.RoofDTO;
import com.propertyvista.dto.SiteDescriptorDTO;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.server.common.breadcurmbs.BreadcrumbsHelper.LabelCreator;

public class BreadcrumbsConfig {

    public static final Map<Class<? extends IEntity>, LabelCreator> LABEL_CREATOR_MAP;

    public static final Map<Class<? extends IEntity>, Class<? extends IEntity>> DTO_TO_DBO_MAP;

    private final static I18n i18n = I18n.get(BreadcrumbsConfig.class);

    static {

        {
            // CUSOMIZE LABEL GENERATION
            Map<Class<? extends IEntity>, LabelCreator> map = new HashMap<Class<? extends IEntity>, LabelCreator>();
            map.put(Building.class, new LabelCreator() {
                @Override
                public String label(IEntity entity) {
                    return i18n.tr("Building: {0}", ((Building) entity).propertyCode().getValue());
                }
            });
            map.put(AptUnit.class, new LabelCreator() {
                @Override
                public String label(IEntity entity) {
                    return i18n.tr("Unit: {0}", ((AptUnit) entity).info().number().getValue());
                }
            });
            map.put(AptUnitItem.class, new LabelCreator() {
                @Override
                public String label(IEntity entity) {
                    return i18n.tr("Details for {0}", ((AptUnitItem) entity).type().getValue());
                }
            });

            LABEL_CREATOR_MAP = Collections.unmodifiableMap(map);
        }

        {
            // SET UP DTO TO PARENT DBO MAPPING        
            Map<Class<? extends IEntity>, Class<? extends IEntity>> map = new HashMap<Class<? extends IEntity>, Class<? extends IEntity>>();
            map.put(ApplicationDTO.class, Application.class);
            map.put(AptUnitDTO.class, AptUnit.class);
            map.put(BillDTO.class, Bill.class);
            map.put(BoilerDTO.class, Boiler.class);
            map.put(BuildingDTO.class, Building.class);
            map.put(ComplexDTO.class, Complex.class);
            map.put(ElevatorDTO.class, Elevator.class);
            map.put(FloorplanDTO.class, Floorplan.class);
            map.put(GuarantorDTO.class, Guarantor.class);
            map.put(LeaseDTO.class, Lease.class);
            map.put(LockerAreaDTO.class, LockerArea.class);
            map.put(MaintenanceRequestDTO.class, MaintenanceRequest.class);
            map.put(MasterApplicationDTO.class, MasterApplication.class);
            map.put(ParkingDTO.class, Parking.class);
            map.put(PetsDTO.class, Pet.class);
            map.put(RoofDTO.class, Roof.class);
            map.put(SiteDescriptorDTO.class, SiteDescriptor.class);
            map.put(TenantDTO.class, Tenant.class);
            map.put(TenantInfoDTO.class, PersonScreening.class);
            map.put(TenantInLeaseDTO.class, TenantInLease.class);

            // etc..
            DTO_TO_DBO_MAP = Collections.unmodifiableMap(map);
        }
    }

}
