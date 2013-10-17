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

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.domain.legal.N4LegalLetter;

public class N4DownloadDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    private final AtomicInteger progress;

    public N4DownloadDeferredProcess(Vector<N4LegalLetter> accepted) {
        progress = new AtomicInteger();
        progress.set(0);
    }

    @Override
    public void execute() {
        completed = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse status = super.status();
        status.setProgress(progress.get());
        status.setProgressMaximum(1);
        return status;
    }

}
