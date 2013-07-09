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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
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

    private FormFlexPanel createDetailsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name()), 15).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().description()), 25).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().levels()), 3).build());

        row = -1;
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().totalLockers()), 3).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().regularLockers()), 3).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().largeLockers()), 3).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().smallLockers()), 3).build());

        return main;
    }
}