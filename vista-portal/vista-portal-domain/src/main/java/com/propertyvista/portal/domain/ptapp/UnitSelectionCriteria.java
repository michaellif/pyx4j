/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.domain.ptapp;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

//TODO this needs to be saved as part of UnitSelection BUT not by itself
//@Transient
public interface UnitSelectionCriteria extends IEntity {

    // TODO change the query later to use building id and floorplan id

    IPrimitive<String> floorplanName();

    // as per Vlad's suggestion we will be using property code instead of building name
    IPrimitive<String> propertyCode();

    //Criteria
    @Caption(name = "From")
    IPrimitive<Date> availableFrom();

    //Criteria
    @Caption(name = "To")
    IPrimitive<Date> availableTo();
}
