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
 */
package com.propertyvista.portal.resident.ui.maintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CImageSlider;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.RateIt;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.PrintUtils;
import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestPicture;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.portal.resident.ui.maintenance.MaintenanceRequestPageView.MaintenanceRequestPagePresenter;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.resident.services.maintenance.MaintenanceRequestPictureUploadPortalService;
import com.propertyvista.portal.shared.themes.EntityViewTheme;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;

public class MaintenanceRequestPage extends CPortalEntityForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestPage.class);

    private final Button btnCancel;

    private final Button btnPrint;

    private FormPanel imagePanel;

    private FormPanel scheduledPanel;

    private RateIt rateIt;

    private Anchor communicationLink;

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
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Basic Information"));
        formPanel.append(Location.Left, proto().requestId(), new CLabel<String>()).decorate().componentWidth(250);
        formPanel.append(Location.Left, proto().reportedForOwnUnit()).decorate().componentWidth(250);
        formPanel.append(Location.Left, proto().category(), new CEntityLabel<MaintenanceRequestCategory>() {
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
        }).decorate().componentWidth(250);

        formPanel.append(Location.Left, proto().summary()).decorate().componentWidth(250);
        formPanel.append(Location.Left, proto().description()).decorate().componentWidth(250);
        formPanel.append(Location.Left, proto().priority(), new CEntityLabel<MaintenanceRequestPriority>()).decorate().componentWidth(250);
        formPanel.append(Location.Left, proto().status().phase()).decorate().componentWidth(250);

        imagePanel = new FormPanel(this);
        CImageSlider<MaintenanceRequestPicture> imageSlider = new CImageSlider<MaintenanceRequestPicture>(MaintenanceRequestPicture.class,
                GWT.<MaintenanceRequestPictureUploadPortalService> create(MaintenanceRequestPictureUploadPortalService.class), new VistaFileURLBuilder(
                        MaintenanceRequestPicture.class)) {
            @Override
            protected FolderImages getFolderIcons() {
                return VistaImages.INSTANCE;
            }

            @Override
            public IsWidget getImageEntryView(CForm<MaintenanceRequestPicture> entryForm) {
                FormPanel formPanel = new FormPanel(entryForm);
                formPanel.append(Location.Left, entryForm.proto().description()).decorate().componentWidth(150);
                return formPanel;
            }
        };
        imageSlider.setImageSize(250, 240);
        imagePanel.append(Location.Left, proto().pictures(), imageSlider).decorate().componentWidth(100);
        formPanel.append(Location.Left, imagePanel);

        scheduledPanel = new FormPanel(this);

        scheduledPanel.append(Location.Left, proto().petInstructions()).decorate().componentWidth(250);

        scheduledPanel.append(Location.Left, proto().preferredDate1()).decorate().componentWidth(100);
        scheduledPanel.append(Location.Left, proto().preferredTime1()).decorate().componentWidth(100);
        scheduledPanel.append(Location.Left, proto().preferredDate2()).decorate().componentWidth(100);
        scheduledPanel.append(Location.Left, proto().preferredTime2()).decorate().componentWidth(100);
        formPanel.append(Location.Left, scheduledPanel);

        formPanel.br();

        communicationLink = new Anchor(i18n.tr("Associated Communication"), new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new PortalSiteMap.Message.MessagePage(getValue().message().getPrimaryKey()));
            }
        });
        formPanel.append(Location.Left, communicationLink);
        formPanel.br();

        rateIt = new RateIt(5);
        rateIt.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                if (event.getValue() != null) {
                    ((MaintenanceRequestPagePresenter) getView().getPresenter()).rateRequest(event.getValue());
                }

            }
        });

        SimplePanel rateItHolder = new SimplePanel(rateIt);
        rateItHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        formPanel.append(Location.Left, rateItHolder);

        formPanel.br();
        return formPanel;
    }

    @Override
    protected FormDecorator<MaintenanceRequestDTO> createDecorator() {
        FormDecorator<MaintenanceRequestDTO> decorator = super.createDecorator();

        decorator.addHeaderToolbarWidget(btnCancel);
        decorator.addHeaderToolbarWidget(btnPrint);

        return decorator;
    }

    @Override
    protected void onValueSet(boolean populate) {
        MaintenanceRequestDTO mr = getValue();

        communicationLink.setVisible(mr != null && mr.message() != null && !mr.message().isNull() && !mr.message().isPrototype());

        btnCancel.setVisible(mr != null && StatusPhase.open().contains(mr.status().phase().getValue()));
        if (mr == null) {
            return;
        }
        imagePanel.setVisible(mr.pictures() != null && !mr.pictures().isNull() && !mr.pictures().isEmpty());
        rateIt.setVisible(StatusPhase.Resolved.equals(mr.status().phase().getValue()));
        scheduledPanel.setVisible(mr.reportedForOwnUnit().getValue(false));
        if (!mr.surveyResponse().isNull() && !mr.surveyResponse().isEmpty() && !mr.surveyResponse().rating().isNull()) {
            rateIt.setRating(mr.surveyResponse().rating().getValue());
        }
    }
}
