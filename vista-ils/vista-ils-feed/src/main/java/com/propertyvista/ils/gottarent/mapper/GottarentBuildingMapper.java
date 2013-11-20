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
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import com.gottarent.rs.Address;
import com.gottarent.rs.Building;
import com.gottarent.rs.BuildingAmenity;
import com.gottarent.rs.BuildingIncentives;
import com.gottarent.rs.BuildingInfo;
import com.gottarent.rs.BuildingRentalOffice;
import com.gottarent.rs.BuildingVacancies;
import com.gottarent.rs.BuildingVacancy;
import com.gottarent.rs.CommunityInfo;
import com.gottarent.rs.ObjectFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyContact.PropertyContactType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.StructureType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.Type;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.ils.common.ILSUtils;
import com.propertyvista.ils.gottarent.mapper.dto.ILSBuildingDTO;
import com.propertyvista.ils.gottarent.mapper.dto.ILSFloorplanDTO;
import com.propertyvista.ils.gottarent.mapper.dto.ILSUnitDTO;

/**
 * The class responsible to convert ILS Building DTO into gottarent Building DTO
 * 
 * @author smolka
 * 
 */
public class GottarentBuildingMapper {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private final ObjectFactory factory;

    public GottarentBuildingMapper(ObjectFactory factory) {
        this.factory = factory;
    }

    private BuildingIncentives createBuildingIncentives(com.propertyvista.domain.property.asset.building.Building from) {
        BuildingIncentives to = factory.createBuildingIncentives();
        return to;
    }

    private BuildingVacancies createBuildingFloorplanVacancies(com.propertyvista.domain.property.asset.building.Building from,
            Collection<ILSFloorplanDTO> fpList) {
        BuildingVacancies to = factory.createBuildingVacancies();
        List<BuildingVacancy> toVacancies = to.getBuildingVacancy();
        if (fpList != null && fpList.size() > 0) {
            for (ILSFloorplanDTO fromVacancy : fpList) {
                toVacancies.add(createBuildingVacancies(fromVacancy));
            }
        }
        return to;
    }

    private BuildingVacancy createBuildingVacancies(ILSFloorplanDTO from) {
        BuildingVacancy to = factory.createBuildingVacancy();
        IList<ILSUnitDTO> units = from.units();
        if (units != null && units.size() > 0) {
            for (ILSUnitDTO unit : units) {
                //to.setBuildingVacancyAltTag(value);// TODO: Smolka
                setAvailability(to, unit);
                to.setBuildingVacancyBaths(ILSUtils.getBathrooms(from.floorplan().bathrooms().getValue(), from.floorplan().halfBath().getValue()).value());
                //to.setBuildingVacancyBedroomSize(value);// TODO: Smolka; does not exist
                to.setBuildingVacancyPrice(from.minPrice().getValue().toPlainString());// TODO: Smolka Why always 0? Should be updated by yaris
                to.setBuildingVacancySize((DomainUtil.getAreaInSqFeet(from.floorplan().area(), from.floorplan().areaUnits())).toString());// Smolka m^2 or feets; number or words?
                to.setExternalBuildingVacancySuiteID(unit.externalId().getValue());
            }
        }
        return to;
    }

    private void setAvailability(BuildingVacancy to, ILSUnitDTO from) {
        if (from.availability() != null && !from.availability().isNull()) {
            to.setBuildingVacancyAvailabilityDate(from.availability().getValue().toString());
            Calendar cal = new GregorianCalendar();
            cal.setTime(new LogicalDate());
            cal.add(Calendar.MONTH, -1);// TODO, Smolka. Which time buffer to check

            if (from.availability().getValue().after(cal.getTime())) {
                to.setBuildingVacancyVisible(ILSUtils.boolean2String(Boolean.TRUE));
                to.setBuildingVacancyEnabled(ILSUtils.boolean2String(Boolean.TRUE));
                return;
            }
        }
        to.setBuildingVacancyVisible(ILSUtils.boolean2String(Boolean.FALSE));
        to.setBuildingVacancyEnabled(ILSUtils.boolean2String(Boolean.FALSE));

    }

    private BuildingRentalOffice createBuildingRentalOffice(ILSBuildingDTO from) {
        BuildingRentalOffice to = factory.createBuildingRentalOffice();
        //to.setBuildingRentalOfficeSaturday(value);// TODO: Smolka, has no value in vista
        //to.setBuildingRentalOfficeSunday(value);// TODO: Smolka, has no value in vista
        //to.setBuildingRentalOfficeWeekday(value);// TODO: Smolka, has no value in vista

        Marketing info = from.building().marketing();
        String phone, email;
        if (!from.profile().preferredContacts().isEmpty()) {
            phone = from.profile().preferredContacts().phone().value().getValue();
            email = from.profile().preferredContacts().email().value().getValue();
        } else {
            phone = info.marketingContacts().phone().value().getValue();
            email = info.marketingContacts().email().value().getValue();
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
        }

        to.setRentalOfficeEmail(email);
        to.setRentalOfficePhone(phone);
        //to.setRentalOfficePhoneExt(value);// TODO: Smolka, has no value in vista
        return to;
    }

