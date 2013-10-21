/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.maintenance;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTimeLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.MaintenanceRequestCategoryChoice;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.web.client.ui.CPortalEntityWizard;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class MaintenanceRequestWizard extends CPortalEntityWizard<MaintenanceRequestDTO> {

    private final static I18n i18n = I18n.get(MaintenanceRequestWizard.class);

    private MaintenanceRequestMetadata meta;

    private MaintenanceRequestCategoryChoice mrCategory;

    private final BasicFlexFormPanel categoryPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel permissionPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel accessPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel statusPanel = new BasicFlexFormPanel();

    private final PrioritySelector prioritySelector = new PrioritySelector();

    public MaintenanceRequestWizard(MaintenanceRequestWizardView view) {
        super(MaintenanceRequestDTO.class, view, i18n.tr("New Maintenance Request"), i18n.tr("Submit"), ThemeColor.contrast5);

        addStep(createDetailsStep());
    }

    public void setMaintenanceRequestCategoryMeta(MaintenanceRequestMetadata meta) {
        if (meta == null || meta.isNull()) {
            throw new RuntimeException(i18n.tr("Maintenance Metadata not configured."));
        }
        this.meta = meta;
        initSelectors();
    }

    public TwoColumnFlexFormPanel createDetailsStep() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().requestId(), new CLabel<String>()), 250).build());
        content.setBR(++row, 0, 1);
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().reportedForOwnUnit()), 250).build());
        content.setBR(++row, 0, 1);

        // category panel
        mrCategory = new MaintenanceRequestCategoryChoice();
        bind(mrCategory, proto().category());
        content.setWidget(++row, 0, categoryPanel);
        content.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);

        // Description
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().summary()), 250).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().description()), 250).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().priority(), prioritySelector), 250).build());
        content.setBR(++row, 0, 1);

        TwoColumnFlexFormPanel schedulePanel = new TwoColumnFlexFormPanel();
        schedulePanel.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().preferredDate1()), 110).build());
        schedulePanel.setWidget(1, 0, new FormDecoratorBuilder(inject(proto().preferredTime1()), 110).build());
        schedulePanel.setWidget(2, 0, new FormDecoratorBuilder(inject(proto().preferredDate2()), 110).build());
        schedulePanel.setWidget(3, 0, new FormDecoratorBuilder(inject(proto().preferredTime2()), 110).build());

        accessPanel.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().petInstructions()), 250).build());
        accessPanel.setWidget(1, 0, schedulePanel);

        permissionPanel.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().permissionToEnter()), 250).build());
        permissionPanel.setWidget(1, 0, accessPanel);
        content.setWidget(++row, 0, permissionPanel);
        content.setBR(++row, 0, 1);

        int innerRow = -1;
        statusPanel.setH1(++innerRow, 0, 1, i18n.tr("Status"));
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().status(), new CEntityLabel<MaintenanceRequestStatus>()), 100).build());
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().updated(), new CDateLabel()), 100).build());
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().submitted(), new CDateLabel()), 100).build());
        statusPanel.setBR(++innerRow, 0, 1);
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().scheduledDate(), new CDateLabel()), 100).build());
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().scheduledTimeFrom(), new CTimeLabel()), 100).build());
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().scheduledTimeTo(), new CTimeLabel()), 100).build());
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
    protected MaintenanceRequestDTO preprocessValue(MaintenanceRequestDTO value, boolean fireEvent, boolean populate) {
        if (value.reportedForOwnUnit().isNull()) {
            value.reportedForOwnUnit().setValue(true);
        }
        if (value.permissionToEnter().isNull()) {
            value.permissionToEnter().setValue(true);
        }
        return value;
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
        if (meta == null || meta.isNull()) {
            return;
        }

        prioritySelector.setOptions(meta.priorities());

        // create category selectors - bottom-up
        int levels = meta.categoryLevels().size();
        if (levels < 1) {
            throw new RuntimeException(i18n.tr("Invalid Maintenance configuration: no category levels found."));
        }
        MaintenanceRequestCategoryChoice choice = null;
        for (int i = 0; i < levels; i++) {
            if (i == 0) {
                choice = mrCategory;
            } else {
                MaintenanceRequestCategoryChoice parent = new MaintenanceRequestCategoryChoice();
                choice.assignParent(parent);
                choice = parent;
            }
            choice.setViewable(isViewable());
            choice.setTitle(EnglishGrammar.capitalize(meta.categoryLevels().get(levels - 1 - i).name().getValue()));
        }
        if (!isViewable()) {
            // set options and re-populate
            mrCategory.setOptionsMeta(meta);
        }
        // re-populate after parent categories have been added
        if (getValue() != null) {
            mrCategory.populate(getValue().category());
        }

        // attach selectors to the panel - bottom up
        categoryPanel.clear();
        int row = levels;
        for (choice = mrCategory; choice != null; choice = choice.getParentSelector()) {
            categoryPanel.setWidget(--row, 0, new FormDecoratorBuilder(choice, 250).build());
        }
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

        @Override
        public boolean isValueEmpty() {
            return getValue() == null || getValue().isNull();
        }
    }
}