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
package com.propertyvista.domain.policy.policies.domain;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.policy.policies.ILSPolicy;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Province;

/**
 * ILSPolicy defines a set of attributes to be used to select a subset of available units
 * that will be published with a given ILS provider
 */
public interface ILSPolicyItem extends IEntity {

    public enum ILSProvider {
        kijiji, gottarent, emg
    }

    IPrimitive<ILSProvider> provider();

    /*
     * --- listing restrictions ---
     */
    // TODO - switch to ISet once Set-mapped containers available in UI
    IList<Province> allowedProvinces();

    // TODO - switch to ISet once Set-mapped containers available in UI
    IList<City> allowedCities();

    IPrimitive<Integer> maxBuildings();

    IPrimitive<Integer> maxUnits();

    IPrimitive<Integer> maxUnitsPerBuilding();

    IPrimitive<Integer> minBeds();

    IPrimitive<Integer> maxBeds();

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
