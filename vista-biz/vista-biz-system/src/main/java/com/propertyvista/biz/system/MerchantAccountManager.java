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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityDiff;

import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.server.TaskRunner;

//TODO Hide it in facade
public class MerchantAccountManager {

    public void persistMerchantAccount(Pmc pmc, final MerchantAccount merchantAccount) {

        MerchantAccount orig = null;
        if (merchantAccount.getPrimaryKey() != null) {
            orig = TaskRunner.runInTargetNamespace(pmc, new Callable<MerchantAccount>() {
                @Override
                public MerchantAccount call() {
                    return Persistence.service().retrieve(MerchantAccount.class, merchantAccount.getPrimaryKey());
                }
            });
        }

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

        if (orig == null) {
            ServerSideFactory.create(AuditFacade.class).created(pmcMerchantAccountIndex);
        } else {
            ServerSideFactory.create(AuditFacade.class).updated(pmcMerchantAccountIndex, EntityDiff.getChanges(orig, merchantAccount));
        }

        pmcMerchantAccountIndex.merchantAccountKey().setValue(merchantAccount.getPrimaryKey());
        pmcMerchantAccountIndex.merchantTerminalId().setValue(merchantAccount.merchantTerminalId().getValue());
        Persistence.service().persist(pmcMerchantAccountIndex);
    }
}
