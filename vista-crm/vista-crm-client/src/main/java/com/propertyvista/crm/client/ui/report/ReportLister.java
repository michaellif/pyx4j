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
package com.propertyvista.crm.client.ui.report;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.dashboard.DashboardMetadata;

public class ReportLister extends ListerBase<DashboardMetadata> {

    private static final I18n i18n = I18n.get(ReportLister.class);

    public ReportLister() {
        super(DashboardMetadata.class, false, true);
        getDataTablePanel().setFilteringEnabled(false);
        getDataTablePanel().getDataTable().setHasCheckboxColumn(true);

        addActionItem(new Button(i18n.tr("Delete Checked"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                for (DashboardMetadata item : getDataTablePanel().getDataTable().getCheckedItems()) {
                    getPresenter().delete(item.getPrimaryKey());

                }
            }
        }));

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().type()).build(),
            new MemberColumnDescriptor.Builder(proto().name()).build(),
            new MemberColumnDescriptor.Builder(proto().isShared()).build(),
            new MemberColumnDescriptor.Builder(proto().isFavorite()).build(),
            new MemberColumnDescriptor.Builder(proto().description()).build()
        );//@formatter:on
    }
}
