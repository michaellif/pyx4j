/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 29, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config;

import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

public class ThreadPoolNames {

    public static final String DOWNLOADS = DeferredProcessRegistry.THREAD_POOL_DOWNLOADS;

    public static final String IMPORTS = "Import";

    public static final String BILL_SINGLE = "Billing1";

    public static final String BILL_BUILDING = "BillingB";

}
