/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 4, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.ils.GeneralAmenityType;
import com.yardi.entity.ils.ILSIdentification;
import com.yardi.entity.ils.Property;
import com.yardi.entity.mits.Identification;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.geo.GeoPoint;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.yardi.mapper.MappingUtils;

public class YardiILSMarketingProcessor {

    private final static Logger log = LoggerFactory.getLogger(YardiILSMarketingProcessor.class);

    private final ExecutionMonitor executionMonitor;

    public YardiILSMarketingProcessor() {
        this(null);
    }

    public YardiILSMarketingProcessor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    public Building updateProperty(Key yardiInterfaceId, Property propertyInfo) throws YardiServiceException {
        Identification propertyId = propertyInfo.getPropertyID().getIdentification();
        if (propertyId == null) {
            throw new YardiServiceException("Property id not found");
        }

        Building building = MappingUtils.getBuilding(yardiInterfaceId, propertyId.getPrimaryID());

        if (propertyId.getMarketingName() != null) {
            building.marketing().name().setValue(propertyId.getMarketingName());
        }

        if (propertyId.getWebSite() != null) {
            building.contacts().website().setValue(propertyId.getWebSite());
        }

        if (propertyInfo.getPropertyID().getAddress().size() > 0) {
            building.marketing().marketingAddress().set(MappingUtils.getAddress(propertyInfo.getPropertyID().getAddress().get(0)));
        }

        ILSIdentification ilsId = propertyInfo.getILSIdentification();
        if (ilsId != null) {
            if (ilsId.getType() != null) {
                building.info().type().setValue(getBuildingType(ilsId.getType()));
            }
            if (ilsId.getLatitude() != null && ilsId.getLongitude() != null) {
                GeoPoint geo = new GeoPoint(ilsId.getLatitude().doubleValue(), ilsId.getLongitude().doubleValue());
                building.info().address().location().setValue(geo);
            }
        }

        // check for building info
        com.yardi.entity.ils.Building ilsBuilding = propertyInfo.getBuilding().get(0);
        if (ilsBuilding != null) {
            if (ilsBuilding.getDescription() != null) {
                building.marketing().description().setValue(ilsBuilding.getDescription());
            }

            if (ilsBuilding.getType() != null) {
                building.info().structureType().setValue(getStructureType(ilsBuilding.getType()));
            }

            if (ilsBuilding.getAmenity().size() > 0) {
                building.amenities().clear();
                for (GeneralAmenityType amenity : ilsBuilding.getAmenity()) {
                    building.amenities().add(getBuildingAmenity(amenity));
                }
            }
        }

        return building;
    }

    public Floorplan updateFloorplan(Building building, com.yardi.entity.ils.Floorplan floorplan) {
        return null;
    }

    private BuildingAmenity getBuildingAmenity(GeneralAmenityType amenity) {
        BuildingAmenity.Type type = BuildingAmenity.Type.valueOf(StringUtils.uncapitalize(amenity.getType()));
        if (type != null) {
            BuildingAmenity bam = EntityFactory.create(BuildingAmenity.class);
            bam.type().setValue(type);
            return bam;
        }
        return null;
    }

    private BuildingInfo.Type getBuildingType(String yardiPropertyType) {
        if ("Appartment".equals(yardiPropertyType)) {
            return BuildingInfo.Type.residential;
        } else if ("Condo".equals(yardiPropertyType)) {
            return BuildingInfo.Type.condo;
        } else if ("Senior".equals(yardiPropertyType)) {
            return BuildingInfo.Type.seniorHousing;
        } else if ("Subsidized".equals(yardiPropertyType)) {
            return BuildingInfo.Type.socialHousing;
        } else if ("Garden Style".equals(yardiPropertyType)) {
            return BuildingInfo.Type.agricultural;
        } else if ("Mixed Use".equals(yardiPropertyType)) {
            return BuildingInfo.Type.mixedResidential;
        } else if ("Military".equals(yardiPropertyType)) {
            return BuildingInfo.Type.military;
        } else if ("House for Rent".equals(yardiPropertyType)) {
            return BuildingInfo.Type.residential;
        } else if ("Corporate".equals(yardiPropertyType)) {
            return BuildingInfo.Type.commercial;
        } else if ("Unspecified".equals(yardiPropertyType)) {
            return null;
        }
        return BuildingInfo.Type.other;
    }

    private BuildingInfo.StructureType getStructureType(String yardiStructureType) {
        if ("High Rise".equals(yardiStructureType)) {
            return BuildingInfo.StructureType.highRise;
        } else if ("Mid Rise".equals(yardiStructureType)) {
            return BuildingInfo.StructureType.midRise;
        } else if ("Low Rise".equals(yardiStructureType)) {
            return BuildingInfo.StructureType.lowRise;
        } else if ("Condo for Rent".equals(yardiStructureType)) {
            return BuildingInfo.StructureType.condo;
        } else if ("Walkup".equals(yardiStructureType)) {
            return BuildingInfo.StructureType.walkUp;
        } else if ("Townhouse".equals(yardiStructureType)) {
            return BuildingInfo.StructureType.townhouse;
        } else if ("Unspecified".equals(yardiStructureType)) {
            return null;
        }
        return BuildingInfo.StructureType.other;
    }
}
