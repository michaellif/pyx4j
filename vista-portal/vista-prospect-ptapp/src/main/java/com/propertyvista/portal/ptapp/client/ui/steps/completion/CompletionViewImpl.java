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
package com.propertyvista.portal.ptapp.client.ui.steps.completion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
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
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.ptapp.client.resources.PortalResources;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.services.PtAuthenticationService;

public class CompletionViewImpl extends FlowPanel implements CompletionView {

    private static final I18n i18n = I18n.get(CompletionViewImpl.class);

    public CompletionViewImpl() {

        VerticalPanel main = new VerticalPanel();

        HTML titleHtml = new HTML(HtmlUtils.h3(i18n.tr("Congratulation! You have successfully completed your application!")));
        main.add(titleHtml);
        main.setCellHorizontalAlignment(titleHtml, HasHorizontalAlignment.ALIGN_CENTER);

        HTML messageHtml = new HTML(PortalResources.INSTANCE.completionMessage().getText());
        messageHtml.setWidth("50em");
        messageHtml.getElement().getStyle().setMarginTop(1, Unit.EM);
        main.add(messageHtml);
        main.setCellHorizontalAlignment(messageHtml, HasHorizontalAlignment.ALIGN_CENTER);

        HorizontalPanel actions = new HorizontalPanel();

        Button viewAction = new Button(i18n.tr("View Status"), new Command() {

            @Override
            public void execute() {
                // TODO Auto-generated method stub
                AppSite.getPlaceController().goTo(new PtSiteMap.ApplicationStatus());
            }

        });
        viewAction.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        actions.add(viewAction);

        Button logoutAction = new Button(i18n.tr("Log Out"), new Command() {

            @Override
            public void execute() {
                // TODO Auto-generated method stub
                ClientContext.logout((AuthenticationService) GWT.create(PtAuthenticationService.class), null);
            }

        });
        logoutAction.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
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