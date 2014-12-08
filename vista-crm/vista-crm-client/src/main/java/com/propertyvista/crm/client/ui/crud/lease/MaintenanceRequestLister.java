/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.maintenance.MaintenanceCrudService;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestLister extends SiteDataTablePanel<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestLister.class);

    public MaintenanceRequestLister() {
        super(MaintenanceRequestDTO.class, GWT.<MaintenanceCrudService> create(MaintenanceCrudService.class), true);

        setColumnDescriptors(createColumnDescriptors());
        setDataTableModel(new DataTableModel<MaintenanceRequestDTO>());
    }

    public static ColumnDescriptor[] createColumnDescriptors() {
        MaintenanceRequestDTO proto = EntityFactory.getEntityPrototype(MaintenanceRequestDTO.class);

        return new ColumnDescriptor[] {
                new MemberColumnDescriptor.Builder(proto.requestId()).build(),
                new MemberColumnDescriptor.Builder(proto.priority()).build(),
                new MemberColumnDescriptor.Builder(proto.summary()).build(),
                new MemberColumnDescriptor.Builder(proto.reporterName()).columnTitle(i18n.tr("Tenant")).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.reporterPhone(), false).build(),
                new MemberColumnDescriptor.Builder(proto.permissionToEnter()).columnTitle(i18n.tr("Entry Allowed")).build(),
                new MemberColumnDescriptor.Builder(proto.petInstructions()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.submitted()).build(),
                new MemberColumnDescriptor.Builder(proto.status()).build(),
                new MemberColumnDescriptor.Builder(proto.updated()).build(),
                new MemberColumnDescriptor.Builder(proto.surveyResponse().rating()).build(),
                new MemberColumnDescriptor.Builder(proto.surveyResponse().description()).visible(false)
                        .columnTitle(proto.surveyResponse().getMeta().getCaption()).build() };
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().submitted(), true), new Sort(proto().updated(), false));
    }

}
