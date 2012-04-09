/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.simulation;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.rpc.SimulationDTO;

public class SimulationEditorForm extends AdminEntityForm<SimulationDTO> {

    private final static I18n i18n = I18n.get(SimulationEditorForm.class);

    public SimulationEditorForm(boolean viewable) {
        super(SimulationDTO.class, viewable);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;

        content.setH1(++row, 0, 1, i18n.tr("Cache"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().entityCacheServiceEnabled())).build());

        content.setH1(++row, 0, 1, i18n.tr("Network"));

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().networkSimulation().enabled())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().networkSimulation().delay())).build());

        return content;
    }

}
