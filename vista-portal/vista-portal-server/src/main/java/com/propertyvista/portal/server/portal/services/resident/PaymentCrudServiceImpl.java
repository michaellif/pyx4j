/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-01
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.Validate;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.payment.PaymentException;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentCrudService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class PaymentCrudServiceImpl extends AbstractCrudServiceDtoImpl<PaymentRecord, PaymentRecordDTO> implements PaymentCrudService {

    private static final I18n i18n = I18n.get(PaymentCrudServiceImpl.class);

    public PaymentCrudServiceImpl() {
        super(PaymentRecord.class, PaymentRecordDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(PaymentRecord entity, PaymentRecordDTO dto, RetrieveTraget retrieveTraget) {
        super.enhanceRetrieved(entity, dto, retrieveTraget);
        enhanceListRetrieved(entity, dto);

        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsAllowed(dto.billingAccount()));
        dto.allowedPaymentTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(dto.billingAccount(), VistaApplication.resident));
    }

    @Override
    protected void enhanceListRetrieved(PaymentRecord entity, PaymentRecordDTO dto) {
        super.enhanceListRetrieved(entity, dto);

        Persistence.service().retrieve(dto.billingAccount());
        Persistence.service().retrieve(dto.billingAccount().lease());
        Persistence.service().retrieve(dto.billingAccount().lease().unit());
        Persistence.service().retrieve(dto.billingAccount().lease().unit().building());

        dto.leaseId().set(dto.billingAccount().lease().leaseId());
        dto.leaseStatus().set(dto.billingAccount().lease().status());
        dto.propertyCode().set(dto.billingAccount().lease().unit().building().propertyCode());
        dto.unitNumber().set(dto.billingAccount().lease().unit().info().number());
        dto.addThisPaymentMethodToProfile().setValue(entity.paymentMethod().isProfiledMethod().getValue());

        Persistence.service().retrieve(dto.paymentMethod());
        Persistence.service().retrieve(dto.paymentMethod().customer());
        Persistence.service().retrieve(dto.leaseTermParticipant());
    }

    @Override
    protected void persist(PaymentRecord entity, PaymentRecordDTO dto) {
        entity.paymentMethod().customer().set(dto.leaseTermParticipant().leaseParticipant().customer());

        Validate.isTrue(entity.paymentMethod().customer().equals(TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().customer()));
        Validate.isTrue(PaymentType.avalableInPortal().contains(dto.paymentMethod().type().getValue()));

        Lease lease = Persistence.service().retrieve(Lease.class, TenantAppContext.getCurrentUserLeaseIdStub().getPrimaryKey());
        Collection<PaymentType> allowedPaymentTypes = ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(lease.billingAccount(),
                VistaApplication.resident);
        Validate.isTrue(allowedPaymentTypes.contains(dto.paymentMethod().type().getValue()));

        // Do not change profile methods
        if (entity.paymentMethod().id().isNull()) {
            if (dto.addThisPaymentMethodToProfile().isBooleanTrue() && PaymentType.avalableInProfile().contains(dto.paymentMethod().type().getValue())) {
                entity.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);
            } else {
                entity.paymentMethod().isProfiledMethod().setValue(Boolean.FALSE);
            }

            // some corrections for particular method types:
            if (dto.paymentMethod().type().getValue() == PaymentType.Echeck) {
                entity.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);
            }
        }

        ServerSideFactory.create(PaymentFacade.class).persistPayment(entity);
        Persistence.service().commit();

        try {
            ServerSideFactory.create(PaymentFacade.class).processPayment(entity);
        } catch (PaymentException e) {
            throw new UserRuntimeException(i18n.tr("Payment Failed"), e);
        }

        Persistence.service().commit();
    }

    @Override
    public void initNew(AsyncCallback<PaymentRecordDTO> callback) {
        LeaseTermTenant tenant = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenant.leaseTermV());
        Persistence.service().retrieve(tenant.leaseTermV().holder().lease());

        Lease lease = tenant.leaseTermV().holder().lease();
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().building());

        PaymentRecordDTO dto = EntityFactory.create(PaymentRecordDTO.class);

        dto.billingAccount().set(lease.billingAccount());
        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsAllowed(lease.billingAccount()));
        dto.allowedPaymentTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(dto.billingAccount(), VistaApplication.resident));

        dto.leaseId().set(lease.leaseId());
        dto.leaseStatus().set(lease.status());
        dto.propertyCode().set(lease.unit().building().propertyCode());
        dto.unitNumber().set(lease.unit().info().number());
        dto.leaseTermParticipant().set(tenant);

        // some default values:
        dto.createdDate().setValue(new LogicalDate(SystemDateManager.getDate()));

        // calculate current balance:
        dto.amount().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(lease.billingAccount()));
        if (dto.amount().isNull() || dto.amount().getValue().signum() == -1) {
            dto.amount().setValue(new BigDecimal("0.00"));
        }

        callback.onSuccess(dto);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressStructured> callback) {
        AddressRetriever.getLeaseParticipantCurrentAddress(callback, TenantAppContext.getCurrentUserTenantInLease());
    }

    @Override
    public void getProfiledPaymentMethods(AsyncCallback<Vector<LeasePaymentMethod>> callback) {
        LeaseTermTenant payer = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.ensureRetrieve(payer.leaseParticipant().lease(), AttachLevel.Attached);
        Collection<PaymentType> allowedTypes = ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(
                payer.leaseParticipant().lease().billingAccount(), VistaApplication.resident);
        // get payer's payment methods and remove non-allowed ones: 
        List<LeasePaymentMethod> methods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(payer);
        Iterator<LeasePaymentMethod> it = methods.iterator();
        while (it.hasNext()) {
            if (!allowedTypes.contains(it.next().type().getValue())) {
                it.remove();
            }
        }

        callback.onSuccess(new Vector<LeasePaymentMethod>(methods));

    }
}
