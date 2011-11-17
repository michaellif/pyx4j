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

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.AptUnitDTO;

public class UnitLister extends ListerBase<AptUnitDTO> {

    public UnitLister() {
        super(AptUnitDTO.class, CrmSiteMap.Properties.Unit.class);
        getDataTablePanel().getDataTable().setHasCheckboxColumn(false);
    }

    public UnitLister(boolean readOnly) {
        super(AptUnitDTO.class, CrmSiteMap.Properties.Unit.class, readOnly);
        getDataTablePanel().getDataTable().setHasCheckboxColumn(false);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<AptUnitDTO>> columnDescriptors, AptUnitDTO proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.buildingCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createTitledColumnDescriptor(proto, proto.floorplan().name(), i18n.tr("Floorplan Name")));
        columnDescriptors.add(ColumnDescriptorFactory.createTitledColumnDescriptor(proto, proto.floorplan().marketingName(), i18n.tr("Floorplan Marketing Name")));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().floor()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().number()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().area()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info()._bedrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info()._bathrooms()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial()._unitRent()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial()._marketRent()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.availableForRent()));
    }

    @Override
    protected void fillAvailableColumnDescriptors(List<ColumnDescriptor<AptUnitDTO>> columnDescriptors, AptUnitDTO proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.buildingCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createTitledColumnDescriptor(proto, proto.floorplan().name(), i18n.tr("Floorplan Name")));
        columnDescriptors.add(ColumnDescriptorFactory.createTitledColumnDescriptor(proto, proto.floorplan().marketingName(), i18n.tr("Floorplan Marketing Name")));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().economicStatus()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().floor()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().number()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().area()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().areaUnits()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info()._bedrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info()._bathrooms()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial()._unitRent()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial()._marketRent()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.availableForRent()));
    }
}
