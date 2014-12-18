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
 */
package com.propertyvista.crm.client.ui.crud.maintenance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.CBooleanLabel;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CImageSlider;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTimeLabel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.ItemActionsBar.ActionType;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.CEntitySelectorHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;
import com.pyx4j.widgets.client.images.HelperImages;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.MaintenanceRequestCategoryChoice;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.validators.FutureDateValidator;
import com.propertyvista.crm.client.ui.components.boxes.TenantSelectionDialog;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectionDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.maintenance.MaintenanceRequestPictureUploadService;
import com.propertyvista.domain.TimeWindow;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.maintenance.MaintenanceRequest.ContactPhoneType;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestPicture;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatusRecord;
import com.propertyvista.domain.maintenance.MaintenanceRequestWorkOrder;
import com.propertyvista.domain.maintenance.NoticeOfEntry;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.MessageDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class MaintenanceRequestForm extends CrmEntityForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestForm.class);

    private FormPanel categoryPanel;

    private FormPanel accessPanel;

    private FormPanel unitAccessPanel;

    private FormPanel statusPanel;

    private FormPanel scheduledPanel;

    private FormPanel resolvedPanel;

    private FormPanel surveyPanel;

    private final TenantSelector reporterSelector = new TenantSelector();

    private final UnitSelector unitSelector = new UnitSelector();

    private final PrioritySelector prioritySelector = new PrioritySelector();

    private final PreferredTimeSelector preferredTimeSelector1 = new PreferredTimeSelector();

    private final PreferredTimeSelector preferredTimeSelector2 = new PreferredTimeSelector();

    private MaintenanceRequestMetadata meta;

    private MaintenanceRequestCategoryChoice mrCategory;

    private final ValueChangeScope valueChangeScope = new ValueChangeScope();

    // TODO - YARDI: per building cache is easy to implement with current data model, but it is INEFFICIENT since
    // multiple buildings that belong to the same Yardi account will share the same Meta
    private final Map<String, MaintenanceRequestMetadata> categoryMetaCache = new HashMap<String, MaintenanceRequestMetadata>();

    public MaintenanceRequestForm(IPrimeFormView<MaintenanceRequestDTO, ?> view) {
        super(MaintenanceRequestDTO.class, view);

        selectTab(addTab(createGeneralTab(), i18n.tr("General")));
        addTab(createWorkHistoryTab(), proto().workHistory().getMeta().getCaption());
        addTab(createStatusHistoryTab(), proto().statusHistory().getMeta().getCaption());
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
                    initSelectors(meta);
                }
            };
            if (getParentView() instanceof IPrimeEditorView) {
                ((MaintenanceRequestEditorView.Presenter) getParentView().getPresenter()).getCategoryMeta(callback, bld.getPrimaryKey());
            } else {
                ((MaintenanceRequestViewerView.Presenter) getParentView().getPresenter()).getCategoryMeta(callback, bld.getPrimaryKey());
            }
        } else {
            initSelectors(meta);
        }
    }

    private FormPanel createGeneralTab() {
        FormPanel panel = new FormPanel(this);

        panel.append(Location.Left, inject(proto().reportedDate())).decorate().componentWidth(100);
        panel.append(Location.Right, inject(proto().requestId())).decorate().componentWidth(200);

        panel.h1(i18n.tr("Issue Location"));

        panel.append(Location.Left, inject(proto().building(), new CEntityLabel<Building>())).decorate().componentWidth(200);
        panel.append(Location.Left, inject(proto().reportedForOwnUnit())).decorate().componentWidth(200);
        panel.append(Location.Left, inject(proto().unit(), unitSelector)).decorate().componentWidth(200);

        panel.append(Location.Right, inject(proto().reporter(), reporterSelector)).decorate().componentWidth(200);
        panel.append(Location.Right, inject(proto().reporterName())).decorate().componentWidth(200);
        panel.append(Location.Right, inject(proto().reporterPhone())).decorate().componentWidth(200);
        panel.append(Location.Right, inject(proto().phoneType())).decorate().componentWidth(100);
        get(proto().reporterName()).setVisible(false);
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
        panel.h1(i18n.tr("Issue Classification"));
        panel.append(Location.Dual, categoryPanel = new FormPanel(this));
        // bind root category component
        bind(mrCategory = new MaintenanceRequestCategoryChoice(), proto().category());

        panel.h1(i18n.tr("Issue Details"));
        panel.append(Location.Left, inject(proto().priority(), prioritySelector)).decorate();
        panel.append(Location.Dual, inject(proto().summary())).decorate();
        panel.append(Location.Dual, inject(proto().description())).decorate();
        panel.append(Location.Dual, inject(proto().message(), new CAssociationLabel())).decorate().customLabel(i18n.tr("Associated"));

        // --------------------------------------------------------------------------------------------------------------------
        CImageSlider<MaintenanceRequestPicture> imageSlider = new CImageSlider<MaintenanceRequestPicture>(MaintenanceRequestPicture.class,
                GWT.<MaintenanceRequestPictureUploadService> create(MaintenanceRequestPictureUploadService.class), new VistaFileURLBuilder(
                        MaintenanceRequestPicture.class)) {
            @Override
            protected FolderImages getFolderIcons() {
                return VistaImages.INSTANCE;
            }

            @Override
            public Widget getImageEntryView(CForm<MaintenanceRequestPicture> entryForm) {
                FormPanel main = new FormPanel(entryForm);
                main.append(Location.Dual, entryForm.proto().description()).decorate().labelWidth(100).componentWidth(180).build();

                return main.asWidget();
            }
        };
        imageSlider.setImageSize(240, 180);
        imageSlider.setOrganizerWidth(550);
        panel.append(Location.Dual, inject(proto().pictures(), imageSlider)).decorate();

        // --------------------------------------------------------------------------------------------------------------------
        unitAccessPanel = new FormPanel(this);

        unitAccessPanel.h1(i18n.tr("Unit Access"));

        unitAccessPanel.append(Location.Dual, inject(proto().permissionToEnter())).decorate();
        unitAccessPanel.append(Location.Dual, accessPanel = new FormPanel(this));

        get(proto().permissionToEnter()).setNote(i18n.tr("Indicate whether Permission to Enter has been granted by Tenant."));
        get(proto().permissionToEnter()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                accessPanel.setVisible(event.getValue());
            }
        });

        // --------------------------------------------------------------------------------------------------------------------
        accessPanel.append(Location.Dual, inject(proto().petInstructions())).decorate().customLabel(i18n.tr("Entry Instructions"));
        get(proto().petInstructions()).setNote(i18n.tr("Entry instructions, including Pet Warnings, etc"));

        accessPanel.append(Location.Left, inject(proto().preferredDate1())).decorate().componentWidth(120);
        accessPanel.append(Location.Left, inject(proto().preferredDate2())).decorate().componentWidth(120);
        accessPanel.append(Location.Right, inject(proto().preferredTime1(), preferredTimeSelector1)).decorate().componentWidth(120);
        accessPanel.append(Location.Right, inject(proto().preferredTime2(), preferredTimeSelector2)).decorate().componentWidth(120);
        get(proto().preferredDate1()).addComponentValidator(new FutureDateValidator());
        get(proto().preferredDate2()).addComponentValidator(new FutureDateValidator());

        panel.append(Location.Dual, unitAccessPanel);

        // --------------------------------------------------------------------------------------------------------------------
        statusPanel = new FormPanel(this);

        statusPanel.h1(i18n.tr("Status"));

        statusPanel.append(Location.Left, inject(proto().status(), new CEntityLabel<MaintenanceRequestStatus>())).decorate().componentWidth(100);
        statusPanel.append(Location.Left, inject(proto().updated(), new CDateLabel())).decorate().componentWidth(100);
        statusPanel.append(Location.Left, inject(proto().submitted(), new CDateLabel())).decorate().componentWidth(100);

        FlowPanel detailHolder = new FlowPanel();
        statusPanel.append(Location.Right, detailHolder);
        //       statusPanel.getFlexCellFormatter().getElement(rowAnchor, 1).getStyle().setVerticalAlign(VerticalAlign.TOP);

        // --------------------------------------------------------------------------------------------------------------------
        scheduledPanel = new FormPanel(this);

        scheduledPanel.append(Location.Left, inject(proto().scheduledDate(), new CDateLabel())).decorate().componentWidth(100);
        scheduledPanel.append(Location.Left, inject(proto().scheduledTime().timeFrom(), new CTimeLabel())).decorate().componentWidth(100);
        scheduledPanel.append(Location.Left, inject(proto().scheduledTime().timeTo(), new CTimeLabel())).decorate().componentWidth(100);

        // --------------------------------------------------------------------------------------------------------------------
        resolvedPanel = new FormPanel(this);

        resolvedPanel.append(Location.Left, inject(proto().resolvedDate(), new CDateLabel())).decorate().componentWidth(100);
        resolvedPanel.append(Location.Dual, inject(proto().resolution(), new CLabel<String>())).decorate();

        detailHolder.add(scheduledPanel);
        detailHolder.add(resolvedPanel);

        panel.append(Location.Dual, statusPanel);

        // --------------------------------------------------------------------------------------------------------------------
        surveyPanel = new FormPanel(this);

        surveyPanel.h1(proto().surveyResponse().getMeta().getCaption());

        surveyPanel.append(Location.Left, inject(proto().surveyResponse().rating(), new CLabel<Integer>())).decorate().componentWidth(100);
        surveyPanel.append(Location.Right, inject(proto().surveyResponse().description(), new CLabel<String>())).decorate().componentWidth(100);

        panel.append(Location.Dual, surveyPanel);

        // --------------------------------------------------------------------------------------------------------------------
        unitSelector.addValueChangeHandler(new ValueChangeHandler<AptUnit>() {
            @Override
            public void onValueChange(ValueChangeEvent<AptUnit> event) {
                // prevent mutual reset
                valueChangeScope.setScope(unitSelector);
                AptUnit unit = event.getValue();
                if (unit != null) {
                    if (!valueChangeScope.inScope(reporterSelector)) {
                        reporterSelector.setValue(null);
                    }
                }
                valueChangeScope.clearScope(unitSelector);
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
                }
                valueChangeScope.clearScope(reporterSelector);
            }
        });

        get(proto().reportedForOwnUnit()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                unitSelector.setVisible(event.getValue().booleanValue());
                get(proto().permissionToEnter()).setValue(event.getValue());
                unitAccessPanel.setVisible(event.getValue().booleanValue());
                accessPanel.setVisible(getValue().permissionToEnter().getValue(false) && event.getValue().booleanValue());
                getValue().category().set(null);
                mrCategory.setOptionsMeta(meta, event.getValue().booleanValue());
            }
        });
        panel.br();

        return panel;
    }

    public class CAssociationLabel extends CEntityHyperlink<Message> {

        public CAssociationLabel() {
            super();
            setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    Message value = getValue();
                    if (value != null && value.getPrimaryKey() != null) {
                        AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(MessageDTO.class).formViewerPlace(value.getPrimaryKey()));

                    }
                }
            });
        }

        @Override
        public String format(Message value) {
            if (value == null) {
                return "";
            } else {

                return i18n.tr("Communication");
            }
        }
    }

    @Override
    protected MaintenanceRequestDTO preprocessValue(MaintenanceRequestDTO value, boolean fireEvent, boolean populate) {
        if (value == null || value.getPrimaryKey() == null || value.getPrimaryKey().isDraft()) {
            if (value.reportedForOwnUnit().isNull()) {
                value.reportedForOwnUnit().setValue(true);
            }
            if (value.permissionToEnter().isNull()) {
                value.permissionToEnter().setValue(value.reportedForOwnUnit().getValue(false)); // according reportedForOwnUnit
            }
        }
        return value;
    }

    private FormPanel createWorkHistoryTab() {
        FormPanel panel = new FormPanel(this);
        panel.append(Location.Dual, inject(proto().workHistory(), new MaintenanceRequestScheduleFolder()));
        return panel;
    }

    private FormPanel createStatusHistoryTab() {
        FormPanel panel = new FormPanel(this);
        panel.append(Location.Dual, inject(proto().statusHistory(), new StatusHistoryFolder()));
        return panel;
    }

    private void initSelectors(MaintenanceRequestMetadata meta) {
        if (meta == null || meta.isNull() || meta.equals(this.meta)) {
            return;
        }

        this.meta = meta;

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
            mrCategory.setOptionsMeta(meta, get(proto().reportedForOwnUnit()).getValue());
        }
        // re-populate after parent categories have been added
        if (getValue() != null) {
            mrCategory.populate(getValue().category());
        }

        // attach selectors to the panel - bottom up
        categoryPanel.clear();
        List<MaintenanceRequestCategoryChoice> list = new ArrayList<>();
        for (choice = mrCategory; choice != null; choice = choice.getParentSelector()) {
            list.add(choice);
        }
        Collections.reverse(list);
        for (MaintenanceRequestCategoryChoice c : list) {
            categoryPanel.append(Location.Left, c).decorate();
        }
        // will need to revalidate after this
        setVisited(false);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isEditable()) {
            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.maintenance, get(proto().requestId()), getValue().getPrimaryKey());
        }

        MaintenanceRequestDTO mr = getValue();
        get(proto().message()).setVisible(mr != null && mr.message() != null && !mr.message().isNull() && !mr.message().isPrototype());

        if (mr != null) {
            boolean hasPreferredTime = !mr.policy().tenantPreferredWindows().isEmpty();
            get(proto().preferredDate1()).setVisible(hasPreferredTime);
            get(proto().preferredDate2()).setVisible(hasPreferredTime);
            get(proto().preferredTime1()).setVisible(hasPreferredTime);
            get(proto().preferredTime2()).setVisible(hasPreferredTime);

            if (isEditable() && hasPreferredTime) {
                List<TimeWindow> opts = new ArrayList<>();
                opts.addAll(getValue().policy().tenantPreferredWindows());
                preferredTimeSelector1.setOptions(opts);
                preferredTimeSelector2.setOptions(opts);
            }

            if (VistaFeatures.instance().yardiIntegration() && !mr.id().isNull()) {
                // if reporter (tenant) not set (Yardi-originated requests), we just show the name
                get(proto().reporter()).setVisible(!mr.reporter().isNull());
                get(proto().reporterName()).setVisible(mr.reporter().isNull());
            }

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

            unitSelector.setVisible(mr.reportedForOwnUnit().getValue(false));
            unitAccessPanel.setVisible(mr.reportedForOwnUnit().getValue(false));
            accessPanel.setVisible(mr.permissionToEnter().getValue(false) && getValue().reportedForOwnUnit().getValue(false));
        }

        // to support yardi mode with multiple interfaces; will also call setMaintenanceRequestCategoryMeta()
        setMaintenanceRequestCategoryMeta();
    }

    class UnitSelector extends CEntitySelectorHyperlink<AptUnit> {
        @Override
        protected AppPlace getTargetPlace() {
            return AppPlaceEntityMapper.resolvePlace(AptUnit.class, getValue().getPrimaryKey());
        }

        @Override
        protected UnitSelectionDialog getSelectorDialog() {
            return new UnitSelectionDialog() {

                @Override
                public boolean onClickOk() {
                    setValue(getSelectedItem());
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

                @Override
                protected List<ColumnDescriptor> defineColumnDescriptors() {
                    return Arrays.asList( //
                            // building data                
                            new ColumnDescriptor.Builder(proto().building().propertyCode(), true).build(), //
                            // unit data
                            new ColumnDescriptor.Builder(proto().info().number(), true).build(), //
                            new ColumnDescriptor.Builder(proto().info().floor(), false).build(), //
                            new ColumnDescriptor.Builder(proto().info().area()).build(), //
                            new ColumnDescriptor.Builder(proto().info()._bedrooms()).build(), //
                            new ColumnDescriptor.Builder(proto().info()._bathrooms()).build(), //
                            new ColumnDescriptor.Builder(proto().availability().availableForRent()).build(), //
                            new ColumnDescriptor.Builder(proto().financial()._marketRent()).build() //
                            );
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
        protected TenantSelectionDialog getSelectorDialog() {
            return new TenantSelectionDialog() {

                @Override
                public boolean onClickOk() {
                    setValue(getSelectedItem());
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

                @Override
                protected List<ColumnDescriptor> defineColumnDescriptors() {
                    return Arrays.asList( //
                            new ColumnDescriptor.Builder(proto().lease().unit()).searchable(false).build(), //
                            new ColumnDescriptor.Builder(proto().customer().person().name()).searchable(false).build(), //
                            new ColumnDescriptor.Builder(proto().customer().person().name().firstName()).searchableOnly().build(), //
                            new ColumnDescriptor.Builder(proto().customer().person().name().lastName()).searchableOnly().build(), //
                            new ColumnDescriptor.Builder(proto().customer().person().sex()).visible(false).build(), //
                            new ColumnDescriptor.Builder(proto().customer().person().birthDate(), false).build(), //
                            new ColumnDescriptor.Builder(proto().customer().person().email(), false).build(), //
                            new ColumnDescriptor.Builder(proto().customer().person().homePhone()).build(), //
                            new ColumnDescriptor.Builder(proto().customer().person().mobilePhone()).build(), //
                            new ColumnDescriptor.Builder(proto().customer().person().workPhone()).build(), //

                            new ColumnDescriptor.Builder(proto().lease().leaseId()).searchableOnly().build() //
                            );
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
        public boolean isValuesEqual(MaintenanceRequestPriority value1, MaintenanceRequestPriority value2) {
            return value1 != null && value2 != null && value1.name().equals(value2.name());
        }

        @Override
        public boolean isValueEmpty() {
            return getValue() == null || getValue().isNull();
        }

        @Override
        protected String getDebugInfo() {
            // to avoid large meta tree dump
            return "value=" + (getValue() == null ? "null" : getValue().getStringView()) + ";";
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
        public boolean isValuesEqual(MaintenanceRequestStatus value1, MaintenanceRequestStatus value2) {
            return value1 != null && value2 != null && value1.name().equals(value2.name());
        }

        @Override
        public boolean isValueEmpty() {
            return getValue() == null || getValue().isNull();
        }

        @Override
        protected String getDebugInfo() {
            return "value=" + (getValue() == null ? "null" : getValue().getStringView()) + ";";
        }
    }

    class PreferredTimeSelector extends CComboBox<TimeWindow> {

        public PreferredTimeSelector() {
            super(NotInOptionsPolicy.DISCARD, new IFormatter<TimeWindow, String>() {

                @Override
                public String format(TimeWindow value) {
                    return value == null ? "" : value.getStringView();
                }
            });
        }
    }

    class MaintenanceRequestScheduleFolder extends VistaBoxFolder<MaintenanceRequestWorkOrder> {
        public MaintenanceRequestScheduleFolder() {
            super(MaintenanceRequestWorkOrder.class, false);
        }

        @Override
        protected CForm<MaintenanceRequestWorkOrder> createItemForm(IObject<?> member) {
            return new MaintenanceRequestScheduleViewer();
        }

        @Override
        protected CFolderItem<MaintenanceRequestWorkOrder> createItem(boolean first) {
            final CFolderItem<MaintenanceRequestWorkOrder> item = super.createItem(first);
            if (!isEditable()) {
                item.addAction(ActionType.Cust1, "Add Progress Note", HelperImages.INSTANCE.editButton(), new Command() {
                    @Override
                    public void execute() {
                        new OkCancelDialog("Enter Progress Note") {
                            private final CForm<MaintenanceRequestWorkOrder> content = createContent();

                            private CForm<MaintenanceRequestWorkOrder> createContent() {
                                CForm<MaintenanceRequestWorkOrder> content = new CForm<MaintenanceRequestWorkOrder>(MaintenanceRequestWorkOrder.class) {

                                    @Override
                                    protected IsWidget createContent() {
                                        FormPanel main = new FormPanel(this);

                                        main.append(Location.Dual, inject(proto().isEmergencyWork(), new CBooleanLabel())).decorate();
                                        main.append(Location.Left, inject(proto().scheduledDate(), new CDateLabel())).decorate().componentWidth(120);
                                        main.append(Location.Dual, inject(proto().workDescription(), new CLabel<String>())).decorate();
                                        main.append(Location.Dual, inject(proto().progressNote())).decorate();

                                        return main;
                                    }
                                };

                                content.init();
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

        class MaintenanceRequestScheduleViewer extends CForm<MaintenanceRequestWorkOrder> {

            public MaintenanceRequestScheduleViewer() {
                super(MaintenanceRequestWorkOrder.class);
                setViewable(true);
                setEditable(false);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel content = new FormPanel(this);
                content.h1(i18n.tr("Work Order"));
                content.append(Location.Left, inject(proto().isEmergencyWork())).decorate();
                content.append(Location.Left, inject(proto().scheduledDate())).decorate().componentWidth(120);
                content.append(Location.Left, inject(proto().scheduledTime().timeFrom())).decorate().componentWidth(120);
                content.append(Location.Left, inject(proto().scheduledTime().timeTo())).decorate().componentWidth(120);
                content.append(Location.Left, inject(proto().workDescription())).decorate().componentWidth(200);
                content.append(Location.Dual, inject(proto().progressNote())).decorate();

                content.append(Location.Dual, inject(proto().noticeOfEntry(), new NoticeOfEntryViewer()));

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                boolean noticeVisible = !getValue().noticeOfEntry().messageId().isNull();
                get(proto().noticeOfEntry()).asWidget().setVisible(noticeVisible);
            }
        }

        class NoticeOfEntryViewer extends CForm<NoticeOfEntry> {

            public NoticeOfEntryViewer() {
                super(NoticeOfEntry.class);
                setViewable(true);
                setEditable(false);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel content = new FormPanel(this);

                content.h1(i18n.tr("Notice Of Entry"));
                content.append(Location.Dual, inject(proto().messageDate())).decorate();
                content.append(Location.Dual, inject(proto().messageId())).decorate();
                content.append(Location.Dual, inject(proto().text()));

                return content;
            }
        }
    }

    class StatusHistoryFolder extends VistaBoxFolder<MaintenanceRequestStatusRecord> {
        public StatusHistoryFolder() {
            super(MaintenanceRequestStatusRecord.class, false);
        }

        @Override
        protected CForm<MaintenanceRequestStatusRecord> createItemForm(IObject<?> member) {
            return new StatusRecordViewer();
        }

        class StatusRecordViewer extends CForm<MaintenanceRequestStatusRecord> {

            public StatusRecordViewer() {
                super(MaintenanceRequestStatusRecord.class);
                setViewable(true);
                setEditable(false);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel content = new FormPanel(this);
                // left side
                content.append(Location.Left, inject(proto().created())).decorate();
                content.append(Location.Left, inject(proto().updatedBy(), new CEntityLabel<AbstractPmcUser>())).decorate();
                // right side
                content.append(Location.Right, inject(proto().oldStatus(), new CEntityLabel<MaintenanceRequestStatus>())).decorate();
                content.append(Location.Right, inject(proto().newStatus(), new CEntityLabel<MaintenanceRequestStatus>())).decorate();

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
