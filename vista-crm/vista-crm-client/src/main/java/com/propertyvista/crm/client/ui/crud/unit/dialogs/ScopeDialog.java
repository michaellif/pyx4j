/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit.dialogs;

import java.util.EnumSet;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;

public abstract class ScopeDialog extends OkCancelDialog {

    private final static I18n i18n = I18n.get(ScopeDialog.class);

    public enum ScopingResult {

        available, renovation, offMarket

    }

    private ScopingResult scopingResult;

    private final String SCOPING_RESULT = "scoping_result";

    private RadioButton availableChoice;

    private RadioButton renovationChoice;

    private CDatePicker renovationEndDate;

    private Label renovationEndLabel;

    private RadioButton offMarketChoice;

    private Label offMarketStartLabel;

    private CDatePicker offMarketStartDate;

    private Label offMarketTypeLabel;

    private CComboBox<OffMarketType> offMarketType;

    public ScopeDialog() {
        super(i18n.tr("Scoping Result"));

        FormFlexPanel panel = new FormFlexPanel();

        ValueChangeHandler<Boolean> handler = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (availableChoice.getValue()) {
                    scopingResult = ScopingResult.available;
                } else if (renovationChoice.getValue()) {
                    scopingResult = ScopingResult.renovation;
                } else if (offMarketChoice.getValue()) {
                    scopingResult = ScopingResult.offMarket;
                }
                boolean displayRenovationDate = scopingResult == ScopingResult.renovation;
                renovationEndLabel.setVisible(displayRenovationDate);
                renovationEndDate.setVisible(displayRenovationDate);

                boolean displayOffMarket = scopingResult == ScopingResult.offMarket;
                offMarketStartLabel.setVisible(displayOffMarket);
                offMarketStartDate.setVisible(displayOffMarket);

                offMarketTypeLabel.setVisible(displayOffMarket);
                offMarketType.setVisible(displayOffMarket);
            }
        };

        int row = -1;
        panel.setWidget(++row, 0, availableChoice = new RadioButton(SCOPING_RESULT, new SafeHtmlBuilder().appendEscaped(i18n.tr("Availalble")).toSafeHtml()));
        availableChoice.addValueChangeHandler(handler);
        panel.setWidget(++row, 0, renovationChoice = new RadioButton(SCOPING_RESULT, new SafeHtmlBuilder().appendEscaped(i18n.tr("Renovation")).toSafeHtml()));
        renovationChoice.addValueChangeHandler(handler);
        panel.setWidget(++row, 0, offMarketChoice = new RadioButton(SCOPING_RESULT, new SafeHtmlBuilder().appendEscaped(i18n.tr("Off Market")).toSafeHtml()));
        offMarketChoice.addValueChangeHandler(handler);

        panel.setWidget(++row, 0, renovationEndLabel = new Label(new SafeHtmlBuilder().appendEscaped(i18n.tr("Renovation Ends On") + ": ").toSafeHtml()
                .asString()));
        renovationEndLabel.setVisible(false);
        panel.setWidget(row, 1, renovationEndDate = new CDatePicker());
        renovationEndDate.setVisible(false);
        renovationEndDate.setValue(new LogicalDate());

        panel.setWidget(++row, 0, offMarketStartLabel = new Label(new SafeHtmlBuilder().appendEscaped(i18n.tr("Off Market Begins On") + ": ").toSafeHtml()
                .asString()));
        offMarketStartLabel.setVisible(false);
        panel.setWidget(row, 1, offMarketStartDate = new CDatePicker());
        offMarketStartDate.setVisible(false);
        offMarketStartDate.setValue(new LogicalDate());

        panel.setWidget(++row, 0,
                offMarketTypeLabel = new Label(new SafeHtmlBuilder().appendEscaped(i18n.tr("Off Market Type") + ": ").toSafeHtml().asString()));
        offMarketTypeLabel.setVisible(false);
        panel.setWidget(row, 1, offMarketType = new CComboBox<AptUnitOccupancySegment.OffMarketType>());
        offMarketType.setVisible(false);
        offMarketType.setOptions(EnumSet.allOf(OffMarketType.class));
        setBody(panel);
    }

    protected ScopingResult getResult() {
        return scopingResult;
    }

    protected LogicalDate getOffMarketStartDate() {
        if (getResult() == ScopingResult.offMarket) {
            return new LogicalDate(offMarketStartDate.getValue());
        } else {
            throw new IllegalStateException("can't produce off market start date when not off market");
        }
    }

    protected OffMarketType getOffMarketType() {
        if (getResult() == ScopingResult.offMarket) {
            return offMarketType.getValue();
        } else {
            throw new IllegalStateException("can't produce off market type when not off market");
        }
    }

    protected LogicalDate getRenovationEndDate() {
        if (getResult() == ScopingResult.renovation) {
            return new LogicalDate(renovationEndDate.getValue());
        } else {
            throw new IllegalStateException("can't produce renovation end date when the selected scoping result is not renovation");
        }
    }

}
