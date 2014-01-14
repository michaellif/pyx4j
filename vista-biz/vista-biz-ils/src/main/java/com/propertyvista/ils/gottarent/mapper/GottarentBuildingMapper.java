/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 17, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils.gottarent.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.gottarent.rs.Address;
import com.gottarent.rs.Building;
import com.gottarent.rs.BuildingAmenity;
import com.gottarent.rs.BuildingInfo;
import com.gottarent.rs.BuildingRentalOffice;
import com.gottarent.rs.BuildingVacancies;
import com.gottarent.rs.CommunityAmenity;
import com.gottarent.rs.ObjectFactory;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.geo.GeoPoint;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.marketing.MarketingContacts;
import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyContact.PropertyContactType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.StructureType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.Type;
import com.propertyvista.ils.gottarent.mapper.dto.ILSBuildingDTO;

/**
 * The class responsible to convert ILS Building DTO into gottarent Building DTO
 * 
 * @author smolka
 * 
 */
public class GottarentBuildingMapper {
    private static final short MAX_NAME_LENGTH = 120;

    private final ObjectFactory factory;

    public GottarentBuildingMapper(ObjectFactory factory) {
        this.factory = factory;
    }

    public Building createBuilding(ILSBuildingDTO bldDto) {
        com.propertyvista.domain.property.asset.building.Building from = bldDto.building();

        Address address = getAddress(from);
        String id = getBuildingId(from);
        String buildingName = generateBuildingName(bldDto);
        BuildingRentalOffice rentalOffice = getBuildingRentalOffice(bldDto);
        // if no mandatory fields, return
        if (address == null || id == null || buildingName == null || rentalOffice == null) {
            return null;
        }
        Building to = factory.createBuilding();

        to.setExternalBuildingId(id);
        to.setBuildingName(buildingName);
        to.setAddress(address);
        to.setBuildingEnabled(GottarentMapperUtils.booleanNot2String(bldDto.profile().disabled().getValue()));
        to.setBuildingRentalOffice(rentalOffice);

        setBuildingTypeOptionalData(from.info(), to);
        setBuildingInfoOptionalData(bldDto, to);

        setCommunityAmenityOptionalData(from.amenities(), to);
        //to.setBuildingIncentives(createBuildingIncentives(from)); // TODO: Smolka,  not now
        setBuildingAmenityOptionalData(from.amenities(), to);

        BuildingVacancies vacancies = new GottarentUnitMapper(factory, from).createBuildingFloorplanVacancies(bldDto.floorplans());
        if (vacancies.getBuildingVacancy().size() > 0) {
            to.setBuildingVacancies(vacancies);
        }
        return to;
    }

    private BuildingRentalOffice getBuildingRentalOffice(ILSBuildingDTO from) {
        Marketing info = from.building().marketing();
        if (GottarentMapperUtils.isNull(info) || info.isEmpty()) {
            return null;
        }
        String phone = null, email = null;
        ILSProfileBuilding profile = from.profile();
        if (GottarentMapperUtils.isNull(profile) || profile.isEmpty() || GottarentMapperUtils.isNull(from.profile().preferredContacts())
                || from.profile().preferredContacts().isEmpty()) {
            MarketingContacts marketingContacts = info.marketingContacts();
            phone = marketingContacts.phone().value().getValue();
            email = marketingContacts.email().value().getValue();
            if (email == null || phone == null) {
                // check main office contact
                Persistence.service().retrieveMember(from.building().contacts().propertyContacts(), AttachLevel.Attached);
                for (PropertyContact contact : from.building().contacts().propertyContacts()) {
                    if (contact.type().getValue() == PropertyContactType.mainOffice) {
                        if (email == null) {
                            email = contact.email().getValue();
                        }
                        if (phone == null) {
                            phone = contact.phone().getValue();
                        }
                    }
                }
            }
        } else {
            phone = from.profile().preferredContacts().phone().value().getValue();
            email = from.profile().preferredContacts().email().value().getValue();
        }

        if (email == null) {
            return null;
        }

        BuildingRentalOffice rentalOffice = factory.createBuildingRentalOffice();
        rentalOffice.setRentalOfficeEmail(email);

        if (phone != null) {
            rentalOffice.setRentalOfficePhone(GottarentMapperUtils.formatPhone(phone));
        }

        //to.setBuildingRentalOfficeSaturday(value);// TODO: Smolka, has no value in vista
        //to.setBuildingRentalOfficeSunday(value);// TODO: Smolka, has no value in vista
        //to.setBuildingRentalOfficeWeekday(value);// TODO: Smolka, has no value in vista

        //to.setRentalOfficePhoneExt(value);// TODO: Smolka, has no value in vista
        return rentalOffice;
    }

