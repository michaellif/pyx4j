/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system.yardi;

import java.rmi.RemoteException;
import java.util.List;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.payment.PaymentBatchContext;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.yardi.YardiPropertyConfiguration;
import com.propertyvista.domain.tenant.lease.Lease;

public interface YardiARFacade {

    void doAllImport(ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException;

    public void validateCreditCardAcceptance(Lease lease) throws YardiServiceException, RemoteException;

    /**
     * set the same uid as during import so the pap will not be suspended.
     */
    void setLeaseChargesComaptibleIds(Lease lease);

    void updateLease(Lease lease, ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException;

    void updateBuilding(Building building, ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException;

    PaymentBatchContext createPaymentBatchContext(Building building) throws YardiServiceException, RemoteException;

    void postReceipt(YardiReceipt receipt, PaymentBatchContext paymentBatchContext) throws ARException, YardiServiceException, RemoteException;

    void postReceiptReversal(YardiReceiptReversal reversal) throws YardiUnableToPostReversalException, YardiPropertyNoAccessException, YardiServiceException,
            RemoteException;

    List<YardiPropertyConfiguration> getPropertyConfigurations() throws YardiServiceException, RemoteException;

    void updateUnitAvailability(AptUnit aptUnit) throws YardiServiceException, RemoteException;
}
