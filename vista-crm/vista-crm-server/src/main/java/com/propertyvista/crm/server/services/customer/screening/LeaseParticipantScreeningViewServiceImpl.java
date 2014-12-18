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
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.AbstractVersionedCrudServiceDtoImpl;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.crm.rpc.services.customer.screening.LeaseParticipantScreeningViewService;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseParticipantScreeningTO;

public class LeaseParticipantScreeningViewServiceImpl extends AbstractVersionedCrudServiceDtoImpl<CustomerScreening, LeaseParticipantScreeningTO> implements
        LeaseParticipantScreeningViewService {

    public LeaseParticipantScreeningViewServiceImpl() {
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

    @Override
    protected void retrievedSingle(CustomerScreening bo, RetrieveTarget retrieveTarget) {
        Persistence.ensureRetrieve(bo.version().incomes(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.version().assets(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.version().documents(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.version().legalQuestions(), AttachLevel.Attached);

        Persistence.service().retrieve(bo.screene(), AttachLevel.ToStringMembers, false);
    }

    @Override
    protected void enhanceRetrieved(CustomerScreening bo, LeaseParticipantScreeningTO to, RetrieveTarget retrieveTarget) {
        if (retrieveTarget != RetrieveTarget.View) {
            throw new Error("Shouldn't be called - view service only");
        }

        // BO Key is now TO Key. And TO is leaseParticipantId
        to.leaseParticipantId().setPrimaryKey(to.getPrimaryKey().asCurrentKey());

        LeaseParticipant<?> leaseParticipant = Persistence.service().retrieve(LeaseParticipant.class, to.leaseParticipantId().getPrimaryKey());
        Persistence.service().retrieve(leaseParticipant.lease());
        to.leaseStatus().setValue(leaseParticipant.lease().status().getValue());
    }

    @Override
    protected boolean persist(CustomerScreening bo, LeaseParticipantScreeningTO to) {
        throw new Error("Facade should be used");
    }
}
