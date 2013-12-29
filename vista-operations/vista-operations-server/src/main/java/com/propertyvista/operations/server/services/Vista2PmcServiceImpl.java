/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityDiff;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.operations.domain.vista2pmc.DefaultEquifaxFee;
import com.propertyvista.operations.domain.vista2pmc.DefaultEquifaxLimit;
import com.propertyvista.operations.domain.vista2pmc.DefaultPaymentFees;
import com.propertyvista.operations.domain.vista2pmc.TenantSureMerchantAccount;
import com.propertyvista.operations.domain.vista2pmc.VistaMerchantAccount;
import com.propertyvista.operations.domain.vista2pmc.VistaMerchantAccount.AccountType;
import com.propertyvista.operations.rpc.dto.VistaSystemDefaultsDTO;
import com.propertyvista.operations.rpc.services.Vista2PmcService;

public class Vista2PmcServiceImpl implements Vista2PmcService {

    private VistaMerchantAccount getVistaMerchantAccount(AccountType accountType) {
        EntityQueryCriteria<VistaMerchantAccount> criteria = EntityQueryCriteria.create(VistaMerchantAccount.class);
        criteria.eq(criteria.proto().accountType(), accountType);
        VistaMerchantAccount account = Persistence.service().retrieve(criteria);
        if (account == null) {
            account = EntityFactory.create(VistaMerchantAccount.class);
        }
        account.accountType().setValue(accountType);
        return account;
    }

    @Override
    public void retrieve(AsyncCallback<VistaSystemDefaultsDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        VistaSystemDefaultsDTO dto = EntityFactory.create(VistaSystemDefaultsDTO.class);

        dto.paymentFees().set(Persistence.service().retrieve(EntityQueryCriteria.create(DefaultPaymentFees.class)));
        dto.equifaxFees().set(Persistence.service().retrieve(EntityQueryCriteria.create(DefaultEquifaxFee.class)));
        dto.equifaxLimit().set(Persistence.service().retrieve(EntityQueryCriteria.create(DefaultEquifaxLimit.class)));
        dto.vistaMerchantAccountPayments().set(getVistaMerchantAccount(VistaMerchantAccount.AccountType.PaymentAggregation));
        dto.vistaMerchantAccountEquifax().set(getVistaMerchantAccount(VistaMerchantAccount.AccountType.Equifax));
        dto.tenantSureMerchantAccount().set(Persistence.service().retrieve(EntityQueryCriteria.create(TenantSureMerchantAccount.class)));

        callback.onSuccess(dto);
    }

    @Override
    public void save(AsyncCallback<Key> callback, VistaSystemDefaultsDTO dto) {
        auditChanges(dto.paymentFees(), Persistence.service().retrieve(EntityQueryCriteria.create(DefaultPaymentFees.class)));
        Persistence.service().merge(dto.paymentFees());

        auditChanges(dto.equifaxFees(), Persistence.service().retrieve(EntityQueryCriteria.create(DefaultEquifaxFee.class)));
        Persistence.service().merge(dto.equifaxFees());

        Persistence.service().merge(dto.equifaxLimit());

        auditChanges(dto.vistaMerchantAccountPayments(), getVistaMerchantAccount(VistaMerchantAccount.AccountType.PaymentAggregation));
        Persistence.service().merge(dto.vistaMerchantAccountPayments());

        auditChanges(dto.vistaMerchantAccountEquifax(), getVistaMerchantAccount(VistaMerchantAccount.AccountType.Equifax));
        Persistence.service().merge(dto.vistaMerchantAccountEquifax());

        auditChanges(dto.tenantSureMerchantAccount(), Persistence.service().retrieve(EntityQueryCriteria.create(TenantSureMerchantAccount.class)));
        Persistence.service().merge(dto.tenantSureMerchantAccount());

        Persistence.service().commit();
        callback.onSuccess(new Key(-1));
    }

    private void auditChanges(IEntity entityOrid, IEntity entityNew, IObject<?>... ignoreMembers) {
        String changes = EntityDiff.getChanges(entityOrid, entityNew, ignoreMembers);
        if (changes.length() > 0) {
            ServerSideFactory.create(AuditFacade.class).updated(entityNew, changes);
        }
    }

    @Override
    public void init(AsyncCallback<VistaSystemDefaultsDTO> callback, InitializationData initializationData) {
        throw new IllegalAccessError("this is not supposed to be used!!!");
    }

    @Override
    public void create(AsyncCallback<Key> callback, VistaSystemDefaultsDTO editableEntity) {
        throw new IllegalAccessError("this is not supposed to be used!!!");
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<VistaSystemDefaultsDTO>> callback, EntityListCriteria<VistaSystemDefaultsDTO> criteria) {
        throw new IllegalAccessError("this is not supposed to be used!!!");
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new IllegalAccessError("this is not supposed to be used!!!");
    }

}
