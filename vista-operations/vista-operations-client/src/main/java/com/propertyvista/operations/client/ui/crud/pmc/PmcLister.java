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
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.operations.rpc.dto.PmcDTO;
import com.propertyvista.operations.rpc.services.PmcCrudService;

public class PmcLister extends SiteDataTablePanel<PmcDTO> {

    protected static final I18n i18n = I18n.get(PmcLister.class);

    public PmcLister(final PmcListerView view) {
        super(PmcDTO.class, GWT.<AbstractCrudService<PmcDTO>> create(PmcCrudService.class), true);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().name()).build(), //
                new ColumnDescriptor.Builder(proto().dnsName()).build(), //
                new ColumnDescriptor.Builder(proto().namespace()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().status()).build(), //
                new ColumnDescriptor.Builder(proto().created()).build(), //
                new ColumnDescriptor.Builder(proto().updated()).build(), //
                new ColumnDescriptor.Builder(proto().features().yardiIntegration()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().features().yardiMaintenance()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().features().yardiInterfaces()).searchable(false).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().features().onlineApplication()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().features().whiteLabelPortal()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().features().tenantSureIntegration()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().features().tenantEmailEnabled()).visible(false).build(), //

                new ColumnDescriptor.Builder(proto().features().countryOfOperation()).visible(false).build(), //

                new ColumnDescriptor.Builder(proto().schemaVersion()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().schemaDataUpgradeSteps()).visible(false).build(), //

                new ColumnDescriptor.Builder(proto().equifaxInfo().status()).columnTitle("Equifax Status").visible(false).build(), //
                new ColumnDescriptor.Builder(proto().equifaxInfo().reportType()).visible(false).build());

        setDataTableModel(new DataTableModel<PmcDTO>());

        addUpperActionItem(new Button(i18n.tr("Upload Merchant Accounts"), new Command() {
            @Override
            public void execute() {
                ((PmcListerView.Presenter) view.getPresenter()).uploadMerchantAccounts();
            }
        }));

    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }
}
