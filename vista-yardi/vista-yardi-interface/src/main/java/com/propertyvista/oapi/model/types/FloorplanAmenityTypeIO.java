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

import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.oapi.xml.Action;
import com.propertyvista.oapi.xml.PrimitiveIO;

public class FloorplanAmenityTypeIO implements PrimitiveIO<FloorplanAmenity.Type> {

    private FloorplanAmenity.Type value;

    private Action action;

    public FloorplanAmenityTypeIO() {
    }

    public FloorplanAmenityTypeIO(FloorplanAmenity.Type value) {
        this.value = value;
    }

    @Override
    @XmlValue
    public FloorplanAmenity.Type getValue() {
        return value;
    }

    @Override
    public void setValue(FloorplanAmenity.Type value) {
        this.value = value;
    }

    @Override
    public Action getAction() {
        return action;
    }
}
