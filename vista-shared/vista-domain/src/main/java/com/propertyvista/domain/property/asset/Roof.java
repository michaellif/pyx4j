/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import java.util.Date;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.Notes;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.vendor.Contract;

public interface Roof extends IEntity, Notes {

    Building belongsTo();

    @MemberColumn(name = "roofType")
    IPrimitive<String> type();

    IPrimitive<String> make();

    @MemberColumn(name = "roofYear")
    IPrimitive<Date> year();

    IPrimitive<String> build();

    IPrimitive<String> warrantee();

    Contract contractor();

}