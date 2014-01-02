/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 2, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant.lease.print;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public class LeaseTermAgreementPrinter {

    public static void startLeaseTermAgreementDocumentCreation(LeaseTerm leaseTerm) {
        //TODO add detection of DB dev preloader 
        if (ServerSideConfiguration.isStartedUnderUnitTest()) {
            return;
        }
        // Create thread and save LeaseTermAgreementDocument in this thread
        DeferredProcessRegistry.fork(new LeaseTermAgreementPrinterDeferredProcess(leaseTerm), ThreadPoolNames.IMPORTS);
    }

}
