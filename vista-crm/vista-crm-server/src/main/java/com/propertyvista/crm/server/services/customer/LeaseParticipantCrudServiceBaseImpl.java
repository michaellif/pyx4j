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

import java.util.EnumSet;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade.PaymentMethodUsage;
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.crm.rpc.services.customer.LeaseParticipantCrudServiceBase;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.LeaseParticipantDTO;
import com.propertyvista.server.common.util.AddressRetriever;
import com.propertyvista.server.common.util.LeaseParticipantUtils;

public abstract class LeaseParticipantCrudServiceBaseImpl<BO extends LeaseParticipant<? extends LeaseTermParticipant<?>>, TO extends LeaseParticipantDTO<? extends LeaseTermParticipant<?>>>
        extends AbstractCrudServiceDtoImpl<BO, TO> implements LeaseParticipantCrudServiceBase<TO> {

    public LeaseParticipantCrudServiceBaseImpl(Class<BO> boClass, Class<TO> toClass) {
        super(boClass, toClass);
    }

    @Override
    protected void bind() {
        bind(LeaseParticipant.class, toProto, boProto);
    }

    @Override
    protected void enhanceRetrieved(BO bo, TO to, RetrieveTarget retrieveTarget) {
        to.leaseTermV().set(retrieveLeaseTerm(bo));

        LeaseParticipantUtils.retrieveCustomerScreeningPointer(to.customer());

        // fill/update payment methods: 
        to.paymentMethods().clear();
        List<LeasePaymentMethod> methods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(bo, PaymentMethodUsage.InProfile,
                VistaApplication.crm);
        to.paymentMethods().addAll(methods);
        if (retrieveTarget == RetrieveTarget.Edit) {
            for (LeasePaymentMethod method : to.paymentMethods()) {
                Persistence.service().retrieve(method.details());
            }
        }

        to.allowedCardTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedCardTypes(to.lease().billingAccount(), VistaApplication.crm));

        to.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsSetup(to.leaseTermV().holder()));

        Persistence.service().retrieve(to.customer().picture());

        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.eq(criteria.proto().leaseParticipants().$().customer(), bo.customer());
            criteria.in(criteria.proto().status(), Status.present());
            to.leasesOfThisCustomer().setCollectionSizeOnly(Persistence.service().count(criteria));
        }
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.eq(criteria.proto().leaseParticipants().$().customer(), bo.customer());
            criteria.in(criteria.proto().status(), EnumSet.of(Lease.Status.Application, Lease.Status.Approved));
            criteria.isNotNull(criteria.proto().leaseApplication().status());
            to.applicationsOfThisCustomer().setCollectionSizeOnly(Persistence.service().count(criteria));
        }
    }

    @Override
    protected void enhanceListRetrieved(BO bo, TO to) {
        to.leaseTermV().set(retrieveLeaseTerm(bo));
    }

    @Override
    protected void persist(BO bo, TO to) {
        ServerSideFactory.create(CustomerFacade.class).persistCustomer(bo.customer());

        // delete payment methods removed in UI:
        for (LeasePaymentMethod paymentMethod : ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(bo,
                PaymentMethodUsage.InProfile, VistaApplication.crm)) {
            if (!to.paymentMethods().contains(paymentMethod)) {
                ServerSideFactory.create(PaymentMethodFacade.class).deleteLeasePaymentMethod(paymentMethod);
            }
        }

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().units().$()._Leases().$().currentTerm().versions(), to.leaseTermV()));
        Building building = Persistence.service().retrieve(criteria);

        // save new/edited ones:
        Persistence.ensureRetrieve(bo.lease(), AttachLevel.Attached);
        for (LeasePaymentMethod paymentMethod : to.paymentMethods()) {
            paymentMethod.customer().set(bo.customer());
            paymentMethod.isProfiledMethod().setValue(true);

            ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(bo.lease().billingAccount(), paymentMethod, VistaApplication.crm);
            ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethod, building);
        }

        super.persist(bo, to);
    }

    @Override
    public void getAllowedPaymentTypes(AsyncCallback<Vector<PaymentType>> callback, TO participantId) {
        BO leaseParticipant = Persistence.service().retrieve(boClass, participantId.getPrimaryKey());
        Persistence.ensureRetrieve(leaseParticipant.lease(), AttachLevel.Attached);
        callback.onSuccess(new Vector<PaymentType>(ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(
                leaseParticipant.lease().billingAccount(), VistaApplication.crm)));
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressSimple> callback, TO participantId) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddressSimple(EntityFactory.createIdentityStub(boClass, participantId.getPrimaryKey())));
    }

    private LeaseTerm.LeaseTermV retrieveLeaseTerm(BO leaseParticipant) {
        LeaseTerm.LeaseTermV term = null;

        // case of 'current' Tenants for applications: 
        if (leaseParticipant.lease().status().getValue().isDraft()) {
            EntityQueryCriteria<LeaseTerm> criteria = EntityQueryCriteria.create(LeaseTerm.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().id(), leaseParticipant.lease().currentTerm().id()));
            criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
            term = Persistence.service().retrieve(criteria).version();
        } else {
            // case of 'current' Tenants: 
            {
                EntityQueryCriteria<LeaseTerm> criteria = EntityQueryCriteria.create(LeaseTerm.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().id(), leaseParticipant.lease().currentTerm().id()));
                if (leaseParticipant instanceof Tenant) {
                    criteria.add(PropertyCriterion.eq(criteria.proto().version().tenants().$().leaseParticipant(), leaseParticipant));
                } else {
                    criteria.add(PropertyCriterion.eq(criteria.proto().version().guarantors().$().leaseParticipant(), leaseParticipant));
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
                criteria.add(PropertyCriterion.eq(criteria.proto().holder().lease(), leaseParticipant.lease()));
                if (leaseParticipant instanceof Tenant) {
                    criteria.add(PropertyCriterion.eq(criteria.proto().tenants().$().leaseParticipant(), leaseParticipant));
                } else {
                    criteria.add(PropertyCriterion.eq(criteria.proto().guarantors().$().leaseParticipant(), leaseParticipant));
                }
                criteria.desc(criteria.proto().id());
                term = Persistence.service().retrieve(criteria);
            }
        }

        //This is wrong!  TODO debug this.
        Persistence.service().retrieve(term.holder(), AttachLevel.ToStringMembers, false);

        return term;
    }
}
