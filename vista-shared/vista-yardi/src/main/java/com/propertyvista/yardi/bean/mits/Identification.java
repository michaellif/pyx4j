/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.bean.mits;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Identification {

    private String type;

    private String primaryId; // building.info.propertyCode

    private String marketingName;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Property ").append(primaryId).append("\n");
        sb.append("(").append(type).append(")\n");
        sb.append(marketingName);

        return sb.toString();
    }

    @XmlAttribute(name = "Type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = "PrimaryID")
    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    @XmlElement(name = "MarketingName")
    public String getMarketingName() {
        return marketingName;
    }

    public void setMarketingName(String marketingName) {
        this.marketingName = marketingName;
    }
}
