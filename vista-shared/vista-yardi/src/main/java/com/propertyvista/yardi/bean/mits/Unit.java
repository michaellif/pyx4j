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
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Unit {

    protected String propertyPrimaryId;

    private String marketingName;

    private Information information;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(propertyPrimaryId);
        sb.append(", ").append(marketingName);
        if (information != null) {
            sb.append("\n");
            sb.append(information);
        }

        return sb.toString();
    }

    @XmlElement(name = "MarketingName", required = true)
    public String getMarketingName() {
        return marketingName;
    }

    public void setMarketingName(String marketingName) {
        this.marketingName = marketingName;
    }

    @XmlElement(name = "PropertyPrimaryID", required = true)
    public String getPropertyPrimaryId() {
        return propertyPrimaryId;
    }

    public void setPropertyPrimaryId(String propertyPrimaryId) {
        this.propertyPrimaryId = propertyPrimaryId;
    }

    @XmlElement(name = "Information", required = true)
    public Information getInformation() {
        return information;
    }

    public void setInformation(Information information) {
        this.information = information;
    }
}
