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
package com.propertyvista.crm.server.services.customer.screening;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.AbstractVersionedCrudServiceDtoImpl;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.crm.rpc.services.customer.screening.LeaseParticipantScreeningCrudService;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseParticipantScreeningTO;
import com.propertyvista.misc.VistaTODO;

public class LeaseParticipantScreeningCrudServiceImpl extends AbstractVersionedCrudServiceDtoImpl<CustomerScreening, LeaseParticipantScreeningTO> implements
        LeaseParticipantScreeningCrudService {

    public LeaseParticipantScreeningCrudServiceImpl() {
        super(new CrudEntityBinder<CustomerScreening, LeaseParticipantScreeningTO>(CustomerScreening.class, LeaseParticipantScreeningTO.class) {
            @Override
            protected void bind() {
                bind(toProto.data().id(), boProto.id());
                bind(toProto.data().screene(), boProto.screene());
                bind(toProto.data().version(), boProto.version());

                bind(toProto.version().versionNumber(), boProto.version().versionNumber());
                bind(toProto.version().fromDate(), boProto.version().fromDate());
                bind(toProto.version().toDate(), boProto.version().toDate());
            }
        });
    }

    @Override
    protected Key getBOKey(LeaseParticipantScreeningTO to) {
        // Translate PK from LeaseParticipant to CustomerScreening
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._tenantInLease(), to.getPrimaryKey().asCurrentKey()));
        Customer customer = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);
        CustomerScreening screeningId = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftOrFinal(customer, AttachLevel.IdOnly);
        return new Key(screeningId.getPrimaryKey().asLong(), to.getPrimaryKey().getVersion());
    }

    @Override
    protected Key getTOKey(CustomerScreening bo, LeaseParticipantScreeningTO to) {
        if (!to.leaseParticipantId().isNull()) {
            return new Key(to.leaseParticipantId().getPrimaryKey().asLong(), bo.getPrimaryKey().getVersion());
        } else {
            return new Key(to.getPrimaryKey().asLong(), bo.getPrimaryKey().getVersion());
        }
    }

    private void retriveSecurityAttributes(LeaseParticipantScreeningTO to) {
        LeaseParticipant<?> leaseParticipant = Persistence.service().retrieve(LeaseParticipant.class, to.leaseParticipantId().getPrimaryKey());
        Persistence.service().retrieve(leaseParticipant.lease());
        to.leaseStatus().setValue(leaseParticipant.lease().status().getValue());
    }

    private CustomerScreening retrivePersonScreeningDraftForEdit(LeaseParticipant<?> leaseParticipantId) {
        LeaseParticipant<?> leaseParticipant = Persistence.service().retrieve(LeaseParticipant.class, leaseParticipantId.getPrimaryKey());
        PolicyNode policyNode = ServerSideFactory.create(LeaseFacade.class).getLeasePolicyNode(leaseParticipant.lease());
        CustomerScreening screening = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftForEdit(leaseParticipant.customer(),
                policyNode);
        Persistence.ensureRetrieve(screening.version().incomes(), AttachLevel.Attached);
        Persistence.ensureRetrieve(screening.version().assets(), AttachLevel.Attached);
        Persistence.ensureRetrieve(screening.version().documents(), AttachLevel.Attached);

        ServerSideFactory.create(ScreeningFacade.class).registerUploadedDocuments(screening);

        return screening;
    }

    @Override
    protected LeaseParticipantScreeningTO init(InitializationData initializationData) {
        CustomerScreeningInitializationData initData = (CustomerScreeningInitializationData) initializationData;
        LeaseParticipantScreeningTO to = EntityFactory.create(LeaseParticipantScreeningTO.class);
        to.leaseParticipantId().set(initData.leaseParticipantId());
        retriveSecurityAttributes(to);

        to.data().set(retrivePersonScreeningDraftForEdit(initData.leaseParticipantId()));
        if (VistaTODO.VISTA_4498_Remove_Unnecessary_Validation_Screening_CRM) {
            to.data().version().documents().clear();
        }

        loadRestrictions(to, to.leaseParticipantId());

        return to;
    }

    @Override
    protected void retrievedSingle(CustomerScreening bo, RetrieveTarget retrieveTarget) {
        Persistence.ensureRetrieve(bo.version().incomes(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.version().assets(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.version().documents(), AttachLevel.Attached);
        Persistence.service().retrieve(bo.screene(), AttachLevel.ToStringMembers, false);
    }

    @Override
    protected void enhanceRetrieved(CustomerScreening bo, LeaseParticipantScreeningTO to, RetrieveTarget retrieveTarget) {
        // BO Key is now TO Key. And TO is leaseParticipantId
        to.leaseParticipantId().setPrimaryKey(to.getPrimaryKey().asCurrentKey());
        retriveSecurityAttributes(to);
        // If Just created duplicate ForDraftEdit
        if (retrieveTarget == RetrieveTarget.Edit) {
            to.data().set(retrivePersonScreeningDraftForEdit(to.leaseParticipantId()));
            Persistence.service().retrieve(to.data().screene(), AttachLevel.ToStringMembers, false);
        }
        loadRestrictions(to, to.leaseParticipantId());
    }

    private void loadRestrictions(LeaseParticipantScreeningTO to, LeaseParticipant<?> leaseParticipantId) {
        LeaseParticipant<?> leaseParticipant = Persistence.service().retrieve(LeaseParticipant.class, leaseParticipantId.getPrimaryKey());
        PolicyNode policyNode = ServerSideFactory.create(LeaseFacade.class).getLeasePolicyNode(leaseParticipant.lease());
        RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode, RestrictionsPolicy.class);

        to.yearsToForcingPreviousAddress().setValue(restrictionsPolicy.yearsToForcingPreviousAddress().getValue());
    }
}
