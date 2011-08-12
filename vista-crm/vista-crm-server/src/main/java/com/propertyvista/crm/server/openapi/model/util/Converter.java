/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 27, 2011
 * @author dmitry
 */
package com.propertyvista.crm.server.openapi.model.util;

import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.server.openapi.model.AddressRS;
import com.propertyvista.crm.server.openapi.model.AdvertisingBlurbRS;
import com.propertyvista.crm.server.openapi.model.BuildingInfoRS;
import com.propertyvista.crm.server.openapi.model.BuildingRS;
import com.propertyvista.crm.server.openapi.model.BuildingsRS;
import com.propertyvista.crm.server.openapi.model.FloorplanRS;
import com.propertyvista.crm.server.openapi.model.FloorplansRS;
import com.propertyvista.crm.server.openapi.model.MarketingRS;
import com.propertyvista.crm.server.openapi.model.MediaRS;
import com.propertyvista.domain.contact.Address;
import com.propertyvista.domain.contact.Address.AddressType;
import com.propertyvista.domain.contact.IAddressFull.StreetDirection;
import com.propertyvista.domain.contact.IAddressFull.StreetType;
import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.domain.property.asset.building.BuildingInfo.Shape;
import com.propertyvista.server.common.reference.SharedData;

public class Converter {
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static BuildingsRS convertBuildings(List<Building> from) {
        BuildingsRS to = new BuildingsRS();
        for (Building building : from) {
            to.buildings.add(convertBuilding(building));
        }
        return to;
    }

    public static BuildingRS convertBuilding(Building from) {
        BuildingRS to = new BuildingRS();

        to.propertyCode = from.propertyCode().getStringView();

        to.info = convertBuildingInfo(from.info());
        to.marketing = convertMarketing(from.marketing());

        return to;
    }

    public static Building convertBuilding(BuildingRS from) {
        Building to = EntityFactory.create(Building.class);

        to.propertyCode().setValue(from.propertyCode);

        // TODO finish this up, conversion of all other parts
        to.info().set(convertBuildingInfo(from.info));
        to.marketing().set(convertMarketing(from.marketing));

        return to;
    }

    public static MarketingRS convertMarketing(Marketing from) {
        MarketingRS to = new MarketingRS();

        to.name = from.name().getStringView();
        to.description = from.description().getStringView();

        for (AdvertisingBlurb blurb : from.adBlurbs()) {
            AdvertisingBlurbRS blurbRS = convertBlurb(blurb);
            to.blurbs.blurbs.add(blurbRS);
        }

        return to;
    }

    public static Marketing convertMarketing(MarketingRS from) {
        Marketing to = EntityFactory.create(Marketing.class);

        to.name().setValue(from.name);
        to.description().setValue(from.description);

        for (AdvertisingBlurbRS blurbRS : from.blurbs.blurbs) {
            AdvertisingBlurb blurb = convertBlurb(blurbRS);
            to.adBlurbs().add(blurb);
        }

        return to;
    }

    public static AdvertisingBlurbRS convertBlurb(AdvertisingBlurb blurb) {
        AdvertisingBlurbRS to = new AdvertisingBlurbRS();

        to.content = blurb.content().getStringView();

        return to;
    }

    public static AdvertisingBlurb convertBlurb(AdvertisingBlurbRS blurb) {
        AdvertisingBlurb to = EntityFactory.create(AdvertisingBlurb.class);

        to.content().setValue(blurb.content);

        return to;
    }

    public static BuildingInfoRS convertBuildingInfo(BuildingInfo from) {
        BuildingInfoRS to = new BuildingInfoRS();

        to.name = from.name().getStringView();
        to.address = convertAddress(from.address());
        to.type = from.type().getValue();
        if (!from.shape().isNull()) {
            to.shape = from.shape().getValue().name();
        }

        to.totalStoreys = from.totalStoreys().getStringView();
        to.residentialStoreys = from.residentialStoreys().getStringView();
        to.structureType = from.structureType().getValue();
        if (!from.structureBuildYear().isNull()) {
            to.structureBuildYear = TimeUtils.simpleFormat(from.structureBuildYear().getValue(), DATE_PATTERN);
        }
        to.constructionType = from.constructionType().getValue();
        to.foundationType = from.foundationType().getValue();
        to.floorType = from.floorType().getValue();
        to.landArea = from.landArea().getValue();
        to.waterSupply = from.waterSupply().getValue();
        to.centralAir = from.centralAir().getValue();
        to.centralHeat = from.centralHeat().getValue();

        return to;
    }

