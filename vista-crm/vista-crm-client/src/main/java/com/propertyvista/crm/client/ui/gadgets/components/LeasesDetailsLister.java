/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.BasicLister;

import com.propertyvista.dto.LeaseDTO;

public class LeasesDetailsLister extends BasicLister<LeaseDTO> {

    private static final I18n i18n = I18n.get(LeasesDetailsLister.class);

    public LeasesDetailsLister() {
        super(LeaseDTO.class, true, false);
        setColumnDescriptors(//@formatter:off
                new Builder(proto().leaseId()).build(),
                new Builder(proto().type()).build(),
                
                new Builder(proto().unit().building().propertyCode()).build(),
                new Builder(proto().unit()).searchableOnly().build(),
                new Builder(proto().unit().info().number()).searchableOnly().columnTitle(proto().unit().getMeta().getCaption()).build(),
                
                new Builder(proto().status()).build(),
                new Builder(proto().completion()).build(),
                new Builder(proto().billingAccount().accountNumber()).build(),
                
                new Builder(proto().leaseFrom()).build(),
                new Builder(proto().leaseTo()).build(),
                
                new Builder(proto().expectedMoveIn()).build(),
                new Builder(proto().expectedMoveOut()).build(),
                new Builder(proto().actualMoveIn(), false).build(),
                new Builder(proto().actualMoveOut(), false).build(),
                new Builder(proto().moveOutNotice()).build(),
                
                new Builder(proto().approvalDate(), false).build(),
                new Builder(proto().creationDate(), false).build()
        );//@formatter:on

    }

    @Override
    protected void onItemSelect(LeaseDTO item) {
        AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(proto().getInstanceValueClass()).formViewerPlace(item.getPrimaryKey()));
    }
}
