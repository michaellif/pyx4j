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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.crm.rpc.services.admin.MerchantAccountCrudService;
import com.propertyvista.domain.financial.MerchantAccount;

public class MerchantAccountCrudServiceImpl extends AbstractCrudServiceImpl<MerchantAccount> implements MerchantAccountCrudService {

    public MerchantAccountCrudServiceImpl() {
        super(MerchantAccount.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.id(), dboProto.id());
        bind(dtoProto.accountNumber(), dboProto.accountNumber());
        bind(dtoProto.bankId(), dboProto.bankId());
        bind(dtoProto.branchTransitNumber(), dboProto.branchTransitNumber());
        bind(dtoProto.chargeDescription(), dboProto.chargeDescription());
    }

    @Override
    public void copyDTOtoDBO(MerchantAccount dto, MerchantAccount dbo) {
        // TODO move to Minding type
        dbo.chargeDescription().setValue(dto.chargeDescription().getValue());
    }

    private void setCalulatedFileds(MerchantAccount entity, MerchantAccount dto) {
        ServerSideFactory.create(Vista2PmcFacade.class).calulateMerchantAccountStatus(entity);
        dto.merchantTerminalId().setValue(null);
        dto.paymentsStatus().setValue(entity.paymentsStatus().getValue());
    }

    @Override
    protected void enhanceRetrieved(MerchantAccount entity, MerchantAccount dto, RetrieveTarget retrieveTarget) {
        setCalulatedFileds(entity, dto);
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

}
