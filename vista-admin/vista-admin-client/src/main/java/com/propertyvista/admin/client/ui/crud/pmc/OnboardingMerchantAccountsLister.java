/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.pmc;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.BasicLister;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.OnboardingMerchantAccountDTO;

public class OnboardingMerchantAccountsLister extends BasicLister<OnboardingMerchantAccountDTO> {

    private Pmc parentPmc;

    public OnboardingMerchantAccountsLister() {
        super(OnboardingMerchantAccountDTO.class, true, true);
        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().onboardingBankAccountId()).build(),
                new MemberColumnDescriptor.Builder(proto().merchantTerminalId()).build(),
                new MemberColumnDescriptor.Builder(proto().status()).searchable(false).sortable(false).build()
        );//@formatter:on
    }

    public void setParentPmc(Pmc parentPmc) {
        this.parentPmc = parentPmc;
        this.getDataSource().setParentFiltering(parentPmc.getPrimaryKey());
    }

    @Override
    protected void onItemNew() {
        if (parentPmc != null && parentPmc.getPrimaryKey() != null) {
            AppSite.getPlaceController().goTo(new AdminSiteMap.Management.OnboardingMerchantAccounts().formNewItemPlace(parentPmc.getPrimaryKey()));
        }
    }

    @Override
    protected void onItemSelect(OnboardingMerchantAccountDTO item) {
        AppSite.getPlaceController().goTo(new AdminSiteMap.Management.OnboardingMerchantAccounts().formViewerPlace(item.getPrimaryKey()));
    }

}
