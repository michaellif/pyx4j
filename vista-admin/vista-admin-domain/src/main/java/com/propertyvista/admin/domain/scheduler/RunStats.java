/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 4, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.scheduler;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.StatisticsRecord;
import com.propertyvista.domain.VistaNamespace;

@ToStringFormat("Total {0}, Processed {1}, Failed {2}, Erred {3}, Avg {4}")
@Table(prefix = "scheduler", namespace = VistaNamespace.adminNamespace)
public interface RunStats extends StatisticsRecord {

    @Override
    @ToString(index = 0)
    IPrimitive<Long> total();

    @Override
    @ToString(index = 1)
    IPrimitive<Long> processed();

    @Override
    @ToString(index = 2)
    IPrimitive<Long> failed();

    @Override
    @ToString(index = 3)
    IPrimitive<Long> erred();

    @ToString(index = 4)
    @Format(value = "{0,duration}", messageFormat = true)
    IPrimitive<Long> averageDuration();

    /** This used for avg calculation **/
    IPrimitive<Long> totalDuration();

}
