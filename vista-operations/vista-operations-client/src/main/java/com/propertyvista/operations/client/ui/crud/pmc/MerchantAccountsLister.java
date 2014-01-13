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
package com.propertyvista.operations.client.ui.crud.pmc;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.dto.PmcMerchantAccountDTO;

public class MerchantAccountsLister extends EntityDataTablePanel<PmcMerchantAccountDTO> {

    private Pmc parentPmc;

    public MerchantAccountsLister() {
        super(PmcMerchantAccountDTO.class, true, true);
        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().merchantTerminalId()).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().merchantTerminalIdConvenienceFee()).searchable(false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().status()).searchable(false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().paymentsStatus()).searchable(false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().bankId()).searchable(false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().branchTransitNumber()).searchable(false).sortable(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantAccount().accountNumber()).searchable(false).sortable(false).build()
        );//@formatter:on
    }

    public void setParentPmc(Pmc parentPmc) {
        this.parentPmc = parentPmc;
        this.getDataSource().setParentFiltering(parentPmc.getPrimaryKey());
    }

    @Override
    protected void onItemNew() {
        if (parentPmc != null && parentPmc.getPrimaryKey() != null) {
            AppSite.getPlaceController().goTo(new OperationsSiteMap.Management.PmcMerchantAccount().formNewItemPlace(parentPmc.getPrimaryKey()));
        }
    }

    @Override
    protected void onItemSelect(PmcMerchantAccountDTO item) {
        AppSite.getPlaceController().goTo(new OperationsSiteMap.Management.PmcMerchantAccount().formViewerPlace(item.getPrimaryKey()));
    }

}
