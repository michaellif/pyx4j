/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.ref.City;

@Transient
public interface PropertySearchCriteria extends IEntity {

    public enum SearchType {
        city, proximity;
    }

    @NotNull
    @Caption(name = "SEARCH BY")
    IPrimitive<SearchType> searchType();

    City city();

    IPrimitive<String> location();

    IPrimitive<Integer> distance();

    IPrimitive<LogicalDate> startingFrom();

    IPrimitive<Integer> minBeds();

    IPrimitive<Integer> maxBeds();

    IPrimitive<Integer> minBath();

    IPrimitive<Integer> maxBath();

    IPrimitive<Integer> minPrice();

    IPrimitive<Integer> maxPrice();

    IPrimitive<Boolean> elevator();

    IPrimitive<Boolean> fitness();

    IPrimitive<Boolean> parking();

    IPrimitive<Boolean> pool();

}
