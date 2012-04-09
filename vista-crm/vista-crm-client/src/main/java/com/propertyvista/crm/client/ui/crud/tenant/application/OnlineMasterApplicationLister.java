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
package com.propertyvista.crm.client.ui.crud.tenant.application;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.dto.OnlineMasterApplicationDTO;
import com.propertyvista.misc.VistaTODO;

public class OnlineMasterApplicationLister extends ListerBase<OnlineMasterApplicationDTO> {

    private static final I18n i18n = I18n.get(OnlineMasterApplicationLister.class);

    public OnlineMasterApplicationLister() {
        super(OnlineMasterApplicationDTO.class, false, false);

        setColumnDescriptors(new ColumnDescriptor[] {//@formatter:off

                new MemberColumnDescriptor.Builder(proto().onlineApplicationId(), true).build(), 
                new MemberColumnDescriptor.Builder(proto().lease().type(), true).build(),
                new MemberColumnDescriptor.Builder(proto().lease().unit().belongsTo().propertyCode()).build(),
                new MemberColumnDescriptor.Builder(proto().lease().unit(), true).build(),
                new MemberColumnDescriptor.Builder(proto().mainApplicant()).sortable(!VistaTODO.complextQueryCriteria).build(),

                new MemberColumnDescriptor.Builder(proto().numberOfOccupants(), false).sortable(false).title(i18n.tr("Occupants")).build(),
                new MemberColumnDescriptor.Builder(proto().numberOfCoApplicants(), true).sortable(false).title(i18n.tr("Applicants")).build(),
                new MemberColumnDescriptor.Builder(proto().numberOfGuarantors(), true).sortable(false).title(i18n.tr("Guarantors")).build(),

                new MemberColumnDescriptor.Builder(proto().rentPrice(), true).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().parkingPrice(), false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().otherPrice(), false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().discounts(), false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().createDate(), true).build(),
                new MemberColumnDescriptor.Builder(proto().lease().leaseFrom(), true).build(),
                new MemberColumnDescriptor.Builder(proto().lease().leaseTo(), true).build(),

                new MemberColumnDescriptor.Builder(proto().lease().version().expectedMoveIn(), false).build(),
                new MemberColumnDescriptor.Builder(proto().lease().version().expectedMoveOut(), false).build(),
                new MemberColumnDescriptor.Builder(proto().lease().version().actualMoveIn(), false).build(),
                new MemberColumnDescriptor.Builder(proto().lease().version().actualMoveOut(), false).build(),
                new MemberColumnDescriptor.Builder(proto().lease().version().moveOutNotice(), false).build(),

                new MemberColumnDescriptor.Builder(proto().status(), true).build(),

                new MemberColumnDescriptor.Builder(proto().equifaxApproval().percenrtageApproved(), false).build(),
                new MemberColumnDescriptor.Builder(proto().equifaxApproval().suggestedDecision(), false).build()
//                new MemberColumnDescriptor.Builder(proto().decidedBy(), false).build(),
//                new MemberColumnDescriptor.Builder(proto().decisionDate(), false).build(),
//                new MemberColumnDescriptor.Builder(proto().decisionReason(), false).build()
                }); //@formatter:on
    }
}
