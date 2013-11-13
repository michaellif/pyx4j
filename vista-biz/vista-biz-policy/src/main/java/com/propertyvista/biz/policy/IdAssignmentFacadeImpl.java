/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 25, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.biz.policy;

import java.security.InvalidParameterException;
import java.util.concurrent.Callable;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplication;
import com.propertyvista.server.domain.IdAssignmentSequence;
import com.propertyvista.server.jobs.TaskRunner;

public class IdAssignmentFacadeImpl implements IdAssignmentFacade {

    private static char[] codes = { '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    @Override
    public void assignId(Building building) {
        if (needsGeneratedId(IdTarget.propertyCode)) {
            building.propertyCode().setValue(getId(IdTarget.propertyCode));
        }
    }

    @Override
    public void assignId(Lead lead) {
        if (needsGeneratedId(IdTarget.lead)) {
            lead.leadId().setValue(getId(IdTarget.lead));
        }
    }

    @Override
    public void assignId(MasterOnlineApplication masterOnlineApplication) {
        if (needsGeneratedId(IdTarget.application)) {
            masterOnlineApplication.onlineApplicationId().setValue(getId(IdTarget.application));
        }
    }

    @Override
    public void assignId(Customer customer) {
        if (customer.id().isNull() && needsGeneratedId(IdTarget.customer)) {
            customer.customerId().setValue(getId(IdTarget.customer));
        }
    }

    @Override
    public void assignId(Lease lease) {
        if (needsGeneratedId(IdTarget.lease)) {
            lease.leaseId().setValue(getId(IdTarget.lease));
        }
    }

    @Override
    public <E extends LeaseParticipant<?>> void assignId(E leaseCustomer) {
        if (leaseCustomer instanceof Tenant) {
            if (needsGeneratedId(IdTarget.tenant)) {
                leaseCustomer.participantId().setValue(getId(IdTarget.tenant));
            }
        } else if (leaseCustomer instanceof Guarantor) {
            if (needsGeneratedId(IdTarget.guarantor)) {
                leaseCustomer.participantId().setValue(getId(IdTarget.guarantor));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void assignId(Employee employee) {
        if (needsGeneratedId(IdTarget.employee)) {
            employee.employeeId().setValue(getId(IdTarget.employee));
        }
    }

    @Override
    public void assignId(MaintenanceRequest maintenanceRequest) {
        if (needsGeneratedId(IdTarget.maintenance)) {
            maintenanceRequest.requestId().setValue(getId(IdTarget.maintenance));
        }
    }

    @Override
    public String createAccountNumber() {
        return AccountNumberSequence.getNextSequence();
    }

    @Override
    public Pmc getPmcByAccountNumber(String accountNumber) {
        return AccountNumberSequence.getPmcByAccountNumber(accountNumber);
    }

    // internals:

    private static boolean needsGeneratedId(IdAssignmentItem.IdTarget target) {
        IdAssignmentItem targetItem = getIdAssignmentItem(target);

        return targetItem.type().getValue() == IdAssignmentType.generatedNumber || targetItem.type().getValue() == IdAssignmentType.generatedAlphaNumeric;
    }

    private static String getId(final IdAssignmentItem.IdTarget target) {
        IdAssignmentItem targetItem = getIdAssignmentItem(target);

        Long nextId = TaskRunner.runAutonomousTransation(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                EntityQueryCriteria<IdAssignmentSequence> criteria = EntityQueryCriteria.create(IdAssignmentSequence.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().target(), target));
                IdAssignmentSequence sequence = Persistence.service().retrieve(criteria);

                if (sequence == null) {
                    sequence = EntityFactory.create(IdAssignmentSequence.class);
                    sequence.target().setValue(target);
                    sequence.number().setValue(0l);
                }

                long id = sequence.number().getValue() + 1;
                sequence.number().setValue(id);
                Persistence.service().persist(sequence);
                Persistence.service().commit();
                return id;
            }
        });

        String res = "";

        if (targetItem.type().getValue() == IdAssignmentType.generatedNumber) {
            res = new Long(nextId).toString();
        } else if (targetItem.type().getValue() == IdAssignmentType.generatedAlphaNumeric) {
            StringBuffer buf = new StringBuffer();
            generatedAlphabetical(buf, nextId);
            res = buf.toString();
        }

        return res;
    }

    private static IdAssignmentItem getIdAssignmentItem(final IdAssignmentItem.IdTarget target) {
        IdAssignmentPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class),
                IdAssignmentPolicy.class);

        if (policy == null) {
            throw new InvalidParameterException("Node OrganizationPoliciesNode has no : " + IdAssignmentPolicy.TO_STRING_ATTR + " assigned");
        }

        IdAssignmentItem targetItem = null;
        for (IdAssignmentItem item : policy.items()) {
            if (item.target().getValue() == target) {
                targetItem = item;
                break;
            }
        }

        if (targetItem == null) {
            throw new InvalidParameterException("No item for target: " + target.toString());
        }
        return targetItem;
    }

    private static void generatedAlphabetical(final StringBuffer res, long val) {
        long div = val / codes.length;
        long mod = val % codes.length;

        if (val > codes.length) {
            generatedAlphabetical(res, mod == 0 ? div - 1 : div);
        }

        if (val == codes.length || mod == 0) {
            res.append(codes[codes.length - 1]);
        } else {
            res.append(codes[(int) mod - 1]);
        }
    }
}
