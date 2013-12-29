/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 2, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.ils.emailfeed.EmailFeedClient;
import com.propertyvista.ils.gottarent.GottarentClient;

public class VistaILSFacadeImpl implements VistaILSFacade {

    private static final Logger log = LoggerFactory.getLogger(VistaILSFacadeImpl.class);

    @Override
    public void emailFeed(ExecutionMonitor executionMonitor) {
        EmailFeedClient.emailFeed(executionMonitor);
    }

    @Override
    public void updateGottarentListing(ExecutionMonitor executionMonitor) {
        GottarentClient.updateGottarentListing(executionMonitor);
    }

}
