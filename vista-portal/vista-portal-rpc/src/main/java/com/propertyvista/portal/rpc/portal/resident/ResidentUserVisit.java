/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.resident;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.CustomerUserVisit;

@SuppressWarnings("serial")
public class ResidentUserVisit extends CustomerUserVisit {

    private Lease selectedLeaseId;

    private String paymentDeferredCorrelationId;

    private PaymentRecord paymentRecordId;

    // to make it GWT Serializable ?
    public ResidentUserVisit() {
        super();
    }

    public ResidentUserVisit(VistaApplication application, CustomerUser user) {
        super(application, user);
    }

    @Override
    public String toString() {
        return "Resident " + super.toString();
    }

    public Lease getLeaseId() {
        return selectedLeaseId;
    }

    public void setLease(Lease lease) {
        if (lease != null) {
            this.selectedLeaseId = lease.createIdentityStub();
        } else {
            this.selectedLeaseId = null;
        }
        setChanged();
    }

    public String getPaymentDeferredCorrelationId() {
        return paymentDeferredCorrelationId;
    }

    public void setPaymentDeferredCorrelationId(String deferredCorrelationId) {
        this.paymentDeferredCorrelationId = deferredCorrelationId;
        setChanged();
    }

    public PaymentRecord getPaymentRecord() {
        return paymentRecordId;
    }

    public void setPaymentRecord(PaymentRecord paymentRecord) {
        if (paymentRecord != null) {
            this.paymentRecordId = paymentRecord.createIdentityStub();
        } else {
            this.paymentRecordId = null;
        }
        setChanged();
    }
}
