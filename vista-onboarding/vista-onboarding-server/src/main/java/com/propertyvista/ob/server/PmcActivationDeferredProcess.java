/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.ob.server;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.server.contexts.Lifecycle;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.biz.system.PmcFacade;

public class PmcActivationDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    protected final Pmc pmcId;

    public PmcActivationDeferredProcess(Pmc pmcId) {
        this.pmcId = pmcId;
    }

    @Override
    public void execute() {
        try {
            Persistence.service().startBackgroundProcessTransaction();
            Lifecycle.startElevatedUserContext();
            ServerSideFactory.create(PmcFacade.class).activatePmc(pmcId);
            Persistence.service().commit();
            onPmcCreated();
        } finally {
            Persistence.service().endTransaction();
        }
        completed = true;
    }

    protected void onPmcCreated() {
    }
}
