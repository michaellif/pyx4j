/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 7, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.geo.GeoPoint;

import com.propertyvista.domain.RangeGroup;
import com.propertyvista.domain.marketing.yield.Amenity;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;

public class ApartmentModel {
    private final Map<Long, Property> propertyList = new HashMap<Long, Property>();

    public void setBuildingList(List<Building> bldList) {
        for (Building bld : bldList) {
            Property prop = new Property();
            prop.ID = bld.id().getValue().asLong();
            prop.address.street1 = bld.info().address().streetName().getValue();
            prop.address.street2 = bld.info().address().streetNumber().getValue();
            prop.address.city = bld.info().address().city().getValue();
            prop.address.province = bld.info().address().province().name().getValue();
            prop.address.country = bld.info().address().country().name().getValue();
            prop.address.postalCode = bld.info().address().postalCode().getValue();
            prop.description = bld.marketing().description().getValue();
            prop.location = bld.info().address().location().getValue();

            propertyList.put(prop.ID, prop);
        }
    }

    public void putBuildingAmenities(long propId, List<Amenity> amList) {
        Property prop = propertyList.get(propId);
        if (prop == null) {
            return;
        }
        prop.amenities = new ArrayList<String>(amList.size());
        for (Amenity am : amList) {
            prop.amenities.add(am.name().getValue());
        }
    }

    public void putBuildingMedia(long propId, List<Media> medList) {
        Property prop = propertyList.get(propId);
        if (prop == null) {
            return;
        }
        prop.mediaList = new ArrayList<Long>(medList.size());
        for (Media med : medList) {
            prop.mediaList.add(med.id().getValue().asLong());
        }
    }

    public void putBuildingUnits(long propId, List<Floorplan> fpList) {
        Property prop = propertyList.get(propId);
        if (prop == null) {
            return;
        }
        prop.unitList = new HashMap<Long, UnitType>(fpList.size());
        for (Floorplan fp : fpList) {
            UnitType unit = new UnitType();
            unit.ID = fp.id().getValue().asLong();
            unit.name = fp.name().getValue();
            unit.description = fp.description().getValue();
            unit.bedrooms = fp.bedrooms().getValue();
            unit.bathrooms = fp.bathrooms().getValue();
            unit.halfBath = fp.halfBath().getValue();
            prop.unitList.put(unit.ID, unit);
        }
    }

    public void setBuildingUnitPrice(long propId, long unitId, double price) {
        Property prop = propertyList.get(propId);
        if (prop != null) {
            UnitType unit = prop.unitList.get(unitId);
            if (unit != null) {
                unit.rentFrom = price;
            }
        }
    }

    public void setBuildingUnitArea(long propId, long unitId, int area) {
        Property prop = propertyList.get(propId);
        if (prop != null) {
            UnitType unit = prop.unitList.get(unitId);
            if (unit != null) {
                unit.sqftFrom = area;
            }
        }
    }

    public void putBuildingUnitAmenities(long propId, long unitId, List<Amenity> amList) {
        Property prop = propertyList.get(propId);
        if (prop == null) {
            return;
        }
        UnitType unit = prop.unitList.get(unitId);
        if (unit == null) {
            return;
        }
        unit.amenities = new ArrayList<String>(amList.size());
        for (Amenity am : amList) {
            unit.amenities.add(am.name().getValue());
        }
    }

    public void putBuildingUnitMedia(long propId, long unitId, List<Media> medList) {
        Property prop = propertyList.get(propId);
        if (prop == null) {
            return;
        }
        UnitType unit = prop.unitList.get(unitId);
        if (unit == null) {
            return;
        }
        unit.mediaList = new ArrayList<Long>(medList.size());
        for (Media med : medList) {
            unit.mediaList.add(med.id().getValue().asLong());
        }
    }

    public Map<Long, Property> getPropertyList() {
        return propertyList;
    }

    public class Property {
        private long ID;

        private Address address;

        private GeoPoint location;

        private RangeGroup price;

        private String description;

        private List<String> amenities;

        private List<Long> mediaList;

        private Date availableForRent;

        private Map<Long, UnitType> unitList;

        public long getId() {
            return ID;
        }

        public Address getAddress() {
            return address;
        }

        public GeoPoint getLocation() {
            return location;
        }

        public RangeGroup getPrice() {
            return price;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getAmenities() {
            return amenities;
        }

        public List<Long> getMediaList() {
            return mediaList;
        }

        public Date getAvailableForRent() {
            return availableForRent;
        }

        public Map<Long, UnitType> getUnitList() {
            return unitList;
        }
    }

    public class UnitType {
        private long ID;

        private String name;

        private String description;

        private Integer bedrooms;

        private Integer bathrooms;

        private Integer halfBath;

        private Double rentFrom;

        private Integer sqftFrom;

        private Date available;

        private List<String> amenities;

        private List<Long> mediaList;

        public long getId() {
            return ID;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Integer getBedrooms() {
            return bedrooms;
        }

        public Integer getBathrooms() {
            return bathrooms;
        }

        public Integer getHalfBath() {
            return halfBath;
        }

        public Double getRentFrom() {
            return rentFrom;
        }

        public Integer getSqftFrom() {
            return sqftFrom;
        }

        public Date getAvailable() {
            return available;
        }

        public List<String> getAmenities() {
            return amenities;
        }

        public List<Long> getMediaList() {
            return mediaList;
        }
    }

    public class Address {
        private String street1;

        private String street2;

        private String city;

        private String province;

        private String country;

        private String postalCode;

        public String getStreet1() {
            return street1;
        }

        public String getStreet2() {
            return street2;
        }

        public String getCity() {
            return city;
        }

        public String getProvince() {
            return province;
        }

        public String getCountry() {
            return country;
        }

        public String getPostalCode() {
            return postalCode;
        }
    }
}
