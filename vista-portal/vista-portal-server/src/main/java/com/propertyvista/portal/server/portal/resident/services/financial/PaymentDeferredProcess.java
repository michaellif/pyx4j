/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 3, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.financial;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.portal.rpc.portal.resident.ResidentUserVisit;
import com.propertyvista.shared.config.VistaFeatures;

class PaymentDeferredProcess extends AbstractDeferredProcess {

    private static final I18n i18n = I18n.get(PaymentDeferredProcess.class);

    private static final long serialVersionUID = 1L;

    private final PaymentRecord paymentRecord;

    public PaymentDeferredProcess(PaymentRecord paymentRecord) {
        this.paymentRecord = paymentRecord;
    }

    @Override
    public void execute() {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() throws RuntimeException {
                ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
                return null;
            }
        });

        ServerSideFactory.create(PaymentFacade.class).processPaymentUnitOfWork(paymentRecord, true);

        if (VistaFeatures.instance().yardiIntegration()) {
            DeferredProcessRegistry.fork(new LeaseYardiUpdateDeferredProcess(paymentRecord), ThreadPoolNames.IMPORTS);
        }

//        // delay for testing purpose:
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }

        ServerContext.visit(ResidentUserVisit.class).setPaymentRecord(paymentRecord);
        completed = true;
    }
}