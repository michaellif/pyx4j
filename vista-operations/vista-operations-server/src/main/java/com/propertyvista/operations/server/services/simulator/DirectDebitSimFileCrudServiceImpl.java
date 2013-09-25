/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services.simulator;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.payment.PadTransactionUtils;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimFile.DirectDebitSimFileStatus;
import com.propertyvista.operations.rpc.services.simulator.DirectDebitSimFileCrudService;
import com.propertyvista.payment.dbp.simulator.DirectDebitSimManager;

public class DirectDebitSimFileCrudServiceImpl extends AbstractCrudServiceImpl<DirectDebitSimFile> implements DirectDebitSimFileCrudService {

    public DirectDebitSimFileCrudServiceImpl() {
        super(DirectDebitSimFile.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void create(DirectDebitSimFile entity, DirectDebitSimFile dto) {
        createNewFile(entity);
    }

    //TODO Move to DirectDebitSimManager
    static DirectDebitSimFile createNewFile(DirectDebitSimFile directDebitSimFile) {
        directDebitSimFile.status().setValue(DirectDebitSimFileStatus.New);
        Persistence.service().persist(directDebitSimFile);

        String serialNumber = lastSignificantNumbers(PadTransactionUtils.readTestDBversionIdInOperations(), 2);
        serialNumber += lastSignificantNumbers(String.valueOf(directDebitSimFile.getPrimaryKey().asLong()), 4);

        directDebitSimFile.serialNumber().setValue(Integer.valueOf(serialNumber));

        Persistence.service().persist(directDebitSimFile);
        return directDebitSimFile;
    }

    private static String lastSignificantNumbers(String number, int size) {
        if (number.length() > size) {
            return number.substring(number.length() - size, number.length());
        } else {
            return CommonsStringUtils.paddingLeft(number, size, '0');
        }
    }

    @Override
    protected void retrievedSingle(DirectDebitSimFile bo, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieveMember(bo.records());
    }

    @Override
    public void send(AsyncCallback<VoidSerializable> callback, DirectDebitSimFile directDebitSimFileId) {
        new DirectDebitSimManager().send(directDebitSimFileId);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

}
