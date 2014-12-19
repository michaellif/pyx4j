/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 26, 2014
 * @author VladL
 */
package com.propertyvista.crm.client.ui.crud.customer.common;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseParticipantDTO;

public abstract class LeaseParticipantLister<P extends LeaseParticipantDTO<?>> extends SiteDataTablePanel<P> {

    public LeaseParticipantLister(Class<P> entityClass, AbstractListCrudService<P> service) {
        super(entityClass, service, false);
    }

    @Override
    public boolean canCreateNewItem() {
        return false; // disable creation of the new stand-alone LeaseParticipant - just from within the new Lease/Application!..
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().lease().leaseId(), false), new Sort(proto().customer().person().name(), false));
    }

    //
    // Helpers for potential/current/former list participants filtering:
    //
    protected EntityListCriteria<P> updateListCriteriaForPotentialLeaseParticipants(EntityListCriteria<P> criteria) {
        criteria.in(criteria.proto().lease().status(), Lease.Status.draft());

        return super.updateCriteria(criteria);
    }

    protected EntityListCriteria<P> updateListCriteriaForCurrentLeaseParticipants(EntityListCriteria<P> criteria) {
        // filter out just current tenants:
        criteria.in(criteria.proto().lease().status(), Lease.Status.current());
        criteria.eq(criteria.proto().leaseTermParticipants().$().leaseTermV().holder(), criteria.proto().lease().currentTerm());
        // and finalized e.g. last only:
        criteria.isCurrent(criteria.proto().leaseTermParticipants().$().leaseTermV());

        return super.updateCriteria(criteria);
    }

    protected EntityListCriteria<P> updateListCriteriaForFormerLeaseParticipants(EntityListCriteria<P> criteria) {
        // filter out just former tenants:
        OrCriterion or = criteria.or();

        or.left().in(criteria.proto().lease().status(), Lease.Status.former());

        AndCriterion currentTermCriterion = new AndCriterion();
        currentTermCriterion.eq(criteria.proto().leaseTermParticipants().$().leaseTermV().holder(), criteria.proto().lease().currentTerm());
        // and finalized e.g. last only:
        currentTermCriterion.isCurrent(criteria.proto().leaseTermParticipants().$().leaseTermV());

        or.right().notExists(criteria.proto().leaseTermParticipants(), currentTermCriterion);
        or.right().ne(criteria.proto().lease().status(), Lease.Status.Application);
        or.right().ne(criteria.proto().lease().status(), Lease.Status.ExistingLease);

        return super.updateCriteria(criteria);
    }
}
