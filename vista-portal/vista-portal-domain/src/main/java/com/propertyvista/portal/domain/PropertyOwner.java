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
package com.propertyvista.portal.domain;

import java.util.Date;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

//It should be either company or person set at the same time
public interface PropertyOwner extends IEntity {

    /**
     * Company that owns property
     */
    Company company();

    /**
     * Person that owns property
     */
    Person person();

    Property property();

    /**
     * Percent of share
     */
    IPrimitive<Float> share();

    IPrimitive<Date> startDate();

    IPrimitive<Date> endDate();

}
