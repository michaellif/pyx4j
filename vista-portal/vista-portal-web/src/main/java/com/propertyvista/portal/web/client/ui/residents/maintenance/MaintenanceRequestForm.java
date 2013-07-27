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
package com.propertyvista.portal.web.client.ui.residents.maintenance;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTimeLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.MaintenanceRequestCategoryChoice;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestForm extends CEntityDecoratableForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestForm.class);

    private MaintenanceRequestMetadata meta;

    private final VerticalPanel categoryPanel = new VerticalPanel();

    private final VerticalPanel permissionPanel = new VerticalPanel();

    private final VerticalPanel accessPanel = new VerticalPanel();

    private final TwoColumnFlexFormPanel statusPanel = new TwoColumnFlexFormPanel();

    private final PrioritySelector prioritySelector = new PrioritySelector();

    private boolean choicesReady = false;

    public MaintenanceRequestForm() {
        super(MaintenanceRequestDTO.class, new VistaEditorsComponentFactory());
    }

    public void setMaintenanceRequestCategoryMeta(MaintenanceRequestMetadata meta) {
        this.meta = meta;
        initSelectors();

        // set value again in case meta comes after the form was populated
        if (getValue() != null) {
            setComponentsValue(getValue(), false, true);
        }
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().requestId(), new CLabel<String>()), 25).build());
        content.setBR(++row, 0, 1);
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().reportedForOwnUnit()), 25).build());
        content.setBR(++row, 0, 1);

        content.setWidget(++row, 0, categoryPanel);
        content.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);

        // Description
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().summary()), 25).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().description()), 25).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().priority(), prioritySelector), 25).build());
        content.setBR(++row, 0, 1);

        VerticalPanel schedulePanel = new VerticalPanel();
        schedulePanel.add(new FormDecoratorBuilder(inject(proto().preferredDate1()), 10).build());
        schedulePanel.add(new FormDecoratorBuilder(inject(proto().preferredTime1()), 10).build());
        schedulePanel.add(new FormDecoratorBuilder(inject(proto().preferredDate2()), 10).build());
        schedulePanel.add(new FormDecoratorBuilder(inject(proto().preferredTime2()), 10).build());

        accessPanel.add(new FormDecoratorBuilder(inject(proto().petInstructions()), 25).build());
        accessPanel.add(schedulePanel);

        permissionPanel.add(new FormDecoratorBuilder(inject(proto().permissionToEnter()), 25).build());
        permissionPanel.add(accessPanel);
        content.setWidget(++row, 0, permissionPanel);
        content.setBR(++row, 0, 1);

        int innerRow = -1;
        statusPanel.setH1(++innerRow, 0, 2, i18n.tr("Status"));
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().status(), new CEntityLabel<MaintenanceRequestStatus>()), 10).build());
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().updated(), new CDateLabel()), 10).build());
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().submitted(), new CDateLabel()), 10).build());
        statusPanel.setBR(++innerRow, 0, 1);
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().scheduledDate(), new CDateLabel()), 10).build());
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().scheduledTimeFrom(), new CTimeLabel()), 10).build());
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().scheduledTimeTo(), new CTimeLabel()), 10).build());
        content.setWidget(++row, 0, statusPanel);

        // tweaks:
        get(proto().reportedForOwnUnit()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                permissionPanel.setVisible(event.getValue());
            }
        });

        get(proto().permissionToEnter()).setNote(i18n.tr("To allow our service personnel to enter your apartment"));
        get(proto().permissionToEnter()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                accessPanel.setVisible(event.getValue());
            }
        });

        get(proto().petInstructions()).setNote(i18n.tr("Special instructions in case you have a pet in the apartment"));

        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (getValue() == null) {
            return;
        }

        if (isEditable()) {
            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.maintenance, get(proto().requestId()), getValue().getPrimaryKey());
        }

        StatusPhase phase = getValue().status().phase().getValue();
        get(proto().scheduledDate()).setVisible(phase == StatusPhase.Scheduled);
        get(proto().scheduledTimeFrom()).setVisible(phase == StatusPhase.Scheduled);
        get(proto().scheduledTimeTo()).setVisible(phase == StatusPhase.Scheduled);

        get(proto().submitted()).setVisible(!getValue().submitted().isNull());
        get(proto().updated()).setVisible(!getValue().updated().isNull());
        get(proto().status()).setVisible(!getValue().submitted().isNull());

        permissionPanel.setVisible(getValue().reportedForOwnUnit().isBooleanTrue());
        accessPanel.setVisible(getValue().permissionToEnter().isBooleanTrue());
        statusPanel.setVisible(!getValue().id().isNull());
    }

    public void initSelectors() {
        if (meta == null || choicesReady) {
            return;
        }

        prioritySelector.setOptions(meta.priorities());
        int levels = meta.categoryLevels().size();
        // create selectors
        MaintenanceRequestCategoryChoice child = null;
        MaintenanceRequestCategoryChoice mrCategory = null;
        for (int i = 0; i < levels; i++) {
            MaintenanceRequestCategoryChoice choice = new MaintenanceRequestCategoryChoice();
            String choiceLabel = EnglishGrammar.capitalize(meta.categoryLevels().get(levels - 1 - i).name().getValue());
            if (i == 0) {
                categoryPanel.insert(new FormDecoratorBuilder(inject(proto().category(), choice), 20).customLabel(choiceLabel).build(), 0);
                mrCategory = choice;
            } else {
                categoryPanel.insert(new FormDecoratorBuilder(choice, 20).customLabel(choiceLabel).build(), 0);
            }
            if (child != null) {
                child.assignParent(choice);
            }
            child = choice;
        }
        mrCategory.setOptionsMeta(meta);
        choicesReady = true;
    }

    class PrioritySelector extends CComboBox<MaintenanceRequestPriority> {
        @Override
        public String getItemName(MaintenanceRequestPriority o) {
            if (o == null) {
                return super.getItemName(o);
            } else {
                return o.getStringView();
            }
        }

        @Override
        public boolean isValuesEquals(MaintenanceRequestPriority value1, MaintenanceRequestPriority value2) {
            return value1 != null && value2 != null && value1.name().equals(value2.name());
        }
    }
}
