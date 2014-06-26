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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityDiff;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.server.TaskRunner;

//TODO Hide it in facade
public class MerchantAccountManager {

    public void persistMerchantAccount(Pmc pmc, final MerchantAccount merchantAccount) {

        EntityGraph.setDefault(merchantAccount.invalid(), Boolean.FALSE);
        EntityGraph.setDefault(merchantAccount.status(), MerchantAccount.MerchantAccountActivationStatus.PendindAppoval);
        EntityGraph.setDefault(merchantAccount.setup().acceptedEcheck(), true);
        EntityGraph.setDefault(merchantAccount.setup().acceptedDirectBanking(), true);
        EntityGraph.setDefault(merchantAccount.setup().acceptedInterac(), true);
        EntityGraph.setDefault(merchantAccount.setup().acceptedCreditCard(), true);
        EntityGraph.setDefault(merchantAccount.setup().acceptedCreditCardConvenienceFee(), true);
        EntityGraph.setDefault(merchantAccount.setup().acceptedCreditCardVisaDebit(), true);

        final MerchantAccount orig = EntityFactory.create(MerchantAccount.class);
        if (merchantAccount.getPrimaryKey() != null) {
            orig.set(TaskRunner.runInTargetNamespace(pmc, new Callable<MerchantAccount>() {
                @Override
                public MerchantAccount call() {
                    return Persistence.service().retrieve(MerchantAccount.class, merchantAccount.getPrimaryKey());
                }
            }));
        }

        PmcMerchantAccountIndex pmcMerchantAccountMainIndex = null;
        PmcMerchantAccountIndex pmcMerchantAccountConvenienceFeeIndex = null;
        if (orig != null) {
            if (!orig.merchantTerminalId().isNull()) {
                EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
                criteria.eq(criteria.proto().pmc(), pmc);
                criteria.eq(criteria.proto().merchantTerminalId(), orig.merchantTerminalId());
                criteria.eq(criteria.proto().merchantAccountKey(), merchantAccount.getPrimaryKey());
                pmcMerchantAccountMainIndex = Persistence.service().retrieve(criteria);
                if (pmcMerchantAccountMainIndex == null) {
                    throw new Error("MerchantAccount integrity broken");
                }
            }
            if (!orig.merchantTerminalIdConvenienceFee().isNull()) {
                EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
                criteria.eq(criteria.proto().pmc(), pmc);
                criteria.eq(criteria.proto().merchantTerminalId(), orig.merchantTerminalIdConvenienceFee());
                criteria.eq(criteria.proto().merchantAccountKey(), merchantAccount.getPrimaryKey());
                pmcMerchantAccountConvenienceFeeIndex = Persistence.service().retrieve(criteria);
                if (pmcMerchantAccountConvenienceFeeIndex == null) {
                    throw new Error("MerchantAccount integrity broken");
                }
            }
        }
        if (pmcMerchantAccountMainIndex == null) {
            pmcMerchantAccountMainIndex = EntityFactory.create(PmcMerchantAccountIndex.class);
            pmcMerchantAccountMainIndex.pmc().set(pmc);
        }
        if (pmcMerchantAccountConvenienceFeeIndex == null) {
            pmcMerchantAccountConvenienceFeeIndex = EntityFactory.create(PmcMerchantAccountIndex.class);
            pmcMerchantAccountConvenienceFeeIndex.pmc().set(pmc);
        }

        TaskRunner.runInTargetNamespace(pmc, new Callable<Void>() {
            @Override
            public Void call() {
                Persistence.service().persist(merchantAccount);

                if (orig.isNull()) {
                    ServerSideFactory.create(AuditFacade.class).created(merchantAccount);
                } else {
                    ServerSideFactory.create(AuditFacade.class).updated(merchantAccount, EntityDiff.getChanges(orig, merchantAccount));
                }

                return null;
            }
        });

        if (orig.isNull()) {
            ServerSideFactory.create(AuditFacade.class).created(pmcMerchantAccountMainIndex);
        } else {
            ServerSideFactory.create(AuditFacade.class).updated(pmcMerchantAccountMainIndex, EntityDiff.getChanges(orig, merchantAccount));
        }

        pmcMerchantAccountMainIndex.merchantAccountKey().setValue(merchantAccount.getPrimaryKey());
        pmcMerchantAccountMainIndex.merchantTerminalId().setValue(merchantAccount.merchantTerminalId().getValue());
        Persistence.service().persist(pmcMerchantAccountMainIndex);

        if (!pmcMerchantAccountConvenienceFeeIndex.id().isNull() || !merchantAccount.merchantTerminalIdConvenienceFee().isNull()) {
            pmcMerchantAccountConvenienceFeeIndex.merchantAccountKey().setValue(merchantAccount.getPrimaryKey());
            pmcMerchantAccountConvenienceFeeIndex.merchantTerminalId().setValue(merchantAccount.merchantTerminalIdConvenienceFee().getValue());
            Persistence.service().persist(pmcMerchantAccountConvenienceFeeIndex);
        }
    }
}
