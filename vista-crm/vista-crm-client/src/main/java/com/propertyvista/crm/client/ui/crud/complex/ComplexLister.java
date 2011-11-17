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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.complex;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.ComplexDTO;

public class ComplexLister extends ListerBase<ComplexDTO> {

    public ComplexLister() {
        super(ComplexDTO.class, CrmSiteMap.Properties.Complex.class);
    }

    @Override
    protected List<ColumnDescriptor<ComplexDTO>> getDefaultColumnDescriptors(ComplexDTO proto) {
        List<ColumnDescriptor<ComplexDTO>> columnDescriptors = new ArrayList<ColumnDescriptor<ComplexDTO>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.name()));
        // TODO: enable when we know to deal with filtering (case all this info comes from primary building)
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.address().streetName()));
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.address().city()));
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.address().province()));
        return columnDescriptors;
    }
}
