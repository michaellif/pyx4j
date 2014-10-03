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

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.biz.financial.payment.PaymentException;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.domain.financial.PaymentRecord;

class PaymentDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    private final PaymentRecord paymentRecordStub;

    public PaymentDeferredProcess(PaymentRecord paymentRecordStub) {
        this.paymentRecordStub = paymentRecordStub;
    }

    @Override
    public void execute() {
        new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.TransactionProcessing).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() throws RuntimeException {
                try {
                    ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecordStub, null);
                } catch (PaymentException e) {
                    throw new UserRuntimeException(PaymentWizardServiceImpl.i18n.tr("Payment processing has been Failed!"), e);
                }
                return null;
            }
        });
        completed = true;
    }
}