/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 28, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.bean.mits;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 <MITS:Information>
 * <MITS:UnitID>411</MITS:UnitID>
 * <MITS:UnitType>prv11b1</MITS:UnitType>
 * <MITS:UnitBedrooms>1</MITS:UnitBedrooms>
 * <MITS:UnitBathrooms>1.000000</MITS:UnitBathrooms>
 * <MITS:MinSquareFeet>900</MITS:MinSquareFeet>
 * <MITS:MaxSquareFeet>900</MITS:MaxSquareFeet>
 * <MITS:UnitRent>850.00</MITS:UnitRent>
 * <MITS:MarketRent>850.000000</MITS:MarketRent>
 * <MITS:UnitEconomicStatus>residential</MITS:UnitEconomicStatus>
 * <MITS:UnitOccupancyStatus>occupied</MITS:UnitOccupancyStatus>
 * <MITS:UnitLeasedStatus>on notice</MITS:UnitLeasedStatus>
 * <MITS:FloorPlanID>prv11b1</MITS:FloorPlanID>
 * <MITS:FloorplanName>1 Bedroom 1 Bath</MITS:FloorplanName>
 * </MITS:Information>
 */
public class Information {

    private String unitId; // same as unitId in the two classes above

    private String unitType; // TODO prv11b1 - not sure how to decipher this (b1 - 1 bedroom?)

    private Double unitBedrooms; // unit.info.bedrooms

    private Double unitBathrooms; // unit.info.bathrooms

    private Integer minSquareFeet; // TODO not found yet 

    private Integer maxSquareFeet; // unit.info.area

    private Double unitRent;

    private Double marketRent;

    private String unitEconomicStatus; // unit.info.economicStatus

    private String unitOccupancyStatus;

    private String unitLeasedStatus;

    private String floorplanId;

    private String floorplanName;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (unitType != null) {
            sb.append("(").append(unitType).append(") ");
        }

        if (unitBedrooms != null) {
            sb.append(unitBedrooms).append(" bedrooms, ");
        }
        if (unitBathrooms != null) {
            sb.append(unitBathrooms).append(" bathrooms ");
        }
        if (minSquareFeet != null) {
            sb.append(minSquareFeet).append(" sq ft to ");
        }
        if (maxSquareFeet != null) {
            sb.append(maxSquareFeet).append(" sq ft\n");
        }
        if (unitRent != null) {
            sb.append("$").append(unitRent).append(", $");
        }
        if (marketRent != null) {
            sb.append(marketRent).append("\n");
        }
        if (unitEconomicStatus != null) {
            sb.append("Economic: ").append(unitEconomicStatus);
        }
        if (unitOccupancyStatus != null) {
            sb.append(", Occupancy: ").append(unitOccupancyStatus);
        }
        if (unitLeasedStatus != null) {
            sb.append(", Leased: ").append(unitLeasedStatus);
        }
        if (floorplanId != null) {
            sb.append("\n").append(floorplanId).append(" ").append(floorplanName);
        }

        return sb.toString();
    }

    @XmlElement(name = "UnitID")
    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    @XmlElement(name = "UnitType")
    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    @XmlElement(name = "UnitBedrooms")
    public Double getUnitBedrooms() {
        return unitBedrooms;
    }

    public void setUnitBedrooms(Double unitBedrooms) {
        this.unitBedrooms = unitBedrooms;
    }

    @XmlElement(name = "UnitBathrooms")
    public Double getUnitBathrooms() {
        return unitBathrooms;
    }

    public void setUnitBathrooms(Double unitBathrooms) {
        this.unitBathrooms = unitBathrooms;
    }

    @XmlElement(name = "MinSquareFeet")
    public Integer getMinSquareFeet() {
        return minSquareFeet;
    }

    public void setMinSquareFeet(Integer minSquareFeet) {
        this.minSquareFeet = minSquareFeet;
    }

    @XmlElement(name = "MaxSquareFeet")
    public Integer getMaxSquareFeet() {
        return maxSquareFeet;
    }

    public void setMaxSquareFeet(Integer maxSquareFeet) {
        this.maxSquareFeet = maxSquareFeet;
    }

    @XmlElement(name = "UnitRent")
    public Double getUnitRent() {
        return unitRent;
    }

    public void setUnitRent(Double unitRent) {
        this.unitRent = unitRent;
    }

    @XmlElement(name = "MarketRent")
    public Double getMarketRent() {
        return marketRent;
    }

    public void setMarketRent(Double marketRent) {
        this.marketRent = marketRent;
    }

    @XmlElement(name = "UnitEconomicStatus")
    public String getUnitEconomicStatus() {
        return unitEconomicStatus;
    }

    public void setUnitEconomicStatus(String unitEconomicStatus) {
        this.unitEconomicStatus = unitEconomicStatus;
    }

    @XmlElement(name = "UnitOccupancyStatus")
    public String getUnitOccupancyStatus() {
        return unitOccupancyStatus;
    }

    public void setUnitOccupancyStatus(String unitOccupancyStatus) {
        this.unitOccupancyStatus = unitOccupancyStatus;
    }

    @XmlElement(name = "UnitLeasedStatus")
    public String getUnitLeasedStatus() {
        return unitLeasedStatus;
    }

    public void setUnitLeasedStatus(String unitLeasedStatus) {
        this.unitLeasedStatus = unitLeasedStatus;
    }

    @XmlElement(name = "FloorPlanID")
    public String getFloorplanId() {
        return floorplanId;
    }

    public void setFloorplanId(String floorplanId) {
        this.floorplanId = floorplanId;
    }

    @XmlElement(name = "FloorplanName")
    public String getFloorplanName() {
        return floorplanName;
    }

    public void setFloorplanName(String floorplanName) {
        this.floorplanName = floorplanName;
    }
}
