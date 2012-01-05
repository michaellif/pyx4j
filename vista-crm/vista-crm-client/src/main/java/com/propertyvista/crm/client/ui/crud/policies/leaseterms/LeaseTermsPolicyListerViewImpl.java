/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leaseterms;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.policy.dto.LeaseTermsPolicyDTO;

public class LeaseTermsPolicyListerViewImpl extends CrmListerViewImplBase<LeaseTermsPolicyDTO> implements LeaseTermsPolicyListerView {

    public LeaseTermsPolicyListerViewImpl() {
        super(CrmSiteMap.Settings.Policies.LeaseTerms.class);
        setLister(new LeaseTermsPolicyLister());
    }

    private static class LeaseTermsPolicyLister extends ListerBase<LeaseTermsPolicyDTO> {

        public LeaseTermsPolicyLister() {
            super(LeaseTermsPolicyDTO.class, CrmSiteMap.Settings.Policies.LeaseTerms.class, false, true);
            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(),
                    new MemberColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build()
            );//@formatter:on
        }
    }
}
