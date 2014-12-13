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

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.dto.PmcMerchantAccountDTO;
import com.propertyvista.operations.rpc.services.PmcMerchantAccountCrudService;

public class MerchantAccountsLister extends SiteDataTablePanel<PmcMerchantAccountDTO> {

    private Pmc parentPmc;

    public MerchantAccountsLister() {
        super(PmcMerchantAccountDTO.class, GWT.<AbstractListCrudService<PmcMerchantAccountDTO>> create(PmcMerchantAccountCrudService.class), true, true);

        setItemZoomInCommand(new ItemZoomInCommand<PmcMerchantAccountDTO>() {
            @Override
            public void execute(PmcMerchantAccountDTO item) {
                AppSite.getPlaceController().goTo(new OperationsSiteMap.Management.PmcMerchantAccount().formViewerPlace(item.getPrimaryKey()));
            }
        });

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().terminalId()).build(), //
                new ColumnDescriptor.Builder(proto().merchantTerminalIdConvenienceFee()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().merchantAccount().status()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().merchantAccount().paymentsStatus()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().merchantAccount().bankId()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().merchantAccount().branchTransitNumber()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().merchantAccount().accountNumber()).searchable(false).sortable(false).build());

        setDataTableModel(new DataTableModel<PmcMerchantAccountDTO>());
    }

    public void setParentPmc(Pmc parentPmc) {
        this.parentPmc = parentPmc;
        this.getDataSource().setParentEntityId(parentPmc.getPrimaryKey());
    }

    @Override
    protected void onItemNew() {
        if (parentPmc != null && parentPmc.getPrimaryKey() != null) {
            AppSite.getPlaceController().goTo(new OperationsSiteMap.Management.PmcMerchantAccount().formNewItemPlace(parentPmc.getPrimaryKey()));
        }
    }

}
