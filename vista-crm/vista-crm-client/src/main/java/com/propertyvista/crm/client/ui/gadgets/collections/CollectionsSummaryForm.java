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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.CollectionsGadgetDataDTO;

public class CollectionsSummaryForm extends ZoomableViewForm<CollectionsGadgetDataDTO> {

    private static final I18n i18n = I18n.get(CollectionsSummaryForm.class);

    public CollectionsSummaryForm() {
        super(CollectionsGadgetDataDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().tenantsPaidThisMonth())).componentWidth(10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().fundsCollectedThisMonthLabel())).componentWidth(10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().fundsInProcessingLabel())).componentWidth(10).build());
        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().fundsCollectedThisMonthLabel()).setValue(i18n.tr("${0,number}", getValue().fundsCollectedThisMonth().getValue()));
        get(proto().fundsInProcessingLabel()).setValue(i18n.tr("${0,number}", getValue().fundsInProcessing().getValue()));
    }
}
