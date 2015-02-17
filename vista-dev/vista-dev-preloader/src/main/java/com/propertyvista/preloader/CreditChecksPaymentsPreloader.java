/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 13, 2015
 * @author ernestog
 */
package com.propertyvista.preloader;

import java.util.concurrent.Callable;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcPaymentMethod;
import com.propertyvista.misc.CreditCardNumberGenerator;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.shared.util.CreditCardFormatter;

public class CreditChecksPaymentsPreloader extends BaseVistaDevDataPreloader {

    @Override
    public String create() {

        if (config().creditCheckPaymentPreloader) {

            final Pmc pmc = VistaDeployment.getCurrentPmc();

            TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                @Override
                public Void call() {
                    PmcPaymentMethod pmcPaymentMethod = EntityFactory.create(PmcPaymentMethod.class);
                    pmcPaymentMethod.type().setValue(PaymentType.CreditCard);
                    pmcPaymentMethod.details().set(createMockCreditCardInfo());
                    pmcPaymentMethod.pmc().set(pmc);

                    PmcPaymentMethod pm = ServerSideFactory.create(PaymentMethodFacade.class).persistPmcPaymentMethod(pmcPaymentMethod);
                    setEquifaxPayment(pm);

                    return null;
                }

                /**
                 * update pmc's equifax payment method:
                 */
                private void setEquifaxPayment(PmcPaymentMethod paymentMethod) {
                    Persistence.service().retrieveMember(pmc.equifaxInfo());
                    pmc.equifaxInfo().paymentMethod().set(paymentMethod);
                    Persistence.service().persist(pmc.equifaxInfo());
                }

                private CreditCardInfo createMockCreditCardInfo() {
                    CreditCardInfo ccInfo = EntityFactory.create(CreditCardInfo.class);
                    ccInfo.nameOn().setValue(pmc.name().getValue());
                    ccInfo.cardType().setValue(CreditCardType.Visa);
                    ccInfo.card().number().setValue(CreditCardNumberGenerator.generateCardNumber(ccInfo.cardType().getValue()));
                    ccInfo.card().obfuscatedNumber().setValue(new CreditCardFormatter().obfuscate(ccInfo.card().number().getValue()));
                    LogicalDate nextMonth = new LogicalDate();
                    TimeUtils.addDays(nextMonth, 31);
                    ccInfo.expiryDate().setValue(nextMonth);
                    ccInfo.securityCode().setValue("423");
                    return ccInfo;
                }
            });

            return "Credit Check payment setup for PMC: " + pmc.name().getValue();
        }

        return null;
    }

    @Override
    public String delete() {
        // TODO Auto-generated method stub
        return null;
    }

}
