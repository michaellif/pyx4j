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

import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTimeLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.MaintenanceRequestCategoryChoice;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.TenantSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestForm extends CrmEntityForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestForm.class);

    private VerticalPanel categoryPanel;

    private FormFlexPanel accessPanel;

    private FormFlexPanel statusPanel;

    private FormFlexPanel surveyPanel;

    private final BuildingSelector buildingSelector = new BuildingSelector();

    private final TenantSelector reporterSelector = new TenantSelector();

    private final UnitSelector unitSelector = new UnitSelector();

    private final PrioritySelector prioritySelector = new PrioritySelector();

    private final StatusSelector statusSelector = new StatusSelector();

    private MaintenanceRequestMetadata meta;

    private boolean choicesReady = false;

    public MaintenanceRequestForm(IForm<MaintenanceRequestDTO> view) {
        super(MaintenanceRequestDTO.class, view);
        selectTab(addTab(createGeneralTab()));
        initSelectors();

    }

    public void setMaintenanceRequestCategoryMeta(MaintenanceRequestMetadata meta) {
        this.meta = meta;
        initSelectors();

        // set value again in case meta comes after the form was populated
        if (getValue() != null) {
            setComponentsValue(getValue(), false, true);
        }
    }

    private FormFlexPanel createGeneralTab() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("General"));
        int row = -1;

        VerticalPanel left = new VerticalPanel();
        left.add(new FormDecoratorBuilder(inject(proto().requestId(), new CLabel<String>()), 25).build());
        left.add(new HTML("&nbsp"));
        left.add(new FormDecoratorBuilder(inject(proto().building(), buildingSelector), 25).build());
        buildingSelector.addValueChangeHandler(new ValueChangeHandler<Building>() {
            @Override
            public void onValueChange(ValueChangeEvent<Building> event) {
                unitSelector.setValue(null);
                reporterSelector.setValue(null);
            }
        });
        left.add(new FormDecoratorBuilder(inject(proto().unit(), unitSelector), 25).build());
        left.add(new FormDecoratorBuilder(inject(proto().reporter(), reporterSelector), 25).build());

        // --------------------------------------------------------------------------------------------------------------------
        VerticalPanel right = new VerticalPanel();
        categoryPanel = new VerticalPanel();
        right.add(categoryPanel);
        right.add(new HTML("&nbsp"));
        right.add(new FormDecoratorBuilder(inject(proto().summary()), 25).build());
        right.add(new FormDecoratorBuilder(inject(proto().description()), 25).build());
        right.add(new FormDecoratorBuilder(inject(proto().priority(), prioritySelector), 25).build());

        // --------------------------------------------------------------------------------------------------------------------

        panel.setH1(++row, 0, 2, i18n.tr("Issue Details"));
        panel.setWidget(++row, 0, left);
        panel.setWidget(row, 1, right);
        panel.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
