/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.concurrent.Callable;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.operations.domain.vista2pmc.VistaMerchantAccount;
import com.propertyvista.biz.financial.payment.CreditCardProcessor.MerchantTerminalSource;
import com.propertyvista.server.jobs.TaskRunner;

public class MerchantTerminalSourceVista implements MerchantTerminalSource {

    @Override
    public String getMerchantTerminalId() {
        VistaMerchantAccount ma = TaskRunner.runInOperationsNamespace(new Callable<VistaMerchantAccount>() {
            @Override
            public VistaMerchantAccount call() {
                return Persistence.service().retrieve(EntityQueryCriteria.create(VistaMerchantAccount.class));
            }
        });
        if ((ma == null || ma.merchantTerminalId().isNull())) {
            throw new UserRuntimeException("Vista MerchantAccount is not setup");
        }
        return ma.merchantTerminalId().getValue();
    }
}
