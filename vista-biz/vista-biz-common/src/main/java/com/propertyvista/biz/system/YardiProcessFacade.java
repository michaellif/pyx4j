/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import com.propertyvista.domain.StatisticsRecord;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.tenant.lease.Lease;

public interface YardiProcessFacade {

    void doAllImport(StatisticsRecord dynamicStatisticsRecord);

    @Deprecated
    void postAllPayments(StatisticsRecord dynamicStatisticsRecord);

    @Deprecated
    void postAllNSF(StatisticsRecord dynamicStatisticsRecord);

    void updateLease(StatisticsRecord dynamicStatisticsRecord, Lease lease);

    void postReceipt(StatisticsRecord dynamicStatisticsRecord, YardiReceipt receipt);

    void postReceiptReversal(StatisticsRecord dynamicStatisticsRecord, YardiReceipt receipt, boolean isNSF);

}
