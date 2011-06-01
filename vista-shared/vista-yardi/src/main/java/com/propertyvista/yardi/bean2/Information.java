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
package com.propertyvista.yardi.bean2;

import javax.xml.bind.annotation.XmlElement;

public class Information {

    private String unitId;

    private String unitEconomicStatusDescription;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("unitId=").append(unitId);
        sb.append(" unitEconomicStatusDescription=").append(unitEconomicStatusDescription);

        return sb.toString();
    }

    @XmlElement(name = "UnitID", namespace = "http://my-company.com/namespace")
    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    @XmlElement(name = "UnitEconomicStatusDescription")
    public String getUnitEconomicStatusDescription() {
        return unitEconomicStatusDescription;
    }

    public void setUnitEconomicStatusDescription(String unitEconomicStatusDescription) {
        this.unitEconomicStatusDescription = unitEconomicStatusDescription;
    }
}
