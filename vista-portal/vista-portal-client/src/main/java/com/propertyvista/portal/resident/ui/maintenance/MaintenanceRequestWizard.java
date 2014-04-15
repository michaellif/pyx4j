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
package com.propertyvista.portal.resident.ui.maintenance;

import java.util.Random;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CImageSlider;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTimeLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.MaintenanceRequestCategoryChoice;
import com.propertyvista.domain.maintenance.IssueElementType;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestPicture;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.resident.services.maintenance.MaintenanceRequestPictureUploadPortalService;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class MaintenanceRequestWizard extends CPortalEntityWizard<MaintenanceRequestDTO> {

    private final static I18n i18n = I18n.get(MaintenanceRequestWizard.class);

    private MaintenanceRequestMetadata meta;

    private MaintenanceRequestCategoryChoice mrCategory;

    private final BasicFlexFormPanel categoryPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel permissionPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel accessPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel statusPanel = new BasicFlexFormPanel();

    private final PrioritySelector prioritySelector = new PrioritySelector();

    private BasicFlexFormPanel imagePanel;

    public MaintenanceRequestWizard(MaintenanceRequestWizardView view) {
        super(MaintenanceRequestDTO.class, view, i18n.tr("New Maintenance Request"), i18n.tr("Submit"), ThemeColor.contrast5);

        addStep(createDetailsStep());
    }

    @Override
    protected void onFinish() {
        if (getValue() != null && //
                getValue().reportedForOwnUnit().getValue(false) && //
                !getValue().permissionToEnter().getValue(false) && //
                !getValue().confirmedNoPermissionToEnter().getValue(false) //
        ) {
            MessageDialog.confirm( //
                    i18n.tr("Confirm 'No Entry'"), //
                    i18n.tr("Please confirm that you do not wish to grant our staff Permission To Enter your apartment. " + //
                            "Please note that this may delay resolution of reported Issue. " + //
                            "Also, please be sure to provide your Preferred Date/Time for our visit." //
                    ), //
                    new Command() {
                        @Override
                        public void execute() {
                            getValue().confirmedNoPermissionToEnter().setValue(true);
                        }
                    } //
                    );
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

    public TwoColumnFlexFormPanel createDetailsStep() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, inject(proto().requestId(), new CLabel<String>(), new FormWidgetDecoratorBuilder(250).build()));
        content.setBR(++row, 0, 1);
        content.setWidget(++row, 0, inject(proto().reportedForOwnUnit(), new FormWidgetDecoratorBuilder(250).build()));
        content.setBR(++row, 0, 1);

        // category panel
        mrCategory = new MaintenanceRequestCategoryChoice();
        bind(mrCategory, proto().category());
        content.setWidget(++row, 0, categoryPanel);
        content.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);

        // Description
        content.setWidget(++row, 0, inject(proto().summary(), new FormWidgetDecoratorBuilder(250).build()));
        content.setWidget(++row, 0, inject(proto().description(), new FormWidgetDecoratorBuilder(250).build()));
        content.setWidget(++row, 0, inject(proto().priority(), prioritySelector, new FormWidgetDecoratorBuilder(250).build()));

        // phone is mandatory
        content.setWidget(++row, 0, inject(proto().reporterPhone(), new FormWidgetDecoratorBuilder(250).customLabel(i18n.tr("Contact Phone")).build()));
        content.setWidget(++row, 0, inject(proto().phoneType(), new FormWidgetDecoratorBuilder(250).build()));
        get(proto().phoneType()).setMandatory(true);
        get(proto().reporterPhone()).setMandatory(true);
        content.setBR(++row, 0, 1);

        imagePanel = new TwoColumnFlexFormPanel();
        CImageSlider<MaintenanceRequestPicture> imageSlider = new CImageSlider<MaintenanceRequestPicture>(MaintenanceRequestPicture.class,
                GWT.<MaintenanceRequestPictureUploadPortalService> create(MaintenanceRequestPictureUploadPortalService.class), new VistaFileURLBuilder(
                        MaintenanceRequestPicture.class)) {
            @Override
            protected EntityFolderImages getFolderIcons() {
                return VistaImages.INSTANCE;
            }

            @Override
            public Widget getImageEntryView(CEntityForm<MaintenanceRequestPicture> entryForm) {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
                main.setWidget(0, 0, 2, entryForm.inject(entryForm.proto().description(), new FormDecoratorBuilder(8, 15, 16).build()));
                return main;
            }
        };
        imageSlider.setImageSize(250, 240);
        imageSlider.setOrganizerWidth(600);
        imagePanel.setWidget(0, 0, 1, inject(proto().pictures(), imageSlider, new FormWidgetDecoratorBuilder(100).build()));
        content.setWidget(++row, 0, imagePanel);

        accessPanel.setWidget(0, 0, inject(proto().petInstructions(), new FormWidgetDecoratorBuilder(250).build()));
        // schedule panel
        TwoColumnFlexFormPanel schedulePanel = new TwoColumnFlexFormPanel();
        schedulePanel.setWidget(0, 0, inject(proto().preferredDate1(), new FormWidgetDecoratorBuilder(120).build()));
        schedulePanel.setWidget(1, 0, inject(proto().preferredTime1(), new FormWidgetDecoratorBuilder(120).build()));
        schedulePanel.setWidget(2, 0, inject(proto().preferredDate2(), new FormWidgetDecoratorBuilder(120).build()));
        schedulePanel.setWidget(3, 0, inject(proto().preferredTime2(), new FormWidgetDecoratorBuilder(120).build()));
        // past dates not allowed
        ((CDatePicker) get(proto().preferredDate1())).setPastDateSelectionAllowed(false);
        ((CDatePicker) get(proto().preferredDate2())).setPastDateSelectionAllowed(false);
        accessPanel.setWidget(1, 0, schedulePanel);

        permissionPanel.setWidget(2, 0, inject(proto().permissionToEnter(), new FormWidgetDecoratorBuilder(250).build()));
        permissionPanel.setWidget(3, 0, accessPanel);
        content.setWidget(++row, 0, permissionPanel);
        content.setBR(++row, 0, 1);

        int panelRow = -1;
        statusPanel.setH1(++panelRow, 0, 1, i18n.tr("Status"));
        statusPanel.setWidget(++panelRow, 0,
                inject(proto().status(), new CEntityLabel<MaintenanceRequestStatus>(), new FormWidgetDecoratorBuilder(100).build()));
        statusPanel.setWidget(++panelRow, 0, inject(proto().updated(), new CDateLabel(), new FormWidgetDecoratorBuilder(100).build()));
        statusPanel.setWidget(++panelRow, 0, inject(proto().submitted(), new CDateLabel(), new FormWidgetDecoratorBuilder(100).build()));
        statusPanel.setBR(++panelRow, 0, 1);
        statusPanel.setWidget(++panelRow, 0, inject(proto().scheduledDate(), new CDateLabel(), new FormWidgetDecoratorBuilder(100).build()));
        statusPanel.setWidget(++panelRow, 0, inject(proto().scheduledTimeFrom(), new CTimeLabel(), new FormWidgetDecoratorBuilder(100).build()));
        statusPanel.setWidget(++panelRow, 0, inject(proto().scheduledTimeTo(), new CTimeLabel(), new FormWidgetDecoratorBuilder(100).build()));
        content.setWidget(++row, 0, statusPanel);

        content.setBR(++row, 0, 1);

        // tweaks:
        get(proto().reportedForOwnUnit()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                permissionPanel.setVisible(event.getValue().booleanValue());
                getValue().category().set(null);
                if (!event.getValue().booleanValue()) {
                    getValue().unit().set(null);
                }
                categoryPanel.clear();
                initSelectors();
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
            } while (!tmp.type().isNull() && tmp.type().getValue() != IssueElementType.ApartmentUnit);
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
            if (value.permissionToEnter().isNull()) {
                value.permissionToEnter().setValue(value.reportedForOwnUnit().getValue(false)); // according reportedForOwnUnit
            }
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

        // set notes
        String note = getValue().notePermissionToEnter().getValue();
        if (CommonsStringUtils.isEmpty(note)) {
            note = i18n.tr("By checking this box you authorize the Landlord to enter your Apartment to assess and resolve this Issue.");
        }
        get(proto().permissionToEnter()).setNote(note);
        get(proto().petInstructions()).setNote(i18n.tr("Special instructions in case you have a pet in the apartment"));

        StatusPhase phase = getValue().status().phase().getValue();
        get(proto().scheduledDate()).setVisible(phase == StatusPhase.Scheduled);
        get(proto().scheduledTimeFrom()).setVisible(phase == StatusPhase.Scheduled);
        get(proto().scheduledTimeTo()).setVisible(phase == StatusPhase.Scheduled);

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
        int row = levels;
        for (choice = mrCategory; choice != null; choice = choice.getParentSelector()) {
            choice.setDecorator(new FormWidgetDecoratorBuilder(250).build());
            categoryPanel.setWidget(--row, 0, choice);
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