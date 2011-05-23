/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 20, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.importer.bean;

import javax.xml.bind.annotation.XmlAttribute;

public class Property {
    private String code; // building.info.propertyCode

    private String name; // building.info.name

    private String type; // building.info.structureType

    private Integer unitcount; // number of AptUnit

    private Integer sqft; // always zero in xml, not using for now

    private Integer floors; // used in mapper

    private String rentrange; // TODO not found in Vista

    private Address address; // building.info.address

    private String introduction; // TODO not found in Vista

    private Rooms rooms; // floorplan

    private Includes includes; // unit.utitilities

    private ParkingTypes parkingtypes; // TODO needs to be determined

    private Contact contact; // building.contacts

    private AdjacentProperty adjacentprop; // TODO not found

    private Schools schools; // TODO not found

    private String promotions; // empty in XML

    private String maplink; // empty in XML

    private String otherinfo; // not found

    private String youtube; // TODO this probably goes to building.marketing

    private String building; // TODO building.name is already taken, must be
                             // something else

    private String thirdparty; // TODO not found

    private String website; // building.contacts.website

    private String featured;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(code);
        sb.append(" ").append(name).append(" (").append(type).append(")");

        sb.append("\n");
        sb.append(unitcount).append(" units, ");
        sb.append(floors).append(" floors, ");
        sb.append(sqft).append(" sqft, ");
        sb.append(rentrange);

        sb.append("\n");
        sb.append(introduction);

        sb.append("\n");
        sb.append(address);

        sb.append("\n");
        sb.append(rooms);

        sb.append("\n");
        sb.append(includes);

        sb.append("\n");
        sb.append(parkingtypes);

        sb.append("\n").append(contact);
        sb.append("\n").append(schools);

        sb.append("\n").append(otherinfo);
        sb.append("\n").append(youtube);

        sb.append("\n").append(building).append(",").append(thirdparty);
        sb.append("\n").append(website);
        sb.append("\n").append(featured);

        return sb.toString();
    }

    @XmlAttribute
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getUnitcount() {
        return unitcount;
    }

    public void setUnitcount(Integer unitcount) {
        this.unitcount = unitcount;
    }

    public Integer getSqft() {
        return sqft;
    }

    public void setSqft(Integer sqft) {
        this.sqft = sqft;
    }

    public Integer getFloors() {
        return floors;
    }

    public void setFloors(Integer floors) {
        this.floors = floors;
    }

    public String getRentrange() {
        return rentrange;
    }

    public void setRentrange(String rentrange) {
        this.rentrange = rentrange;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Rooms getRooms() {
        return rooms;
    }

    public void setRooms(Rooms rooms) {
        this.rooms = rooms;
    }

    public Includes getIncludes() {
        return includes;
    }

    public void setIncludes(Includes includes) {
        this.includes = includes;
    }

    public ParkingTypes getParkingtypes() {
        return parkingtypes;
    }

    public void setParkingtypes(ParkingTypes parkingtypes) {
        this.parkingtypes = parkingtypes;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public AdjacentProperty getAdjacentprop() {
        return adjacentprop;
    }

    public void setAdjacentprop(AdjacentProperty adjacentprop) {
        this.adjacentprop = adjacentprop;
    }

    public Schools getSchools() {
        return schools;
    }

    public void setSchools(Schools schools) {
        this.schools = schools;
    }

    public String getPromotions() {
        return promotions;
    }

    public void setPromotions(String promotions) {
        this.promotions = promotions;
    }

    public String getMaplink() {
        return maplink;
    }

    public void setMaplink(String maplink) {
        this.maplink = maplink;
    }

    public String getOtherinfo() {
        return otherinfo;
    }

    public void setOtherinfo(String otherinfo) {
        this.otherinfo = otherinfo;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getThirdparty() {
        return thirdparty;
    }

    public void setThirdparty(String thirdparty) {
        this.thirdparty = thirdparty;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }
}
