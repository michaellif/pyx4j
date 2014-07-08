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

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

class KeyRotationDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    Key fromPublicKeyKey;

    Key toPublicKeyKey;

    KeyRotationDeferredProcess(int total, Key fromPublicKeyKey, Key toPublicKeyKey) {
        this.fromPublicKeyKey = fromPublicKeyKey;
        this.toPublicKeyKey = toPublicKeyKey;
        progress.progressMaximum.set(total);

    }

    @Override
    public void execute() {
        new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.BackgroundProcess).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {

                ServerSideFactory.create(EncryptedStorageFacade.class).keyRotationProcess(progress.progress, fromPublicKeyKey, toPublicKeyKey);

                completed = true;

                return null;
            }

        });
    }

}
