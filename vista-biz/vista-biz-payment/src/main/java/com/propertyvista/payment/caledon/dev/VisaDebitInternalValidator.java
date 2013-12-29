/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.caledon.dev;

import java.util.concurrent.Callable;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.operations.domain.dev.VisaDebitRange;
import com.propertyvista.server.jobs.TaskRunner;

public class VisaDebitInternalValidator {

    public static boolean isVisaDebitValid(String cardNumber) {
        if (!ValidationUtils.isCreditCardNumberValid(cardNumber)) {
            return false;
        }
        final long value = Long.valueOf(cardNumber.substring(0, 12));
        return TaskRunner.runInOperationsNamespace(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                EntityQueryCriteria<VisaDebitRange> criteria = EntityQueryCriteria.create(VisaDebitRange.class);
                criteria.le(criteria.proto().rangeStart(), value);
                criteria.ge(criteria.proto().rangeEnd(), value);
                VisaDebitRange rangeMatch = Persistence.service().retrieve(criteria);
                return rangeMatch != null;
            }
        });
    }
}
