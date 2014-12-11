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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.financial.paps;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.dto.financial.AutoPayHistoryDTO;
import com.propertyvista.crm.rpc.services.financial.AutoPayHistoryCrudService;

public class AutoPayHistoryLister extends SiteDataTablePanel<AutoPayHistoryDTO> {

    private static final I18n i18n = I18n.get(AutoPayHistoryLister.class);

    public AutoPayHistoryLister() {
        super(AutoPayHistoryDTO.class, GWT.<AutoPayHistoryCrudService> create(AutoPayHistoryCrudService.class), false);

        setColumnDescriptors( //
                new MemberColumnDescriptor.Builder(proto().id()).build(), //
                new MemberColumnDescriptor.Builder(proto().isDeleted()).build(), //

                new MemberColumnDescriptor.Builder(proto().price()).searchable(false).sortable(false).build(), //
                new MemberColumnDescriptor.Builder(proto().payment()).searchable(false).sortable(false).build(), //

                new MemberColumnDescriptor.Builder(proto().tenant().lease()).searchable(false).build(), //
                new MemberColumnDescriptor.Builder(proto().tenant().lease().leaseId()).searchableOnly().build(), //

                new MemberColumnDescriptor.Builder(proto().tenant()).searchable(false).build(), //
                new MemberColumnDescriptor.Builder(proto().tenant().participantId()).columnTitle(i18n.tr("Tenant Id")).searchableOnly().build(), //

                new MemberColumnDescriptor.Builder(proto().tenant().lease().unit().building()).searchable(false).build(), //
                new MemberColumnDescriptor.Builder(proto().tenant().lease().unit().building().propertyCode()).searchableOnly().build(), //

                new MemberColumnDescriptor.Builder(proto().paymentMethod()).searchable(false).build(), //
                new MemberColumnDescriptor.Builder(proto().paymentMethod().type()).title(i18n.tr("Payment Method Type")).visible(false).build(), //

                new MemberColumnDescriptor.Builder(proto().comments()).build(), //

                new MemberColumnDescriptor.Builder(proto().effectiveFrom()).build(), //
                new MemberColumnDescriptor.Builder(proto().expiredFrom()).build(), //

                new MemberColumnDescriptor.Builder(proto().createdBy(), false).searchable(false).build(), //
                new MemberColumnDescriptor.Builder(proto().created()).searchable(false).build(), //

                new MemberColumnDescriptor.Builder(proto().updatedByTenant(), false).build(), //
                new MemberColumnDescriptor.Builder(proto().updatedBySystem(), false).build(), //
                new MemberColumnDescriptor.Builder(proto().updated()).searchable(false).build() //
        );

        setDataTableModel(new DataTableModel<AutoPayHistoryDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().effectiveFrom(), true), new Sort(proto().expiredFrom(), false));
    }
}
