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
 */
package com.propertyvista.portal.resident.ui.maintenance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CImageSlider;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.CTimeLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.MaintenanceRequestCategoryChoice;
import com.propertyvista.domain.TimeWindow;
import com.propertyvista.domain.maintenance.EntryInstructionsNote;
import com.propertyvista.domain.maintenance.EntryNotGrantedAlert;
import com.propertyvista.domain.maintenance.IssueElementType;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestPicture;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.PermissionToEnterNote;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.resident.services.maintenance.MaintenanceRequestPictureUploadPortalService;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;

public class MaintenanceRequestWizard extends CPortalEntityWizard<MaintenanceRequestDTO> {

    private final static I18n i18n = I18n.get(MaintenanceRequestWizard.class);

    private MaintenanceRequestMetadata meta;

    private MaintenanceRequestCategoryChoice mrCategory;

    private FormPanel categoryPanel;

    private FormPanel permissionPanel;

    private FormPanel accessPanel;

    private FormPanel statusPanel;

    private final PrioritySelector prioritySelector = new PrioritySelector();

    private final PreferredTimeSelector preferredTimeSelector1 = new PreferredTimeSelector();

    private final PreferredTimeSelector preferredTimeSelector2 = new PreferredTimeSelector();

    private FormPanel imagePanel;

    public MaintenanceRequestWizard(MaintenanceRequestWizardView view) {
        super(MaintenanceRequestDTO.class, view, i18n.tr("New Maintenance Request"), i18n.tr("Submit"), ThemeColor.contrast5);

        addStep(createDetailsStep(), i18n.tr("General"));
    }

    @Override
    protected void onFinish() {
        if (getValue() != null && //
                getValue().reportedForOwnUnit().getValue(false) && //
                !getValue().permissionToEnter().getValue(false) && //
                !getValue().confirmedNoPermissionToEnter().getValue(false) //
        ) {
            EntryNotGrantedAlert noEntryAlert = getValue().policy().entryNotGrantedAlert().get(0);
            MessageDialog.confirm(noEntryAlert.title().getValue(), noEntryAlert.text().getValue(), new Command() {
                @Override
                public void execute() {
                    getValue().confirmedNoPermissionToEnter().setValue(true);
                    MaintenanceRequestWizard.super.onFinish();
                }
            });
        } else {
            super.onFinish();
        }
    }

    public void setMaintenanceRequestCategoryMeta(MaintenanceRequestMetadata meta) {
        if (meta == null || meta.isNull()) {
            throw new RuntimeException(i18n.tr("Maintenance Metadata not configured."));
        }
        this.meta = meta;
        initSelectors();
    }

