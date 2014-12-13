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
package com.propertyvista.crm.client.ui.crud.building.parking;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.building.ParkingSpotCrudService;
import com.propertyvista.domain.property.asset.ParkingSpot;

public class ParkingSpotLister extends SiteDataTablePanel<ParkingSpot> {

    public ParkingSpotLister() {
        super(ParkingSpot.class, GWT.<ParkingSpotCrudService> create(ParkingSpotCrudService.class), true);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().name()).build(), //
                new ColumnDescriptor.Builder(proto().type()).build());

        setDataTableModel(new DataTableModel<ParkingSpot>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }

}
