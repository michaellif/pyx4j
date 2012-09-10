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

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.client.ui.crud.lease.common.dialogs.SelectLeaseTypeDialog;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.misc.VistaTODO;

public class LeaseApplicationLister extends ListerBase<LeaseApplicationDTO> {

    private final static I18n i18n = I18n.get(LeaseApplicationLister.class);

    public LeaseApplicationLister() {
        super(LeaseApplicationDTO.class, true);

        setColumnDescriptors(//@formatter:off
            new Builder(proto().leaseId()).build(),
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
            new Builder(proto().moveOutNotice(), false).build(),
            
            new Builder(proto().creationDate(), false).build(),
            
            new Builder(proto().mainApplicant()).sortable(!VistaTODO.complextQueryCriteria).searchable(!VistaTODO.complextQueryCriteria).build(),
            

            new Builder(proto().leaseApplication().equifaxApproval().percenrtageApproved(), false).build(),
            new Builder(proto().leaseApplication().equifaxApproval().suggestedDecision(), false).build(),

            new Builder(proto().numberOfOccupants(), false).sortable(false).searchable(false).title(i18n.tr("Occupants")).build(),
            new Builder(proto().numberOfApplicants(), true).sortable(false).searchable(false).title(i18n.tr("Applicants")).build(),
            new Builder(proto().numberOfGuarantors(), true).sortable(false).searchable(false).title(i18n.tr("Guarantors")).build(),
            
            new Builder(proto().currentTerm().version().tenants()).build()
        );//@formatter:on
    }

    @Override
    protected EntityListCriteria<LeaseApplicationDTO> updateCriteria(EntityListCriteria<LeaseApplicationDTO> criteria) {
        // TODO : set all that stuff in CRUD service:
        criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Lease.Status.Application));
        return super.updateCriteria(criteria);
    }

    @Override
    protected void onItemNew() {
        new SelectLeaseTypeDialog().show();
    }
}
