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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.ptapp.client.ui.steps.summary.SummaryViewForm;
import com.propertyvista.portal.rpc.ptapp.dto.ApplicationStatusSummaryDTO;

public class ApplicationStatusViewImpl extends FlowPanel implements ApplicationStatusView {

    private static final I18n i18n = I18n.get(ApplicationStatusViewImpl.class);

    private static final String showSummary = i18n.tr("Show Summary");

    private static final String hideSummary = i18n.tr("Hide Summary");

    private Presenter presenter;

    private final ApplicationStatusViewForm statusForm;

    private final SummaryViewForm summaryForm;

    private final Anchor summaryAction;

    public ApplicationStatusViewImpl() {

        statusForm = new ApplicationStatusViewForm();
        statusForm.setEditable(false);
        statusForm.initContent();

        summaryForm = new SummaryViewForm(true);
        summaryForm.setEditable(false);
        summaryForm.setVisible(false);
        summaryForm.initContent();

        summaryAction = new Anchor(showSummary);
        summaryAction.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        summaryAction.getElement().getStyle().setProperty("lineHeight", "2em");
        summaryAction.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        summaryAction.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                summaryForm.setVisible(!summaryForm.isVisible());
                summaryAction.setText(summaryForm.isVisible() ? hideSummary : showSummary);
            }
        });

        add(createContent());
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Current status of Your Application"), summaryAction);
        main.setWidget(++row, 0, statusForm.asWidget());
        main.setWidget(++row, 0, summaryForm.asWidget());

        return main;
    }

    @Override
    public void populate(ApplicationStatusSummaryDTO entity) {
        statusForm.populate(entity.status());
        summaryForm.populate(entity.summary());
    }
}