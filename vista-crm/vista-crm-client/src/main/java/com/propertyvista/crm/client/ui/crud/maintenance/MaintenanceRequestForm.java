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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

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
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.MaintenanceRequestCategoryChoice;
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

    private FormFlexPanel statusPanel;

    private FormFlexPanel surveyPanel;

    private final BuildingSelector buildingSelector = new BuildingSelector();

    private final TenantSelector reporterSelector = new TenantSelector();

    private final UnitSelector unitSelector = new UnitSelector();

    private final PrioritySelector priority = new PrioritySelector();

    private final StatusSelector status = new StatusSelector();

    private MaintenanceRequestMetadata meta;

    private boolean choicesReady = false;

    public MaintenanceRequestForm(IForm<MaintenanceRequestDTO> view) {
        super(MaintenanceRequestDTO.class, view);
        selectTab(addTab(createGeneralTab()));
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

        panel.setH1(++row, 0, 2, i18n.tr("Issue Details"));

        VerticalPanel left = new VerticalPanel();
        left.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        panel.setWidget(++row, 0, left);

        left.add(new DecoratorBuilder(inject(proto().requestId()), 20).build());
        get(proto().requestId()).inheritViewable(false);
        get(proto().requestId()).setViewable(true);

        left.add(new DecoratorBuilder(inject(proto().building(), buildingSelector), 25).build());
        if (isEditable()) {
            left.add(new DecoratorBuilder(inject(proto().reporter(), reporterSelector), 25).build());
            buildingSelector.addValueChangeHandler(new ValueChangeHandler<Building>() {
                @Override
                public void onValueChange(ValueChangeEvent<Building> event) {
                    reporterSelector.setValue(null);
                }
            });
        } else {
            left.add(new DecoratorBuilder(inject(proto().reporterName()), 20).build());
        }
        left.add(new DecoratorBuilder(inject(proto().reporterPhone()), 20).build());
        left.add(new DecoratorBuilder(inject(proto().reporterEmail()), 20).build());
        left.add(new DecoratorBuilder(inject(proto().buildingElement(), unitSelector), 25).build());

        // create category selection panel
        categoryPanel = new VerticalPanel();
        categoryPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        left.add(categoryPanel);

        VerticalPanel right = new VerticalPanel();
        right.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        panel.setWidget(row, 1, right);
        panel.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
        panel.getCellFormatter().setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_TOP);

        right.add(new DecoratorBuilder(inject(proto().summary()), 20).build());
        right.add(new DecoratorBuilder(inject(proto().description()), 20).build());
        right.add(new DecoratorBuilder(inject(proto().priority(), priority), 10).build());

        VerticalPanel permPanel = new VerticalPanel();
        permPanel.add(new DecoratorBuilder(inject(proto().permissionToEnter()), 20).build());
        permPanel.add(new DecoratorBuilder(inject(proto().petInstructions()), 20).build());
        right.add(permPanel);
        get(proto().permissionToEnter()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().petInstructions()).setEnabled((getValue().permissionToEnter().isBooleanTrue()));
            }
        });
        get(proto().permissionToEnter()).setNote(i18n.tr("To allow our service personnel to enter your apartment"));
        get(proto().petInstructions()).setNote(i18n.tr("Special instructions in case you have a pet in the apartment"));

        statusPanel = new FormFlexPanel();
        panel.getFlexCellFormatter().setColSpan(++row, 0, 2);
        panel.setWidget(row, 0, statusPanel);
        {
            int innerRow = -1;
            statusPanel.setH1(++innerRow, 0, 2, i18n.tr("Status"));
            statusPanel.setWidget(++innerRow, 0, new DecoratorBuilder(inject(proto().status(), status), 10).build());
            statusPanel.setWidget(++innerRow, 0, new DecoratorBuilder(inject(proto().scheduledDate(), new CDateLabel()), 10).build());
            statusPanel.setWidget(++innerRow, 0, new DecoratorBuilder(inject(proto().scheduledTime(), new CTimeLabel()), 10).build());
            statusPanel.setWidget(++innerRow, 0, new DecoratorBuilder(inject(proto().updated(), new CDateLabel()), 10).build());
            statusPanel.setWidget(++innerRow, 0, new DecoratorBuilder(inject(proto().submitted(), new CDateLabel()), 10).build());

            get(proto().status()).setViewable(true);
        }

        surveyPanel = new FormFlexPanel();
        panel.getFlexCellFormatter().setColSpan(++row, 0, 2);
        panel.setWidget(row, 0, surveyPanel);
        {
            int innerRow = -1;
            surveyPanel.setH1(++innerRow, 0, 2, proto().surveyResponse().getMeta().getCaption());
            surveyPanel.setWidget(++innerRow, 0, new DecoratorBuilder(inject(proto().surveyResponse().rating(), new CLabel<Integer>()), 10).build());
            surveyPanel.setWidget(innerRow, 1, new DecoratorBuilder(inject(proto().surveyResponse().description(), new CLabel<String>()), 10).build());
        }

        panel.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);

        return panel;
    }

    private void initSelectors() {
        if (meta == null || choicesReady) {
            return;
        }
        priority.setOptions(meta.priorities());
        status.setOptions(meta.statuses());
        // create selectors
        int levels = meta.categoryLevels().size();
        MaintenanceRequestCategoryChoice child = null;
        MaintenanceRequestCategoryChoice mrCategory = null;
        for (int i = 0; i < levels; i++) {
            MaintenanceRequestCategoryChoice choice = new MaintenanceRequestCategoryChoice();
            String choiceLabel = meta.categoryLevels().get(levels - 1 - i).name().getValue();
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

    @Override
    protected void setNativeValue(MaintenanceRequestDTO value) {
        initSelectors();
        super.setNativeValue(value);
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

        StatusPhase phase = mr.status().phase().getValue();
        get(proto().scheduledDate()).setVisible(phase == StatusPhase.Scheduled);
        get(proto().scheduledTime()).setVisible(phase == StatusPhase.Scheduled);

        get(proto().submitted()).setVisible(!mr.submitted().isNull());
        get(proto().updated()).setVisible(!mr.updated().isNull());
        get(proto().status()).setVisible(!mr.submitted().isNull());

        statusPanel.setVisible(!mr.id().isNull());
        surveyPanel.setVisible(phase == StatusPhase.Resolved);

        if (isEditable()) {
            buildingSelector.setEditable(getValue().building().isNull());
            reporterSelector.setEditable(getValue().reporter().isNull());
        }

        get(proto().petInstructions()).setEnabled((getValue().permissionToEnter().isBooleanTrue()));
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
