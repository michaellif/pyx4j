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
 */
package com.propertyvista.biz.system.yardi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.payment.PaymentBatchContext;
import com.propertyvista.biz.system.AbstractYardiFacadeImpl;
import com.propertyvista.biz.system.YardiPaymentBatchContext;
import com.propertyvista.biz.system.YardiPaymentBatchContext.InfoBatchType;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.yardi.YardiPaymentPostingBatch;
import com.propertyvista.domain.financial.yardi.YardiPaymentPostingBatch.YardiPostingStatus;
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
import com.propertyvista.yardi.stubs.YardiStubFactory;
import com.propertyvista.yardi.stubs.YardiSystemBatchesStub;

public class YardiARFacadeImpl extends AbstractYardiFacadeImpl implements YardiARFacade {

    private static final Logger log = LoggerFactory.getLogger(YardiARFacadeImpl.class);

    @Override
    public void doAllImport(ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException {
        assert VistaFeatures.instance().yardiIntegration();
        StringBuilder errors = new StringBuilder();
        for (PmcYardiCredential yc : getPmcYardiCredentials()) {
            try {
                YardiResidentTransactionsService.getInstance().updateAll(yc, executionMonitor);
            } catch (YardiServiceException | UserRuntimeException e) {
                executionMonitor.addFailedEvent("Yardi Interface", yc.serviceURLBase().getValue(), e);
                errors.append(e.getMessage() + "\n");
            } catch (RemoteException e) {
                executionMonitor.addFailedEvent("Yardi Interface", yc.serviceURLBase().getValue(), e);
                errors.append("Connection failed\n");
            }
        }
        if (errors.length() > 0) {
            throw new YardiServiceException(errors.toString());
        }
    }

    @Override
    public void setLeaseChargesComaptibleIds(Lease lease) {
        YardiLeaseProcessor.setLeaseChargesComaptibleIds(lease);
    }

    @Override
    public void updateLease(Lease lease, ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException {
        assert VistaFeatures.instance().yardiIntegration();

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);

        YardiResidentTransactionsService.getInstance().updateLease(getPmcYardiCredential(lease), lease, executionMonitor);
    }

    @Override
    public void updateBuilding(Building building, ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException {
        assert VistaFeatures.instance().yardiIntegration();

        Persistence.ensureRetrieve(building, AttachLevel.Attached);

        YardiResidentTransactionsService.getInstance().updateBuilding(getPmcYardiCredential(building), building, executionMonitor);
    }

    @Override
    public PaymentBatchContext createPaymentBatchContext(Building building) throws RemoteException, YardiServiceException {
        assert VistaFeatures.instance().yardiIntegration();

        YardiPaymentBatchContext paymentBatchContext = new YardiPaymentBatchContext(InfoBatchType.EFTBatch);
        paymentBatchContext.ensureOpenBatch(getPmcYardiCredential(building), building);

        return paymentBatchContext;
    }

    @Override
    public void postReceipt(YardiReceipt receipt, PaymentBatchContext paymentBatchContext) throws ARException, YardiServiceException, RemoteException {
        assert VistaFeatures.instance().yardiIntegration();

        Persistence.ensureRetrieve(receipt.billingAccount(), AttachLevel.Attached);

        if (VistaTODO.POSTING_SPEED) {
            Building buildingId = ServerSideFactory.create(LeaseFacade.class).getLeaseBuilding(receipt.billingAccount().lease());
            String propertyCode = Persistence.service().retrieveMember(buildingId.propertyCode());

            YardiSystemBatchesService.getInstance().postReceipt(YardiCredentials.get(buildingId), receipt, buildingId,
                    (YardiPaymentBatchContext) paymentBatchContext);
        } else {
            Persistence.ensureRetrieve(receipt.billingAccount().lease().unit().building(), AttachLevel.Attached);
            Building building = receipt.billingAccount().lease().unit().building();
            YardiSystemBatchesService.getInstance().postReceipt(YardiCredentials.get(building), receipt, building,
                    (YardiPaymentBatchContext) paymentBatchContext);
        }
    }

    @Override
    public void postReceiptReversal(YardiReceiptReversal reversal) throws YardiServiceException, RemoteException {
        assert VistaFeatures.instance().yardiIntegration();

        Persistence.ensureRetrieve(reversal.billingAccount(), AttachLevel.Attached);

        if (!VistaTODO.VISTA_2693_Yardi_Plugin_V1_1_Upgrade_Completed) {
            YardiResidentTransactionsService.getInstance().postReceiptReversal(getPmcYardiCredential(reversal.billingAccount().lease()), reversal);
        } else {
            try {
                Persistence.ensureRetrieve(reversal.billingAccount().lease().unit().building(), AttachLevel.Attached);
                YardiSystemBatchesService.getInstance().postReceiptReversal(getPmcYardiCredential(reversal.billingAccount().lease()), reversal,
                        reversal.billingAccount().lease().unit().building(), null);
            } catch (ARException e) {
                throw new YardiServiceException(e);
            }
        }
    }

    @Override
    public List<YardiPropertyConfiguration> getPropertyConfigurations() throws YardiServiceException, RemoteException {
        List<YardiPropertyConfiguration> propertyConfigurations = new ArrayList<YardiPropertyConfiguration>();

        StringBuilder errors = new StringBuilder();
        for (PmcYardiCredential yc : getPmcYardiCredentials()) {
            try {
                propertyConfigurations.addAll(YardiResidentTransactionsService.getInstance().getPropertyConfigurations(yc));
            } catch (YardiServiceException e) {
                log.error("Yardi Interface: {} {}", yc.serviceURLBase().getValue(), e);
                errors.append(e.getMessage() + "\n");
            } catch (RemoteException e) {
                log.error("Yardi Interface: {} {}", yc.serviceURLBase().getValue(), e);
                errors.append("Connection failed\n");
            }
        }
        if (errors.length() > 0) {
            throw new YardiServiceException(errors.toString());
        }

        return propertyConfigurations;
    }

    @Override
    public void updateUnitAvailability(AptUnit aptUnit) throws YardiServiceException, RemoteException {
        YardiILSGuestCardService.getInstance().updateUnitAvailability(getPmcYardiCredential(aptUnit.building()), aptUnit);
    }

    @Override
    public void validateCreditCardAcceptance(Lease lease) throws YardiServiceException, RemoteException {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        PmcYardiCredential yc = YardiCredentials.get(lease.unit().building());
        // Just ping the interface for now
        YardiStubFactory.create(YardiSystemBatchesStub.class).ping(yc);
    }

    @Override
    public String getExternalBatchNumber(PaymentRecord paymentRecord, boolean reversal) {
        EntityQueryCriteria<YardiPaymentPostingBatch> criteria = new EntityQueryCriteria<>(YardiPaymentPostingBatch.class);

        criteria.eq(criteria.proto().status(), YardiPostingStatus.Posted);
        criteria.eq(criteria.proto().records().$().paymentRecord(), paymentRecord);
        criteria.eq(criteria.proto().records().$().reversal(), reversal);

        YardiPaymentPostingBatch batch = Persistence.service().retrieve(criteria);
        return (batch != null ? batch.externalBatchNumber().getValue() : null);
    }
}
