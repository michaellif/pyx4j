/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 29, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import com.propertyvista.domain.settings.PmcVistaFeatures;

public class PaymentsDbpSendProcess implements PmcProcess {

    @Override
    public boolean start(PmcProcessContext context) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void complete(PmcProcessContext context) {
        // TODO Auto-generated method stub

    }

}
