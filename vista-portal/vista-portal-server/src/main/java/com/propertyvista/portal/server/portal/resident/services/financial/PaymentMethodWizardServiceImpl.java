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
package com.propertyvista.portal.server.portal.resident.services.financial;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentMethodWizardService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class PaymentMethodWizardServiceImpl extends AbstractCrudServiceDtoImpl<LeasePaymentMethod, PaymentMethodDTO> implements PaymentMethodWizardService {

    public PaymentMethodWizardServiceImpl() {
        super(LeasePaymentMethod.class, PaymentMethodDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDtoMember(toProto.paymentMethod());
    }

    @Override
    protected PaymentMethodDTO init(InitializationData initializationData) {
        Lease lease = ResidentPortalContext.getLease();

        PaymentMethodDTO dto = EntityFactory.create(PaymentMethodDTO.class);

        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsSetup(lease.billingAccount()));
        dto.allowedPaymentTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(lease.billingAccount(), VistaApplication.resident));
        dto.allowedCardTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedCardTypes(lease.billingAccount(), VistaApplication.resident));
        dto.convenienceFeeApplicableCardTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getConvenienceFeeApplicableCardTypes(lease.billingAccount(), VistaApplication.resident));

        if (dto.allowedPaymentTypes().contains(PaymentType.Echeck)) {
            dto.paymentMethod().type().setValue(PaymentType.Echeck);
        } else if (!dto.allowedPaymentTypes().isEmpty()) {
            dto.paymentMethod().type().setValue(dto.allowedPaymentTypes().iterator().next());
        }

        return dto;
    }

    @Override
    protected void persist(LeasePaymentMethod bo, PaymentMethodDTO to) {
        Lease lease = ResidentPortalContext.getLease();

        bo.customer().set(ResidentPortalContext.getCustomer());
        bo.isProfiledMethod().setValue(Boolean.TRUE);

        ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(lease.billingAccount(), bo, VistaApplication.resident);
        ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(bo, lease.unit().building());
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressSimple> callback) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddressSimple(ResidentPortalContext.getTenant()));
    }
}
