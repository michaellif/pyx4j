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
package com.propertyvista.crm.client.ui.crud.lease.application;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor.Builder;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseDataDialog;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationViewerCrudService;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseApplicationLister extends SiteDataTablePanel<LeaseApplicationDTO> {

    private final static I18n i18n = I18n.get(LeaseApplicationLister.class);

    public LeaseApplicationLister() {
        super(LeaseApplicationDTO.class, GWT.<LeaseApplicationViewerCrudService> create(LeaseApplicationViewerCrudService.class), true);

        setColumnDescriptors(
                //
                new Builder(proto().leaseApplication().applicationId()).columnTitle(i18n.tr("Id"))
                        .filterAlwaysShown(VistaFeatures.instance().yardiIntegration()).build(),//
                new Builder(proto().type()).build(),//
                new Builder(proto().unit().building().propertyCode()).filterAlwaysShown(true).build(),//
                new Builder(proto().unit()).filterAlwaysShown(true).build(),//
                new Builder(proto().leaseApplication().status()).filterAlwaysShown(true).build(),//

                new Builder(proto().currentTerm().termFrom()).build(),//
                new Builder(proto().currentTerm().termTo()).build(),//
                new Builder(proto().expectedMoveIn()).build(),//
                new Builder(proto().expectedMoveOut()).visible(false).build(),//
                new Builder(proto().actualMoveIn()).visible(false).build(),//
                new Builder(proto().actualMoveOut()).visible(false).build(),//
                new Builder(proto().moveOutSubmissionDate()).visible(false).build(),//
                new Builder(proto().creationDate()).visible(false).build(),//
                new Builder(proto().onlineApplication()).sortable(false).searchable(false).build(),//

                new Builder(proto()._applicant().customer().person().name()).columnTitle(i18n.tr("Primary Tenant Name")).searchable(false).build(),//
                new Builder(proto()._applicant().customer().person().name().firstName()).visible(false).columnTitle(i18n.tr("Primary Tenant First Name"))
                        .build(),//
                new Builder(proto()._applicant().customer().person().name().lastName()).visible(false).columnTitle(i18n.tr("Primary Tenant Last Name")).build(),//

                new Builder(proto().leaseParticipants().$().customer().customerId()).visible(false).searchableOnly().build(),//

                new Builder(proto().numberOfOccupants()).visible(false).sortable(false).searchable(false).columnTitle(i18n.tr("Occupants")).build(),//
                new Builder(proto().numberOfApplicants()).sortable(false).searchable(false).columnTitle(i18n.tr("Applicants")).build(),//
                new Builder(proto().numberOfDepentands()).sortable(false).searchable(false).columnTitle(i18n.tr("Dependents")).build(),//
                new Builder(proto().numberOfGuarantors()).sortable(false).searchable(false).columnTitle(i18n.tr("Guarantors")).build(),//

                //TODO make this work
                //new Builder(proto().currentTerm().version().tenants().$().leaseParticipant().customer().person().name().firstName()).visible(false).build(),//
                //new Builder(proto().currentTerm().version().tenants().$().leaseParticipant().customer().person().name().lastName()).visible(false).build(),//
                new Builder(proto().currentTerm().version().tenants()).searchable(false).build());

        setDataTableModel(new DataTableModel<LeaseApplicationDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().leaseApplication().applicationId(), true));
    }

    @Override
    protected void onItemNew() {
        new LeaseDataDialog(LeaseDataDialog.Type.Application).show();
    }
}
