/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.concurrent.Callable;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.operations.domain.payment.pad.PadTestTransactionOffset;
import com.propertyvista.server.jobs.TaskRunner;

/**
 * Used to enforce unique transaction number in development.
 * In production the values are mapped one to one.
 */
class PadTransactionUtils {

    private static final char transactionSeparator = '-';

    private static final String transactionOffsetProperty = "_transaction_offset_";

    static String toCaldeonTransactionId(IPrimitive<Key> paymentRecordId) {
        if (VistaDeployment.isVistaProduction()) {
            return paymentRecordId.getStringView();
        } else {
            return testDBversionId() + transactionSeparator + paymentRecordId.getStringView();
        }
    }

    static Key toVistaPaymentRecordId(IPrimitive<String> transactionId) {
        if (VistaDeployment.isVistaProduction()) {
            return new Key(transactionId.getValue());
        } else {
            int separator = transactionId.getValue().indexOf(transactionSeparator);
            if (separator == -1) {
                // TODO throw error
                //throw new Error("Unexpected production transactionId " + transactionId.getValue());
                return new Key(transactionId.getValue());
            } else {
                String versionId = transactionId.getValue().substring(0, separator);
                if (!versionId.equals(testDBversionId())) {
                    throw new Error("Unexpected transactionId " + transactionId.getValue() + "; expected prefix " + testDBversionId());
                }
                return new Key(transactionId.getValue().substring(separator + 1));
            }
        }
    }

    static String testDBversionId() {
        return TaskRunner.runInOperationsNamespace(new Callable<String>() {
            @Override
            public String call() {
                return readTestDBversionId();
            }
        });
    }

    private static String readTestDBversionId() {
        PadTestTransactionOffset dbResetSequence = Persistence.service().retrieve(EntityQueryCriteria.create(PadTestTransactionOffset.class));
        if (dbResetSequence == null) {
            dbResetSequence = EntityFactory.create(PadTestTransactionOffset.class);
            dbResetSequence.number().setValue(readNextTestDBversionId());
            Persistence.service().persist(dbResetSequence);
        }
        return String.valueOf(dbResetSequence.number().getValue());
    }

    private static int readNextTestDBversionId() {
        int id = PadCaledonDev.restoreFileProperty(transactionOffsetProperty) + 1;
        PadCaledonDev.saveFileProperty(transactionOffsetProperty, id);
        return id;
    }
}
