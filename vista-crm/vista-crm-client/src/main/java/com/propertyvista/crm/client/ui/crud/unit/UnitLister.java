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
import com.pyx4j.site.client.ui.crud.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.AptUnitDTO;

public class UnitLister extends ListerBase<AptUnitDTO> {

    public UnitLister() {
        super(AptUnitDTO.class, CrmSiteMap.Properties.Unit.class);
    }

    public UnitLister(boolean readOnly) {
        super(AptUnitDTO.class, CrmSiteMap.Properties.Unit.class, readOnly);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<AptUnitDTO>> columnDescriptors, AptUnitDTO proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().floor()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().number()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().area()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().bedrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().bathrooms()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().unitRent()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().marketRent()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptorEx(proto, proto.marketing().name(), i18n.tr("Marketing Name")));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptorEx(proto, proto.marketing().floorplan().name(), i18n.tr("Floorplan Name")));
    }

    @Override
    protected void fillAvailableColumnDescriptors(List<ColumnDescriptor<AptUnitDTO>> columnDescriptors, AptUnitDTO proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().typeDescription()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().economicStatus()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().economicStatusDescription()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().floor()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().number()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().area()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().areaUnits()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().bedrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().bathrooms()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().unitRent()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.financial().marketRent()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptorEx(proto, proto.marketing().name(), i18n.tr("Marketing Name")));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptorEx(proto, proto.marketing().description(), i18n.tr("Marketing Description")));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptorEx(proto, proto.marketing().floorplan().name(), i18n.tr("Floorplan Name")));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptorEx(proto, proto.marketing().floorplan().description(),
                i18n.tr("Floorplan Description")));
    }
}
