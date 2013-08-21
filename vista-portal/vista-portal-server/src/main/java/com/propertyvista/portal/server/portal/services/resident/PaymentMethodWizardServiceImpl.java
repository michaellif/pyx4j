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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.dto.PaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodWizardService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class PaymentMethodWizardServiceImpl extends EntityDtoBinder<LeasePaymentMethod, PaymentMethodDTO> implements PaymentMethodWizardService {

    public PaymentMethodWizardServiceImpl() {
        super(LeasePaymentMethod.class, PaymentMethodDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDtoMember(dtoProto.paymentMethod());
    }

    @Override
    public void create(AsyncCallback<PaymentMethodDTO> callback) {
        Lease lease = TenantAppContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().building());

        PaymentMethodDTO dto = EntityFactory.create(PaymentMethodDTO.class);

        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsSetup(lease.billingAccount()));
        dto.allowedPaymentTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(lease.billingAccount(), VistaApplication.residentPortal));
        dto.allowedCardTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedCardTypes(lease.billingAccount(), VistaApplication.residentPortal));

        if (dto.allowedPaymentTypes().contains(PaymentType.Echeck)) {
            dto.paymentMethod().type().setValue(PaymentType.Echeck);
        } else if (!dto.allowedPaymentTypes().isEmpty()) {
            dto.paymentMethod().type().setValue(dto.allowedPaymentTypes().iterator().next());
        }

        callback.onSuccess(dto);
    }

    @Override
    @ServiceExecution(waitCaption = "Submitting...")
    public void finish(AsyncCallback<Key> callback, PaymentMethodDTO dto) {
        Lease lease = TenantAppContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.unit());

        dto.paymentMethod().customer().set(TenantAppContext.getCurrentUserCustomer());

        ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(lease.billingAccount(), dto.paymentMethod(), VistaApplication.residentPortal);

        dto.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);

        LeasePaymentMethod entity = createDBO(dto);
        ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(entity, lease.unit().building());
        Key key = entity.getPrimaryKey();
        Persistence.service().commit();

        callback.onSuccess(key);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressSimple> callback) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddressSimple(TenantAppContext.getCurrentUserTenant()));
    }
}
