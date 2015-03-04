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
package com.propertyvista.crm.client.ui.crud.building;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.building.FloorplanCrudService;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class FloorplanLister extends SiteDataTablePanel<FloorplanDTO> {

    public FloorplanLister() {
        super(FloorplanDTO.class, GWT.<AbstractCrudService<FloorplanDTO>> create(FloorplanCrudService.class),
                !VistaFeatures.instance().yardiIntegration() ? true : false);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().name()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().marketingName()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().floorCount()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().bedrooms()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().dens()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().bathrooms()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().counters()._unitCount()).build(), //
                new ColumnDescriptor.Builder(proto().counters()._marketingUnitCount()).visible(false).build());

        setDataTableModel(new DataTableModel<FloorplanDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().bedrooms(), false), new Sort(proto().bathrooms(), false));
    }
}
