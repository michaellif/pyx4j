/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.administration.financial.arcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.admin.ARCodeCrudService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.shared.config.VistaFeatures;

public class ARCodeLister extends SiteDataTablePanel<ARCode> {

    private static final I18n i18n = I18n.get(ARCodeLister.class);

    public ARCodeLister() {
        super(ARCode.class, GWT.<ARCodeCrudService> create(ARCodeCrudService.class), true, true);

        List<ColumnDescriptor> columnDescriptors = new ArrayList<ColumnDescriptor>(Arrays.asList(
        //@formatter:off
            new ColumnDescriptor.Builder(proto().name()).build(),
            new ColumnDescriptor.Builder(proto().type()).build(),
            new ColumnDescriptor.Builder(proto().type()).searchable(false).sortable(false).title(i18n.tr("Debit/Credit")).formatter(new IFormatter<IEntity, SafeHtml>() {
                @Override
                public SafeHtml format(IEntity value) {
                    SafeHtmlBuilder builder = new SafeHtmlBuilder();
                  ARCode.Type type = (ARCode.Type) value.getMember(proto().type().getPath()).getValue();
                    if (type != null) {
                        builder.appendHtmlConstant(type.getActionType().toString());
                    }
                    return builder.toSafeHtml();
                }
            }).build(),
            new ColumnDescriptor.Builder(proto().glCode()).build(),
            new ColumnDescriptor.Builder(proto().reserved()).build()
        ));//@formatter:on

        if (VistaFeatures.instance().yardiIntegration()) {
            columnDescriptors.add(new ColumnDescriptor.Builder(proto().yardiChargeCodes()).build());
        }

        setColumnDescriptors(columnDescriptors);

        setDataTableModel(new DataTableModel<ARCode>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }
}
