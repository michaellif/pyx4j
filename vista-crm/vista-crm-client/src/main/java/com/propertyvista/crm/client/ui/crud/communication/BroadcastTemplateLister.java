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
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.BroadcastTemplateCrudService;
import com.propertyvista.domain.communication.BroadcastTemplate;

public class BroadcastTemplateLister extends SiteDataTablePanel<BroadcastTemplate> {

    private static final I18n i18n = I18n.get(BroadcastTemplateLister.class);

    public BroadcastTemplateLister() {
        super(BroadcastTemplate.class, GWT.<AbstractCrudService<BroadcastTemplate>> create(BroadcastTemplateCrudService.class), true, true);

        setColumnDescriptors(new ColumnDescriptor[] { //
        new ColumnDescriptor.Builder(proto().name()).build(), //
                new ColumnDescriptor.Builder(proto().subject()).build(), //
                new ColumnDescriptor.Builder(proto().category()).build(), //
                new ColumnDescriptor.Builder(proto().highImportance()).build() //
        });

        setDataTableModel(new DataTableModel<BroadcastTemplate>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }
}
