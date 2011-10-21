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

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.domain.property.asset.building.Building;

public class SelectedBuildingLister extends ListerBase<Building> {

    public SelectedBuildingLister() {
        super(Building.class, null, true);
        getListPanel().getDataTable().setMarkSelectedRow(true);
        getListPanel().setPageSize(5);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<Building>> columnDescriptors, Building proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().country()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().province()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().city()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().streetName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().streetNumber()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().name()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.complex()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptorEx(proto, proto.marketing().name(), i18n.tr("Marketing Name")));

    }

    @Override
    protected void fillAvailableColumnDescriptors(List<ColumnDescriptor<Building>> columnDescriptors, Building proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().country()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().province()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().city()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().streetName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().streetNumber()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().name()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.complex()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptorEx(proto, proto.marketing().name(), i18n.tr("Marketing Name")));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().shape()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().totalStoreys()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().residentialStoreys()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().structureType()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().structureBuildYear()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().constructionType()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().foundationType()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().floorType()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().landArea()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().waterSupply()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().centralAir()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().centralHeat()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.contacts().website()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptorEx(proto, proto.contacts().email().address(), i18n.tr("Email")));
    }
}
