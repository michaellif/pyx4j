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
package com.propertyvista.crm.client.ui.crud.customer.lead;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.domain.tenant.lead.Lead;

public class LeadLister extends ListerBase<Lead> {

    private final static I18n i18n = I18n.get(LeadLister.class);

    public LeadLister() {
        super(Lead.class, true);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().leadId(), true).build(),
            new MemberColumnDescriptor.Builder(proto().guests(), true).build(),
            new MemberColumnDescriptor.Builder(proto().guests().$().person().name().lastName()).columnTitle(i18n.tr("Guest Last Name")).searchableOnly().build(),
            new MemberColumnDescriptor.Builder(proto().moveInDate(), true).build(),
            new MemberColumnDescriptor.Builder(proto().leaseTerm(), true).build(),
            new MemberColumnDescriptor.Builder(proto().floorplan(), true).searchable(false).build(),
            new MemberColumnDescriptor.Builder(proto().createDate(), true).build(),
            new MemberColumnDescriptor.Builder(proto().status(), true).build()
        );//@formatter:on
    }
}
