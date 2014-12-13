/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.availablereport;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.crm.rpc.services.admin.AvailableCrmReportAdminCrudService;
import com.propertyvista.domain.reports.AvailableCrmReport;

public class AvailableCrmReportListerViewImpl extends AbstractListerView<AvailableCrmReport> implements AvailableCrmReportListerView {

    public AvailableCrmReportListerViewImpl() {
        setDataTablePanel(new CrmRoleLister());
    }

    public static class CrmRoleLister extends SiteDataTablePanel<AvailableCrmReport> {

        public CrmRoleLister() {
            super(AvailableCrmReport.class, GWT.<AbstractListCrudService<AvailableCrmReport>> create(AvailableCrmReportAdminCrudService.class), false);

            setColumnDescriptors( //
                    new ColumnDescriptor.Builder(proto().reportType()).build(), //
                    new ColumnDescriptor.Builder(proto().roles()).build());

            setDataTableModel(new DataTableModel<AvailableCrmReport>());
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().reportType(), false));
        }
    }
}
