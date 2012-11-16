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
package com.propertyvista.oapi.marshaling;

import java.util.List;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.oapi.model.AddressRS;
import com.propertyvista.oapi.model.AdvertisingBlurbRS;
import com.propertyvista.oapi.model.AmenityRS;
import com.propertyvista.oapi.model.BuildingInfoRS;
import com.propertyvista.oapi.model.BuildingRS;
import com.propertyvista.oapi.model.BuildingsRS;
import com.propertyvista.oapi.model.FloorplanRS;
import com.propertyvista.oapi.model.GeoLocationRS;
import com.propertyvista.oapi.model.MarketingRS;
import com.propertyvista.oapi.model.MediaRS;
import com.propertyvista.oapi.model.ParkingRS;
import com.propertyvista.oapi.model.PhoneRS;
import com.propertyvista.oapi.model.UtilityRS;

public class Converter {

    private static final I18n i18n = I18n.get(Converter.class);

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

        copyDBOtoRS(from, to);

        return to;
    }

    public static void copyDBOtoRS(Building from, BuildingRS to) {
        to.propertyCode = from.propertyCode().getStringView();

        to.info = convertBuildingInfo(from.info());
        to.marketing = convertMarketing(from.marketing());

        for (PropertyContact contact : from.contacts().propertyContacts()) {
            if (PublicVisibilityType.global.equals(contact.visibility().getValue()) && !contact.phone().isNull()) {
                to.marketing.phones.add(convertPropertyContact(contact));
//                if (EnumSet.of(PropertyContactType.mainOffice, PropertyContactType.pointOfSale).contains(contact.type().getValue())) {
//                    to.contactPhones.add(contact.phone().getValue());
//                    if ((to.contactEmail == null) && (!contact.email().isNull())) {
//                        to.contactEmail = contact.email().getStringView();
//                    }
//                }
            }

        }
    }

    public static MarketingRS convertMarketing(Marketing from) {
        MarketingRS to = new MarketingRS();

        to.name = from.name().getStringView();
        to.description = from.description().getStringView();

        for (AdvertisingBlurb blurb : from.adBlurbs()) {
            AdvertisingBlurbRS blurbRS = convertBlurb(blurb);
            to.blurbs.add(blurbRS);
        }

        return to;
    }

    public static AdvertisingBlurbRS convertBlurb(AdvertisingBlurb blurb) {
        AdvertisingBlurbRS to = new AdvertisingBlurbRS();

        to.content = blurb.content().getStringView();

        return to;
    }

    public static PhoneRS convertPropertyContact(PropertyContact propertyContact) {
        PhoneRS to = new PhoneRS();
        to.number = propertyContact.phone().getStringView();
        to.description = propertyContact.description().getStringView();
        return to;
    }

    public static BuildingInfoRS convertBuildingInfo(BuildingInfo from) {
        BuildingInfoRS to = new BuildingInfoRS();

        to.name = from.name().getStringView();
        to.address = convertAddress(from.address());
        if (!from.type().isNull()) {
            to.buildingType = BuildingInfoRS.BuildingType.valueOf(from.type().getValue().name());
        }
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

    public static AddressRS convertAddress(AddressStructured from) {
        AddressRS to = new AddressRS();

        to.streetName = from.streetName().getStringView();
        to.streetNumber = from.streetNumber().getStringView();
        to.streetNumberSuffix = from.streetNumberSuffix().getStringView();
        if (!from.streetDirection().isNull()) {
            to.streetDirection = from.streetDirection().getValue().name();
        }
        if (!from.streetType().isNull()) {
            to.streetType = from.streetType().getValue().name();
        }
        if (!from.suiteNumber().isNull()) {
            to.unitNumber = from.suiteNumber().getValue();
        }
        to.city = from.city().getStringView();
        to.province = from.province().getStringView();
        to.provinceCode = from.province().code().getStringView();
        to.postalCode = from.postalCode().getStringView();
        to.country = from.country().getStringView();
        if (!from.county().isNull()) {
            to.county = from.county().getStringView();
        }

        if (!from.location().isNull()) {
            to.location = new GeoLocationRS();
            to.location.latitude = from.location().getValue().getLat();
            to.location.longitude = from.location().getValue().getLng();
        }

        return to;
    }

    public static AmenityRS convertBuildingAmenity(BuildingAmenity from) {
        AmenityRS to = new AmenityRS();

        if (!from.name().isNull()) {
            to.name = from.name().getStringView();
        } else {
            to.name = from.type().getStringView();
        }
        to.description = from.description().getStringView();

        return to;
    }

    public static UtilityRS convertBuildingIncludedUtility(Utility from) {
        UtilityRS to = new UtilityRS();

        to.name = from.name().getStringView();

        return to;
    }

    public static ParkingRS convertParking(Parking from) {
        ParkingRS to = new ParkingRS();

        to.name = from.name().getStringView();
        to.description = from.name().getStringView();

        return to;
    }

    public static AmenityRS convertFloorplanAmenity(FloorplanAmenity from) {
        AmenityRS to = new AmenityRS();

        if (!from.name().isNull()) {
            to.name = from.name().getStringView();
        } else {
            to.name = from.type().getStringView();
        }
        to.description = from.description().getStringView();

        return to;
    }

    public static FloorplanRS convertFloorplan(Floorplan from) {
        FloorplanRS to = new FloorplanRS();

        to.name = from.name().getStringView();
        to.marketingName = from.marketingName().getStringView();
        if (CommonsStringUtils.isEmpty(to.marketingName)) {
            to.marketingName = i18n.tr("{0} Bedroom", from.bedrooms().getStringView());
        }
        to.description = from.description().getStringView();
        to.floorCount = from.floorCount().getValue();
        to.bedrooms = from.bedrooms().getValue();
        to.dens = from.dens().getValue();
        to.bathrooms = from.bathrooms().getValue();
        to.halfBath = from.halfBath().getValue();

        return to;
    }

    public static MediaRS convertMedia(Media from) {
        MediaRS to = new MediaRS();

        switch (from.type().getValue()) {
        case file:
            to.fileId = from.id().getStringView();
            to.mediaType = MediaRS.MediaType.file;
            to.mimeType = from.file().contentMimeType().getStringView();
            break;
        case externalUrl:
            to.mediaType = MediaRS.MediaType.externalUrl;
            to.url = from.url().getValue();
            break;
        case youTube:
            to.mediaType = MediaRS.MediaType.youTube;
            to.youTubeVideoID = from.youTubeVideoID().getValue();
        }
        to.caption = from.caption().getStringView();

        return to;
    }
}