    public static BuildingInfo convertBuildingInfo(BuildingInfoRS from) {
        BuildingInfo to = EntityFactory.create(BuildingInfo.class);

        to.name().setValue(from.name);
        to.address().set(convertAddress(from.address));
        to.type().setValue(from.type);

        to.shape().setValue(Shape.valueOf(from.shape));
        to.totalStoreys().setValue(from.totalStoreys);
        to.residentialStoreys().setValue(from.residentialStoreys);
        to.structureType().setValue(from.structureType);
        to.structureBuildYear().setValue(new LogicalDate(TimeUtils.simpleParse(from.structureBuildYear, DATE_PATTERN)));

        to.constructionType().setValue(from.constructionType);
        to.foundationType().setValue(from.foundationType);
        to.floorType().setValue(from.floorType);
        to.landArea().setValue(from.landArea);
        to.waterSupply().setValue(from.waterSupply);
        to.centralAir().setValue(from.centralAir);
        to.centralHeat().setValue(from.centralHeat);

        return to;
    }

    public static AddressRS convertAddress(Address from) {
        AddressRS to = new AddressRS();

        to.addressType = from.addressType().getValue().name();
        to.streetName = from.streetName().getStringView();
        to.streetNumber = from.streetNumber().getStringView();
        to.streetNumberSuffix = from.streetNumberSuffix().getStringView();
        if (!from.streetDirection().isNull()) {
            to.streetDirection = from.streetDirection().getValue().name();
        }
        if (!from.streetType().isNull()) {
            to.streetType = from.streetType().getValue().name();
        }
        if (!from.unitNumber().isNull()) {
            to.unitNumber = from.unitNumber().getValue();
        }
        to.city = from.city().getStringView();
        to.province = from.province().getStringView();
        to.postalCode = from.postalCode().getStringView();
        to.country = from.country().getStringView();
        if (!from.county().isNull()) {
            to.county = from.county().getStringView();
        }

        return to;
    }

    public static Address convertAddress(AddressRS from) {
        Address to = EntityFactory.create(Address.class);

        to.addressType().setValue(AddressType.valueOf(from.addressType));
        to.streetName().setValue(from.streetName);
        to.streetNumber().setValue(from.streetNumber);
        to.streetNumberSuffix().setValue(from.streetNumberSuffix);
        to.streetDirection().setValue(StreetDirection.valueOf(from.streetDirection));
        to.streetType().setValue(StreetType.valueOf(from.streetType));
        to.unitNumber().setValue(from.unitNumber);
        to.city().setValue(from.city);
        to.province().set(SharedData.findProvinceByCode(from.province));
        to.postalCode().setValue(from.postalCode);
        to.country().set(to.province().country());
        to.county().setValue(from.county);

        return to;
    }

    public static FloorplansRS convertFloorplans(List<Floorplan> from) {
        FloorplansRS to = new FloorplansRS();

        for (Floorplan floorplan : from) {
            to.floorplans.add(convertFloorplan(floorplan));
        }

        return to;
    }

    public static FloorplanRS convertFloorplan(Floorplan from) {
        FloorplanRS to = new FloorplanRS();

        to.name = from.name().getStringView();
        to.description = from.description().getStringView();
        to.floorCount = from.floorCount().getValue();
        to.bedrooms = from.bedrooms().getValue();
        to.bathrooms = from.bathrooms().getValue();

        return to;
    }

    public static MediaRS convertMedia(Media from) {
        MediaRS to = new MediaRS();

        to.fileId = from.file().blobKey().getStringView();
        to.caption = from.file().caption().getStringView();

        return to;
    }
}
