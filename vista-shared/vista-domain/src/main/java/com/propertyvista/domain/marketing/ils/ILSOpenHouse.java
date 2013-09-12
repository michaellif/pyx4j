/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 12, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.marketing.ils;

import java.sql.Time;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface ILSOpenHouse extends IEntity {
    IPrimitive<LogicalDate> date();

    IPrimitive<Time> startTime();

    IPrimitive<Time> endTime();

    IPrimitive<String> details();

    IPrimitive<Boolean> appointmentRequired();

}
