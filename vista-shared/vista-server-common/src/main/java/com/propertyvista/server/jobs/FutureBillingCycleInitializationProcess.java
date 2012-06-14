/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.billing.BillingFacade;

public class FutureBillingCycleInitializationProcess implements PmcProcess {

    @Override
    public boolean start(PmcProcessContext context) {
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        ServerSideFactory.create(BillingFacade.class).initializeFutureBillingCycles();
        Persistence.service().commit();
    }

    @Override
    public void complete(PmcProcessContext context) {
    }

}
