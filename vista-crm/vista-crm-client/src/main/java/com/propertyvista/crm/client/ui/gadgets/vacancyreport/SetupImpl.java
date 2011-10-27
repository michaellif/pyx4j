/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 24, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.vacancyreport;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dashboard.IGadget.ISetup;

import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportGadgetSettings;

public class SetupImpl implements ISetup {
    private final static I18n i18n = I18n.get(SetupImpl.class);

    UnitVacancyReportGadgetSettings settings;

    private final CheckBox showUnits;

    private final CheckBox showSummary;

    private final CheckBox showTurnoverAnalysis;

    private final TextBox itemsPerPage;

    private final VerticalPanel setupPanel;

    private final FlexTable listerSettingsPanel;

    private final VacancyReportGadget gadget;

    /**
     * @param settings
     *            must not be <code>null</code>.
     * @param gadget
     */
    public SetupImpl(UnitVacancyReportGadgetSettings settings, VacancyReportGadget gadget) {
        this.settings = settings;
        this.gadget = gadget;

        final double INDENTATION = 1d;
        final Label itemsPerPageLabel = new Label(i18n.tr("Units per page") + ":");
        showUnits = new CheckBox(i18n.tr("Units"));
        showUnits.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                itemsPerPage.setVisible(event.getValue());
                itemsPerPageLabel.setVisible(event.getValue());
            }
        });
        showUnits.setValue(settings.showUnits().getValue());

        itemsPerPageLabel.getElement().getStyle().setPaddingLeft(5, Unit.EM);
        itemsPerPageLabel.getElement().getStyle().setPaddingRight(1, Unit.EM);
        itemsPerPageLabel.setVisible(showUnits.getValue());
        itemsPerPage = new TextBox();
        itemsPerPage.setText(String.valueOf(settings.itemsPerPage().getValue()));
        itemsPerPage.setWidth("3em");
        itemsPerPage.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        itemsPerPage.setVisible(showUnits.getValue());

        listerSettingsPanel = new FlexTable();
        listerSettingsPanel.setWidget(0, 0, showUnits);
        listerSettingsPanel.setWidget(0, 1, itemsPerPageLabel);
        listerSettingsPanel.setWidget(0, 2, itemsPerPage);
        listerSettingsPanel.getFlexCellFormatter().setColSpan(0, 1, 2);
        listerSettingsPanel.getElement().getStyle().setPadding(0, Unit.EM);
        listerSettingsPanel.getElement().getStyle().setPaddingLeft(INDENTATION, Unit.EM);

        showSummary = new CheckBox(i18n.tr("Summary"));
        showSummary.setValue(settings.showSummary().getValue());
        showSummary.getElement().getStyle().setPaddingLeft(INDENTATION, Unit.EM);

        showTurnoverAnalysis = new CheckBox(i18n.tr("Turnover Analysis"));
        showTurnoverAnalysis.setValue(settings.showTurnoverAnalysis().getValue());
        showTurnoverAnalysis.getElement().getStyle().setPaddingLeft(INDENTATION, Unit.EM);

        setupPanel = new VerticalPanel();
        setupPanel.setWidth("100%");
        setupPanel.setHeight("100%");

        setupPanel.add(new Label(i18n.tr("Display") + ":"));
        setupPanel.add(listerSettingsPanel);
        setupPanel.add(showSummary);
        setupPanel.add(showTurnoverAnalysis);
        setupPanel.getElement().getStyle().setPaddingLeft(2, Unit.EM);
        setupPanel.getElement().getStyle().setPaddingTop(1, Unit.EM);
    }

    @Override
    public Widget asWidget() {
        return setupPanel;
    }

    @Override
    public boolean onStart() {
        gadget.suspend();
        return true;
    }

    @Override
    public boolean onOk() {
        int itemsPerPageCount = 5;
        try {
            itemsPerPageCount = Integer.parseInt(itemsPerPage.getText());
        } finally {
            settings.itemsPerPage().setValue(itemsPerPageCount);
            settings.showSummary().setValue(showSummary.getValue());
            settings.showTurnoverAnalysis().setValue(showTurnoverAnalysis.getValue());
            settings.showUnits().setValue(showUnits.getValue());
        }
        gadget.stop();
        gadget.start();

        return true;
    }

    @Override
    public void onCancel() {
        gadget.start();
    }

}
