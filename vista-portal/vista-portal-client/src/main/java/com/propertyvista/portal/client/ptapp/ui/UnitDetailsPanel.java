/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import static com.pyx4j.commons.HtmlUtils.h3;

import java.util.Map;
import java.util.TreeMap;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.domain.ApptUnit;
import com.propertyvista.portal.domain.MarketRent;

import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupInteger;

public class UnitDetailsPanel extends FlowPanel {

    private static I18n i18n = I18nFactory.getI18n(UnitsTable.class);

    public UnitDetailsPanel() {

    }

    public void showUnitDetail(final ApptUnit unit, final MarketRent marketRent) {
        this.clear();

        FlowPanel unitDetailPanel = new FlowPanel();
        unitDetailPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);

        FlowPanel infoPanel = new FlowPanel();
        infoPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        infoPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        infoPanel.add(new HTML(h3(i18n.tr("Info"))));
        infoPanel.add(new HTML(unit.infoDetails().getStringView()));
        infoPanel.getElement().getStyle().setMarginRight(3, Unit.PCT);
        infoPanel.setWidth("30%");
        unitDetailPanel.add(infoPanel);

        FlowPanel amenitiesPanel = new FlowPanel();
        amenitiesPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        amenitiesPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        amenitiesPanel.add(new HTML(h3(i18n.tr("Amenities/Utilities"))));
        amenitiesPanel.add(new HTML(unit.amenities().getStringView()));
        amenitiesPanel.add(new HTML(unit.utilities().getStringView()));
        amenitiesPanel.getElement().getStyle().setMarginRight(3, Unit.PCT);
        amenitiesPanel.setWidth("30%");
        unitDetailPanel.add(amenitiesPanel);

        FlowPanel concessionPanel = new FlowPanel();
        concessionPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        concessionPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        concessionPanel.add(new HTML(h3(i18n.tr("Concession"))));
        concessionPanel.add(new HTML(unit.concessions().getStringView()));
        concessionPanel.getElement().getStyle().setMarginRight(3, Unit.PCT);
        concessionPanel.setWidth("30%");
        unitDetailPanel.add(concessionPanel);

        Widget sp = new ViewLineSeparator(98, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
        sp.getElement().getStyle().setPaddingLeft(0, Unit.EM);
        unitDetailPanel.add(sp);

        FlowPanel addonsPanel = new FlowPanel();
        addonsPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        addonsPanel.add(new HTML(h3(i18n.tr("Available add-ons"))));
        addonsPanel.setWidth("33%");
        unitDetailPanel.add(addonsPanel);
        addonsPanel.add(new HTML(unit.concessions().getStringView()));

        sp = new ViewLineSeparator(98, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
        sp.getElement().getStyle().setPaddingLeft(0, Unit.EM);
        unitDetailPanel.add(sp);

        // lease term:
        unitDetailPanel.add(new HTML());
        unitDetailPanel.add(new HTML(h3(i18n.tr("Lease Terms"))));
        Map<Integer, String> options = new TreeMap<Integer, String>();
        for (final MarketRent mr : unit.marketRent()) {
            options.put(mr.leaseTerm().getValue(), mr.leaseTerm().getStringView() + " $" + mr.rent().amount().getValue());
        }
        CRadioGroupInteger mr = new CRadioGroupInteger(CRadioGroup.Layout.VERTICAL, options);
        if (unit.marketRent().contains(marketRent)) {
            mr.populate(marketRent.leaseTerm().getValue());
        }
        mr.addValueChangeHandler(new ValueChangeHandler<Integer>() {

            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                for (final MarketRent mr : unit.marketRent()) {
                    if (event.getValue().equals(mr.leaseTerm().getValue())) {
                        marketRent.setValue(mr.getValue());
                        break;
                    }
                }
            }
        });

        unitDetailPanel.add(mr);

        unitDetailPanel.getElement().getStyle().setPadding(1, Unit.EM);
        unitDetailPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
        unitDetailPanel.getElement().getStyle().setBackgroundColor("white");
        this.add(unitDetailPanel);
    }

    public void hide() {
        this.clear();
    }

}
