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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.crm.rpc.services.customer.LeaseCustomerCrudServiceBase;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseCustomer;
import com.propertyvista.domain.tenant.lease.LeaseCustomerTenant;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseCustomerDTO;
import com.propertyvista.server.common.util.AddressRetriever;
import com.propertyvista.server.common.util.LeaseParticipantUtils;

public abstract class LeaseCustomerCrudServiceBaseImpl<E extends LeaseParticipant<?>, DBO extends LeaseCustomer<E>, DTO extends LeaseCustomerDTO<E>> extends
        AbstractCrudServiceDtoImpl<DBO, DTO> implements LeaseCustomerCrudServiceBase<E, DTO> {

    public LeaseCustomerCrudServiceBaseImpl(Class<DBO> dboClass, Class<DTO> dtoClass) {
        super(dboClass, dtoClass);
    }

    @Override
    protected void bind() {
        bind(LeaseCustomer.class, dtoProto, dboProto);
    }

    @Override
    protected void enhanceRetrieved(DBO entity, DTO dto, RetrieveTraget retrieveTraget) {
        dto.leaseTermV().set(retrieveLeaseTerm(entity));

        LeaseParticipantUtils.retrieveCustomerScreeningPointer(dto.customer());

        // fill/update payment methods: 
        dto.paymentMethods().clear();
        dto.paymentMethods().addAll(ServerSideFactory.create(PaymentFacade.class).retrievePaymentMethods(entity.customer()));
        if (retrieveTraget == RetrieveTraget.Edit) {
            for (PaymentMethod method : dto.paymentMethods()) {
                Persistence.service().retrieve(method.details());
            }
        }

        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsAllowed(dto.leaseTermV().holder()));
    }

    @Override
    protected void enhanceListRetrieved(DBO entity, DTO dto) {
        dto.leaseTermV().set(retrieveLeaseTerm(entity));
    }

    @Override
    protected void persist(DBO entity, DTO dto) {
        ServerSideFactory.create(CustomerFacade.class).persistCustomer(entity.customer());

        // delete payment methods removed in UI:
        for (PaymentMethod paymentMethod : ServerSideFactory.create(PaymentFacade.class).retrievePaymentMethods(entity.customer())) {
            if (!dto.paymentMethods().contains(paymentMethod)) {
                ServerSideFactory.create(PaymentFacade.class).deletePaymentMethod(paymentMethod);
            }
        }

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._Units().$()._Leases().$().currentTerm().versions(), dto.leaseTermV()));
        Building building = Persistence.service().retrieve(criteria);

        // save new/edited ones:
        for (PaymentMethod paymentMethod : dto.paymentMethods()) {
            paymentMethod.customer().set(entity.customer());
            paymentMethod.isOneTimePayment().setValue(false);
            ServerSideFactory.create(PaymentFacade.class).persistPaymentMethod(building, paymentMethod);
        }

        super.persist(entity, dto);
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
        AddressRetriever.getLeaseParticipantCurrentAddress(callback, EntityFactory.createIdentityStub(entityClass, entityId));
    }

    private LeaseTerm.LeaseTermV retrieveLeaseTerm(LeaseCustomer<E> leaseCustomer) {
        LeaseTerm.LeaseTermV term = null;

        // case of 'current' Tenants for applications: 
        if (leaseCustomer.lease().status().getValue().isDraft()) {
            EntityQueryCriteria<LeaseTerm> criteria = EntityQueryCriteria.create(LeaseTerm.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().id(), leaseCustomer.lease().currentTerm().id()));
            criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
            term = Persistence.service().retrieve(criteria).version();
        } else {
            // case of 'current' Tenants: 
            {
                EntityQueryCriteria<LeaseTerm> criteria = EntityQueryCriteria.create(LeaseTerm.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().id(), leaseCustomer.lease().currentTerm().id()));
                if (leaseCustomer instanceof LeaseCustomerTenant) {
                    criteria.add(PropertyCriterion.eq(criteria.proto().version().tenants().$().leaseCustomer(), leaseCustomer));
                } else {
                    criteria.add(PropertyCriterion.eq(criteria.proto().version().guarantors().$().leaseCustomer(), leaseCustomer));
                }
                criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
                LeaseTerm leaseTerm = Persistence.service().retrieve(criteria);
                if (leaseTerm != null) {
                    term = leaseTerm.version();
                }
            }
            // case of 'Former' Tenants: 
            if (term == null) {
                EntityQueryCriteria<LeaseTerm.LeaseTermV> criteria = EntityQueryCriteria.create(LeaseTerm.LeaseTermV.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().holder().lease(), leaseCustomer.lease()));
                if (leaseCustomer instanceof LeaseCustomerTenant) {
                    criteria.add(PropertyCriterion.eq(criteria.proto().tenants().$().leaseCustomer(), leaseCustomer));
                } else {
                    criteria.add(PropertyCriterion.eq(criteria.proto().guarantors().$().leaseCustomer(), leaseCustomer));
                }
                criteria.desc(criteria.proto().id());
                term = Persistence.service().retrieve(criteria);
            }
        }

        //This is wrong!  TODO debug this.
        Persistence.service().retrieve(term.holder(), AttachLevel.ToStringMembers);

        return term;
    }
}
