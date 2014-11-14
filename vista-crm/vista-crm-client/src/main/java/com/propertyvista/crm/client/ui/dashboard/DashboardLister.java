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
package com.propertyvista.crm.client.ui.dashboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.SiteDataTablePanel;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardLister extends SiteDataTablePanel<DashboardMetadata> {

    private static final I18n i18n = I18n.get(DashboardLister.class);

    public DashboardLister() {
        super(DashboardMetadata.class, GWT.<AbstractCrudService<DashboardMetadata>> create(DashboardMetadataCrudService.class), true, true);

        setDataTableModel(new DataTableModel<DashboardMetadata>(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().type()).build(),
            new MemberColumnDescriptor.Builder(proto().name()).build(),
            new MemberColumnDescriptor.Builder(proto().isShared()).build(),
            new MemberColumnDescriptor.Builder(proto().ownerUser()).title(i18n.tr("Owner")).build(),
            new MemberColumnDescriptor.Builder(proto().description()).build()
        ));//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }

    @Override
    protected void onItemsDelete(final Collection<DashboardMetadata> items) {
        MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Do you really want to delete checked items?"), new Command() {
            @Override
            public void execute() {
                for (DashboardMetadata item : items) {
                    if (ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(item.ownerUser().getPrimaryKey())) {
                        delete(item.getPrimaryKey());
                    } else {
                        MessageDialog.info(i18n.tr("You must be owner of dashboard \"{0}\" to delete it", item.name().getValue()));
                    }
                }
            }
        });
    }

    @Override
    public void onDeleted(Key itemID, boolean isSuccessful) {
        super.onDeleted(itemID, isSuccessful);
        if (isSuccessful) {
            AppSite.instance();
            AppSite.getEventBus().fireEvent(new BoardUpdateEvent(DashboardMetadata.class));
        }
    }
}
