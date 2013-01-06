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
package com.propertyvista.admin.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.admin.domain.vista2pmc.DefaultEquifaxFee;
import com.propertyvista.admin.domain.vista2pmc.DefaultPaymentFees;
import com.propertyvista.admin.domain.vista2pmc.TenantSureMerchantAccount;
import com.propertyvista.admin.domain.vista2pmc.VistaMerchantAccount;
import com.propertyvista.admin.rpc.VistaSystemDefaultsDTO;
import com.propertyvista.admin.rpc.services.Vista2PmcService;

public class Vista2PmcServiceImpl implements Vista2PmcService {

    @Override
    public void retrieve(AsyncCallback<VistaSystemDefaultsDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTraget retrieveTraget) {
        VistaSystemDefaultsDTO dto = EntityFactory.create(VistaSystemDefaultsDTO.class);

        dto.paymentFees().set(Persistence.service().retrieve(EntityQueryCriteria.create(DefaultPaymentFees.class)));
        dto.equifaxFees().set(Persistence.service().retrieve(EntityQueryCriteria.create(DefaultEquifaxFee.class)));
        dto.vistaMerchantAccount().set(Persistence.service().retrieve(EntityQueryCriteria.create(VistaMerchantAccount.class)));
        dto.tenantSureMerchantAccount().set(Persistence.service().retrieve(EntityQueryCriteria.create(TenantSureMerchantAccount.class)));

        callback.onSuccess(dto);
    }

    @Override
    public void save(AsyncCallback<Key> callback, VistaSystemDefaultsDTO dto) {
        Persistence.service().merge(dto.paymentFees());
        Persistence.service().merge(dto.equifaxFees());
        Persistence.service().merge(dto.vistaMerchantAccount());
        Persistence.service().merge(dto.tenantSureMerchantAccount());
        Persistence.service().commit();
        callback.onSuccess(new Key(-1));
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
