/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.portal.domain.dto.DashboardDTO;

public class DashboardForm extends CEntityDecoratableEditor<DashboardDTO> implements DashboardView {

    private static I18n i18n = I18n.get(DashboardForm.class);

    private Presenter presenter;

    public DashboardForm() {
        super(DashboardDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel container = new FormFlexPanel();

        int row = 0;

        container.setH1(row++, 0, 1, i18n.tr("Dashboard"));

        container.setWidget(row++, 0, new DecoratorBuilder(inject(proto().notification()), 12).build());

        return container;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

}
