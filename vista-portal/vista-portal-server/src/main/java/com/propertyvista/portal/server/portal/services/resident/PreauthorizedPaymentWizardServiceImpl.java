/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.AmountType;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentWizardService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class PreauthorizedPaymentWizardServiceImpl extends EntityDtoBinder<PreauthorizedPayment, PreauthorizedPaymentDTO> implements
        PreauthorizedPaymentWizardService {

    private static final I18n i18n = I18n.get(AbstractCrudServiceDtoImpl.class);

    public PreauthorizedPaymentWizardServiceImpl() {
        super(PreauthorizedPayment.class, PreauthorizedPaymentDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    public void create(AsyncCallback<PreauthorizedPaymentDTO> callback) {
        PreauthorizedPaymentDTO dto = EntityFactory.create(PreauthorizedPaymentDTO.class);

        dto.tenant().set(TenantAppContext.getCurrentUserTenant());
        dto.amountType().setValue(AmountType.Value);
        dto.value().setValue(BigDecimal.ZERO);

        callback.onSuccess(dto);
    }

    @Override
    @ServiceExecution(waitCaption = "Submitting...")
    public void finish(AsyncCallback<VoidSerializable> callback, PreauthorizedPaymentDTO dto) {
        PreauthorizedPayment entity = createDBO(dto);

        if (entity.paymentMethod().getPrimaryKey() == null) {
            // TODO: persist new PM here... 
        }

        ServerSideFactory.create(PaymentMethodFacade.class).persistPreauthorizedPayment(entity, TenantAppContext.getCurrentUserTenant());
        Persistence.service().commit();

        callback.onSuccess(null);
    }
}
