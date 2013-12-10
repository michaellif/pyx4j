/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInPaymentDTO;

public class MoneyInCreateBatchDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 4099464017607928359L;

    private volatile int progress;

    private volatile int progressMax;

    public MoneyInCreateBatchDeferredProcess(LogicalDate receiptDate, Vector<MoneyInPaymentDTO> payments) {
        progressMax = 100;
        progress = 0;
    }

    @Override
    public void execute() {
        // TODO implement
        try {
            ++progress;
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        completed = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse status = super.status();
        status.setProgress(progress);
        status.setProgressMaximum(progressMax);
        if (completed) {
            status.setCompleted();
        }
        return status;
    }
}
