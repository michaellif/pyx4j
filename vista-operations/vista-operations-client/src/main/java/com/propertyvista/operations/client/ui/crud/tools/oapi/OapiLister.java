/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2014
 * @author ernestog
 */
package com.propertyvista.operations.client.ui.crud.tools.oapi;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.rpc.dto.OapiConversionDTO;
import com.propertyvista.operations.rpc.services.OapiCrudService;

public class OapiLister extends SiteDataTablePanel<OapiConversionDTO> {

    protected static final I18n i18n = I18n.get(OapiLister.class);

    public OapiLister() {
        super(OapiConversionDTO.class, GWT.<AbstractCrudService<OapiConversionDTO>> create(OapiCrudService.class), true);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().created()).build(), //
                new ColumnDescriptor.Builder(proto().name()).build(), //
                new ColumnDescriptor.Builder(proto().description()).build(), //
                new ColumnDescriptor.Builder(proto().filesNumber()).build());

        setDataTableModel(new DataTableModel<OapiConversionDTO>());

        setDeleteActionEnabled(true);
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().created(), false));
    }
}
