/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.importer.csv;

import java.util.Date;

public class AvailableUnit {

    private String propertyCode; // aptUnit.building - set actual building

    private String address; // building has it

    private String city; // building has it

    private String province; // building has it

    private String unitNumber; // aptUnit.info.name

    private String type; // aptUnit.info.type

    private Double area; // aptUnit.info.area

    private Double rent;

    private String description; // aptUnit.info.typeDescription

    private Date available; // aptUnit.currentOccupancy.dateFrom

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(propertyCode).append(" #").append(unitNumber);
        sb.append(" $").append(rent).append(" ");
        sb.append(area).append(" sqft, availalbe ").append(available);

        return sb.toString();
    }

    public String getPropertyCode() {
        return propertyCode;
    }

    public void setPropertyCode(String propertyCode) {
        this.propertyCode = propertyCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Double getRent() {
        return rent;
    }

    public void setRent(Double rent) {
        this.rent = rent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getAvailable() {
        return available;
    }

    public void setAvailable(Date available) {
        this.available = available;
    }
}
