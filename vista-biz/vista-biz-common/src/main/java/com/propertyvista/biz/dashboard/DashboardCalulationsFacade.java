/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 1, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.dashboard;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;

public interface DashboardCalulationsFacade {

    public interface PaymentsSummarySnapshotHook {

        /**
         * @param summmary
         * @return if the snapshot taking should continue
         */
        boolean onPaymentsSummarySnapshotTaken(PaymentsSummary summmary);

        /**
         * @param caught
         * @return if the snapshot taking should continue
         */
        boolean onPaymentsSummarySnapshotFailed(Throwable caught);

    }

    public void takePaymentsSummarySnapshots(final LogicalDate snapshotDay, PaymentsSummarySnapshotHook paymentsSummarySnapshotHook);

}
