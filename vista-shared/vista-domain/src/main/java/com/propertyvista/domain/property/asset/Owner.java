/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.asset.building.Building;

//It should be either company or person set at the same time
public interface Owner extends IEntity {

    /**
     * Company that owns property
     */
    Company company();

    /**
     * Person that owns property
     */
    Person person();

    Building building();

    /**
     * Percent of share
     */
    @MemberColumn(name = "shr")
    IPrimitive<Double> share();

    IPrimitive<LogicalDate> startDate();

    IPrimitive<LogicalDate> endDate();

}
