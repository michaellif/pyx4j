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
package com.propertyvista.operations.server.services.sim;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.operations.domain.payment.pad.sim.PadSimFile;
import com.propertyvista.operations.rpc.services.sim.PadSimFileCrudService;

public class PadSimFileCrudServiceImpl extends AbstractCrudServiceImpl<PadSimFile> implements PadSimFileCrudService {

    public PadSimFileCrudServiceImpl() {
        super(PadSimFile.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    public void loadPadFile(AsyncCallback<PadSimFile> callback) {
        callback.onSuccess(new PadSim().loadPadFile());
    }

    @Override
    public void replyAcknowledgment(AsyncCallback<VoidSerializable> callback, PadSimFile triggerStub) {
        new PadSim().replyAcknowledgment(triggerStub);
        callback.onSuccess(null);
    }

    @Override
    public void replyReconciliation(AsyncCallback<VoidSerializable> callback, PadSimFile triggerStub) {
        new PadSim().replyReconciliation(triggerStub);
        callback.onSuccess(null);
    }

}
