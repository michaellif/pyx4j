/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 10, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.biz.system.yardi.YardiOperationsFacade;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.operations.rpc.dto.ConnectionTestResultDTO;

class TestYardiConnectionDeferredProcess extends AbstractDeferredProcess {
    private static final long serialVersionUID = 1L;

    private final PmcYardiCredential credential;

    private final ConnectionTestResultDTO result;

    TestYardiConnectionDeferredProcess(PmcYardiCredential credential) {
        this.credential = credential;
        result = new ConnectionTestResultDTO();
    }

    @Override
    public void execute() {
        ServerSideFactory.create(YardiOperationsFacade.class).verifyInterface(credential, result);
        completed = true;
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse r = super.status();
        r.setProgressMaximum(100);
        r.setProgress(result.getProgressPct());
        r.setMessage(r.isCompleted() ? result.toString() : result.getProgressMessage());
        return r;
    }
}
