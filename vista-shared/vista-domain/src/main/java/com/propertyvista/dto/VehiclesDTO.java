/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-23
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.Vehicle;
import com.propertyvista.domain.VehicleChargeRule;

public interface VehiclesDTO extends IEntity {

    @Owned
    @MemberColumn(name = "vehicleList")
    IList<Vehicle> list();

    @Transient
    VehicleChargeRule chargeRule();

    @Transient
    IPrimitive<Integer> maxTotal();
}
