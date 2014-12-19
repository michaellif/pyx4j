/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author TPRGLET
 */
package com.propertyvista.crm.client.ui.crud.unit;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.unit.UnitItemCrudService;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;

public class UnitItemLister extends SiteDataTablePanel<AptUnitItem> {

    public UnitItemLister() {
        super(AptUnitItem.class, GWT.<UnitItemCrudService> create(UnitItemCrudService.class), true);
        setFilteringEnabled(false);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().type()).build(), //
                new ColumnDescriptor.Builder(proto().description()).build(), //
                new ColumnDescriptor.Builder(proto().flooringType()).build(), //
                new ColumnDescriptor.Builder(proto().cabinetsType()).build(), //
                new ColumnDescriptor.Builder(proto().counterTopType()).build());

        setDataTableModel(new DataTableModel<AptUnitItem>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().type(), false));
    }
}
