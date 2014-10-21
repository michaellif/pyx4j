/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 30, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.maintenance;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.RateIt;

import com.propertyvista.domain.maintenance.MaintenanceRequestPriority.PriorityLevel;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.portal.resident.ui.maintenance.MaintenanceDashboardView.MaintenanceDashboardPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceRequestStatusDTO;

public class MaintenanceRequestFolderItem extends CForm<MaintenanceRequestStatusDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestFolderItem.class);

    private Anchor detailsLink;

    private Anchor communicationLink;

    private RateIt rateIt;

    private final MaintenanceDashboardPresenter presenter;

    public MaintenanceRequestFolderItem(MaintenanceDashboardPresenter presenter) {
        super(MaintenanceRequestStatusDTO.class);

        this.presenter = presenter;
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().subject(), new CLabel<String>()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().description(), new CLabel<String>()).decorate().componentWidth(250);
        formPanel.append(Location.Left, proto().status().phase(), new CLabel<StatusPhase>()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().priority().level(), new CLabel<PriorityLevel>()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().lastUpdated(), new CLabel<String>()).decorate().componentWidth(180);

        communicationLink = new Anchor(i18n.tr("Associated Communication"), new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Message.MessagePage(getValue().message().getPrimaryKey()));
            }
        });
        formPanel.append(Location.Left, communicationLink);

        formPanel.br();

        rateIt = new RateIt(5);
        rateIt.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                if (event.getValue() != null) {
                    presenter.rateRequest(getValue().getPrimaryKey(), event.getValue());
                }
            }
        });

        SimplePanel rateItHolder = new SimplePanel(rateIt);
        rateItHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        formPanel.append(Location.Left, rateItHolder);

        detailsLink = new Anchor(i18n.tr("View Details"), new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Maintenance.MaintenanceRequestPage().formPlace(getValue().getPrimaryKey()));
            }
        });
        detailsLink.getElement().getStyle().setMarginTop(10, Unit.PX);
        formPanel.append(Location.Left, detailsLink);

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        MaintenanceRequestStatusDTO mr = getValue();
        rateIt.setVisible(StatusPhase.Resolved.equals(mr.status().phase().getValue()));
        if (!mr.surveyResponse().isNull() && !mr.surveyResponse().isEmpty() && !mr.surveyResponse().rating().isNull()) {
            rateIt.setRating(mr.surveyResponse().rating().getValue());
        }
        communicationLink.setVisible(getValue() != null && getValue().message() != null && !getValue().message().isNull()
                && !getValue().message().isPrototype());
    }
}