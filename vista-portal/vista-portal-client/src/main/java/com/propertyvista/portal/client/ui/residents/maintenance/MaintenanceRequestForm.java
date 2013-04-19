/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 6, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.maintenance;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.MaintenanceRequestCategoryChoice;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategoryMeta;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestForm extends CEntityDecoratableForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestForm.class);

    private MaintenanceRequestCategoryMeta meta;

    private VerticalPanel categoryPanel;

    private boolean choicesReady = false;

    public MaintenanceRequestForm() {
        super(MaintenanceRequestDTO.class, new VistaEditorsComponentFactory());
    }

    public void setMaintenanceRequestCategoryMeta(MaintenanceRequestCategoryMeta meta) {
        this.meta = meta;
        initSelectors();
        // set value again in case meta comes after the form was populated
        if (getValue() != null) {
            setComponentsValue(getValue(), true, true);
        }
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;

        content.setBR(++row, 0, 1);

        categoryPanel = new VerticalPanel();
        content.setWidget(++row, 0, categoryPanel);
        content.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);

        // Description
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().permissionToEnter()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().petInstructions()), 25).build());

        get(proto().permissionToEnter()).setNote(i18n.tr("To allow our service personnel to enter your apartment"));
        get(proto().petInstructions()).setNote(i18n.tr("Special instructions in case you have a pet in the apartment"));

        return content;
    }

    public void initSelectors() {
        if (meta == null || choicesReady) {
            return;
        }
        int levels = meta.levels().size();
        // create selectors
        MaintenanceRequestCategoryChoice child = null;
        MaintenanceRequestCategoryChoice mrCategory = null;
        for (int i = 0; i < levels; i++) {
            MaintenanceRequestCategoryChoice choice = new MaintenanceRequestCategoryChoice();
            String choiceLabel = meta.levels().get(levels - 1 - i).name().getValue();
            if (i == 0) {
                categoryPanel.insert(new DecoratorBuilder(inject(proto().category(), choice), 20).customLabel(choiceLabel).build(), 0);
                mrCategory = choice;
            } else {
                categoryPanel.insert(new DecoratorBuilder(choice, 20).customLabel(choiceLabel).build(), 0);
            }
            if (child != null) {
                child.assignParent(choice);
            }
            child = choice;
        }
        mrCategory.setOptionsMeta(meta);
        choicesReady = true;
    }
}
