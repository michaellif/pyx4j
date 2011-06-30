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
package com.propertyvista.yardi.bean.resident;

import javax.xml.bind.annotation.XmlElement;

import com.propertyvista.yardi.bean.mits.Unit;

public class RTUnit {

    private String unitId;

    private NumberOccupants numberOccupants;

    private Unit unit;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("#");
        sb.append(unitId);
        sb.append(", ");
        sb.append(numberOccupants.getTotal()).append(" occupants");
        sb.append("\n").append(unit);

        return sb.toString();
    }

    @XmlElement(name = "UnitID")
    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    @XmlElement(name = "NumberOccupants")
    public NumberOccupants getNumberOccupants() {
        return numberOccupants;
    }

    public void setNumberOccupants(NumberOccupants numberOccupants) {
        this.numberOccupants = numberOccupants;
    }

    @XmlElement(name = "Unit")
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
