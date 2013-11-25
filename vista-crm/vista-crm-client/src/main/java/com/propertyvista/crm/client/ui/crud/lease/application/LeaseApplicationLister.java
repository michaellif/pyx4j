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
package com.propertyvista.crm.client.ui.crud.lease.application;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.crm.client.ui.crud.lease.common.dialogs.LeaseDataDialog;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseApplicationDTO;

public class LeaseApplicationLister extends AbstractLister<LeaseApplicationDTO> {

    private final static I18n i18n = I18n.get(LeaseApplicationLister.class);

    public LeaseApplicationLister() {
        super(LeaseApplicationDTO.class, true);

        setColumnDescriptors(//@formatter:off
            new Builder(proto().leaseId()).columnTitle(i18n.tr("Id")).build(),
            new Builder(proto().type()).build(),

            new Builder(proto().unit().building().propertyCode()).build(),
            new Builder(proto().unit()).build(),

            new Builder(proto().leaseApplication().status(), true).build(),

            new Builder(proto().currentTerm().termFrom()).build(),
            new Builder(proto().currentTerm().termTo()).build(),

            new Builder(proto().expectedMoveIn()).build(),
            new Builder(proto().expectedMoveOut(), false).build(),
            new Builder(proto().actualMoveIn(), false).build(),
            new Builder(proto().actualMoveOut(), false).build(),
            new Builder(proto().moveOutSubmissionDate(), false).build(),

            new Builder(proto().creationDate(), false).build(),

            new Builder(proto()._applicant().customer().person().name()).columnTitle(i18n.tr("Primary Tenant Name")).searchable(false).build(),
            new Builder(proto()._applicant().customer().person().name().firstName(), false).columnTitle(i18n.tr("Primary Tenant First Name")).build(),
            new Builder(proto()._applicant().customer().person().name().lastName(), false).columnTitle(i18n.tr("Primary Tenant Last Name")).build(),
            new Builder(proto().leaseParticipants().$().customer().customerId(), false).build(),

            new Builder(proto().numberOfOccupants(), false).sortable(false).searchable(false).title(i18n.tr("Occupants")).build(),
            new Builder(proto().numberOfApplicants(), true).sortable(false).searchable(false).title(i18n.tr("Applicants")).build(),
            new Builder(proto().numberOfDepentands(), true).sortable(false).searchable(false).title(i18n.tr("Depentands")).build(),
            new Builder(proto().numberOfGuarantors(), true).sortable(false).searchable(false).title(i18n.tr("Guarantors")).build(),

            //TODO make this work
            //new Builder(proto().currentTerm().version().tenants().$().leaseParticipant().customer().person().name().firstName(), false).build(),
            //new Builder(proto().currentTerm().version().tenants().$().leaseParticipant().customer().person().name().lastName(), false).build(),
            new Builder(proto().currentTerm().version().tenants()).searchable(false).build()

        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().leaseId(), false));
    }

    @Override
    protected EntityListCriteria<LeaseApplicationDTO> updateCriteria(EntityListCriteria<LeaseApplicationDTO> criteria) {
        // TODO : set all that stuff in Activity (like TenantListers) or CRUD service ?
        criteria.add(PropertyCriterion.in(criteria.proto().status(), EnumSet.of(Lease.Status.Application, Lease.Status.Approved)));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().leaseApplication().status()));
        return super.updateCriteria(criteria);
    }

    @Override
    protected void onItemNew() {
        new LeaseDataDialog(LeaseDataDialog.Type.Application).show();
    }
}
