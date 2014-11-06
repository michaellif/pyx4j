/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 5, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal;

import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

@SuppressWarnings("serial")
public class PortalUserVisit extends CustomerUserVisit {

    private LeaseParticipant<?> leaseParticipantId;

    private Lease selectedLeaseId;

    // to make it GWT Serializable ?
    public PortalUserVisit() {
        super();
    }

    protected PortalUserVisit(VistaApplication application, CustomerUser user) {
        super(application, user);
    }

    public Lease getLeaseId() {
        return selectedLeaseId;
    }

    public void setLease(Lease leaseId, LeaseParticipant<?> leaseParticipantId) {
        if (leaseId != null) {
            this.selectedLeaseId = leaseId.createIdentityStub();
            this.leaseParticipantId = leaseParticipantId.createIdentityStub();
        } else {
            this.selectedLeaseId = null;
            this.leaseParticipantId = null;
        }
        setChanged();
    }

    public LeaseParticipant<?> getLeaseParticipantId() {
        return leaseParticipantId;
    }

}
