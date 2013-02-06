/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.cardservicesimulation;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.admin.client.ui.crud.AdminEditorViewImplBase;
import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.dev.CardServiceSimulation;
import com.propertyvista.admin.rpc.AdminSiteMap;

public class CardServiceSimulationEditorViewImpl extends AdminEditorViewImplBase<CardServiceSimulation> implements CardServiceSimulationEditorView {

    private static class CardServiceSimulationForm extends AdminEntityForm<CardServiceSimulation> {

        public CardServiceSimulationForm(IFormView<CardServiceSimulation> view) {
            super(CardServiceSimulation.class, view);

            FormFlexPanel contentPanel = new FormFlexPanel("Card Service Simulation");

            int row = -1;
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().cardType())).build());
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().number())).build());
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().expiryDate())).build());
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().balance())).build());
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().responseCode())).build());
            // TODO maybe would be nice to have: a hyperlink that opens list of transactiions

            selectTab(addTab(contentPanel));

        }

    }

    public CardServiceSimulationEditorViewImpl() {
        super(AdminSiteMap.Administration.CardServiceSimulation.class);
        setForm(new CardServiceSimulationForm(this));
    }

}
