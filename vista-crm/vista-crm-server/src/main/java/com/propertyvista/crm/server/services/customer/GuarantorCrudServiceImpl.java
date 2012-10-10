/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.crm.rpc.services.customer.GuarantorCrudService;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.GuarantorDTO;
import com.propertyvista.server.common.util.AddressRetriever;
import com.propertyvista.server.common.util.LeaseParticipantUtils;

public class GuarantorCrudServiceImpl extends AbstractCrudServiceDtoImpl<Guarantor, GuarantorDTO> implements GuarantorCrudService {

    public GuarantorCrudServiceImpl() {
        super(Guarantor.class, GuarantorDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Guarantor entity, GuarantorDTO dto, RetrieveTraget retrieveTraget) {
        // load detached data:
        Persistence.service().retrieve(dto.leaseTermV());
        Persistence.service().retrieve(dto.leaseTermV().holder(), AttachLevel.ToStringMembers);
        Persistence.service().retrieve(dto.leaseCustomer().customer().emergencyContacts());

        LeaseParticipantUtils.retrieveCustomerScreeningPointer(dto.leaseCustomer().customer());
    }

    @Override
    protected void enhanceListRetrieved(Guarantor entity, GuarantorDTO dto) {
        Persistence.service().retrieve(dto.leaseTermV());
        Persistence.service().retrieve(dto.leaseTermV().holder(), AttachLevel.ToStringMembers);
    }

    @Override
    protected void persist(Guarantor entity, GuarantorDTO dto) {
        ServerSideFactory.create(CustomerFacade.class).persistCustomer(entity.leaseCustomer().customer());
        entity.role().setValue(LeaseParticipant.Role.Guarantor);
    }

    @Override
    public void deletePaymentMethod(AsyncCallback<Boolean> callback, PaymentMethod paymentMethod) {
        Persistence.service().retrieve(paymentMethod);
        ServerSideFactory.create(PaymentFacade.class).deletePaymentMethod(paymentMethod);
        Persistence.service().commit();
        callback.onSuccess(Boolean.TRUE);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressStructured> callback, Key entityId) {
        AddressRetriever.getLeaseParticipantCurrentAddress(callback, Persistence.service().retrieve(Guarantor.class, entityId));
    }
}
