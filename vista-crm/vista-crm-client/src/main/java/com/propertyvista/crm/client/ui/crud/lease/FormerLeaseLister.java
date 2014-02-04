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

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

public class FormerLeaseLister extends AbstractLister<LeaseDTO> {

    private final static I18n i18n = I18n.get(FormerLeaseLister.class);

    public FormerLeaseLister() {
        super(LeaseDTO.class, false);

        setColumnDescriptors(//@formatter:off
            new Builder(proto().leaseId()).columnTitle(i18n.tr("Id")).build(),
            new Builder(proto().type()).build(),
            
            new Builder(proto().unit().building().propertyCode()).build(),
            new Builder(proto().unit()).searchable(false).build(),
            new Builder(proto().unit().info().number()).columnTitle(proto().unit().getMeta().getCaption()).searchableOnly().build(),
            
            new Builder(proto().status()).build(),
            new Builder(proto().completion()).build(),
            new Builder(proto().billingAccount().accountNumber()).build(),
            
            new Builder(proto().leaseFrom()).build(),
            new Builder(proto().leaseTo()).build(),
            
            new Builder(proto().expectedMoveIn(), false).build(),
            new Builder(proto().expectedMoveOut(), false).build(),
            new Builder(proto().actualMoveIn(), false).build(),
            new Builder(proto().actualMoveOut(), false).build(),
            new Builder(proto().moveOutSubmissionDate(), false).build(),
            
            new Builder(proto().approvalDate(), false).build(),
            new Builder(proto().creationDate(), false).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().leaseId(), false));
    }

    @Override
    protected EntityListCriteria<LeaseDTO> updateCriteria(EntityListCriteria<LeaseDTO> criteria) {
        criteria.in(criteria.proto().status(), Lease.Status.former());
        return super.updateCriteria(criteria);
    }
}
