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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractLister;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.dto.company.EmployeePrivilegesDTO;
import com.propertyvista.domain.company.Notification;

public class EmployeeLister extends AbstractLister<EmployeeDTO> {

    private List<ColumnDescriptor> columns = new ArrayList<ColumnDescriptor>();

    public EmployeeLister() {
        super(EmployeeDTO.class, true);

        // Main columns
        addMainColumns();

        // Behaviour columns
        if (hasEntityReadPermission(EmployeePrivilegesDTO.class)) {
            addBehavioursColumns();
        }

        // Notification columns
        if (hasEntityReadPermission(Notification.class)) {
            addNotificationColumns();
        }

        setDataTableModel(new DataTableModel<EmployeeDTO>(columns));
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().employeeId(), false));
    }

    private void addNotificationColumns() {
        columns.add(new MemberColumnDescriptor.Builder(proto().notificationTypes(), false).sortable(false).build());
    }

    private void addBehavioursColumns() {
        columns.add(new MemberColumnDescriptor.Builder(proto().privileges().roles(), false).sortable(false).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().privileges().behaviors(), false).sortable(false).build());
    }

    private <T extends IEntity> boolean hasEntityReadPermission(Class<T> entity) {
        if (SecurityController.check(DataModelPermission.permissionRead(entity))) {
            return true;
        }
        return false;
    }

    private void addMainColumns() {
        columns.add(new MemberColumnDescriptor.Builder(proto().employeeId()).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().name()).searchable(false).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().title()).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().name().firstName()).searchableOnly().build());
        columns.add(new MemberColumnDescriptor.Builder(proto().name().lastName()).searchableOnly().build());
        columns.add(new MemberColumnDescriptor.Builder(proto().email()).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().updated(), false).build());
    }

}
