/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.scheduler;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
//TODO Make it POJO  ExecutionReportModel
@Deprecated
public interface StatisticsRecord extends IEntity {

    IPrimitive<Long> total();

    IPrimitive<Long> processed();

    @Deprecated
    IPrimitive<Double> amountProcessed();

    IPrimitive<Long> failed();

    @Deprecated
    IPrimitive<Double> amountFailed();

    IPrimitive<Long> erred();

    @Deprecated
    IPrimitive<Double> amountErred();

    IPrimitive<String> message();

    // TODO add Map<of messages>

    // TODO Add duplicate()  should duplicate major counter.  Not messages, Used in executeOneRunData 'Update Statistics in UI'

}
