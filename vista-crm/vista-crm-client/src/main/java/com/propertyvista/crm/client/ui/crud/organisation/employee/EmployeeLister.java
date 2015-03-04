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
 */
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.dto.company.EmployeePrivilegesDTO;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.domain.company.Notification;

public class EmployeeLister extends SiteDataTablePanel<EmployeeDTO> {

    private static final I18n i18n = I18n.get(EmployeeLister.class);

    public EmployeeLister() {
        super(EmployeeDTO.class, GWT.<AbstractCrudService<EmployeeDTO>> create(EmployeeCrudService.class), true);

        List<ColumnDescriptor> cd = new ArrayList<>();

        cd.add(new ColumnDescriptor.Builder(proto().employeeId()).filterAlwaysShown(true).build());
        cd.add(new ColumnDescriptor.Builder(proto().name()).searchable(false).build());
        cd.add(new ColumnDescriptor.Builder(proto().title()).filterAlwaysShown(true).build());
        cd.add(new ColumnDescriptor.Builder(proto().name().firstName()).searchableOnly().filterAlwaysShown(true).build());
        cd.add(new ColumnDescriptor.Builder(proto().name().lastName()).searchableOnly().filterAlwaysShown(true).build());
        cd.add(new ColumnDescriptor.Builder(proto().email()).filterAlwaysShown(true).build());
        cd.add(new ColumnDescriptor.Builder(proto().updated()).visible(false).build());

        if (SecurityController.check(DataModelPermission.permissionRead(EmployeePrivilegesDTO.class))) {
            cd.add(new ColumnDescriptor.Builder(proto().privileges().roles()).visible(false).sortable(false).build());
            cd.add(new ColumnDescriptor.Builder(proto().privileges().behaviors()).visible(false).sortable(false).build());
        }

        if (SecurityController.check(DataModelPermission.permissionRead(Notification.class))) {
            cd.add(new ColumnDescriptor.Builder(proto().notifications().$().type()).columnTitle(i18n.tr("Notification type")).searchableOnly().build());
            cd.add(new ColumnDescriptor.Builder(proto().notifications()).visible(false).displayOnly().build());
        }

        setColumnDescriptors(cd);

        setDataTableModel(new DataTableModel<EmployeeDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().employeeId(), false));
    }
}
