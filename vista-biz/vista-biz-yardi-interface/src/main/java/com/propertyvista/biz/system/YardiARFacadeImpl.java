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
package com.propertyvista.biz.system;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.payment.PaymentBatchContext;
import com.propertyvista.biz.system.yardi.YardiCredentials;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.yardi.YardiPropertyConfiguration;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;
import com.propertyvista.yardi.processors.YardiLeaseProcessor;
import com.propertyvista.yardi.services.YardiILSGuestCardService;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;
import com.propertyvista.yardi.services.YardiSystemBatchesService;

public class YardiARFacadeImpl extends AbstractYardiFacadeImpl implements YardiARFacade {

    @Override
    public void doAllImport(ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException {
        assert VistaFeatures.instance().yardiIntegration();
        for (PmcYardiCredential yc : getPmcYardiCredentials()) {
            YardiResidentTransactionsService.getInstance().updateAll(yc, executionMonitor);
        }
    }

    @Override
    public void setLeaseChargesComaptibleIds(Lease lease) {
        new YardiLeaseProcessor().setLeaseChargesComaptibleIds(lease);
    }

    @Override
    public void updateLease(Lease lease) throws YardiServiceException, RemoteException {
        assert VistaFeatures.instance().yardiIntegration();

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);

        YardiResidentTransactionsService.getInstance().updateLease(getPmcYardiCredential(lease), lease);
    }

    @Override
    public void updateProductCatalog(Building building) throws YardiServiceException, RemoteException {
        assert VistaFeatures.instance().yardiIntegration();

        Persistence.ensureRetrieve(building, AttachLevel.Attached);

        YardiResidentTransactionsService.getInstance().updateProductCatalog(getPmcYardiCredential(building), building);
    }

    @Override
    public PaymentBatchContext createPaymentBatchContext(Building building) throws RemoteException, YardiServiceException {
        assert VistaFeatures.instance().yardiIntegration();

        YardiPaymentBatchContext paymentBatchContext = new YardiPaymentBatchContext();
        paymentBatchContext.ensureOpenBatch(getPmcYardiCredential(building), building.propertyCode().getValue());

        return paymentBatchContext;
    }

    @Override
    public void postReceipt(YardiReceipt receipt, PaymentBatchContext paymentBatchContext) throws ARException, YardiServiceException, RemoteException {
        assert VistaFeatures.instance().yardiIntegration();

        Persistence.ensureRetrieve(receipt.billingAccount(), AttachLevel.Attached);

        if (VistaTODO.POSTING_SPEED) {
            Building buildingId = ServerSideFactory.create(LeaseFacade.class).getLeasePolicyNode(receipt.billingAccount().lease());
            String propertyCode = Persistence.service().retrieveMember(buildingId.propertyCode());

            YardiSystemBatchesService.getInstance().postReceipt(YardiCredentials.get(buildingId), receipt, propertyCode,
                    (YardiPaymentBatchContext) paymentBatchContext);
        } else {
            Persistence.ensureRetrieve(receipt.billingAccount().lease().unit().building(), AttachLevel.Attached);
            Building building = receipt.billingAccount().lease().unit().building();
            YardiSystemBatchesService.getInstance().postReceipt(YardiCredentials.get(building), receipt, building.propertyCode().getValue(),
                    (YardiPaymentBatchContext) paymentBatchContext);
        }
    }

    @Override
    public void postReceiptReversal(YardiReceiptReversal reversal) throws YardiServiceException, RemoteException {
        assert VistaFeatures.instance().yardiIntegration();

        Persistence.ensureRetrieve(reversal.billingAccount(), AttachLevel.Attached);

        YardiResidentTransactionsService.getInstance().postReceiptReversal(getPmcYardiCredential(reversal.billingAccount().lease()), reversal);
    }

    @Override
    public List<YardiPropertyConfiguration> getPropertyConfigurations() throws YardiServiceException, RemoteException {
        List<YardiPropertyConfiguration> propertyConfigurations = new ArrayList<YardiPropertyConfiguration>();

        for (PmcYardiCredential yc : getPmcYardiCredentials()) {
            propertyConfigurations.addAll(YardiResidentTransactionsService.getInstance().getPropertyConfigurations(yc));
        }

        return propertyConfigurations;
    }

    @Override
    public void updateUnitAvailability(AptUnit aptUnit) throws YardiServiceException, RemoteException {
        YardiILSGuestCardService.getInstance().updateUnitAvailability(getPmcYardiCredential(aptUnit.building()), aptUnit);
    }

}
