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
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.ConfirmDecline;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardLister extends ListerBase<DashboardMetadata> {

    private static final I18n i18n = I18n.get(DashboardLister.class);

    public DashboardLister() {
        super(DashboardMetadata.class, true);
        getDataTablePanel().setFilteringEnabled(true);
        getDataTablePanel().getDataTable().setHasCheckboxColumn(true);

        addActionItem(new Button(i18n.tr("Delete Checked"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Do you really want to delete checked items?"), new ConfirmDecline() {
                    @Override
                    public void onConfirmed() {
                        for (DashboardMetadata item : getDataTablePanel().getDataTable().getCheckedItems()) {
                            if (ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(item.ownerUser().getPrimaryKey())) {
                                getPresenter().delete(item.getPrimaryKey());
                            } else {
                                MessageDialog.info(i18n.tr("You must be owner of dashboard \"{0}\" to delete it", item.name().getValue()));
                            }
                        }
                    }

                    @Override
                    public void onDeclined() {
                    }
                });
            }
        }));

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().type()).build(),
            new MemberColumnDescriptor.Builder(proto().name()).build(),
            new MemberColumnDescriptor.Builder(proto().isShared()).build(),
            new MemberColumnDescriptor.Builder(proto().ownerUser()).title(i18n.tr("Owner")).build(),
            new MemberColumnDescriptor.Builder(proto().description()).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name().getPath().toString(), false));
    }
}
