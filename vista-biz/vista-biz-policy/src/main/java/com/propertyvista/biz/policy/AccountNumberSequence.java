/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.policy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.Callable;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.PmcAccountNumbers;
import com.propertyvista.admin.domain.pmc.PmcAccountNumbers.AccountNumbersRangeType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.server.domain.IdAssignmentSequence;
import com.propertyvista.server.jobs.TaskRunner;

/**
 * @see http://jira.birchwoodsoftwaregroup.com/wiki/display/VISTA/Account+Numbers
 */
class AccountNumberSequence {

    static String getNextSequence() {
        return TaskRunner.runAutonomousTransation(new Callable<String>() {
            @Override
            public String call() throws Exception {
                EntityQueryCriteria<IdAssignmentSequence> criteria = EntityQueryCriteria.create(IdAssignmentSequence.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().target(), IdAssignmentItem.IdTarget.accountNumber));
                IdAssignmentSequence sequence = Persistence.service().retrieve(criteria);

                if ((sequence == null) || (sequence.number().equals(sequence.maximum()))) {
                    if (sequence == null) {
                        sequence = EntityFactory.create(IdAssignmentSequence.class);
                        sequence.target().setValue(IdAssignmentItem.IdTarget.accountNumber);
                    }
                    createNewSequence(sequence);
                }

                long id = sequence.number().getValue() + 1;
                sequence.number().setValue(id);
                Persistence.service().persist(sequence);
                Persistence.service().commit();

                //AccountNumbersRangeType.Small
                NumberFormat nf = new DecimalFormat("0000");
                String accountNumber = AccountNumberGenerator.addChecksum(sequence.prefix().getStringView() + nf.format(sequence.number().getValue()));
                assert ValidationUtils.isCreditCardNumberValid(accountNumber) : " accountNumber '" + accountNumber + "' not valid";
                return accountNumber;
            }

        });
    }

    static void createNewSequence(final IdAssignmentSequence sequence) {
        sequence.number().setValue(0l);
        sequence.maximum().setValue(9999l);
        final String namespace = NamespaceManager.getNamespace();

        Long newPrefix = TaskRunner.runInAdminNamespace(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().namespace(), namespace));
                Pmc pmc = Persistence.service().retrieve(criteria);

                EntityQueryCriteria<PmcAccountNumbers> acnCriteria = EntityQueryCriteria.create(PmcAccountNumbers.class);
                acnCriteria.add(PropertyCriterion.eq(acnCriteria.proto().pmcType(), AccountNumbersRangeType.Small));
                acnCriteria.desc(acnCriteria.proto().accountPrefix());
                PmcAccountNumbers maxRow = Persistence.service().retrieve(acnCriteria);
                Long nextValue;
                if (maxRow == null) {
                    // initialization
                    // 800-ppp-ppp
                    nextValue = Long.valueOf(800000000l);
                } else {
                    nextValue = maxRow.accountPrefix().getValue() + 1l;
                }
                PmcAccountNumbers ar = EntityFactory.create(PmcAccountNumbers.class);
                ar.pmcType().setValue(AccountNumbersRangeType.Small);
                ar.accountPrefix().setValue(nextValue);
                ar.pmc().set(pmc);
                Persistence.service().persist(ar);
                Persistence.service().commit();

                return nextValue;
            }
        });

        sequence.prefix().setValue(newPrefix);

    }
}
