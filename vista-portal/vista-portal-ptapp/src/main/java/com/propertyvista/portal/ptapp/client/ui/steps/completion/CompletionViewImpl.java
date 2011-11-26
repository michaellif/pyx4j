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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.ptapp.client.ui.RetrievePasswordViewImpl;

public class CompletionViewImpl extends FlowPanel implements CompletionView {

    private static I18n i18n = I18n.get(RetrievePasswordViewImpl.class);

    private final HTML titleHtml;

    private final HTML messageHtml;

    private final Button actionButton;

    public CompletionViewImpl() {

        VerticalPanel main = new VerticalPanel();

        titleHtml = new HTML(HtmlUtils.h2(i18n.tr("Application completed!")));
        main.add(titleHtml);
        main.setCellHorizontalAlignment(titleHtml, HasHorizontalAlignment.ALIGN_CENTER);

        messageHtml = new HTML(i18n.tr("Congratulation! You have successfully completed your application."));
        main.add(messageHtml);
        main.setCellHorizontalAlignment(messageHtml, HasHorizontalAlignment.ALIGN_CENTER);

        actionButton = new Button("Back");
        actionButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        actionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        actionButton.getElement().getStyle().setMarginTop(1, Unit.EM);
        main.add(actionButton);
        main.setCellHorizontalAlignment(actionButton, HasHorizontalAlignment.ALIGN_CENTER);

        main.setWidth("100%");
        add(main);

        getElement().getStyle().setMarginBottom(1, Unit.EM);
        setWidth("100%");
    }
}