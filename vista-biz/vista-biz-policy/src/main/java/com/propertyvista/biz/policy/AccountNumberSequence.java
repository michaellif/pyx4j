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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcAccountNumbers;
import com.propertyvista.domain.pmc.PmcAccountNumbers.AccountNumbersRangeType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.server.domain.IdAssignmentSequence;

/**
 * @see http://jira.birchwoodsoftwaregroup.com/wiki/display/VISTA/Account+Numbers
 */
class AccountNumberSequence {

    private static Object sharedAccountNumbersPoolLock = new Object();

    static String getNextSequence() {
        return TaskRunner.runAutonomousTransation(new Callable<String>() {
            @Override
            public String call() throws Exception {
                EntityQueryCriteria<IdAssignmentSequence> criteria = EntityQueryCriteria.create(IdAssignmentSequence.class);
                criteria.eq(criteria.proto().target(), IdAssignmentItem.IdTarget.accountNumber);
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

    private static void createNewSequence(final IdAssignmentSequence sequence) {
        // N.B. Only small AccountNumbersRangeType implemented now

        sequence.number().setValue(0l);
        sequence.maximum().setValue(9999l);
        final Pmc pmc = VistaDeployment.getCurrentPmc();

        Long newPrefix = TaskRunner.runInOperationsNamespace(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                Long nextValue;
                synchronized (sharedAccountNumbersPoolLock) {

                    EntityQueryCriteria<PmcAccountNumbers> criteria = EntityQueryCriteria.create(PmcAccountNumbers.class);
                    criteria.eq(criteria.proto().pmcType(), AccountNumbersRangeType.Small);
                    criteria.desc(criteria.proto().accountPrefix());
                    PmcAccountNumbers maxRow = Persistence.service().retrieve(criteria);
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
                }
                return nextValue;
            }
        });

        sequence.prefix().setValue(newPrefix);

    }

    static Pmc getPmcByAccountNumber(String accountNumber) {
        char sizeCharacter = accountNumber.charAt(0);
        // TODO move to AccountNumbersRangeType enum
        AccountNumbersRangeType rangeType;
        switch (sizeCharacter) {
        case '9':
            rangeType = AccountNumbersRangeType.Small;
            break;
        case '8':
            rangeType = AccountNumbersRangeType.Small;
            break;
//        case '7':
//            rangeType = AccountNumbersRangeType.Medium;
//            break;
//        case '5':
//            rangeType = AccountNumbersRangeType.Medium;
//            break;
//        case '4':
//            rangeType = AccountNumbersRangeType.Large;
//            break;
//        case '3':
//            rangeType = AccountNumbersRangeType.Large;
//            break;
//        case '2':
//            rangeType = AccountNumbersRangeType.Large;
//            break;
//        case '1':
//            rangeType = AccountNumbersRangeType.Large;
//            break;
//        case '0':
//            rangeType = AccountNumbersRangeType.Large;
//            break;
        default:
            return null;
        }
        String accountPrefix = accountNumber.substring(0, rangeType.getAccountPrefixLenght());

        EntityQueryCriteria<PmcAccountNumbers> criteria = EntityQueryCriteria.create(PmcAccountNumbers.class);
        criteria.eq(criteria.proto().accountPrefix(), Long.valueOf(accountPrefix));
        criteria.eq(criteria.proto().pmcType(), rangeType);
        PmcAccountNumbers accountNumbers = Persistence.service().retrieve(criteria);
        if (accountNumbers == null) {
            return null;
        } else {
            return accountNumbers.pmc();
        }
    }
}
