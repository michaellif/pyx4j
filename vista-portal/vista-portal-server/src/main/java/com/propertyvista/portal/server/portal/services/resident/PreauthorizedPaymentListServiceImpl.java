/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentListDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentListService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class PreauthorizedPaymentListServiceImpl extends AbstractListServiceDtoImpl<PreauthorizedPayment, PreauthorizedPaymentListDTO.itemDTO> implements
        PreauthorizedPaymentListService {

    public PreauthorizedPaymentListServiceImpl() {
        super(PreauthorizedPayment.class, PreauthorizedPaymentListDTO.itemDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<PreauthorizedPayment> dbCriteria, EntityListCriteria<PreauthorizedPaymentListDTO.itemDTO> dtoCriteria) {
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().tenant().lease(), TenantAppContext.getCurrentUserLeaseIdStub()));
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().isDeleted(), Boolean.FALSE));
    }

    @Override
    protected void enhanceListRetrieved(PreauthorizedPayment entity, PreauthorizedPaymentListDTO.itemDTO dto) {
        super.enhanceListRetrieved(entity, dto);
        Persistence.ensureRetrieve(dto.tenant(), AttachLevel.ToStringMembers);

        // prepare co-tenant data:
        dto.isCoTenant().setValue(!dto.tenant().equals(TenantAppContext.getCurrentUserTenant()));
        if (dto.isCoTenant().isBooleanTrue()) {
            dto.paymentMethod().clearValues();
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        PreauthorizedPayment pap = Persistence.service().retrieve(entityClass, entityId);
        ServerSideFactory.create(PaymentMethodFacade.class).deletePreauthorizedPayment(pap);
        Persistence.service().commit();

        callback.onSuccess(Boolean.TRUE);
    }
}
