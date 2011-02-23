/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import java.util.Date;

import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.Unit;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface UnitSelection extends IEntity, IApplicationEntity {

    //Criteria
    IPrimitive<String> floorplanName();

    IPrimitive<String> propertyCode();

    Building building();

    //Criteria
    @Caption(name = "From")
    IPrimitive<Date> availableFrom();

    //Criteria
    @Caption(name = "To")
    IPrimitive<Date> availableTo();

    // Found by App server
    @Transient
    AvailableUnitsByFloorplan availableUnits();

    // user selected Unit from availableUnits..
    Unit selectedUnit();

    // user selected term (from Unit.marketRent list)
    IPrimitive<Integer> selectedUnitLeaseTerm();

    IPrimitive<Integer> leaseTerm();

    @Caption(name = " ")
    IPrimitive<Date> rentStart();
}
