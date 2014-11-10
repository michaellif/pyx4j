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
package com.propertyvista.crm.client.ui.crud.building.mech;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.building.mech.RoofCrudService;
import com.propertyvista.dto.RoofDTO;

public class RoofLister extends SiteDataTablePanel<RoofDTO> {

    public RoofLister() {
        super(RoofDTO.class, GWT.<AbstractCrudService<RoofDTO>> create(RoofCrudService.class), true);
        setFilteringEnabled(false);

        setDataTableModel(new DataTableModel<RoofDTO>( //@formatter:off
                new MemberColumnDescriptor.Builder(proto().type()).build(),
                new MemberColumnDescriptor.Builder(proto().year()).build(),
                new MemberColumnDescriptor.Builder(proto().license().number(), false).build(),
                new MemberColumnDescriptor.Builder(proto().license().expiration(), false).build(),
                new MemberColumnDescriptor.Builder(proto().license().renewal(), false).build(),
                new MemberColumnDescriptor.Builder(proto().warranty().type(), false).build()
        )); //@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().year(), false));
    }
}
