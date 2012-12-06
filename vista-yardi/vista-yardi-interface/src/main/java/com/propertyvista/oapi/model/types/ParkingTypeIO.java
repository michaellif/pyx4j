/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.model.types;

import javax.xml.bind.annotation.XmlValue;

import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.oapi.xml.Action;
import com.propertyvista.oapi.xml.PrimitiveIO;

public class ParkingTypeIO implements PrimitiveIO<Parking.Type> {

    private Parking.Type value;

    private Action action;

    public ParkingTypeIO() {
    }

    public ParkingTypeIO(Parking.Type value) {
        this.value = value;
    }

    @Override
    @XmlValue
    public Parking.Type getValue() {
        return value;
    }

    @Override
    public void setValue(Parking.Type value) {
        this.value = value;
    }

    @Override
    public Action getAction() {
        return action;
    }
}
