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
package com.propertyvista.crm.client.ui.crud.unit;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.domain.property.asset.unit.AptUnit;

public class SelectedUnitLister extends ListerBase<AptUnit> {

    private static final I18n i18n = I18n.get(SelectedUnitLister.class);

    public SelectedUnitLister() {
        super(AptUnit.class, null, false, false);
        getDataTablePanel().getDataTable().setMarkSelectedRow(true);
        getDataTablePanel().setPageSize(8);

        List<ColumnDescriptor<AptUnit>> columnDescriptors = new ArrayList<ColumnDescriptor<AptUnit>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().economicStatus(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().floor(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().number(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().area(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().areaUnits(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info()._bedrooms(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info()._bathrooms(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().financial()._unitRent(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().financial()._marketRent(), false));

        columnDescriptors.add(ColumnDescriptorFactory.createTitledColumnDescriptor(proto(), proto().floorplan().name(), i18n.tr("Floorplan Name"), true));
        columnDescriptors.add(ColumnDescriptorFactory.createTitledColumnDescriptor(proto(), proto().floorplan().marketingName(),
                i18n.tr("Floorplan Marketing Name"), true));

        setColumnDescriptors(columnDescriptors);
    }

}
