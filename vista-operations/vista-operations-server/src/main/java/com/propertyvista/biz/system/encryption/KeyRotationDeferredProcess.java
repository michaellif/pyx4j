/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system.encryption;

import java.util.concurrent.atomic.AtomicInteger;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

class KeyRotationDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    Key fromPublicKeyKey;

    Key toPublicKeyKey;

    private final AtomicInteger progress;

    private final int maximum;

    KeyRotationDeferredProcess(int total, Key fromPublicKeyKey, Key toPublicKeyKey) {
        this.fromPublicKeyKey = fromPublicKeyKey;
        this.toPublicKeyKey = toPublicKeyKey;
        maximum = total;
        progress = new AtomicInteger();
        progress.set(0);

    }

    @Override
    public void execute() {
        Persistence.service().startTransaction();
        ServerSideFactory.create(EncryptedStorageFacade.class).keyRotationProcess(progress, fromPublicKeyKey, toPublicKeyKey);
        completed = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse status = super.status();
        status.setProgress(progress.get());
        status.setProgressMaximum(maximum);
        return status;
    }

}
