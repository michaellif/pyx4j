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
package com.propertyvista.crm.client.ui.crud.building;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.domain.property.asset.building.Building;

public class SelectedBuildingLister extends ListerBase<Building> {

    public SelectedBuildingLister() {
        super(Building.class, null, true);
        getDataTablePanel().getDataTable().setMarkSelectedRow(true);
        getDataTablePanel().setPageSize(5);

        List<ColumnDescriptor<Building>> columnDescriptors = new ArrayList<ColumnDescriptor<Building>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().address().country(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().address().province(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().address().city(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().address().streetName(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().address().streetNumber(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().type(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().name(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().propertyCode(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().complex(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createTitledColumnDescriptor(proto(), proto().marketing().name(), i18n.tr("Marketing Name"), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().shape(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().totalStoreys(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().residentialStoreys(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().structureType(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().structureBuildYear(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().constructionType(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().foundationType(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().floorType(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().landArea(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().waterSupply(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().centralAir(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().centralHeat(), false));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().info().address(), false));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().contacts().website(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createTitledColumnDescriptor(proto(), proto().contacts().email().address(), i18n.tr("Email"), false));

        setColumnDescriptors(columnDescriptors);

    }

}
