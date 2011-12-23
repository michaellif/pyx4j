/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.maintenance;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestEditorForm extends CrmEntityForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public MaintenanceRequestEditorForm(Class<MaintenanceRequestDTO> rootClass) {
        super(rootClass);
    }

    public MaintenanceRequestEditorForm(IEditableComponentFactory factory) {
        super(MaintenanceRequestDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    private Widget createGeneralTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().issue()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().priority()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 20).build());
        main.setBR(++row, 0, 2);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().surveyResponse().rating()), 20).build());

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().submited()), 10).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().status()), 10).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().updated()), 10).build());
        row++;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().surveyResponse().description()), 10).build());

        return new CrmScrollPanel(main);
    }
}
