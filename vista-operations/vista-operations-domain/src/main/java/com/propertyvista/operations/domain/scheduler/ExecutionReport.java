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
package com.propertyvista.operations.domain.scheduler;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

import com.propertyvista.domain.VistaNamespace;

@ToStringFormat("Total {0}, Processed {1}, Failed {2}, Erred {3}, Avg {4}")
@Table(prefix = "scheduler", namespace = VistaNamespace.operationsNamespace)
public interface ExecutionReport extends IEntity {

    @ToString(index = 0)
    IPrimitive<Long> total();

    @ToString(index = 1)
    IPrimitive<Long> processed();

    @ToString(index = 2)
    IPrimitive<Long> failed();

    @ToString(index = 3)
    IPrimitive<Long> erred();

    @Deprecated
    IPrimitive<Double> amountProcessed();

    @Deprecated
    IPrimitive<Double> amountFailed();

    @Deprecated
    IPrimitive<Double> amountErred();

    IPrimitive<String> message();

    @ToString(index = 4)
    @Format(value = "{0,duration}", messageFormat = true)
    IPrimitive<Long> averageDuration();

    /** This used for avg calculation **/
    IPrimitive<Long> totalDuration();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<ExecutionReportSection> details();

}
