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
import java.util.Collection;
import java.util.Vector;

import org.apache.commons.lang.Validate;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.AmountType;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentWizardService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressConverter;
import com.propertyvista.server.common.util.AddressRetriever;
import com.propertyvista.server.common.util.LeaseParticipantUtils;

public class PreauthorizedPaymentWizardServiceImpl extends EntityDtoBinder<PreauthorizedPayment, PreauthorizedPaymentDTO> implements
        PreauthorizedPaymentWizardService {

    public PreauthorizedPaymentWizardServiceImpl() {
        super(PreauthorizedPayment.class, PreauthorizedPaymentDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    public void create(AsyncCallback<PreauthorizedPaymentDTO> callback) {
        Lease lease = TenantAppContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().building());

        PreauthorizedPaymentDTO dto = EntityFactory.create(PreauthorizedPaymentDTO.class);

        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsAllowed(lease.billingAccount()));
        dto.allowedPaymentTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(lease.billingAccount(), VistaApplication.resident));

        new AddressConverter.StructuredToSimpleAddressConverter().copyDBOtoDTO(AddressRetriever.getLeaseAddress(lease), dto.propertyAddress());

        dto.propertyCode().set(lease.unit().building().propertyCode());
        dto.unitNumber().set(lease.unit().info().number());

        dto.leaseId().set(lease.leaseId());
        dto.leaseStatus().set(lease.status());

        dto.tenant().set(TenantAppContext.getCurrentUserTenant());

        // some default values:
        dto.amountType().setValue(AmountType.Value);
        dto.value().setValue(BigDecimal.ZERO);

        callback.onSuccess(dto);
    }

    @Override
    @ServiceExecution(waitCaption = "Submitting...")
    public void finish(AsyncCallback<Key> callback, PreauthorizedPaymentDTO dto) {
        PreauthorizedPayment entity = createDBO(dto);

        if (entity.paymentMethod().getPrimaryKey() == null) {
            Lease lease = TenantAppContext.getCurrentUserLease();
            Persistence.service().retrieve(lease.unit());

            entity.paymentMethod().customer().set(TenantAppContext.getCurrentUserCustomer());

            Validate.isTrue(PaymentType.avalableInPortal().contains(entity.paymentMethod().type().getValue()));
            Collection<PaymentType> allowedPaymentTypes = ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(lease.billingAccount(),
                    VistaApplication.resident);

            // save just allowed methods here:
            if (allowedPaymentTypes.contains(entity.paymentMethod().type().getValue())) {
                entity.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);

                ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(entity.paymentMethod(), lease.unit().building());
            }
        }

        ServerSideFactory.create(PaymentMethodFacade.class).persistPreauthorizedPayment(entity,
                EntityFactory.createIdentityStub(Tenant.class, TenantAppContext.getCurrentUserTenant().getPrimaryKey()));
        Persistence.service().commit();

        callback.onSuccess(entity.getPrimaryKey());
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressStructured> callback) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddress(TenantAppContext.getCurrentUserTenant()));
    }

    @Override
    public void getProfiledPaymentMethods(AsyncCallback<Vector<LeasePaymentMethod>> callback) {
        callback.onSuccess(new Vector<LeasePaymentMethod>(LeaseParticipantUtils.getProfiledPaymentMethods(TenantAppContext.getCurrentUserTenantInLease())));
    }
}
