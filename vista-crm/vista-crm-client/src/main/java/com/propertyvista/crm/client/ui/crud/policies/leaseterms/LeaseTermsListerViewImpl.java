/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leaseterms;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.policy.policies.specials.LegalTermsDescriptor;

public class LeaseTermsListerViewImpl extends CrmListerViewImplBase<LegalTermsDescriptor> implements LeaseTermsListerView {

    public LeaseTermsListerViewImpl() {
        super(CrmSiteMap.Settings.LeaseTerms.class);
        setLister(new LeaseTermsLister());
    }

    public static class LeaseTermsLister extends ListerBase<LegalTermsDescriptor> {

        public LeaseTermsLister() {
            super(LegalTermsDescriptor.class, CrmSiteMap.Settings.LeaseTerms.class, true, true);
            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().description()).build());  //@formatter:on
        }

    }
}
