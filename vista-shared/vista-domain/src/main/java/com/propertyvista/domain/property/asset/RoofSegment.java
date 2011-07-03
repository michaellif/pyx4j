/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IPrimitive;

// TODO - replace inheritance on IEntity!!
public interface RoofSegment extends Rentable {

    @Owner
    @Detached
    Roof belongsTo();

    @Override
    @ToString(index = 0)
    IPrimitive<String> name();
}
