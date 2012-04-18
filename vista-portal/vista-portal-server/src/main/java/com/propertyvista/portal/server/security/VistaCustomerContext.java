/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.security;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.security.VistaContext;

public class VistaCustomerContext extends VistaContext {

    private final static String slectedLeaseAtt = "selected-lease";

    public static Lease getCurrentUserLeaseIdStub() {
        return EntityFactory.createIdentityStub(Lease.class, (Key) Context.getVisit().getAttribute(slectedLeaseAtt));
    }

    public static void setCurrentUserLease(Lease lease) {
        Context.getVisit().setAttribute(slectedLeaseAtt, lease.getPrimaryKey());
    }
}
