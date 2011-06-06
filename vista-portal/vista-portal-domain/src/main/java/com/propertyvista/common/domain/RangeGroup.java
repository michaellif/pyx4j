/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-15
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.domain;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@EmbeddedEntity
public interface RangeGroup extends IEntity {

    IPrimitive<Double> average();

    @MemberColumn(name = "rangeMin")
    IPrimitive<Double> min();

    @MemberColumn(name = "rangeMax")
    IPrimitive<Double> max();
}
