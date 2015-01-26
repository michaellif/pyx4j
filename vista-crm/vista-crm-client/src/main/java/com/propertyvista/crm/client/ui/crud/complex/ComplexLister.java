/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.complex;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.building.ComplexCrudService;
import com.propertyvista.dto.ComplexDTO;

public class ComplexLister extends SiteDataTablePanel<ComplexDTO> {

    public ComplexLister() {
        super(ComplexDTO.class, GWT.<AbstractCrudService<ComplexDTO>> create(ComplexCrudService.class), true);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().name()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().address().city()).sortable(false).searchable(false).build(), //
                new ColumnDescriptor.Builder(proto().address().province()).sortable(false).searchable(false).build(), //
                new ColumnDescriptor.Builder(proto().address().country()).sortable(false).searchable(false).build(), //
                new ColumnDescriptor.Builder(proto().primaryBuilding()).sortable(false).searchable(false).build() //
        );

        setDataTableModel(new DataTableModel<ComplexDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }
}