    private void setBuildingAmenityOptionalData(IList<com.propertyvista.domain.property.asset.building.BuildingAmenity> from, com.gottarent.rs.Building to) {
        if (GottarentMapperUtils.isNull(from) || from.size() < 1) {
            return;
        }

        BuildingAmenity toAmenity = factory.createBuildingAmenity();

        List<String> amenities = toAmenity.getBuildingAmenityName();

        for (com.propertyvista.domain.property.asset.building.BuildingAmenity fromAmenity : from) {

            if (!fromAmenity.isNull() && !fromAmenity.isEmpty() && fromAmenity.type() != null) {
                switch (fromAmenity.type().getValue()) {
                case coveredParking:
                    amenities.add("Covered parking");
                    break;
                case elevator:
                    amenities.add("Elevators");
                    break;
                case groupExercise:
                    amenities.add("Exercise room");
                    break;
                case fitness:
                case fitnessCentre:
                    amenities.add("Fitness room");
                    break;
                case storageSpace:
                    amenities.add("In-suite storage");
                    break;
                case laundry:
                    amenities.add("Laundry onsite");
                    break;
                case doorAttendant:
                case housekeeping:
                case houseSitting:
                case concierge:
                case onSiteManagement:
                    amenities.add("On-site staff");
                    break;
                case parking:
                    amenities.add("Parking available");
                    break;
                case basketballCourt:
                case volleyballCourt:
                case playGround:
                case tennisCourt:
                    amenities.add("Outdoor play area");
                    break;
                case recreationalRoom:
                    amenities.add("Recreation room");
                    break;
                case sauna:
                    amenities.add("Sauna");
                    break;
                case tvLounge:
                    amenities.add("Satellite included");
                    break;
                case pool:
                    amenities.add("Swimming poold");
                    break;
                default:
                    // TODO: Smolka Should use aggregated values from units
                    break;
                }
            }
        }

        if (amenities.size() > 0) {
            to.setBuildingAmenity(toAmenity);
        }
    }

    private void setBuildingInfoOptionalData(ILSBuildingDTO bldDto, com.gottarent.rs.Building to) {
        String description = bldDto.building().marketing().description().getValue();
        if (description == null || description.trim().isEmpty()) {
            return;
        }
        BuildingInfo buildingInfo = factory.createBuildingInfo();

        //TODO: Smolka, improve description based on extra-properties
        description = "<![CDATA[" + description + "]]>";
        buildingInfo.setBuildingInfoDescription(description);
        to.setBuildingInfo(buildingInfo);
    }

    private void setCommunityAmenityOptionalData(IList<com.propertyvista.domain.property.asset.building.BuildingAmenity> from, com.gottarent.rs.Building to) {
        if (GottarentMapperUtils.isNull(from) || from.size() < 1) {
            return;
        }

        CommunityAmenity amenities = factory.createCommunityAmenity();

        for (com.propertyvista.domain.property.asset.building.BuildingAmenity fromAmenity : from) {
            if (!fromAmenity.type().isNull()) {
                switch (fromAmenity.type().getValue()) {
                case transportation:
                    amenities.getCommunityAmenityName().add("Public transit");
                    break;
                case childCare:
                    amenities.getCommunityAmenityName().add("Schools nearby");
                    break;
                default:
                    break;
                }
            }
        }

        if (amenities.getCommunityAmenityName().size() > 0) {
            to.setCommunityAmenity(amenities);
        }
    }

    private Address getAddress(com.propertyvista.domain.property.asset.building.Building from) {
        AddressStructured address = GottarentMapperUtils.getAddress(from);
        // if no mandatory fields, return
        if (GottarentMapperUtils.isNull(address) || address.isEmpty() || GottarentMapperUtils.isNull(address.province()) || address.province().isEmpty()
                || GottarentMapperUtils.isNullOrEmpty(address.city())) {
            return null;
        }

        String street = GottarentMapperUtils.formatStreetOnly(address);
        String number = GottarentMapperUtils.formatStreetNumber(address);
        // if no mandatory fields, return
        if (street == null || number == null || street.trim().isEmpty() || number.trim().isEmpty()) {
            return null;
        }

        Address to = factory.createAddress();
        to.setBuildingProvinceAbbreviation(address.province().code().getStringView());
        to.setBuildingCity(address.city().getStringView());
        if (!GottarentMapperUtils.isNullOrEmpty(address.postalCode())) {
            to.setBuildingPostalCode(address.postalCode().getStringView());
        }
        to.setBuildingStreetAddress(street);
        to.setBuildingStreetNumber(number);
        setGeoLocationOptionalData(from, to);

        return to;
    }

    private void setGeoLocationOptionalData(com.propertyvista.domain.property.asset.building.Building from, Address to) {
        GeoPoint gp = GottarentMapperUtils.isNull(from.info()) || from.info().isEmpty() || GottarentMapperUtils.isNull(from.info().location()) ? null : from
                .info().location().getValue();
        if (gp != null) {
            to.setBuildingLatitude(new BigDecimal(gp.getLat()));
            to.setBuildingLongitude(new BigDecimal(gp.getLng()));
        }
    }

    public static String getBuildingId(com.propertyvista.domain.property.asset.building.Building from) {
        return from.propertyCode().getValue();
    }

    private String generateBuildingName(ILSBuildingDTO bldDto) {
        String result = bldDto.building().marketing().name().getValue();

        if (result == null) {
            return null;
        }

        result = result.trim();
        if (result.isEmpty()) {
            return null;
        }
        return result.length() > MAX_NAME_LENGTH ? result.substring(0, MAX_NAME_LENGTH) : result;
    }

    private void setBuildingTypeOptionalData(com.propertyvista.domain.property.asset.building.BuildingInfo from, Building to) {
        if (from == null) {
            return;
        }
        String value = null;
        StructureType structureType = from.structureType().getValue();

        if (structureType != null) {
            switch (structureType) {
            case lowRise:
                value = "Low rise";
                break;
            case highRise:
                value = "High rise";
                break;
            case condo:
                value = "Condo";
                break;
            case townhouse:
                value = "Townhouse";
                break;
            default:
                break;
            }
        }

        if (value == null) {
            Type buildingType = from.type().getValue();

            if (buildingType != null) {
                switch (buildingType) {
                case commercial:
                    value = "Commercial";
                    break;
                case industrial:
                    value = "Industrial";
                    break;
                case condo:
                    value = "Condo";
                    break;
                default:
                    break;
                }
            }
        }
        to.setBuildingType(value == null ? "Apartment" : value);
    }

}
