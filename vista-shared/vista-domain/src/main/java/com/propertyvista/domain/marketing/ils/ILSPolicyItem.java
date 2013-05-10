/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 10, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.marketing.ils;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Province;

/**
 * ILSPolicy defines a set of attributes to be used to select a subset of available units
 * that will be published with a given ILS provider
 */
public interface ILSPolicyItem extends IEntity {

    public enum ILSProvider {
        kijiji
    }

    IPrimitive<ILSProvider> provider();

    ISet<Province> provinces();

    ISet<City> cities();

    ISet<Building> buildings();

    IPrimitive<Integer> maxUnits();

    IPrimitive<Integer> maxUnitsPerBuilding();

    IPrimitive<Integer> minBeds();

    IPrimitive<Integer> maxBeds();

    IPrimitive<Integer> minBaths();

    IPrimitive<Integer> maxBaths();

    IPrimitive<BigDecimal> minPrice();

    IPrimitive<BigDecimal> maxPrice();

    // internals: 

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    ILSPolicy policy();

    @OrderColumn
    IPrimitive<Integer> orderInParent();
}