    private BuildingAmenity createBuildingAmenity(IList<com.propertyvista.domain.property.asset.building.BuildingAmenity> from) {
        BuildingAmenity to = factory.createBuildingAmenity();
        // TODO: Smolka
        /*
         * List<String> toAmenities = to.getBuildingAmenityName();
         * if (from != null && from.size() > 0) {
         * for (com.propertyvista.domain.property.asset.building.BuildingAmenity fromAmenity : from) {
         * toAmenities.add(fromAmenity.type().getStringView()); // TODO: Smolka Should use aggregated values from units
         * }
         * }
         */
        return to;
    }

    private BuildingInfo createBuildingInfo(com.propertyvista.domain.property.asset.building.Building from) {
        BuildingInfo to = factory.createBuildingInfo();

        //TODO: Smolka, improve description based on extra-properties
        to.setBuildingInfoDescription("<![CDATA[<p>" + from.marketing().description().getValue() + "</p>]]>");
        return to;
    }

    private CommunityInfo createCommunityInfo(IList<com.propertyvista.domain.property.asset.building.BuildingAmenity> from) {
        CommunityInfo to = factory.createCommunityInfo();
        if (from != null && from.size() > 0) {
            for (com.propertyvista.domain.property.asset.building.BuildingAmenity fromAmenity : from) {
                switch (fromAmenity.type().getValue()) {
                case transportation:
                    to.setCommunityInfoDescription("Public transit");
                    return to;
                case childCare:
                    to.setCommunityInfoDescription("Schools nearby");
                    return to;
                default:
                    break;
                }
            }
        }

        return to;
    }

    private Address createAddress(com.propertyvista.domain.property.asset.building.Building from) {
        Address to = factory.createAddress();
        AddressStructured address = from.marketing().marketingAddress();
        if (address.isEmpty()) {
            address = from.info().address();
        }
        to.setBuildingCity(address.city().getStringView());
        to.setBuildingProvinceAbbreviation(address.province().code().getStringView());
        to.setBuildingPostalCode(address.postalCode().getStringView());
        //to.setBuildingRegion(address.country().getStringView());// TODO: Smolka, it is unclear whether we need it
        to.setBuildingStreetAddress(ILSUtils.formatStreetOnly(address));
        to.setBuildingStreetNumber(ILSUtils.formatStreetNumber(address));
        to.setBuildingLatitude(new BigDecimal(from.info().location().getValue().getLat()));
        to.setBuildingLongitude(new BigDecimal(from.info().location().getValue().getLng()));
        //to.setBuildingIntersection(value);  //TODO: Smolka does not exist in vista

        return to;
    }

    public Building createBuilding(ILSBuildingDTO bldDto) {
        com.gottarent.rs.Building to = factory.createBuilding();
        com.propertyvista.domain.property.asset.building.Building from = bldDto.building();
        to.setExternalBuildingId(generateBuildingId(from));
        to.setAddress(createAddress(from));
        to.setBuildingRentalOffice(createBuildingRentalOffice(bldDto));
        to.setBuildingEnabled(ILSUtils.booleanNot2String(bldDto.profile().disabled().getValue()));
        to.setBuildingName(from.info().name().getStringView());
        to.setBuildingIncentives(createBuildingIncentives(from)); // TODO: Smolka,  not now
        setBuildingType(from.info(), to);
        to.setBuildingAmenity(createBuildingAmenity(from.amenities()));
        to.setCommunityInfo(createCommunityInfo(from.amenities()));
        to.setBuildingInfo(createBuildingInfo(from));
        to.setBuildingVacancies(createBuildingFloorplanVacancies(from, bldDto.floorplans()));
        return to;
    }

    private String generateBuildingId(com.propertyvista.domain.property.asset.building.Building from) {
        // TODO: Smolka id or  integrationSystemId()
        return from.id().getStringView();
    }

    private void setBuildingType(com.propertyvista.domain.property.asset.building.BuildingInfo from, com.gottarent.rs.Building to) {
        String value = null;
        StructureType structureType = from.structureType().getValue();

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
        if (value == null) {
            Type buildingType = from.type().getValue();

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
        if (value != null) {
            to.setBuildingType(value);
        }
    }
}
