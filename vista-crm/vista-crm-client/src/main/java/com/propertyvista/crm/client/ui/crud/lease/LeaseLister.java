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
package com.propertyvista.crm.client.ui.crud.lease;

import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.client.ui.crud.lease.common.dialogs.ExistingLeaseDataDialog;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

public class LeaseLister extends ListerBase<LeaseDTO> {

    private final static I18n i18n = I18n.get(LeaseLister.class);

    public LeaseLister() {
        super(LeaseDTO.class, true);
        getDataTablePanel().getAddButton().setCaption(i18n.tr("Add Existing Lease"));

        setColumnDescriptors(//@formatter:off
            new Builder(proto().leaseId()).build(),
            new Builder(proto().type()).build(),
            
            new Builder(proto().unit().building().propertyCode()).build(),
            new Builder(proto().unit()).searchable(false).build(),
            new Builder(proto().unit().info().number()).columnTitle(proto().unit().getMeta().getCaption()).searchableOnly().build(),
            
            new Builder(proto().status()).build(),
            new Builder(proto().completion()).build(),
            new Builder(proto().billingAccount().accountNumber()).build(),
            
            new Builder(proto().leaseFrom()).build(),
            new Builder(proto().leaseTo()).build(),
            
            new Builder(proto().expectedMoveIn()).build(),
            new Builder(proto().expectedMoveOut(), false).build(),
            new Builder(proto().actualMoveIn(), false).build(),
            new Builder(proto().actualMoveOut(), false).build(),
            new Builder(proto().moveOutNotice(), false).build(),
            
            new Builder(proto().approvalDate(), false).build(),
            new Builder(proto().creationDate(), false).build()
        );//@formatter:on
    }

    @Override
    protected EntityListCriteria<LeaseDTO> updateCriteria(EntityListCriteria<LeaseDTO> criteria) {
        // TODO : set all that stuff in CRUD service ?
        criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
        criteria.add(PropertyCriterion.in(criteria.proto().status(), Lease.Status.Active));
        return super.updateCriteria(criteria);
    }

    @Override
    protected void onItemNew() {
        new ExistingLeaseDataDialog().show();
    }
}
