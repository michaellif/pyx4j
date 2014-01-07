/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.maintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CImageSlider;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.RateIt;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.PrintUtils;
import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestPicture;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.portal.resident.ui.maintenance.MaintenanceRequestPageView.MaintenanceRequestPagePresenter;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.resident.services.maintenance.MaintenanceRequestPictureUploadPortalService;
import com.propertyvista.portal.shared.themes.EntityViewTheme;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class MaintenanceRequestPage extends CPortalEntityForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestPage.class);

    private final Button btnCancel;

    private final Button btnPrint;

    private BasicFlexFormPanel imagePanel;

    private BasicFlexFormPanel scheduledPanel;

    private RateIt rateIt;

    public MaintenanceRequestPage(MaintenanceRequestPageViewImpl view) {
        super(MaintenanceRequestDTO.class, view, "Maintenance Request", ThemeColor.contrast5);
        btnCancel = new Button(i18n.tr("Cancel Request"), new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Are you sure you would like to cancel this request?"), new Command() {

                    @Override
                    public void execute() {
                        ((MaintenanceRequestPagePresenter) getView().getPresenter()).cancelRequest();
                    }
                });
            }
        });

        btnPrint = new Button(i18n.tr("Print"), new Command() {
            @Override
            public void execute() {
                PrintUtils.print(MaintenanceRequestPage.this.asWidget().getElement());
            }
        });

        asWidget().setStyleName(EntityViewTheme.StyleName.EntityView.name());
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        mainPanel.setH1(++row, 0, 1, i18n.tr("Basic Information"));
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().requestId(), new CLabel<String>()), 250).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().reportedForOwnUnit()), 250).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().category(), new CEntityLabel<MaintenanceRequestCategory>() {
            @Override
            public String format(MaintenanceRequestCategory value) {
                if (value == null) {
                    return "";
                } else {
                    StringBuilder result = new StringBuilder();
                    MaintenanceRequestCategory category = value;
                    while (!category.parent().isNull()) {
                        if (!category.name().isNull()) {
                            result.insert(0, result.length() > 0 ? "/" : "").insert(0, category.name().getValue());
                        }
                        category = category.parent();
                    }
                    return result.toString();
                }
            }
        }), 250).build());

        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().summary()), 250).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().description()), 250).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().priority(), new CEntityLabel<MaintenanceRequestPriority>()), 250).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().status().phase()), 250).build());

        int innerRow = -1;
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
                main.setWidget(0, 0, 2, new FormDecoratorBuilder(entryForm.inject(entryForm.proto().description()), 8, 15, 16).build());
                return main;
            }
        };
        imageSlider.setImageSize(250, 240);
        imagePanel.setWidget(++innerRow, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().pictures(), imageSlider), 100).build());
        mainPanel.setWidget(++row, 0, imagePanel);

        scheduledPanel = new TwoColumnFlexFormPanel();
        innerRow = -1;
        scheduledPanel.setWidget(++innerRow, 0, new FormWidgetDecoratorBuilder(inject(proto().petInstructions()), 250).build());

        scheduledPanel.setWidget(++innerRow, 0, new FormWidgetDecoratorBuilder(inject(proto().preferredDate1()), 100).build());
        scheduledPanel.setWidget(++innerRow, 0, new FormWidgetDecoratorBuilder(inject(proto().preferredTime1()), 100).build());
        scheduledPanel.setWidget(++innerRow, 0, new FormWidgetDecoratorBuilder(inject(proto().preferredDate2()), 100).build());
        scheduledPanel.setWidget(++innerRow, 0, new FormWidgetDecoratorBuilder(inject(proto().preferredTime2()), 100).build());
        mainPanel.setWidget(++row, 0, scheduledPanel);

        rateIt = new RateIt(5);
        mainPanel.setWidget(++row, 0, rateIt);

        mainPanel.setBR(++row, 0, 1);

        rateIt.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                if (event.getValue() != null) {
                    ((MaintenanceRequestPagePresenter) getView().getPresenter()).rateRequest(event.getValue());
                }

            }
        });

        return mainPanel;
    }

    @Override
    protected FormDecorator<MaintenanceRequestDTO, CEntityForm<MaintenanceRequestDTO>> createDecorator() {
        FormDecorator<MaintenanceRequestDTO, CEntityForm<MaintenanceRequestDTO>> decorator = super.createDecorator();

        decorator.addHeaderToolbarWidget(btnCancel);
        decorator.addHeaderToolbarWidget(btnPrint);

        return decorator;
    }

    @Override
    protected void onValueSet(boolean populate) {
        MaintenanceRequestDTO mr = getValue();

        btnCancel.setVisible(mr != null && StatusPhase.open().contains(mr.status().phase().getValue()));
        if (mr == null) {
            return;
        }
        imagePanel.setVisible(mr.pictures() != null && !mr.pictures().isNull() && !mr.pictures().isEmpty());
        rateIt.setVisible(StatusPhase.closed().contains(mr.status().phase().getValue()));
        scheduledPanel.setVisible(mr.reportedForOwnUnit().isBooleanTrue());
        if (!mr.surveyResponse().isNull() && !mr.surveyResponse().isEmpty() && !mr.surveyResponse().rating().isNull()) {
            rateIt.setRating(mr.surveyResponse().rating().getValue());
        }
    }
}
