/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 4, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.admin;

import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.biz.communication.OperationsNotificationFacade;
import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.services.admin.MerchantAccountCrudService;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount.MerchantAccountActivationStatus;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.server.TaskRunner;

public class MerchantAccountCrudServiceImpl extends AbstractCrudServiceImpl<MerchantAccount> implements MerchantAccountCrudService {

    public MerchantAccountCrudServiceImpl() {
        super(MerchantAccount.class);
    }

    @Override
    protected void bind() {
        bind(toProto.id(), boProto.id());
        bind(toProto.accountName(), boProto.accountName());
        bind(toProto.chargeDescription(), boProto.chargeDescription());

        // TODO Read only or Behavior + condition  , See copyTOtoBO
        bind(toProto.bankId(), boProto.bankId());
        bind(toProto.branchTransitNumber(), boProto.branchTransitNumber());
        bind(toProto.accountNumber(), boProto.accountNumber());
    }

    @Override
    public void copyTOtoBO(MerchantAccount dto, MerchantAccount dbo) {
        // TODO move to Minding type
        dbo.accountName().setValue(dto.accountName().getValue());
        dbo.chargeDescription().setValue(dto.chargeDescription().getValue());
    }

    private void setCalulatedFileds(MerchantAccount entity, MerchantAccount dto) {
        ServerSideFactory.create(Vista2PmcFacade.class).calulateMerchantAccountStatus(entity);
        dto.merchantTerminalId().setValue(null);
        dto.paymentsStatus().setValue(entity.paymentsStatus().getValue());
    }

    @Override
    protected void enhanceRetrieved(MerchantAccount bo, MerchantAccount to, RetrieveTarget retrieveTarget) {
        setCalulatedFileds(bo, to);
        if (SecurityController.checkAnyBehavior(VistaCrmBehavior.PropertyVistaAccountOwner, VistaCrmBehavior.PropertyVistaSupport)) {
            to.status().setValue(bo.status().getValue());
        }
    }

    @Override
    protected void enhanceListRetrieved(MerchantAccount entity, MerchantAccount dto) {
        setCalulatedFileds(entity, dto);
    }

    @Override
    public void problemResolved(AsyncCallback<VoidSerializable> callback, MerchantAccount merchantAccountStub) {
        MerchantAccount merchantAccount = Persistence.service().retrieve(MerchantAccount.class, merchantAccountStub.getPrimaryKey());
        merchantAccount.invalid().setValue(Boolean.FALSE);
        Persistence.service().persist(merchantAccount);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    private boolean isEditable(MerchantAccount bo) {
        return (bo.status().getValue(MerchantAccountActivationStatus.PendindAppoval) == MerchantAccountActivationStatus.PendindAppoval)
                && SecurityController.checkAnyBehavior(VistaCrmBehavior.PropertyVistaAccountOwner, VistaCrmBehavior.PropertyVistaSupport);
    }

    @Override
    protected boolean persist(final MerchantAccount bo, MerchantAccount to) {
        if (isEditable(bo)) {
            bo.accountNumber().setValue(to.accountNumber().getValue());
            bo.bankId().setValue(to.bankId().getValue());
            bo.branchTransitNumber().setValue(to.branchTransitNumber().getValue());

            boolean isNew = bo.id().isNull();

            final Pmc pmc = VistaDeployment.getCurrentPmc();
            TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                @Override
                public Void call() {
                    ServerSideFactory.create(PmcFacade.class).persistMerchantAccount(pmc, bo);
                    return null;
                }
            });

            if (isNew) {
                ServerSideFactory.create(OperationsNotificationFacade.class).newMerchantAccountRequested(bo);
            }

            return true;
        } else {
            super.persist(bo, to);
            return true;
        }
    }
}
