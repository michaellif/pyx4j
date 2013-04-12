/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.util.concurrent.Callable;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.server.jobs.TaskRunner;

//TODO Hide it in facade
public class MerchantAccountManager {

    public void persistMerchantAccount(Pmc pmc, final MerchantAccount merchantAccount) {
        PmcMerchantAccountIndex pmcMerchantAccountIndex = null;
        if (!merchantAccount.id().isNull()) {
            EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().pmc(), pmc));
            criteria.add(PropertyCriterion.eq(criteria.proto().merchantAccountKey(), merchantAccount.getPrimaryKey()));
            pmcMerchantAccountIndex = Persistence.service().retrieve(criteria);
            if (pmcMerchantAccountIndex == null) {
                throw new Error("MerchantAccount integrity broken");
            }
        }
        if (pmcMerchantAccountIndex == null) {
            pmcMerchantAccountIndex = EntityFactory.create(PmcMerchantAccountIndex.class);
            pmcMerchantAccountIndex.pmc().set(pmc);
        }

        TaskRunner.runInTargetNamespace(pmc, new Callable<Void>() {
            @Override
            public Void call() {
                Persistence.service().persist(merchantAccount);
                return null;
            }
        });

        pmcMerchantAccountIndex.merchantAccountKey().setValue(merchantAccount.getPrimaryKey());
        pmcMerchantAccountIndex.merchantTerminalId().setValue(merchantAccount.merchantTerminalId().getValue());
        Persistence.service().persist(pmcMerchantAccountIndex);
    }
}
