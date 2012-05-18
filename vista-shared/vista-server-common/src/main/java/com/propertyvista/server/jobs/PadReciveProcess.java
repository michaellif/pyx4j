/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import java.util.Map;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.financial.payment.PaymentProcessFacade;
import com.propertyvista.biz.financial.payment.TransactionsStats;

public class PadReciveProcess implements PmcProcess {

    private Map<String, TransactionsStats> transactionsStats;

    @Override
    public boolean start() {
        transactionsStats = ServerSideFactory.create(PaymentProcessFacade.class).recivePadAcknowledgementFiles();
        return (transactionsStats != null);
    }

    @Override
    public void executePmcJob() {
        TransactionsStats stats = transactionsStats.get(NamespaceManager.getNamespace());
        // TODO Apply stats
    }

    @Override
    public void complete() {
    }

}
