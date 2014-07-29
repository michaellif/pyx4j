/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 29, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.operations.domain.vista2pmc.DefaultPaymentFees;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.test.mock.MockDataModel;

public class VistaOperationsDataModel extends MockDataModel<DefaultPaymentFees> {

    @Override
    protected void generate() {

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                RDBUtils.ensureNamespace();
                generatePaymentSetup();
                return null;
            }
        });
    }

    protected void generatePaymentSetup() {
        DefaultPaymentFees fee = EntityFactory.create(DefaultPaymentFees.class);
        fee.directBankingFee().setValue(new BigDecimal("1.50"));

        fee.acceptedEcheck().setValue(true);
        fee.acceptedDirectBanking().setValue(true);
        fee.acceptedVisa().setValue(true);
        fee.acceptedMasterCard().setValue(true);
        fee.acceptedVisaConvenienceFee().setValue(true);
        fee.acceptedMasterCardConvenienceFee().setValue(true);
        Persistence.service().persist(fee);
    }

}
