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
package com.propertyvista.biz.system;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.deferred.AbstractDeferredProcess;

import com.propertyvista.admin.domain.pmc.Pmc;

public class PmcActivationDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    private final Pmc pmcId;

    public PmcActivationDeferredProcess(Pmc pmcId) {
        this.pmcId = pmcId;
    }

    @Override
    public void execute() {
        try {
            Persistence.service().startBackgroundProcessTransaction();
            ServerSideFactory.create(PmcFacade.class).activatePmc(pmcId);
            Persistence.service().commit();
        } finally {
            Persistence.service().endTransaction();
        }
        complete = true;
    }
}