//        panel.getCellFormatter().setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_TOP);

        // --------------------------------------------------------------------------------------------------------------------

        panel.setH1(++row, 0, 2, i18n.tr("Unit Access"));
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().permissionToEnter()), 25).build());
        get(proto().permissionToEnter()).setNote(i18n.tr("To allow our service personnel to enter your apartment"));
        get(proto().permissionToEnter()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                accessPanel.setVisible(event.getValue());
            }
        });

        // --------------------------------------------------------------------------------------------------------------------

        VerticalPanel permissionPanel = new VerticalPanel();

        permissionPanel.add(new FormDecoratorBuilder(inject(proto().petInstructions()), 25).build());
        get(proto().petInstructions()).setNote(i18n.tr("Special instructions in case you have a pet in the apartment"));
        // --------------------------------------------------------------------------------------------------------------------
        VerticalPanel schedulePanel = new VerticalPanel();

        schedulePanel.add(new FormDecoratorBuilder(inject(proto().preferredDate1()), 10).build());
        schedulePanel.add(new FormDecoratorBuilder(inject(proto().preferredTime1()), 10).build());
        schedulePanel.add(new FormDecoratorBuilder(inject(proto().preferredDate2()), 10).build());
        schedulePanel.add(new FormDecoratorBuilder(inject(proto().preferredTime2()), 10).build());
        // --------------------------------------------------------------------------------------------------------------------
        accessPanel = new FormFlexPanel();

        accessPanel.setWidget(1, 0, permissionPanel);
        accessPanel.setWidget(1, 1, schedulePanel);

        panel.setWidget(++row, 0, accessPanel);
        panel.getFlexCellFormatter().setColSpan(row, 0, 2);

        // --------------------------------------------------------------------------------------------------------------------

        statusPanel = new FormFlexPanel();
        int innerRow = -1;

        statusPanel.setH1(++innerRow, 0, 2, i18n.tr("Status"));
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().status(), statusSelector), 10).build());
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().updated(), new CDateLabel()), 10).build());
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().submitted(), new CDateLabel()), 10).build());

        innerRow = 0;
        statusPanel.setWidget(++innerRow, 1, new FormDecoratorBuilder(inject(proto().scheduledDate(), new CDateLabel()), 10).build());
        statusPanel.setWidget(++innerRow, 1, new FormDecoratorBuilder(inject(proto().scheduledTime(), new CTimeLabel()), 10).build());

        panel.setWidget(++row, 0, statusPanel);
        panel.getFlexCellFormatter().setColSpan(row, 0, 2);

        get(proto().status()).setViewable(true);

        // --------------------------------------------------------------------------------------------------------------------

        surveyPanel = new FormFlexPanel();

        surveyPanel.setH1(++innerRow, 0, 2, proto().surveyResponse().getMeta().getCaption());
        surveyPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().surveyResponse().rating(), new CLabel<Integer>()), 10).build());
        surveyPanel.setWidget(innerRow, 1, new FormDecoratorBuilder(inject(proto().surveyResponse().description(), new CLabel<String>()), 10).build());

        panel.setWidget(++row, 0, surveyPanel);
        panel.getFlexCellFormatter().setColSpan(row, 0, 2);

        // --------------------------------------------------------------------------------------------------------------------

        return panel;
    }

    private void initSelectors() {
        if (meta == null || choicesReady) {
            return;
        }
        prioritySelector.setOptions(meta.priorities());
        statusSelector.setOptions(meta.statuses());
        // create selectors
        int levels = meta.categoryLevels().size();
        MaintenanceRequestCategoryChoice child = null;
        MaintenanceRequestCategoryChoice mrCategory = null;
        for (int i = 0; i < levels; i++) {
            MaintenanceRequestCategoryChoice choice = new MaintenanceRequestCategoryChoice();
            String choiceLabel = EnglishGrammar.capitalize(meta.categoryLevels().get(levels - 1 - i).name().getValue());
            if (i == 0) {
                categoryPanel.insert(new FormDecoratorBuilder(inject(proto().category(), choice), 25).customLabel(choiceLabel).build(), 0);
                mrCategory = choice;
            } else {
                categoryPanel.insert(new FormDecoratorBuilder(choice, 25).customLabel(choiceLabel).build(), 0);
            }
            if (child != null) {
                child.assignParent(choice);
            }
            child = choice;
        }
        mrCategory.setOptionsMeta(meta);
        choicesReady = true;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isEditable()) {
            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.maintenance, get(proto().requestId()), getValue().getPrimaryKey());
        }

        MaintenanceRequestDTO mr = getValue();
        if (mr == null) {
            return;
        }

        if (isEditable()) {
//            buildingSelector.setEditable(getValue().building().isNull());
//            reporterSelector.setEditable(getValue().reporter().isNull());
        }

        StatusPhase phase = mr.status().phase().getValue();
        get(proto().scheduledDate()).setVisible(phase == StatusPhase.Scheduled);
        get(proto().scheduledTime()).setVisible(phase == StatusPhase.Scheduled);

        get(proto().submitted()).setVisible(!mr.submitted().isNull());
        get(proto().updated()).setVisible(!mr.updated().isNull());
        get(proto().status()).setVisible(!mr.submitted().isNull());

        statusPanel.setVisible(!mr.id().isNull());
        surveyPanel.setVisible(phase == StatusPhase.Resolved);
        accessPanel.setVisible(getValue().permissionToEnter().isBooleanTrue());
    }

    class BuildingSelector extends CEntitySelectorHyperlink<Building> {
        @Override
        protected AppPlace getTargetPlace() {
            return AppPlaceEntityMapper.resolvePlace(Building.class, getValue().getPrimaryKey());
        }

        @Override
        protected BuildingSelectorDialog getSelectorDialog() {
            return new BuildingSelectorDialog(false) {

                @Override
                public boolean onClickOk() {
                    if (getSelectedItems().isEmpty()) {
                        return false;
                    }
                    setValue(getSelectedItems().get(0));
                    return true;
                }
            };
        }
    }

    class TenantSelector extends CEntitySelectorHyperlink<Tenant> {
        @Override
        protected AppPlace getTargetPlace() {
            return AppPlaceEntityMapper.resolvePlace(Tenant.class, getValue().getPrimaryKey());
        }

        @Override
        protected TenantSelectorDialog getSelectorDialog() {
            return new TenantSelectorDialog(false) {

                @Override
                public boolean onClickOk() {
                    if (getSelectedItems().isEmpty()) {
                        return false;
                    }
                    setValue(getSelectedItems().get(0));
                    return true;
                }

                @Override
                protected void setFilters(List<Criterion> filters) {
                    super.setFilters(filters);
                    // add building filter if value set
                    Building building = MaintenanceRequestForm.this.getValue().building();
                    if (!building.isNull()) {
                        addFilter(PropertyCriterion.eq(proto().lease().unit().building(), building));
                    }
                }
            };
        }
    }

    class UnitSelector extends CEntitySelectorHyperlink<AptUnit> {
        @Override
        protected AppPlace getTargetPlace() {
            return AppPlaceEntityMapper.resolvePlace(AptUnit.class, getValue().getPrimaryKey());
        }

        @Override
        protected UnitSelectorDialog getSelectorDialog() {
            return new UnitSelectorDialog(false) {

                @Override
                public boolean onClickOk() {
                    if (getSelectedItems().isEmpty()) {
                        return false;
                    }
                    setValue(getSelectedItems().get(0));
                    return true;
                }

                @Override
                protected void setFilters(List<Criterion> filters) {
                    super.setFilters(filters);
                    // add building filter if value set
                    Building building = MaintenanceRequestForm.this.getValue().building();
                    if (!building.isNull()) {
                        addFilter(PropertyCriterion.eq(proto().building(), building));
                    }
                }
            };
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
    }

    class StatusSelector extends CComboBox<MaintenanceRequestStatus> {
        @Override
        public String getItemName(MaintenanceRequestStatus o) {
            if (o == null) {
                return super.getItemName(o);
            } else {
                return o.getStringView();
            }
        }

        @Override
        public boolean isValuesEquals(MaintenanceRequestStatus value1, MaintenanceRequestStatus value2) {
            return value1 != null && value2 != null && value1.name().equals(value2.name());
        }
    }
}
