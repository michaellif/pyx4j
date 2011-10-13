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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog;

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.domain.financial.offering.Feature;

public class SelectFeatrueLister extends ListerBase<Feature> {

    public SelectFeatrueLister() {
        super(Feature.class, null, true);
        getListPanel().getDataTable().setMarkSelectedRow(true);
        getListPanel().getDataTable().setHasCheckboxColumn(false);
        setMultiSelect(true);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<Feature>> columnDescriptors, Feature proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.isMandatory()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.isRecurring()));
    }
}
