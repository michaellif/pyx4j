/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.services.financial.AggregatedTransferCrudService;
import com.propertyvista.domain.financial.AggregatedTransfer;

public class AggregatedTransferCrudServiceImpl extends AbstractCrudServiceImpl<AggregatedTransfer> implements AggregatedTransferCrudService {

    public AggregatedTransferCrudServiceImpl() {
        super(AggregatedTransfer.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    public void resendTransactions(AsyncCallback<VoidSerializable> callback, AggregatedTransfer aggregatedTransferStub) {
        // TODO VladS
    }

    @Override
    public void cancelTransactions(AsyncCallback<VoidSerializable> callback, AggregatedTransfer aggregatedTransferStub) {
        // TODO VladS
    }
}
