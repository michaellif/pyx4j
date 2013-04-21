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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.lease.common.dialogs.LeaseDataDialog;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseLister extends AbstractLister<LeaseDTO> {

    private final static I18n i18n = I18n.get(LeaseLister.class);

    public LeaseLister() {
        super(LeaseDTO.class, false);

        setColumnDescriptors(//@formatter:off
            new Builder(proto().leaseId()).columnTitle(i18n.tr("Id")).build(),
            new Builder(proto().type()).build(),
            
            new Builder(proto().unit().building().propertyCode()).build(),
            new Builder(proto().unit()).searchable(false).build(),
            new Builder(proto().unit().info().number()).columnTitle(proto().unit().getMeta().getCaption()).searchableOnly().build(),
            
            new Builder(proto().status()).build(),
            new Builder(proto().completion()).build(),
            new Builder(proto().billingAccount().accountNumber()).build(),
            new Builder(proto().papPresent()).sortable(false).build(),
            
            new Builder(proto().leaseFrom()).build(),
            new Builder(proto().leaseTo()).build(),
            
            new Builder(proto().expectedMoveIn()).build(),
            new Builder(proto().expectedMoveOut(), false).build(),
            new Builder(proto().actualMoveIn(), false).build(),
            new Builder(proto().actualMoveOut(), false).build(),
            new Builder(proto().moveOutSubmissionDate(), false).build(),
            
            new Builder(proto().approvalDate(), false).build(),
            new Builder(proto().creationDate(), false).build()
        );//@formatter:on

// TODO currently disabled till new drop-down quick filters selector implemented:
//        addActionItem(new Button(new Image(EntityFolderImages.INSTANCE.addButton().hover()), i18n.tr("Application"), new Command() {
//            @Override
//            public void execute() {
//                new LeaseDataDialog(LeaseDataDialog.Type.Application).show();
//            }
//        }));

        if (!VistaFeatures.instance().yardiIntegration()) {
            addActionItem(new Button(new Image(EntityFolderImages.INSTANCE.addButton().hover()), i18n.tr("New Lease"), new Command() {
                @Override
                public void execute() {
                    new LeaseDataDialog(LeaseDataDialog.Type.New).show();
                }
            }));
        }

        if (!VistaFeatures.instance().yardiIntegration()) {
            addActionItem(new Button(new Image(EntityFolderImages.INSTANCE.addButton().hover()), i18n.tr("Current Lease"), new Command() {
                @Override
                public void execute() {
                    new LeaseDataDialog(LeaseDataDialog.Type.Current).show();
                }
            }));
        }
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().leaseId().getPath().toString(), false));
    }

    @Override
    protected EntityListCriteria<LeaseDTO> updateCriteria(EntityListCriteria<LeaseDTO> criteria) {
        // TODO : set all that stuff in Activity (like TenantListers) or CRUD service ?
        criteria.add(PropertyCriterion.in(criteria.proto().status(),
                EnumSet.of(Lease.Status.Active, Lease.Status.ExistingLease, Lease.Status.NewLease, Lease.Status.Approved)));
        return super.updateCriteria(criteria);
    }
}
