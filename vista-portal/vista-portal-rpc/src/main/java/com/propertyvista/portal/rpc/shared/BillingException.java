/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 29, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared;

import com.pyx4j.commons.UserRuntimeException;

import com.propertyvista.domain.tenant.lease.Lease;

@Deprecated
//TODO BillingException should be checked exception of Billing module
public class BillingException extends UserRuntimeException {

    private static final long serialVersionUID = 1L;

    private Lease lease;

    public BillingException() {
    }

    public BillingException(String message) {
        super(message);
    }

    public BillingException(String message, Lease lease) {
        super(message);
        this.lease = lease;
    }

    public Lease getLeaseId() {
        return lease;
    }

}
