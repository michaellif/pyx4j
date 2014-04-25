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

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.LockerAreaDTO;

public class LockerAreaForm extends CrmEntityForm<LockerAreaDTO> {

    private static final I18n i18n = I18n.get(LockerAreaForm.class);

    public LockerAreaForm(IForm<LockerAreaDTO> view) {
        super(LockerAreaDTO.class, view);

        Tab tab = addTab(createDetailsTab(), i18n.tr("Details"));
        selectTab(tab);

        tab = addTab(isEditable() ? new HTML() : ((LockerAreaViewerView) getParentView()).getLockerView().asWidget(), i18n.tr("Lockers"));
        setTabEnabled(tab, !isEditable());
    }

    private TwoColumnFlexFormPanel createDetailsTab() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setWidget(++row, 0, injectAndDecorate(proto().name(), 15));
        main.setWidget(++row, 0, injectAndDecorate(proto().levels(), 3));

        row = -1;
        main.setWidget(++row, 1, injectAndDecorate(proto().totalLockers(), 3));
        main.setWidget(++row, 1, injectAndDecorate(proto().regularLockers(), 3));
        main.setWidget(++row, 1, injectAndDecorate(proto().largeLockers(), 3));
        main.setWidget(++row, 1, injectAndDecorate(proto().smallLockers(), 3));

        main.setWidget(++row, 0, 2, injectAndDecorate(proto().description(), true));

        return main;
    }
}