    public IsWidget createDetailsStep() {
        FormPanel content = new FormPanel(this);

        content.append(Location.Left, inject(proto().requestId(), new CLabel<String>())).decorate().componentWidth(250);
        content.br();
        content.append(Location.Left, inject(proto().reportedForOwnUnit())).decorate().componentWidth(250);

        // category panel
        bind(mrCategory = new MaintenanceRequestCategoryChoice(), proto().category());
        content.append(Location.Dual, categoryPanel = new FormPanel(this));

        // Description
        content.append(Location.Left, inject(proto().summary())).decorate().componentWidth(250);
        content.append(Location.Left, inject(proto().description())).decorate().componentWidth(250);
        content.append(Location.Left, inject(proto().priority(), prioritySelector)).decorate().componentWidth(250);

        // phone is mandatory
        content.append(Location.Left, inject(proto().reporterPhone())).decorate().componentWidth(250).customLabel(i18n.tr("Contact Phone"));
        content.append(Location.Left, inject(proto().phoneType())).decorate().componentWidth(250);
        get(proto().phoneType()).setMandatory(true);
        get(proto().reporterPhone()).setMandatory(true);
        content.br();

        imagePanel = new FormPanel(this);
        CImageSlider<MaintenanceRequestPicture> imageSlider = new CImageSlider<MaintenanceRequestPicture>(MaintenanceRequestPicture.class,
                GWT.<MaintenanceRequestPictureUploadPortalService> create(MaintenanceRequestPictureUploadPortalService.class), new VistaFileURLBuilder(
                        MaintenanceRequestPicture.class)) {
            @Override
            protected FolderImages getFolderIcons() {
                return VistaImages.INSTANCE;
            }

            @Override
            public Widget getImageEntryView(CForm<MaintenanceRequestPicture> entryForm) {
                FormPanel main = new FormPanel(entryForm);
                CTextField descr = new CTextField();
                descr.setWatermark(entryForm.proto().description().getMeta().getCaption());
                main.append(Location.Dual, entryForm.inject(entryForm.proto().description(), descr)).decorate().customLabel("").labelWidth(0)
                        .componentWidth(250);
                return main.asWidget();
            }
        };
        imageSlider.setImageSize(250, 240);
        imageSlider.setOrganizerWidth(500);
        imagePanel.append(Location.Left, inject(proto().pictures(), imageSlider)).decorate().componentWidth(100);
        content.append(Location.Dual, imagePanel);

        accessPanel = new FormPanel(this);
        accessPanel.append(Location.Left, inject(proto().petInstructions())).decorate().componentWidth(250);
        // schedule panel
        FormPanel schedulePanel = new FormPanel(this);
        schedulePanel.append(Location.Left, inject(proto().preferredDate1())).decorate().componentWidth(120);
        schedulePanel.append(Location.Left, inject(proto().preferredTime1(), preferredTimeSelector1)).decorate().componentWidth(120);
        schedulePanel.append(Location.Left, inject(proto().preferredDate2())).decorate().componentWidth(120);
        schedulePanel.append(Location.Left, inject(proto().preferredTime2(), preferredTimeSelector2)).decorate().componentWidth(120);
        // past dates not allowed
        ((CDatePicker) get(proto().preferredDate1())).setPastDateSelectionAllowed(false);
        ((CDatePicker) get(proto().preferredDate2())).setPastDateSelectionAllowed(false);
        accessPanel.append(Location.Dual, schedulePanel);

        permissionPanel = new FormPanel(this);
        permissionPanel.append(Location.Left, inject(proto().permissionToEnter())).decorate().componentWidth(250);
        permissionPanel.append(Location.Dual, accessPanel);
        content.append(Location.Dual, permissionPanel);
        content.br();

        statusPanel = new FormPanel(this);
        statusPanel.h1(i18n.tr("Status"));
        statusPanel.append(Location.Left, inject(proto().status(), new CEntityLabel<MaintenanceRequestStatus>())).decorate().componentWidth(100);
        statusPanel.append(Location.Left, inject(proto().updated(), new CDateLabel())).decorate().componentWidth(100);
        statusPanel.append(Location.Left, inject(proto().submitted(), new CDateLabel())).decorate().componentWidth(100);
        statusPanel.br();
        statusPanel.append(Location.Left, inject(proto().scheduledDate(), new CDateLabel())).decorate().componentWidth(100);
        statusPanel.append(Location.Left, inject(proto().scheduledTime().timeFrom(), new CTimeLabel())).decorate().componentWidth(100);
        statusPanel.append(Location.Left, inject(proto().scheduledTime().timeTo(), new CTimeLabel())).decorate().componentWidth(100);
        content.append(Location.Dual, statusPanel);

        content.br();

        // tweaks:
        get(proto().reportedForOwnUnit()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                permissionPanel.setVisible(event.getValue().booleanValue());
                getValue().category().set(null);
                mrCategory.setOptionsMeta(meta, event.getValue().booleanValue());
            }
        });

        get(proto().permissionToEnter()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                accessPanel.setVisible(event.getValue());
            }
        });

        return content;
    }

    @Override
    public void generateMockData() {
        get(proto().reportedForOwnUnit()).setMockValue(true);
        get(proto().category()).setMockValue(getMockCategory());
        get(proto().summary()).setMockValue("Maintenance Request Summary");
        get(proto().description()).setMockValue("Maintenance Request Description");
        get(proto().priority()).setMockValue(meta.priorities().get(0));
        get(proto().permissionToEnter()).setMockValue(true);
    }

    private MaintenanceRequestCategory getMockCategory() {
        MaintenanceRequestCategory category = meta.rootCategory();
        while (category.subCategories().size() > 0) {
            MaintenanceRequestCategory tmp;
            do {
                // get unit-related category
                tmp = category.subCategories().get(new Random().nextInt(category.subCategories().size()));
            } while (!tmp.elementType().isNull() && tmp.elementType().getValue() != IssueElementType.ApartmentUnit);
            category = tmp;
        }
        return category;
    }

    @Override
    protected MaintenanceRequestDTO preprocessValue(MaintenanceRequestDTO value, boolean fireEvent, boolean populate) {
        if (value == null || value.getPrimaryKey() == null || value.getPrimaryKey().isDraft()) {
            if (value.reportedForOwnUnit().isNull()) {
                value.reportedForOwnUnit().setValue(true);
            }
            if (value.permissionToEnter().isNull() && value.policy().permissionGrantedByDefault().getValue(false)) {
                value.permissionToEnter().setValue(true);
            }
        }
        return super.preprocessValue(value, fireEvent, populate);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (getValue() == null) {
            return;
        }

        boolean hasPreferredTime = !getValue().policy().tenantPreferredWindows().isEmpty();
        get(proto().preferredDate1()).setVisible(hasPreferredTime);
        get(proto().preferredDate2()).setVisible(hasPreferredTime);
        get(proto().preferredTime1()).setVisible(hasPreferredTime);
        get(proto().preferredTime2()).setVisible(hasPreferredTime);

        if (isEditable()) {
            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.maintenance, get(proto().requestId()), getValue().getPrimaryKey());

            if (hasPreferredTime) {
                List<TimeWindow> opts = new ArrayList<>();
                opts.addAll(getValue().policy().tenantPreferredWindows());
                preferredTimeSelector1.setOptions(opts);
                preferredTimeSelector2.setOptions(opts);
            }
        }

        // set notes
        PermissionToEnterNote permissionNote = getValue().policy().permissionToEnterNote().get(0);
        get(proto().permissionToEnter()).setTitle(permissionNote.caption().getValue());
        get(proto().permissionToEnter()).setNote(permissionNote.text().getValue());

        EntryInstructionsNote entryNote = getValue().policy().entryInstructionsNote().get(0);
        get(proto().petInstructions()).setTitle(entryNote.caption().getValue());
        get(proto().petInstructions()).setNote(entryNote.text().getValue());

        StatusPhase phase = getValue().status().phase().getValue();
        get(proto().scheduledDate()).setVisible(phase == StatusPhase.Scheduled);
        get(proto().scheduledTime().timeFrom()).setVisible(phase == StatusPhase.Scheduled);
        get(proto().scheduledTime().timeTo()).setVisible(phase == StatusPhase.Scheduled);

        get(proto().submitted()).setVisible(!getValue().submitted().isNull());
        get(proto().updated()).setVisible(!getValue().updated().isNull());
        get(proto().status()).setVisible(!getValue().submitted().isNull());

        permissionPanel.setVisible(getValue().reportedForOwnUnit().getValue(false));
        accessPanel.setVisible(getValue().permissionToEnter().getValue(false));
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
            mrCategory.setOptionsMeta(meta, getValue() == null ? true : getValue().reportedForOwnUnit().getValue(false));
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
            categoryPanel.append(Location.Left, c).decorate().componentWidth(250);
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
}