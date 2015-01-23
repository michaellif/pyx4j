/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.lease;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.lease.common.DepositLifecycleCrudService;
import com.propertyvista.dto.DepositLifecycleDTO;

public class DepositLifecycleLister extends SiteDataTablePanel<DepositLifecycleDTO> {

    private final static I18n i18n = I18n.get(DepositLifecycleLister.class);

    public DepositLifecycleLister() {
        super(DepositLifecycleDTO.class, GWT.<DepositLifecycleCrudService> create(DepositLifecycleCrudService.class), false);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().deposit().billableItem()).searchable(false).sortable(false).columnTitle(i18n.tr("Service/Feature"))
                        .build(), //
                new ColumnDescriptor.Builder(proto().deposit().type()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().deposit().amount()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().status()).build(), //
                new ColumnDescriptor.Builder(proto().depositDate(), false).build(), //
                new ColumnDescriptor.Builder(proto().refundDate()).build(), //
                new ColumnDescriptor.Builder(proto().currentAmount()).filterAlwaysShown(true).build());

        setDataTableModel(new DataTableModel<DepositLifecycleDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().depositDate(), false));
    }
}
