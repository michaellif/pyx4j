/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnitType;

public interface Floorplan extends IEntity {

    @Owner
    @Detached
    Building building();

    @ToString(index = 0)
    IPrimitive<String> name();

    IPrimitive<String> marketingName();

    IPrimitive<String> description();

    IPrimitive<Integer> floorCount();

    @MemberColumn(name = "unitType")
    IPrimitive<AptUnitType> type();

    @Format("#0.#")
    @Caption(name = "Beds")
    IPrimitive<Double> bedrooms();

    @Format("#0.#")
    @Caption(name = "Baths")
    IPrimitive<Double> bathrooms();

    @Detached
    // should be loaded in service when necessary!..
    IList<Media> media();
}