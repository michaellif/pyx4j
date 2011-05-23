/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 21, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.importer.bean;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class Room {

    private Integer brcount; // no value in xml

    private Integer rate; // no value in xml

    private Integer sqft; // no value in xml

    private String display; // floorplan.description

    private String name; // floorplan.name

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(name);
        sb.append(" ").append(display);
        sb.append(" ").append(sqft).append(" sqft, ");
        sb.append(rate).append(" ").append(brcount).append(" brcount");

        return sb.toString();
    }

    @XmlAttribute
    public Integer getBrcount() {
        return brcount;
    }

    public void setBrcount(Integer brcount) {
        this.brcount = brcount;
    }

    @XmlAttribute
    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    @XmlAttribute
    public Integer getSqft() {
        return sqft;
    }

    public void setSqft(Integer sqft) {
        this.sqft = sqft;
    }

    @XmlAttribute
    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @XmlValue
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
