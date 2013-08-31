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
package com.propertyvista.portal.server.portal.web.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.portal.rpc.portal.web.dto.PreauthorizedPaymentDTO;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPaySummaryDTO;
import com.propertyvista.portal.rpc.portal.web.services.PreauthorizedPaymentListService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class PreauthorizedPaymentListServiceImpl extends AbstractListServiceDtoImpl<PreauthorizedPayment, PreauthorizedPaymentDTO> implements
        PreauthorizedPaymentListService {

    public PreauthorizedPaymentListServiceImpl() {
        super(PreauthorizedPayment.class, PreauthorizedPaymentDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<PreauthorizedPayment> dbCriteria, EntityListCriteria<PreauthorizedPaymentDTO> dtoCriteria) {
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().tenant().lease(), TenantAppContext.getCurrentUserLeaseIdStub()));
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().isDeleted(), Boolean.FALSE));
        dbCriteria.sort(new Sort(dbCriteria.proto().tenant(), false));
    }

    @Override
    protected void enhanceListRetrieved(PreauthorizedPayment entity, PreauthorizedPaymentDTO dto) {
        super.enhanceListRetrieved(entity, dto);
        Persistence.ensureRetrieve(dto.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dto.tenant().customer().user(), AttachLevel.Attached);

        // clear co-tenant data:
        if (!dto.tenant().equals(TenantAppContext.getCurrentUserTenant())) {
            dto.paymentMethod().clearValues();
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        ServerSideFactory.create(PaymentMethodFacade.class).deletePreauthorizedPayment(Persistence.service().retrieve(entityClass, entityId));
        Persistence.service().commit();

        callback.onSuccess(Boolean.TRUE);
    }

    @Override
    public void getData(final AsyncCallback<AutoPaySummaryDTO> callback) {
        list(new AsyncCallback<EntitySearchResult<PreauthorizedPaymentDTO>>() {
            @Override
            public void onSuccess(EntitySearchResult<PreauthorizedPaymentDTO> result) {
                AutoPaySummaryDTO dto = EntityFactory.create(AutoPaySummaryDTO.class);

                dto.currentAutoPayments().addAll(result.getData());
                dto.currentAutoPayDate().setValue(
                        ServerSideFactory.create(PaymentMethodFacade.class).getCurrentPreauthorizedPaymentDate(TenantAppContext.getCurrentUserLeaseIdStub()));
                dto.nextAutoPayDate().setValue(
                        ServerSideFactory.create(PaymentMethodFacade.class).getNextPreauthorizedPaymentDate(TenantAppContext.getCurrentUserLeaseIdStub()));
                dto.modificationsAllowd().setValue(
                        ServerSideFactory.create(LeaseFacade.class).isMoveOutWithinNextBillingCycle(TenantAppContext.getCurrentUserLeaseIdStub()));

                callback.onSuccess(dto);
            }

            @Override
            public void onFailure(Throwable arg0) {
                callback.onFailure(arg0);
            }
        }, new EntityListCriteria<PreauthorizedPaymentDTO>(PreauthorizedPaymentDTO.class));
    }
}
