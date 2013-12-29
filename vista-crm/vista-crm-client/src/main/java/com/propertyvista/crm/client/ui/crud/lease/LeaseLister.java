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
package com.propertyvista.crm.client.ui.crud.lease;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.lease.common.dialogs.LeaseDataDialog;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseLister extends AbstractLister<LeaseDTO> {

    private final static I18n i18n = I18n.get(LeaseLister.class);

    private Button padFileUpload;

    private Button padFileDownload;

    public LeaseLister() {
        super(LeaseDTO.class, false);

        List<ColumnDescriptor> columnDescriptors = new ArrayList<ColumnDescriptor>(Arrays.asList(
        //@formatter:off
            new Builder(proto().leaseId()).columnTitle(i18n.tr("Id")).build(),
            new Builder(proto().type()).build(),
            
            new Builder(proto().unit().building().propertyCode()).build(),
            new Builder(proto().unit().building().info().name(), false).columnTitle(i18n.tr("Building Name")).build(),
            new Builder(proto().unit().building().info().address(), false).columnTitle(i18n.tr("Building Address")).searchable(false).build(),
            new Builder(proto().unit().building().info().address().streetName(), false).columnTitle(i18n.tr("Building Street Name")).build(),
            
            new Builder(proto().unit()).searchable(false).build(),
            new Builder(proto().unit().info().number()).columnTitle(proto().unit().getMeta().getCaption()).searchableOnly().build(),
            
            new Builder(proto()._applicant().customer().person().name()).columnTitle(i18n.tr("Primary Tenant Name")).searchable(false).build(),
            new Builder(proto()._applicant().customer().person().name().firstName(), false).columnTitle(i18n.tr("Primary Tenant First Name")).build(),
            new Builder(proto()._applicant().customer().person().name().lastName(), false).columnTitle(i18n.tr("Primary Tenant Last Name")).build(),
            new Builder(proto()._applicant().customer().registeredInPortal(), false).build(),
            
            new Builder(proto().leaseParticipants().$().customer().customerId(), false).build(),
            
            new Builder(proto().status()).build(),
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
                ));
        //@formatter:on

        if (SecurityController.checkBehavior(VistaCrmBehavior.PropertyVistaSupport)) {
            if (VistaFeatures.instance().yardiIntegration()) {
                columnDescriptors.add(new MemberColumnDescriptor.Builder(proto().billingAccount().invoiceLineItems().$().chargeCode(), false)
                        .columnTitle("Yardi Charge Codes").searchableOnly().build());
            }
            columnDescriptors.add(new MemberColumnDescriptor.Builder(proto().billingAccount().id(), false).columnTitle("Billing Account Id").build());
        }

        setColumnDescriptors(columnDescriptors);

        // TODO currently disabled till new drop-down quick filters selector implemented:
//        addActionItem(new Button(new Image(EntityFolderImages.INSTANCE.addButton().hover()), i18n.tr("Application"), new Command() {
//            @Override
//            public void execute() {
//                new LeaseDataDialog(LeaseDataDialog.Type.Application).show();
//            }
//        }));

        if (!VistaFeatures.instance().yardiIntegration()) {
            addActionItem(new Button(EntityFolderImages.INSTANCE.addButton().hover(), i18n.tr("New Lease"), new Command() {
                @Override
                public void execute() {
                    new LeaseDataDialog(LeaseDataDialog.Type.New).show();
                }
            }));
        }

        if (!VistaFeatures.instance().yardiIntegration()) {
            addActionItem(new Button(EntityFolderImages.INSTANCE.addButton().hover(), i18n.tr("Current Lease"), new Command() {
                @Override
                public void execute() {
                    new LeaseDataDialog(LeaseDataDialog.Type.Current).show();
                }
            }));
        }

        addActionItem(padFileUpload = new Button(i18n.tr("Upload PAD File"), new Command() {
            @Override
            public void execute() {
                onPadFileUpload();
            }
        }));

        addActionItem(padFileDownload = new Button(i18n.tr("Download PAD File"), new Command() {
            @Override
            public void execute() {
                onPadFileDownload();
            }
        }));

    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().leaseId(), false));
    }

    public void onPadFileUpload() {

    }

    public void onPadFileDownload() {

    }

    public void setPadFileControlsEnabled(boolean isEnabled) {
        padFileDownload.setVisible(isEnabled);
        padFileUpload.setVisible(isEnabled);
    }
}
