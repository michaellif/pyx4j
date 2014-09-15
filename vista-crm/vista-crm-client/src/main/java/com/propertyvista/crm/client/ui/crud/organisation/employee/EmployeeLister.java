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
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.backoffice.prime.lister.AbstractLister;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;

public class EmployeeLister extends AbstractLister<EmployeeDTO> {

    public EmployeeLister() {
        super(EmployeeDTO.class, true);

        setDataTableModel(new DataTableModel<EmployeeDTO>(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().employeeId()).build(),
            new MemberColumnDescriptor.Builder(proto().name()).searchable(false).build(),
            new MemberColumnDescriptor.Builder(proto().title()).build(),
            new MemberColumnDescriptor.Builder(proto().name().firstName()).searchableOnly().build(),
            new MemberColumnDescriptor.Builder(proto().name().lastName()).searchableOnly().build(),
            new MemberColumnDescriptor.Builder(proto().email()).build(),
            new MemberColumnDescriptor.Builder(proto().updated(), false).build(),
            //TODO make this conditional if user can read this.
            new MemberColumnDescriptor.Builder(proto().privileges().roles(), false).sortable(false).build(),
            new MemberColumnDescriptor.Builder(proto().privileges().behaviors(), false).sortable(false).build()
        ));//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().employeeId(), false));
    }
}
