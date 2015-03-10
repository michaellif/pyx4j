/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 10, 2015
 * @author vlads
 */
package com.propertyvista.crm.client.ui.reports.autopayreconciliation;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor.Builder;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.dto.reports.AutoPayReconciliationDTO;
import com.propertyvista.crm.rpc.services.reports.AutoPayReconciliationReportListService;
import com.propertyvista.shared.config.VistaFeatures;

public class AutoPayReconciliationReportLister extends SiteDataTablePanel<AutoPayReconciliationDTO> {

    private final static I18n i18n = I18n.get(AutoPayReconciliationReportLister.class);

    public AutoPayReconciliationReportLister() {
        super(AutoPayReconciliationDTO.class, GWT.<AbstractCrudService<AutoPayReconciliationDTO>> create(AutoPayReconciliationReportListService.class), false);

        setColumnDescriptors(
                //
                new Builder(proto().tenant().lease().unit().building().propertyCode()).width("80px").filterAlwaysShown(true).build(), //
                new Builder(proto().tenant().lease().unit()).width("80px").searchable(false).build(),
                new Builder(proto().tenant().lease().leaseId()).width("80px").filterAlwaysShown(VistaFeatures.instance().yardiIntegration()).build(), //

                new Builder(proto().tenant()).searchable(false).build(), //
                new Builder(proto().tenant().participantId()).columnTitle(i18n.tr("Tenant Id")).width("80px")
                        .filterAlwaysShown(VistaFeatures.instance().yardiIntegration()).build(), //
                new Builder(proto().tenant().customer().person().name().firstName()).visible(false).columnTitle(i18n.tr("Tenant First Name")).build(),//
                new Builder(proto().tenant().customer().person().name().lastName()).visible(false).columnTitle(i18n.tr("Tenant Last Name")).build(),//

                new Builder(proto().effectiveFrom()).build(), // 'first billing cycle'
                new Builder(proto().tenant().lease().expectedMoveOut()).visible(false).build(), //
                new Builder(proto().renewalDate()).width("80px").searchable(false).sortable(false).build(), //
                new Builder(proto().created()).build(), //

                new Builder(proto().rentCharge()).width("80px").searchable(false).sortable(false).build(), //
                new Builder(proto().parkingCharges()).width("80px").searchable(false).sortable(false).build(), //
                new Builder(proto().otherCharges()).width("80px").searchable(false).sortable(false).build(), //

                new Builder(proto().price()).width("80px").searchable(false).sortable(false).build(), //
                new Builder(proto().paymentShareAmount()).width("80px").visible(false).searchable(false).sortable(false).build(), //
                new Builder(proto().payment()).width("80px").searchable(false).sortable(false).build(), //
                new Builder(proto().discrepancy()).searchable(false).sortable(false).build(), //

                new Builder(proto().count()).searchable(false).sortable(false).visible(false).build(), //
                new Builder(proto().notice()).searchable(false).sortable(false).visible(true).build(), //
                new Builder(proto().comments()).visible(false).build() //
        );

        setDataTableModel(new DataTableModel<AutoPayReconciliationDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().tenant().lease().leaseId(), false));
    }
}
