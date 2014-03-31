/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.legal;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.legal.N4ManagementFacade;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.domain.legal.errors.FormFillError;

public class N4GenerationDeferredProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(N4GenerationDeferredProcess.class);

    private static final I18n i18n = I18n.get(N4GenerationDeferredProcess.class);

    private static final long serialVersionUID = 1L;

    private final AtomicInteger progress;

    private final int progressMax;

    private final N4BatchRequestDTO batchRequest;

    private Exception error;

    public N4GenerationDeferredProcess(N4BatchRequestDTO batchRequest) {
        this.progress = new AtomicInteger();
        this.progress.set(0);
        this.progressMax = batchRequest.targetDelinquentLeases().size();
        this.batchRequest = batchRequest;
    }

    @Override
    public void execute() {
        try {
            ServerSideFactory.create(N4ManagementFacade.class).issueN4(batchRequest, progress);
        } catch (FormFillError e) {
            error = e;
            log.error("N4 Generation failed", e);
        } catch (Exception e) {
            error = e;
            log.error("N4 generation failed", e);
        } finally {
            completed = true;
        }
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse status = super.status();
        status.setProgress(progress.get());
        status.setProgressMaximum(progressMax);
        if (error != null) {
            status.setCompleted();
            if (error instanceof FormFillError) {
                status.setErrorStatusMessage(error.getMessage());
            } else {
                status.setErrorStatusMessage(i18n.tr("N4 Generation Failed"));
            }
        }
        return status;
    }
}
