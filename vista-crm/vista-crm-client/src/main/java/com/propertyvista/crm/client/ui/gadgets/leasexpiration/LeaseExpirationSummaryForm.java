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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CHyperlink;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;
import com.propertyvista.domain.dashboard.gadgets.type.LeaseExpirationGadgetMeta;

final class LeaseExpirationSummaryForm extends CEntityDecoratableForm<LeaseExpirationGadgetDataDTO> {

    private final LeaseExpirationGadgetFactory.LeaseExpirationGadget leaseExpirationGadget;

    LeaseExpirationSummaryForm(LeaseExpirationGadgetFactory.LeaseExpirationGadget leaseExpirationGadget, Class<LeaseExpirationGadgetDataDTO> clazz) {
        super(clazz);
        this.setEditable(false);
        this.leaseExpirationGadget = leaseExpirationGadget;
    }

    @Override
    public IsWidget createContent() {

        FlowPanel panel = new FlowPanel();
        panel.add(new DecoratorBuilder(inject(proto().unitsOccupied(), new CHyperlink(this.leaseExpirationGadget.openUnitDetals()))).componentWidth(5).build());
        panel.add(new DecoratorBuilder(inject(proto().unitOccupancyPct(), new CHyperlink(this.leaseExpirationGadget.openUnitDetals()))).componentWidth(5)
                .build());
        panel.add(new DecoratorBuilder(inject(proto().numOfLeasesEndingThisMonth(),
                new CHyperlink(this.leaseExpirationGadget.openLeaseDetails(LeaseExpirationGadgetMeta.LeaseFilter.THIS_MONTH)))).componentWidth(5).build());
        panel.add(new DecoratorBuilder(inject(proto().numOfLeasesEndingNextMonth(),
                new CHyperlink(this.leaseExpirationGadget.openLeaseDetails(LeaseExpirationGadgetMeta.LeaseFilter.NEXT_MONTH)))).componentWidth(5).build());
        panel.add(new DecoratorBuilder(inject(proto().numOfLeasesEndingOver90Days(),
                new CHyperlink(this.leaseExpirationGadget.openLeaseDetails(LeaseExpirationGadgetMeta.LeaseFilter.OVER_90_DAYS)))).componentWidth(5).build());
        panel.add(new DecoratorBuilder(inject(proto().numOfLeasesOnMonthToMonth(),
                new CHyperlink(this.leaseExpirationGadget.openLeaseDetails(LeaseExpirationGadgetMeta.LeaseFilter.MONTH_ON_MONTH)))).componentWidth(5).build());

        return panel;
    }
}