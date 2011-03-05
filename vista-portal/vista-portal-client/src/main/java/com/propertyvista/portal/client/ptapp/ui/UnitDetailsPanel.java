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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.domain.MarketRent;

import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class UnitDetailsPanel extends FlowPanel {

    private static I18n i18n = I18nFactory.getI18n(UnitsTable.class);

    public UnitDetailsPanel() {

    }

    public void showUnitDetail(com.propertyvista.portal.domain.ApptUnit unit, MarketRent marketRent) {
        this.clear();

        FlowPanel unitDetailPanel = new FlowPanel();
        unitDetailPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);

        FlowPanel infoPanel = new FlowPanel();
        infoPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        infoPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        infoPanel.add(new HTML(h3(i18n.tr("Info"))));
        infoPanel.add(new HTML(unit.infoDetails().getStringView()));
        infoPanel.setWidth("33%");
        unitDetailPanel.add(infoPanel);

        FlowPanel amenitiesPanel = new FlowPanel();
        amenitiesPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        amenitiesPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        amenitiesPanel.add(new HTML(h3(i18n.tr("Amenities/Utilities:"))));
        amenitiesPanel.add(new HTML(unit.amenities().getStringView()));
        amenitiesPanel.add(new HTML(unit.utilities().getStringView()));
        amenitiesPanel.setWidth("33%");
        unitDetailPanel.add(amenitiesPanel);

        FlowPanel concessionPanel = new FlowPanel();
        concessionPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        concessionPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        concessionPanel.add(new HTML(h3(i18n.tr("Concession"))));
        concessionPanel.add(new HTML(unit.concessions().getStringView()));
        concessionPanel.setWidth("33%");
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
        FlowPanel leaseTermsPanel = new FlowPanel();

        String groupName = "TermVariants" + unit.hashCode();
        RadioButton term = null; // fill the variants:
        for (final MarketRent mr : unit.marketRent()) {
            term = new RadioButton(groupName, mr.leaseTerm().getStringView() + "&nbsp;&nbsp;&nbsp;&nbsp; month &nbsp;&nbsp;&nbsp;&nbsp; $"
                    + mr.rent().amount().getValue(), true);

            // set preselected term for selected unit:
            term.setValue(mr.equals(marketRent));

            term.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
//                    getValue().markerRent().setValue(mr.getValue());
                }
            });

            term.getElement().getStyle().setDisplay(Display.BLOCK);
            leaseTermsPanel.add(term);
        }

        // set last (longest) term for all other units:
//        if (term != null && !unit.equals(getValue().selectedUnit())) {
//            term.setValue(true);
//        }

        unitDetailPanel.add(leaseTermsPanel);

        unitDetailPanel.getElement().getStyle().setPadding(1, Unit.EM);
        unitDetailPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
        unitDetailPanel.getElement().getStyle().setBackgroundColor("white");
        this.add(unitDetailPanel);
    }

    public void hide() {
        this.clear();
//        this.setVisible(false);
    }

//    public void selectUnitRow(FlowPanel unitRowPanel) {
//
//        // tweak selected unit data view:
//        for (Widget w : content) {
//            if (w.getStyleName().contains(DEFAULT_STYLE_PREFIX + StyleSuffix.unitRowPanel)) {
//                w.removeStyleDependentName(StyleDependent.selected.name());
//            }
//            // hide all detail panels:
//            if (w.getStyleName().contains(DEFAULT_STYLE_PREFIX + StyleSuffix.unitDetailPanel)) {
//                w.removeStyleDependentName(StyleDependent.selected.name());
//                w.setVisible(false);
//            }
//        }
//
//        // show current selected row with details:
//        unitRowPanel.addStyleDependentName(StyleDependent.selected.name());
//        Widget unitDetailPanel = content.getWidget(content.getWidgetIndex(unitRowPanel) + 1);
//        unitDetailPanel.addStyleDependentName(StyleDependent.selected.name());
//        unitDetailPanel.setVisible(true);
//    }

//    private double minRentValue(com.propertyvista.portal.domain.Unit unit) {
//        double rent = Double.MAX_VALUE;
//        for (MarketRent mr : unit.marketRent())
//            rent = Math.min(rent, mr.rent().amount().getValue());
//        return (rent != Double.MAX_VALUE ? rent : 0);
//    }
//
//    private double minRentValue(IList<com.propertyvista.portal.domain.Unit> units) {
//        double rent = Double.MAX_VALUE;
//        for (com.propertyvista.portal.domain.Unit u : units)
//            rent = Math.min(rent, minRentValue(u));
//        return (rent != Double.MAX_VALUE ? rent : 0);
//    }
}
