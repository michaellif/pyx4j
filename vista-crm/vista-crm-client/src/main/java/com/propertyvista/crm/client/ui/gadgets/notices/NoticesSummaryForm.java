/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.notices;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CHyperlink;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.gadgets.notices.NoticesGadgetFactory.NoticesGadget;
import com.propertyvista.crm.rpc.dto.gadgets.NoticesGadgetDataDTO;
import com.propertyvista.domain.dashboard.gadgets.type.NoticesGadgetMetadata.NoticesFilter;

public class NoticesSummaryForm extends CEntityDecoratableForm<NoticesGadgetDataDTO> {

    private final NoticesGadget noticesGadget;

    public NoticesSummaryForm(NoticesGadget noticesGadget) {
        super(NoticesGadgetDataDTO.class);
        setEditable(false);
        this.noticesGadget = noticesGadget;
    }

    @Override
    public IsWidget createContent() {
        final double NUMBER_WIDTH = 10d;
        final double LABEL_WIDTH = 18d;
        FlowPanel content = new FlowPanel();

        content.add(new DecoratorBuilder(inject(proto().unitsVacant(), new CHyperlink(noticesGadget.displayVacantUnits()))).componentWidth(NUMBER_WIDTH)
                .labelWidth(LABEL_WIDTH).build());
        content.add(new DecoratorBuilder(inject(proto().unitVacancy(), new CHyperlink(noticesGadget.displayVacantUnits()))).componentWidth(NUMBER_WIDTH)
                .labelWidth(LABEL_WIDTH).build());

        content.add(new DecoratorBuilder(inject(proto().noticesLeavingThisMonth(), new CHyperlink(noticesGadget.displayNotices(NoticesFilter.THIS_MONTH))))
                .componentWidth(NUMBER_WIDTH).labelWidth(LABEL_WIDTH).build());
        content.add(new DecoratorBuilder(inject(proto().noticesLeavingNextMonth(), new CHyperlink(noticesGadget.displayNotices(NoticesFilter.NEXT_MONTH))))
                .componentWidth(NUMBER_WIDTH).labelWidth(LABEL_WIDTH).build());
        content.add(new DecoratorBuilder(inject(proto().noticesLeavingOver90Days(), new CHyperlink(noticesGadget.displayNotices(NoticesFilter.OVER_90_DAYS))))
                .componentWidth(NUMBER_WIDTH).labelWidth(LABEL_WIDTH).build());

        return content;
    }
}
