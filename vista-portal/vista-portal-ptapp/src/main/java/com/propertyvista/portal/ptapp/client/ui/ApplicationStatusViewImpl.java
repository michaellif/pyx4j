/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.ptapp.client.resources.PortalResources;
import com.propertyvista.portal.rpc.ptapp.services.PtAuthenticationService;

public class ApplicationStatusViewImpl extends FlowPanel implements ApplicationStatusView {

    private static final I18n i18n = I18n.get(ApplicationStatusViewImpl.class);

    public ApplicationStatusViewImpl() {

        VerticalPanel main = new VerticalPanel();

        HTML titleHtml = new HTML(HtmlUtils.h3(i18n.tr("Current status of Your Application")));
        main.add(titleHtml);
        main.setCellHorizontalAlignment(titleHtml, HasHorizontalAlignment.ALIGN_CENTER);

        HTML messageHtml = new HTML(PortalResources.INSTANCE.completionMessage().getText());
        messageHtml.setWidth("50em");
        messageHtml.getElement().getStyle().setMarginTop(1, Unit.EM);
        main.add(messageHtml);
        main.setCellHorizontalAlignment(messageHtml, HasHorizontalAlignment.ALIGN_CENTER);

        HorizontalPanel actions = new HorizontalPanel();

//        Button backAction = new Button(i18n.tr("View Status"));
//        backAction.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
//        backAction.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(ApplicationStatus.class));
//            }
//        });
//        actions.add(backAction);

        Button logoutAction = new Button(i18n.tr("Log Out"));
        logoutAction.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        logoutAction.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ClientContext.logout((AuthenticationService) GWT.create(PtAuthenticationService.class), null);
            }
        });
        actions.add(logoutAction);

        actions.setSpacing(15);
        actions.getElement().getStyle().setMarginTop(2, Unit.EM);
        main.add(actions);
        main.setCellHorizontalAlignment(actions, HasHorizontalAlignment.ALIGN_CENTER);

        main.setWidth("100%");
        add(main);

        getElement().getStyle().setMarginBottom(1, Unit.EM);
        setWidth("100%");
    }
}