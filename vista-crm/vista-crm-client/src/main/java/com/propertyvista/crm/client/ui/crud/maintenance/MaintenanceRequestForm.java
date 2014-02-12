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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CImageSlider;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTimeLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.ItemActionsBar.ActionType;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IEditor;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;
import com.pyx4j.widgets.client.images.HelperImages;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.MaintenanceRequestCategoryChoice;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.FutureDateValidator;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.TenantSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.MaintenanceRequestPictureUploadService;
import com.propertyvista.domain.maintenance.MaintenanceRequest.ContactPhoneType;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestPicture;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestSchedule;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatusRecord;
import com.propertyvista.domain.maintenance.NoticeOfEntry;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class MaintenanceRequestForm extends CrmEntityForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestForm.class);

    private BasicFlexFormPanel categoryPanel;

    private TwoColumnFlexFormPanel accessPanel;

    private TwoColumnFlexFormPanel unitAccessPanel;

    private TwoColumnFlexFormPanel statusPanel;

    private TwoColumnFlexFormPanel scheduledPanel;

    private TwoColumnFlexFormPanel resolvedPanel;

    private TwoColumnFlexFormPanel surveyPanel;

    private final BuildingSelector buildingSelector = new BuildingSelector();

    private final TenantSelector reporterSelector = new TenantSelector();

    private final UnitSelector unitSelector = new UnitSelector();

    private final PrioritySelector prioritySelector = new PrioritySelector();

    private MaintenanceRequestMetadata meta;

    private MaintenanceRequestCategoryChoice mrCategory;

    private final ValueChangeScope valueChangeScope = new ValueChangeScope();

    // TODO - YARDI: per building cache is easy to implement with current data model, but it is INEFFICIENT since
    // multiple buildings that belong to the same Yardi account will share the same Meta
    private final Map<String, MaintenanceRequestMetadata> categoryMetaCache = new HashMap<String, MaintenanceRequestMetadata>();

    public MaintenanceRequestForm(IForm<MaintenanceRequestDTO> view) {
        super(MaintenanceRequestDTO.class, view);

        selectTab(addTab(createGeneralTab()));
        addTab(createWorkHistoryTab());
        addTab(createStatusHistoryTab());
    }

    private void ensureBuilding() {
        if (getValue() == null) {
            return;
        }

        Building building = getValue().building();
        if (building.isNull() && VistaFeatures.instance().yardiInterfaces() > 1) {
            // for multiple yardi interfaces ask to select building first
            buildingSelector.getSelectorDialog(false).show();
        } else {
            setMaintenanceRequestCategoryMeta();
        }
    }

    private void setMaintenanceRequestCategoryMeta() {
        Building bld = getValue().building();
        final String buildingCode = bld.propertyCode().getValue();
        MaintenanceRequestMetadata meta = categoryMetaCache.get(buildingCode);
        if (meta == null) {
            DefaultAsyncCallback<MaintenanceRequestMetadata> callback = new DefaultAsyncCallback<MaintenanceRequestMetadata>() {
                @Override
                public void onSuccess(MaintenanceRequestMetadata meta) {
                    if (!meta.rootCategory().isNull()) {
                        MaintenanceRequestForm.this.categoryMetaCache.put(buildingCode, meta);
                    }
                    MaintenanceRequestForm.this.meta = meta;
                    initSelectors();
                }
            };
            if (getParentView() instanceof IEditor) {
                ((MaintenanceRequestEditorView.Presenter) getParentView().getPresenter()).getCategoryMeta(callback, bld.getPrimaryKey());
            } else {
                ((MaintenanceRequestViewerView.Presenter) getParentView().getPresenter()).getCategoryMeta(callback, bld.getPrimaryKey());
            }
        } else {
            this.meta = meta;
            initSelectors();
        }
    }

    private TwoColumnFlexFormPanel createGeneralTab() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("General"));
        int row = -1;

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().reportedDate()), 10).build());
        panel.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().requestId()), 20).build());

        panel.setH1(++row, 0, 2, i18n.tr("Issue Location"));

        int rowAnchor = row;
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().reportedForOwnUnit()), 20).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().reporter(), reporterSelector), 20).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().reporterPhone()), 20).build());
        panel.getRowFormatter().setVerticalAlign(row, HasVerticalAlignment.ALIGN_TOP);
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().phoneType()), 20).build());
        panel.getRowFormatter().setVerticalAlign(row, HasVerticalAlignment.ALIGN_TOP);
        panel.getFlexCellFormatter().setRowSpan(row, 0, 2);
        panel.getCellFormatter().setHeight(row, 0, "100%");

        row = rowAnchor;
        panel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().unit(), unitSelector), 20).build());
        panel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().building(), buildingSelector), 20).build());
        get(proto().reporter()).addValueChangeHandler(new ValueChangeHandler<Tenant>() {
            @Override
            public void onValueChange(ValueChangeEvent<Tenant> event) {
                String phone = null;
                ContactPhoneType type = null;
                Tenant reporter = event.getValue();
                if (reporter != null) {
                    phone = reporter.customer().person().mobilePhone().getStringView();
                    type = ContactPhoneType.mobile;
                    if (phone == null) {
                        phone = reporter.customer().person().homePhone().getStringView();
                        type = ContactPhoneType.home;
                        if (phone == null) {
                            phone = reporter.customer().person().workPhone().getStringView();
                            type = ContactPhoneType.work;
                        }
                    }
                }
                get(proto().reporterPhone()).setValue(phone);
                get(proto().phoneType()).setValue(type);
            }
        });

        // category panel
        mrCategory = new MaintenanceRequestCategoryChoice();
        bind(mrCategory, proto().category());
        categoryPanel = new BasicFlexFormPanel();
        panel.setWidget(++row, 1, categoryPanel);
        panel.getFlexCellFormatter().setRowSpan(row, 1, 3);
        row += 2;

        panel.setH1(++row, 0, 2, i18n.tr("Issue Details"));
        rowAnchor = row;
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().priority(), prioritySelector), 20).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().summary()), 30).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().description()), 30).build());
        panel.getRowFormatter().setVerticalAlign(row, HasVerticalAlignment.ALIGN_TOP);
        panel.getFlexCellFormatter().setRowSpan(row, 0, 2);

        // --------------------------------------------------------------------------------------------------------------------
        row = rowAnchor;
        CImageSlider<MaintenanceRequestPicture> imageSlider = new CImageSlider<MaintenanceRequestPicture>(MaintenanceRequestPicture.class,
                GWT.<MaintenanceRequestPictureUploadService> create(MaintenanceRequestPictureUploadService.class), new VistaFileURLBuilder(
                        MaintenanceRequestPicture.class)) {
            @Override
            protected EntityFolderImages getFolderIcons() {
                return VistaImages.INSTANCE;
            }

            @Override
            public Widget getImageEntryView(CEntityForm<MaintenanceRequestPicture> entryForm) {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
                main.setWidget(0, 0, 2, new FormDecoratorBuilder(entryForm.inject(entryForm.proto().description()), 8, 15, 16).build());

                return main;
            }
        };
        imageSlider.setImageSize(320, 240);
        panel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().pictures(), imageSlider), 32).build());
        panel.getFlexCellFormatter().setRowSpan(row, 1, 4);
        row += 3;

        // --------------------------------------------------------------------------------------------------------------------
        unitAccessPanel = new TwoColumnFlexFormPanel();
        int innerRow = -1;
        unitAccessPanel.setH1(++innerRow, 0, 2, i18n.tr("Unit Access"));
        unitAccessPanel.setWidget(++innerRow, 0, 2, new FormDecoratorBuilder(inject(proto().permissionToEnter()), 20, true).build());
        unitAccessPanel.setWidget(++innerRow, 0, 2, accessPanel = new TwoColumnFlexFormPanel());

        get(proto().permissionToEnter()).setNote(i18n.tr("Indicate whether Permission to Enter has been granted by Tenant."));
        get(proto().permissionToEnter()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                accessPanel.setVisible(event.getValue());
            }
        });

        // --------------------------------------------------------------------------------------------------------------------
        innerRow = -1;
        accessPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().preferredDate1()), 10).build());
        accessPanel.setWidget(innerRow, 1, new FormDecoratorBuilder(inject(proto().preferredDate2()), 10).build());
        accessPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().preferredTime1()), 10).build());
        accessPanel.setWidget(innerRow, 1, new FormDecoratorBuilder(inject(proto().preferredTime2()), 10).build());
        get(proto().preferredDate1()).addValueValidator(new FutureDateValidator());
        get(proto().preferredDate2()).addValueValidator(new FutureDateValidator());

        accessPanel.setWidget(++innerRow, 0, 2, new FormDecoratorBuilder(inject(proto().petInstructions()), 50, true)
                .customLabel(i18n.tr("Entry Instructions")).build());
        get(proto().petInstructions()).setNote(i18n.tr("Entry instructions, including Pet Warnings, etc"));

        panel.setWidget(++row, 0, 2, unitAccessPanel);

        // --------------------------------------------------------------------------------------------------------------------
        statusPanel = new TwoColumnFlexFormPanel();
        innerRow = -1;
        statusPanel.setH1(++innerRow, 0, 2, i18n.tr("Status"));
        rowAnchor = innerRow;
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().status(), new CEntityLabel<MaintenanceRequestStatus>()), 10).build());
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().updated(), new CDateLabel()), 10).build());
        statusPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().submitted(), new CDateLabel()), 10).build());

        FlowPanel detailHolder = new FlowPanel();
        statusPanel.setWidget(++rowAnchor, 1, detailHolder);
        statusPanel.getFlexCellFormatter().setRowSpan(rowAnchor, 1, innerRow - rowAnchor + 1);
        statusPanel.getFlexCellFormatter().getElement(rowAnchor, 1).getStyle().setVerticalAlign(VerticalAlign.TOP);

        // --------------------------------------------------------------------------------------------------------------------
        scheduledPanel = new TwoColumnFlexFormPanel();
        innerRow = -1;
        scheduledPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().scheduledDate(), new CDateLabel()), 10).build());
        scheduledPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().scheduledTimeFrom(), new CTimeLabel()), 10).build());
        scheduledPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().scheduledTimeTo(), new CTimeLabel()), 10).build());

        // --------------------------------------------------------------------------------------------------------------------
        resolvedPanel = new TwoColumnFlexFormPanel();
        innerRow = -1;
        resolvedPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().resolvedDate(), new CDateLabel()), 10).build());
        resolvedPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().resolution(), new CLabel<String>()), 40).build());

        detailHolder.add(scheduledPanel);
        detailHolder.add(resolvedPanel);

        panel.setWidget(++row, 0, 2, statusPanel);

        // --------------------------------------------------------------------------------------------------------------------
        surveyPanel = new TwoColumnFlexFormPanel();
        innerRow = -1;
        surveyPanel.setH1(++innerRow, 0, 2, proto().surveyResponse().getMeta().getCaption());
        surveyPanel.setWidget(++innerRow, 0, new FormDecoratorBuilder(inject(proto().surveyResponse().rating(), new CLabel<Integer>()), 10).build());
        surveyPanel.setWidget(innerRow, 1, new FormDecoratorBuilder(inject(proto().surveyResponse().description(), new CLabel<String>()), 10).build());

        panel.setWidget(++row, 0, 2, surveyPanel);

        // --------------------------------------------------------------------------------------------------------------------
        unitSelector.addValueChangeHandler(new ValueChangeHandler<AptUnit>() {
            @Override
            public void onValueChange(ValueChangeEvent<AptUnit> event) {
                // prevent mutual reset
                valueChangeScope.setScope(unitSelector);
                AptUnit unit = event.getValue();
                if (unit != null) {
                    if (!valueChangeScope.inScope(buildingSelector)) {
                        buildingSelector.setValue(unit.building());
                    }
                    if (!valueChangeScope.inScope(reporterSelector)) {
                        reporterSelector.setValue(null);
                    }
                    setMaintenanceRequestCategoryMeta();
                }
                valueChangeScope.clearScope(unitSelector);
            }
        });
        buildingSelector.addValueChangeHandler(new ValueChangeHandler<Building>() {
            @Override
            public void onValueChange(ValueChangeEvent<Building> event) {
                valueChangeScope.setScope(buildingSelector);
                if (!valueChangeScope.inScope(unitSelector)) {
                    unitSelector.setValue(null);
                }
                if (!valueChangeScope.inScope(reporterSelector)) {
                    reporterSelector.setValue(null);
                }
                setMaintenanceRequestCategoryMeta();
                valueChangeScope.clearScope(buildingSelector);
            }
        });
        reporterSelector.addValueChangeHandler(new ValueChangeHandler<Tenant>() {
            @Override
            public void onValueChange(ValueChangeEvent<Tenant> event) {
                valueChangeScope.setScope(reporterSelector);
                Tenant tenant = event.getValue();
                if (tenant != null) {
                    if (!valueChangeScope.inScope(unitSelector)) {
                        unitSelector.setValue(tenant.lease().unit());
                    }
                    setMaintenanceRequestCategoryMeta();
                }
                valueChangeScope.clearScope(reporterSelector);
            }
        });

        get(proto().reportedForOwnUnit()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (!event.getValue().booleanValue()) {
                    unitSelector.setValue(null);
                }
                unitSelector.setVisible(event.getValue().booleanValue());
                get(proto().permissionToEnter()).setValue(event.getValue());
                unitAccessPanel.setVisible(event.getValue().booleanValue());
                getValue().category().set(null);
                accessPanel.setVisible(getValue().permissionToEnter().isBooleanTrue() && event.getValue().booleanValue());
                setMaintenanceRequestCategoryMeta();
            }
        });

        return panel;
    }

    @Override
    protected MaintenanceRequestDTO preprocessValue(MaintenanceRequestDTO value, boolean fireEvent, boolean populate) {
        if (value == null || value.getPrimaryKey() == null || value.getPrimaryKey().isDraft()) {
            if (value.reportedForOwnUnit().isNull()) {
                value.reportedForOwnUnit().setValue(true);
            }
            if (value.permissionToEnter().isNull()) {
                value.permissionToEnter().setValue(value.reportedForOwnUnit().isBooleanTrue()); // according reportedForOwnUnit
            }
        }
        return value;
    }

    private TwoColumnFlexFormPanel createWorkHistoryTab() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(proto().workHistory().getMeta().getCaption());
        panel.setWidget(0, 0, 2, inject(proto().workHistory(), new MaintenanceRequestScheduleFolder()));
        return panel;
    }

    private TwoColumnFlexFormPanel createStatusHistoryTab() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(proto().statusHistory().getMeta().getCaption());
        panel.setWidget(0, 0, 2, inject(proto().statusHistory(), new StatusHistoryFolder()));
        return panel;
    }

    private void initSelectors() {
        if (meta == null || meta.isNull()) {
            return;
        }

        prioritySelector.setOptions(meta.priorities());

        // create category selectors - bottom-up
        int levels = meta.categoryLevels().size();
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
            // set options
            mrCategory.setOptionsMeta(meta, getValue().reportedForOwnUnit().isNull() ? true : getValue().reportedForOwnUnit().isBooleanTrue());
        }
        // re-populate after parent categories have been added
        if (getValue() != null) {
            mrCategory.populate(getValue().category());
        }

        // attach selectors to the panel - bottom up
        categoryPanel.clear();
        int row = levels;
        for (choice = mrCategory; choice != null; choice = choice.getParentSelector()) {
            categoryPanel.setWidget(--row, 0, new FormDecoratorBuilder(choice, 20).build());
        }
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

        // to support yardi mode with multiple interfaces
        ensureBuilding();

        get(proto().reportedDate()).setEditable(mr.id().isNull());

        get(proto().submitted()).setVisible(!mr.submitted().isNull());
        get(proto().updated()).setVisible(!mr.updated().isNull());
        get(proto().status()).setVisible(!mr.submitted().isNull());

        get(proto().pictures()).setVisible(isEditable() || !mr.pictures().isEmpty());

        StatusPhase phase = mr.status().phase().getValue();

        scheduledPanel.setVisible(phase == StatusPhase.Scheduled);
        resolvedPanel.setVisible(phase == StatusPhase.Resolved);

        statusPanel.setVisible(!mr.id().isNull());
        surveyPanel.setVisible(phase == StatusPhase.Resolved);

        unitSelector.setVisible(mr.reportedForOwnUnit().isBooleanTrue());
        unitAccessPanel.setVisible(mr.reportedForOwnUnit().isBooleanTrue());
        accessPanel.setVisible(mr.permissionToEnter().isBooleanTrue() && getValue().reportedForOwnUnit().isBooleanTrue());
        setMaintenanceRequestCategoryMeta();
    }

    class BuildingSelector extends CEntitySelectorHyperlink<Building> {
        @Override
        protected AppPlace getTargetPlace() {
            return AppPlaceEntityMapper.resolvePlace(Building.class, getValue().getPrimaryKey());
        }

        @Override
        protected BuildingSelectorDialog getSelectorDialog() {
            return getSelectorDialog(true);
        }

        public BuildingSelectorDialog getSelectorDialog(boolean allowCancel) {
            BuildingSelectorDialog buildingDialog = new BuildingSelectorDialog(false) {
                @Override
                public boolean onClickOk() {
                    if (getSelectedItems().isEmpty()) {
                        return false;
                    }
                    setValue(getSelectedItems().get(0));
                    setMaintenanceRequestCategoryMeta();
                    return true;
                }
            };
            buildingDialog.getCancelButton().setVisible(allowCancel);

            return buildingDialog;
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

                    // add unit/building filter if value set
                    AptUnit unit = MaintenanceRequestForm.this.getValue().unit();
                    if (!unit.isNull()) {
                        addFilter(PropertyCriterion.eq(proto().lease().unit(), unit));
                    } else {
                        Building building = MaintenanceRequestForm.this.getValue().building();
                        if (!building.isNull()) {
                            addFilter(PropertyCriterion.eq(proto().lease().unit().building(), building));
                        }
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

        @Override
        public boolean isValueEmpty() {
            return getValue() == null || getValue().isNull();
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

        @Override
        public boolean isValueEmpty() {
            return getValue() == null || getValue().isNull();
        }
    }

    class MaintenanceRequestScheduleFolder extends VistaBoxFolder<MaintenanceRequestSchedule> {
        public MaintenanceRequestScheduleFolder() {
            super(MaintenanceRequestSchedule.class, false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof MaintenanceRequestSchedule) {
                return new MaintenanceRequestScheduleViewer();
            }
            return super.create(member);
        }

        @Override
        protected CEntityFolderItem<MaintenanceRequestSchedule> createItem(boolean first) {
            final CEntityFolderItem<MaintenanceRequestSchedule> item = super.createItem(first);
            if (!isEditable()) {
                item.addAction(ActionType.Cust1, "Add Progress Note", HelperImages.INSTANCE.editButton(), new Command() {
                    @Override
                    public void execute() {
                        new OkCancelDialog("Enter Progress Note") {
                            private final CEntityForm<MaintenanceRequestSchedule> content = createContent();

                            private CEntityForm<MaintenanceRequestSchedule> createContent() {
                                CEntityForm<MaintenanceRequestSchedule> content = new CEntityForm<MaintenanceRequestSchedule>(MaintenanceRequestSchedule.class) {

                                    @Override
                                    public IsWidget createContent() {
                                        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                                        int row = -1;
                                        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().workDescription(), new CLabel<String>()), 40).build());
                                        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().scheduledDate(), new CDateLabel()), 10).build());
                                        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().progressNote()), 25).build());

                                        return main;
                                    }
                                };

                                content.initContent();
                                content.populate(item.getValue());

                                setBody(content.asWidget());

                                return content;
                            }

                            @Override
                            public boolean onClickOk() {
                                ((MaintenanceRequestViewerView.Presenter) getParentView().getPresenter()).updateProgressAction(content.getValue());
                                return true;
                            }
                        }.show();
                    }
                });
            }
            return item;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
        }

        class MaintenanceRequestScheduleViewer extends CEntityForm<MaintenanceRequestSchedule> {

            public MaintenanceRequestScheduleViewer() {
                super(MaintenanceRequestSchedule.class);
                setViewable(true);
                setEditable(false);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                int row = -1;
                // left side
                content.setH2(++row, 0, 1, "Work Order");
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().scheduledDate())).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().scheduledTimeFrom())).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().scheduledTimeTo())).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().workDescription())).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().progressNote())).build());
                // add spacer
                content.setWidget(++row, 0, new HTML());
                content.getCellFormatter().setHeight(row, 0, "120px");
                // right side
                content.setWidget(0, 1, inject(proto().noticeOfEntry(), new NoticeOfEntryViewer()));
                content.getFlexCellFormatter().setRowSpan(0, 1, row + 1);
                content.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                boolean noticeVisible = !getValue().noticeOfEntry().messageId().isNull();
                get(proto().noticeOfEntry()).asWidget().setVisible(noticeVisible);
            }
        }

        class NoticeOfEntryViewer extends CEntityForm<NoticeOfEntry> {

            public NoticeOfEntryViewer() {
                super(NoticeOfEntry.class);
                setViewable(true);
                setEditable(false);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                content.setWidth("400px");
                int row = -1;
                content.setH2(++row, 0, 2, "Notice Of Entry");
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().messageDate())).contentWidth("350px").labelWidth("100px").build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().messageId())).contentWidth("350px").labelWidth("100px").build());
                content.setWidget(++row, 0, inject(proto().text()));
                get(proto().text()).asWidget().setWidth("450px");

                return content;
            }
        }
    }

    class StatusHistoryFolder extends VistaBoxFolder<MaintenanceRequestStatusRecord> {
        public StatusHistoryFolder() {
            super(MaintenanceRequestStatusRecord.class, false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof MaintenanceRequestStatusRecord) {
                return new StatusRecordViewer();
            }
            return super.create(member);
        }

        class StatusRecordViewer extends CEntityForm<MaintenanceRequestStatusRecord> {

            public StatusRecordViewer() {
                super(MaintenanceRequestStatusRecord.class);
                setViewable(true);
                setEditable(false);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                int row = -1;
                // left side
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().created())).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().updatedBy(), new CEntityLabel<AbstractPmcUser>())).build());
                // right side
                row = -1;
                content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().oldStatus(), new CEntityLabel<MaintenanceRequestStatus>())).build());
                content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().newStatus(), new CEntityLabel<MaintenanceRequestStatus>())).build());

                return content;
            }
        }
    }

    class ValueChangeScope {
        private final Set<Object> scopeSet = new HashSet<Object>();

        public void setScope(Object scope) {
            synchronized (this) {
                scopeSet.add(scope);
            }
        }

        public void clearScope(Object scope) {
            synchronized (this) {
                scopeSet.remove(scope);
            }
        }

        public boolean inScope(Object scope) {
            return scopeSet.contains(scope);
        }
    }
}
