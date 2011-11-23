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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.floorplan;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.domain.property.asset.Floorplan;

public class SelectedFloorplanLister extends ListerBase<Floorplan> {

    public SelectedFloorplanLister() {
        super(Floorplan.class, null, false, false);
        getDataTablePanel().getDataTable().setMarkSelectedRow(true);
        getDataTablePanel().setPageSize(8);

        List<ColumnDescriptor<Floorplan>> columnDescriptors = new ArrayList<ColumnDescriptor<Floorplan>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().name(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().marketingName(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().floorCount(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().bedrooms(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().dens(), false));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().bathrooms(), true));
        setColumnDescriptors(columnDescriptors);

    }

}
