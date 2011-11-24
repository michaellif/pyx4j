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
package com.propertyvista.crm.client.ui.crud.marketing.lead;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.misc.VistaTODO;

public class LeadLister extends ListerBase<Lead> {

    public LeadLister() {
        super(Lead.class, Marketing.Lead.class, false, true);

        setColumnDescriptors(new MemberColumnDescriptor.Builder(proto().id(), true).build(),

        new MemberColumnDescriptor.Builder(proto().person(), true).sortable(!VistaTODO.entityAsStringQueryCriteria).build(),

        new MemberColumnDescriptor.Builder(proto().moveInDate(), true).build(),

        new MemberColumnDescriptor.Builder(proto().leaseTerm(), true).build(),

        new MemberColumnDescriptor.Builder(proto().floorplan(), true).build(),

        new MemberColumnDescriptor.Builder(proto().createDate(), true).build(),

        new MemberColumnDescriptor.Builder(proto().status(), true).build()

        );
    }
}
