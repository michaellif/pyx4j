/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.EquifaxProcessFacade;
import com.propertyvista.domain.settings.PmcVistaFeatures;

public class EquifaxRetentionProcess implements PmcProcess {

    @Override
    public boolean start(PmcProcessContext context) {
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return false;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        throw new IllegalArgumentException();

    }

    @Override
    public void complete(PmcProcessContext context) {
        ServerSideFactory.create(EquifaxProcessFacade.class).dataRetention(context.getExecutionMonitor());
    }

}
