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
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.operations.domain.payment.pad.PadTestTransactionOffset;
import com.propertyvista.server.jobs.TaskRunner;

/**
 * Used to enforce unique transaction number in development.
 * In production the values are mapped one to one.
 * 
 * TODO move to vista-payment
 */
public class PadTransactionUtils {

    private static final char transactionSeparator = '-';

    private static final String transactionOffsetProperty = "_transaction_offset_";

    static String toCaldeonTransactionId(IPrimitive<Key> paymentRecordId) {
        if (VistaDeployment.isVistaProduction()) {
            return paymentRecordId.getStringView();
        } else {
            return readTestDBversionIdInOperations() + transactionSeparator + paymentRecordId.getStringView();
        }
    }

    static Key toVistaPaymentRecordId(IPrimitive<String> transactionId) {
        return toVistaPaymentRecordId(transactionId.getValue());
    }

    static Key toVistaPaymentRecordId(String transactionId) {
        if (VistaDeployment.isVistaProduction()) {
            return new Key(transactionId);
        } else {
            int separator = transactionId.indexOf(transactionSeparator);
            if (separator == -1) {
                throw new Error("Unexpected production transactionId " + transactionId);
            } else {
                String versionId = transactionId.substring(0, separator);
                if (!versionId.equals(readTestDBversionIdInOperations())) {
                    throw new Error("Unexpected transactionId " + transactionId + "; expected prefix " + readTestDBversionIdInOperations());
                }
                return new Key(transactionId.substring(separator + 1));
            }
        }
    }

    public static String readTestDBversionIdInOperations() {
        return TaskRunner.runInOperationsNamespace(new Callable<String>() {
            @Override
            public String call() {
                return readTestDBversionId();
            }
        });
    }

    public static String readTestDBversionId() {
        PadTestTransactionOffset dbResetSequence = Persistence.service().retrieve(EntityQueryCriteria.create(PadTestTransactionOffset.class));
        if (dbResetSequence == null) {
            dbResetSequence = new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<PadTestTransactionOffset, RuntimeException>() {
                @Override
                public PadTestTransactionOffset execute() throws RuntimeException {
                    PadTestTransactionOffset dbResetSequence = EntityFactory.create(PadTestTransactionOffset.class);
                    dbResetSequence.number().setValue(readNextTestDBversionId());
                    Persistence.service().persist(dbResetSequence);
                    return dbResetSequence;
                }
            });
        }
        return String.valueOf(dbResetSequence.number().getValue());
    }

    private static int readNextTestDBversionId() {
        int id = PadCaledonDev.restoreFileProperty(transactionOffsetProperty) + 1;
        PadCaledonDev.saveFileProperty(transactionOffsetProperty, id);
        return id;
    }
}
