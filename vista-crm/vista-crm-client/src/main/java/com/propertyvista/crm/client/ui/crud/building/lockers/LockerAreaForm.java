/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.lockers;

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.LockerAreaDTO;

public class LockerAreaForm extends CrmEntityForm<LockerAreaDTO> {

    private static final I18n i18n = I18n.get(LockerAreaForm.class);

    public LockerAreaForm(IForm<LockerAreaDTO> view) {
        super(LockerAreaDTO.class, view);

//        tabPanel.add(isEditable() ? new HTML() : ((LockerAreaView) getParentView()).getDashboardView().asWidget(), i18n.tr("Dashboard"));
        Tab tab = addTab(createDetailsTab(i18n.tr("Details")));
        selectTab(tab);

        tab = addTab(isEditable() ? new HTML() : ((LockerAreaViewerView) getParentView()).getLockerView().asWidget(), i18n.tr("Lockers"));
        setTabEnabled(tab, !isEditable());
    }

    private TwoColumnFlexFormPanel createDetailsTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        int row = -1;
        main.setWidget(++row, 0, inject(proto().name(), new FormDecoratorBuilder(15).build()));
        main.setWidget(++row, 0, inject(proto().levels(), new FormDecoratorBuilder(3).build()));

        row = -1;
        main.setWidget(++row, 1, inject(proto().totalLockers(), new FormDecoratorBuilder(3).build()));
        main.setWidget(++row, 1, inject(proto().regularLockers(), new FormDecoratorBuilder(3).build()));
        main.setWidget(++row, 1, inject(proto().largeLockers(), new FormDecoratorBuilder(3).build()));
        main.setWidget(++row, 1, inject(proto().smallLockers(), new FormDecoratorBuilder(3).build()));

        main.setWidget(++row, 0, 2, inject(proto().description(), new FormDecoratorBuilder(true).build()));

        return main;
    }
}