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
package com.propertyvista.crm.client.ui.crud.lease;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor.Builder;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.SiteDataTablePanel;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseDataDialog;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.ac.PadFileDownload;
import com.propertyvista.crm.rpc.services.lease.ac.PadFileUpload;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseLister extends SiteDataTablePanel<LeaseDTO> {

    private final static I18n i18n = I18n.get(LeaseLister.class);

    public LeaseLister() {
        super(LeaseDTO.class, GWT.<LeaseViewerCrudService> create(LeaseViewerCrudService.class), false);

        List<ColumnDescriptor> columnDescriptors = new ArrayList<ColumnDescriptor>(Arrays.asList(
        //@formatter:off
            new Builder(proto().leaseId()).columnTitle(i18n.tr("Id")).width("80px").build(),
            new Builder(proto().type()).build(),

            new Builder(proto().unit().building().propertyCode()).filterAlwaysShown(true).build(),
            new Builder(proto().unit().building().info().name(), false).columnTitle(i18n.tr("Building Name")).build(),
            new Builder(proto().unit().building().info().address(), false).columnTitle(i18n.tr("Building Address")).searchable(false).build(),
            new Builder(proto().unit().building().info().address().streetNumber(), false).columnTitle(i18n.tr("Building Street Number")).width("80px").build(),
            new Builder(proto().unit().building().info().address().streetName(), false).columnTitle(i18n.tr("Building Street Name")).build(),

            new Builder(proto().unit()).searchable(false).build(),
            new Builder(proto().unit().info().number()).columnTitle(proto().unit().getMeta().getCaption()).searchableOnly().width("80px").build(),

            new Builder(proto()._applicant().customer().person().name()).columnTitle(i18n.tr("Primary Tenant Name")).searchable(false).build(),
            new Builder(proto()._applicant().customer().person().name().firstName(), false).columnTitle(i18n.tr("Primary Tenant First Name")).filterAlwaysShown(true).build(),
            new Builder(proto()._applicant().customer().person().name().lastName(), false).columnTitle(i18n.tr("Primary Tenant Last Name")).filterAlwaysShown(true).build(),
            new Builder(proto()._applicant().customer().registeredInPortal(), false).width("80px").build(),

            new Builder(proto().leaseParticipants().$().customer().customerId(), false).searchableOnly().build(),

            new Builder(proto().status()).filterAlwaysShown(true).width("80px").build(),
            new Builder(proto().completion()).build(),
            new Builder(proto().billingAccount().accountNumber()).build(),
            new Builder(proto().preauthorizedPaymentPresent()).sortable(false).build(),

            new Builder(proto().leaseFrom()).build(),
            new Builder(proto().leaseTo()).build(),

            new Builder(proto().expectedMoveIn()).build(),
            new Builder(proto().expectedMoveOut(), false).build(),
            new Builder(proto().actualMoveIn(), false).build(),
            new Builder(proto().actualMoveOut(), false).build(),
            new Builder(proto().moveOutSubmissionDate(), false).build(),

            new Builder(proto().approvalDate(), false).build(),
            new Builder(proto().creationDate(), false).build()
        )); //@formatter:on

        if (SecurityController.check(VistaBasicBehavior.PropertyVistaSupport)) {
            if (VistaFeatures.instance().yardiIntegration()) {
                columnDescriptors.add(new ColumnDescriptor.Builder(proto().billingAccount().invoiceLineItems().$().chargeCode(), false)
                        .columnTitle("Yardi Charge Codes").searchableOnly().build());
            }
            columnDescriptors.add(new ColumnDescriptor.Builder(proto().billingAccount().id(), false).columnTitle("Billing Account Id").build());
        }

        setColumnDescriptors(columnDescriptors);

        setDataTableModel(new DataTableModel<LeaseDTO>());

        // TODO currently disabled till new drop-down quick filters selector implemented:
//        addActionItem(new Button(new Image(EntityFolderImages.INSTANCE.addButton().hover()), i18n.tr("Application"), new Command() {
//            @Override
//            public void execute() {
//                new LeaseDataDialog(LeaseDataDialog.Type.Application).show();
//            }
//        }));

        if (!VistaFeatures.instance().yardiIntegration()) {
            Button createLease;
            addUpperActionItem(createLease = new Button(FolderImages.INSTANCE.addIcon(), i18n.tr("New Lease"), new Command() {
                @Override
                public void execute() {
                    new LeaseDataDialog(LeaseDataDialog.Type.New).show();
                }
            }));
            createLease.setPermission(DataModelPermission.permissionCreate(LeaseDTO.class));
        }

        if (!VistaFeatures.instance().yardiIntegration()) {
            Button createLease;
            addUpperActionItem(createLease = new Button(FolderImages.INSTANCE.addIcon(), i18n.tr("Current Lease"), new Command() {
                @Override
                public void execute() {
                    new LeaseDataDialog(LeaseDataDialog.Type.Current).show();
                }
            }));
            createLease.setPermission(DataModelPermission.permissionCreate(LeaseDTO.class));
        }

        addUpperActionItem(new Button(i18n.tr("Upload PAD File"), new Command() {
            @Override
            public void execute() {
                onPadFileUpload();
            }
        }, new ActionPermission(PadFileUpload.class)));

        addUpperActionItem(new Button(i18n.tr("Download PAD File"), new Command() {
            @Override
            public void execute() {
                onPadFileDownload();
            }
        }, new ActionPermission(PadFileDownload.class)));
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().leaseId(), false));
    }

    @Override
    protected EntityListCriteria<LeaseDTO> updateCriteria(EntityListCriteria<LeaseDTO> criteria) {
        criteria.in(criteria.proto().status(), Lease.Status.present());
        return super.updateCriteria(criteria);
    }

    public void onPadFileUpload() {

    }

    public void onPadFileDownload() {

    }
}
