/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.merchantaccount;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.MerchantAccount;

public class MerchantAccountListerViewImpl extends CrmListerViewImplBase<MerchantAccount> implements MerchantAccountListerView {

    public MerchantAccountListerViewImpl() {
        super(CrmSiteMap.Settings.MerchantAccount.class);
        setLister(new MerchantAccountLister());
    }

    private static class MerchantAccountLister extends ListerBase<MerchantAccount> {

        public MerchantAccountLister() {
            super(MerchantAccount.class, false);

            setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().bankId()).build(),
                new MemberColumnDescriptor.Builder(proto().branchTransitNumber()).build(),
                new MemberColumnDescriptor.Builder(proto().accountNumber()).build(),
                new MemberColumnDescriptor.Builder(proto().status()).build()
            );//@formatter:on
        }
    }
}
