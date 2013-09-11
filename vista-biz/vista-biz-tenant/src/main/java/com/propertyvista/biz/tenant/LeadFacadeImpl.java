/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.biz.validation.validators.lead.LeadValidator;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lead.Guest;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Lead.Status;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class LeadFacadeImpl implements LeadFacade {

    private static final I18n i18n = I18n.get(LeadFacadeImpl.class);

    @Override
    public Lead init(Lead lead) {
        ServerSideFactory.create(IdAssignmentFacade.class).assignId(lead);
        return lead;
    }

    @Override
    public Lead persist(Lead lead) {
        Persistence.service().merge(lead);
        return lead;
    }

    @Override
    public void convertToApplication(Key leadId, AptUnit unitId) {
        Lead lead = Persistence.service().retrieve(Lead.class, leadId);
        if (!lead.lease().isNull()) {
            throw new UserRuntimeException(i18n.tr("The Lead is converted to Lease already!"));
        }

        Set<ValidationFailure> validationFailures = new LeadValidator().validate(lead);

        if (!validationFailures.isEmpty()) {
            List<String> errorMessages = new ArrayList<String>();
            for (ValidationFailure failure : validationFailures) {
                errorMessages.add(failure.getMessage());
            }
            String errorsRoster = StringUtils.join(errorMessages, ",\n");
            throw new UserRuntimeException(i18n.tr("Unable to convert lead due to the following validation errors:\n{0}", errorsRoster));
        }

        Date leaseEnd = null;
        switch (lead.leaseTerm().getValue()) {
        case months6:
            leaseEnd = DateUtils.monthAdd(lead.moveInDate().getValue(), 6 + 1);
            break;
        case months12:
            leaseEnd = DateUtils.monthAdd(lead.moveInDate().getValue(), 12 + 1);
            break;
        case months18:
            leaseEnd = DateUtils.monthAdd(lead.moveInDate().getValue(), 18 + 1);
            break;
        case other:
            leaseEnd = DateUtils.monthAdd(lead.moveInDate().getValue(), 12 + 1);
            break;
        }

        LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
        Lease lease = leaseFacade.create(Lease.Status.Application);

        lease.type().set(lead.leaseType());

        lease.currentTerm().termFrom().setValue(lead.moveInDate().getValue());
        lease.currentTerm().termTo().setValue(new LogicalDate(leaseEnd));

        lease.expectedMoveIn().setValue(lead.moveInDate().getValue());

        boolean asApplicant = true;
        for (Guest guest : lead.guests()) {
            Customer customer = EntityFactory.create(Customer.class);
            customer.person().set(guest.person());

            LeaseTermTenant tenantInLease = EntityFactory.create(LeaseTermTenant.class);
            tenantInLease.leaseParticipant().customer().set(customer);
            tenantInLease.role().setValue(asApplicant ? LeaseTermParticipant.Role.Applicant : LeaseTermParticipant.Role.CoApplicant);
            lease.currentTerm().version().tenants().add(tenantInLease);
            asApplicant = false;
        }
        lease = leaseFacade.init(lease);
        if (unitId.getPrimaryKey() != null) {
            leaseFacade.setUnit(lease, unitId);
        }
        lease = leaseFacade.persist(lease);

        // mark Lead as converted:
        lead.lease().set(lease);
        Persistence.service().persist(lead);

    }

    @Override
    public void close(Key leadId) {
        Lead lead = Persistence.service().retrieve(Lead.class, leadId);
        lead.status().setValue(Status.closed);
        Persistence.secureSave(lead);
    }

    @Override
    public void setLeadRentedState(Lease leaseId) {
        EntityQueryCriteria<Lead> criteria = new EntityQueryCriteria<Lead>(Lead.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lease(), leaseId));
        Lead lead = Persistence.secureRetrieve(criteria);
        if (lead != null) {
            lead.status().setValue(Lead.Status.rented);
            Persistence.secureSave(lead);
        }
    }
}
