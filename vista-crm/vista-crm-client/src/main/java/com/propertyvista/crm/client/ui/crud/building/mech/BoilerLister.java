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

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.backoffice.prime.lister.AbstractLister;

import com.propertyvista.dto.BoilerDTO;

public class BoilerLister extends AbstractLister<BoilerDTO> {

    public BoilerLister() {
        super(BoilerDTO.class, true);
        getDataTablePanel().setFilteringEnabled(false);

        setDataTableModel(new DataTableModel<BoilerDTO>(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().type()).build(),
            new MemberColumnDescriptor.Builder(proto().make()).build(),
            new MemberColumnDescriptor.Builder(proto().model()).build(),
            new MemberColumnDescriptor.Builder(proto().build()).build()
        ));//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().model(), false));
    }
}
