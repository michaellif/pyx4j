/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 13, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import java.util.concurrent.Callable;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.operations.domain.vista2pmc.TenantSureMerchantAccount;
import com.propertyvista.operations.domain.vista2pmc.VistaMerchantAccount;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.test.mock.MockDataModel;

public class VistaEquifaxMerchantAccountDataModel extends MockDataModel<TenantSureMerchantAccount> {

    @Override
    protected void generate() {
        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                VistaMerchantAccount ma = Persistence.service().retrieve(EntityQueryCriteria.create(VistaMerchantAccount.class));

                if (ma == null) {
                    ma = EntityFactory.create(VistaMerchantAccount.class);
                    ma.accountType().setValue(VistaMerchantAccount.AccountType.Equifax);
                    ma.merchantTerminalId().setValue("ViEfx001");
                    ma.bankId().setValue("010");
                    ma.branchTransitNumber().setValue("00001");
                    ma.accountNumber().setValue("700000010000");
                    Persistence.service().persist(ma);
                }

                return null;
            }
        });
    }

}
