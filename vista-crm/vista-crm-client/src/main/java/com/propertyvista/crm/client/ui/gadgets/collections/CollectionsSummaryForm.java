/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.collections;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetSummaryForm;
import com.propertyvista.crm.rpc.dto.gadgets.CollectionsGadgetDataDTO;

public class CollectionsSummaryForm extends CounterGadgetSummaryForm<CollectionsGadgetDataDTO> {

    public CollectionsSummaryForm() {
        super(CollectionsGadgetDataDTO.class);
    }

    @Override
    public IsWidget createContent() {
        VerticalPanel content = new VerticalPanel();
        content.add(new DecoratorBuilder(inject(proto().tenantsPaidThisMonth())).build());
        content.add(new DecoratorBuilder(inject(proto().fundsCollectedThisMonth())).build());
        content.add(new DecoratorBuilder(inject(proto().fundsInProcessing())).build());
        return content;
    }

}
