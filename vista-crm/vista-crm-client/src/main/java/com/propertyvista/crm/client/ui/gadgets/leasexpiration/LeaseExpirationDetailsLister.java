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
package com.propertyvista.crm.client.ui.gadgets.leasexpiration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.BasicLister;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.dto.LeaseDTO;

public class LeaseExpirationDetailsLister extends BasicLister<LeaseDTO> {

    private static final I18n i18n = I18n.get(LeaseExpirationDetailsLister.class);

    public LeaseExpirationDetailsLister(final Command backCommandHandler) {
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
                new Builder(proto().expectedMoveOut(), false).build(),
                new Builder(proto().actualMoveIn(), false).build(),
                new Builder(proto().actualMoveOut(), false).build(),
                new Builder(proto().moveOutNotice(), false).build(),
                
                new Builder(proto().approvalDate(), false).build(),
                new Builder(proto().creationDate(), false).build()
        );//@formatter:on

        addActionItem(new Button(i18n.tr("Back to Summary"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                backCommandHandler.execute();
            }
        }));
    }

    @Override
    protected void onItemSelect(LeaseDTO item) {
        AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(proto().getInstanceValueClass()).formViewerPlace(item.getPrimaryKey()));
    }
}
