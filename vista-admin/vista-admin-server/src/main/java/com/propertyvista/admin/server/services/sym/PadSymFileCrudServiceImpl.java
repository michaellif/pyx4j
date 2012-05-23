/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services.sym;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.admin.domain.payment.pad.sym.PadSymFile;
import com.propertyvista.admin.rpc.services.sym.PadSymFileCrudService;

public class PadSymFileCrudServiceImpl extends AbstractCrudServiceImpl<PadSymFile> implements PadSymFileCrudService {

    public PadSymFileCrudServiceImpl() {
        super(PadSymFile.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    public void loadPadFile(AsyncCallback<PadSymFile> callback) {
        // TODO Auto-generated method stub
    }

    @Override
    public void replyAcknowledgment(AsyncCallback<VoidSerializable> callback, PadSymFile triggerStub) {
        // TODO Auto-generated method stub
    }

    @Override
    public void replyReconciliation(AsyncCallback<VoidSerializable> callback, PadSymFile triggerStub) {
        // TODO Auto-generated method stub
    }

}
