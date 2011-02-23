/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.domain.MarketRent;
import com.propertyvista.portal.domain.pt.AvailableUnitsByFloorplan;
import com.propertyvista.portal.domain.pt.UnitSelection;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.widgets.client.Button;

public class ApartmentViewForm extends CEntityForm<UnitSelection> {

    private AvailableUnitsTable availableUnitsTable;

    private ApartmentViewPresenter presenter;

    private com.propertyvista.portal.domain.Unit selectedUnit;

    private int selectedTerm;

    public ApartmentViewForm() {
        super(UnitSelection.class);
    }

    public ApartmentViewPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(ApartmentViewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();

        // Form first table header: 
        FlowPanel header = new FlowPanel();
        HTML caption = new HTML("<h2>Available Units</h2>");
        caption.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        caption.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        caption.getElement().getStyle().setMarginRight(8, Unit.EM);
        caption.getElement().getStyle().setPaddingBottom(0, Unit.EM);
        header.add(caption);

        VistaWidgetDecorator dateFrom = new VistaWidgetDecorator(create(proto().selectionCriteria().availableFrom(), this), 0, 7.1);
        dateFrom.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        dateFrom.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        header.add(dateFrom);

        VistaWidgetDecorator dateTo = new VistaWidgetDecorator(create(proto().selectionCriteria().availableTo(), this), 0, 7.1);
        dateTo.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        dateTo.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        header.add(dateTo);

        Button btn = new Button("Change");
        btn.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        btn.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        btn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.selectByDates(getValue());

            }
        });
        header.add(btn);

        Widget w = new ViewHeaderDecorator(header);
        w.getElement().getStyle().setMarginBottom(0, Unit.EM);
        w.getElement().getStyle().setPaddingTop(0.5, Unit.EM);
        w.setHeight("2.2em");
        main.add(w);

        // units table:
        main.add(new HTML());
        availableUnitsTable = new AvailableUnitsTable();
        main.add(availableUnitsTable);

        // start date:
        w = new ViewLineSeparator();
        w.getElement().getStyle().setMarginTop(1, Unit.EM);
        w.getElement().getStyle().setMarginBottom(1, Unit.EM);
        main.add(w);

        caption = new HTML("<h3>Start Rent Date</h3>");
        caption.getElement().getStyle().setFloat(Float.LEFT);
        caption.getElement().getStyle().setMarginTop(3, Unit.PX);
        main.add(caption);
        main.add(new VistaWidgetDecorator(create(proto().rentStart(), this), 0, 7.1));

        setWidget(main);
    }

    @Override
    public void populate(UnitSelection entity) {
        //        System.out.println(">>>" + entity.availableUnits().units().toString());
        availableUnitsTable.populate(entity);
    }

    @Override
    public UnitSelection getValue() {
        UnitSelection v = super.getValue();
        if (v != null) {
            v.selectedUnit().set(selectedUnit);
            v.selectedUnitLeaseTerm().setValue(selectedTerm);
        }
        return v;

    }

    class AvailableUnitsTable extends FlowPanel {

        private final UnitSelection proto = EntityFactory.getEntityPrototype(UnitSelection.class);

        private final FlowPanel content;

        Map<String, String> tableLayout = new LinkedHashMap<String, String>();

        private static final String UNIT_ROW_PANEL_STYLENAME = "unitRowPanel";

        private static final String UNIT_DETAIL_PANEL_STYLENAME = "unitDetailPanel";

        public AvailableUnitsTable() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            setWidth("70%");

            tableLayout.put("Plan", "10%");
            tableLayout.put("Type", "20%");
            tableLayout.put("Rent", "10%");
            tableLayout.put("Deposit", "10%");
            tableLayout.put("Beds", "10%");
            tableLayout.put("Baths", "10%");
            tableLayout.put("Sq F", "10%");
            tableLayout.put("Available", "20%");

            FlowPanel header = new FlowPanel();
            header.getElement().getStyle().setBackgroundColor("lightGray");
            header.getElement().getStyle().setMarginBottom(1, Unit.EM);
            header.setHeight("2.3em");
            lineupWidth(header);

            // fill header:
            for (Entry<String, String> e : tableLayout.entrySet()) {
                HTML label = new HTML(e.getKey());
                label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                label.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
                label.asWidget().getElement().getStyle().setProperty("lineHeight", "2em");
                label.setWidth(e.getValue());
                header.add(label);
            }

            add(header);

            content = new FlowPanel();
            add(content);
        }

        protected void populate(UnitSelection units) {
            ApartmentViewForm.super.populate(units);
            content.clear();

            populateFloorplan(units.availableUnits());
            populateUnits(units.availableUnits());
        }

        private void populateFloorplan(AvailableUnitsByFloorplan availableUnits) {
            FlowPanel floorplan = new FlowPanel();
            floorplan.getElement().getStyle().setPaddingLeft(1, Unit.EM);

            addCell("&nbsp", tableLayout.get("Plan"), floorplan);
            addCell(availableUnits.floorplan().name().getStringView(), tableLayout.get("Type"), floorplan);
            addCell("From " + minRentValue(availableUnits.units()) + "$", tableLayout.get("Rent"), floorplan);
            addCell("&nbsp", tableLayout.get("Deposit"), floorplan);
            addCell("&nbsp", tableLayout.get("Beds"), floorplan);
            addCell("&nbsp", tableLayout.get("Baths"), floorplan);
            addCell(availableUnits.floorplan().area().getStringView(), tableLayout.get("Sq F"), floorplan);
            addCell("&nbsp", tableLayout.get("Available"), floorplan);

            lineupWidth(floorplan);
            content.add(floorplan);

            Widget sp = new ViewLineSeparator();
            sp.getElement().getStyle().setMarginTop(1, Unit.EM);
            sp.getElement().getStyle().setMarginBottom(0.5, Unit.EM);
            sp.setWidth("100%");
            content.add(sp);
        }

        private void populateUnits(AvailableUnitsByFloorplan availableUnits) {

            for (final com.propertyvista.portal.domain.Unit unit : availableUnits.units()) {
                final FlowPanel unitRowPanel = new FlowPanel();
                unitRowPanel.setStyleName(UNIT_ROW_PANEL_STYLENAME);
                unitRowPanel.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                unitRowPanel.addDomHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        for (Widget w : content) {
                            // hide all detail panels:
                            if (w.getStyleName().equals(UNIT_DETAIL_PANEL_STYLENAME))
                                w.setVisible(false);
                            // clear selected background:
                            if (w.getStyleName().equals(UNIT_ROW_PANEL_STYLENAME))
                                w.getElement().getStyle().setBackgroundColor("");
                        }

                        // show current selected row with details: 
                        unitRowPanel.getElement().getStyle().setBackgroundColor("lightGray");
                        content.getWidget(content.getWidgetIndex(unitRowPanel) + 1).setVisible(true);

                        selectedUnit = unit;
                    }
                }, ClickEvent.getType());

                addCell("&nbsp", tableLayout.get("Plan"), unitRowPanel);
                addCell(unit.unitType().getStringView(), tableLayout.get("Type"), unitRowPanel);
                addCell(Double.toString(minRentValue(unit)) + "$", tableLayout.get("Rent"), unitRowPanel);
                addCell(unit.requiredDeposit().getStringView() + "$", tableLayout.get("Deposit"), unitRowPanel);
                addCell(unit.bedrooms().getStringView(), tableLayout.get("Beds"), unitRowPanel);
                addCell(unit.bathrooms().getStringView(), tableLayout.get("Baths"), unitRowPanel);
                addCell(unit.area().getStringView(), tableLayout.get("Sq F"), unitRowPanel);
                addCell(unit.avalableForRent().getStringView(), tableLayout.get("Available"), unitRowPanel);

                lineupWidth(unitRowPanel);
                content.add(unitRowPanel);

                populateUnitDetail(unit);
            }
        }

        private void addCell(String text, String width, FlowPanel container) {
            HTML label = new HTML(text);
            label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            label.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);
            label.setWidth(width);
            container.add(label);
        }

        private void populateUnitDetail(com.propertyvista.portal.domain.Unit unit) {
            FlowPanel unitDetailPanel = new FlowPanel();
            unitDetailPanel.setStyleName(UNIT_DETAIL_PANEL_STYLENAME);

            // TODO fill it here...
            unitDetailPanel.add(new HTML("Unit details goes here..."));

            // lease term:
            unitDetailPanel.add(new HTML());
            unitDetailPanel.add(new HTML("<h4>Lease Terms</h4>"));
            FlowPanel leaseTermsPanel = new FlowPanel();

            String groupName = "TermVariants" + unit.hashCode();
            RadioButton term = null; // fill the variants:
            for (final MarketRent mr : unit.marketRent()) {
                term = new RadioButton(groupName, mr.leaseTerm().getStringView() + "&nbsp&nbsp&nbsp&nbsp month &nbsp&nbsp&nbsp&nbsp "
                        + mr.rent().amount().getValue() + "$", true);
                term.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        selectedTerm = mr.leaseTerm().getValue();
                    }
                });

                term.getElement().getStyle().setDisplay(Display.BLOCK);
                leaseTermsPanel.add(term);
            }
            if (term != null)
                term.setValue(true); // select last term...

            unitDetailPanel.add(leaseTermsPanel);

            Widget sp = new ViewLineSeparator();
            sp.getElement().getStyle().setMarginTop(0.5, Unit.EM);
            sp.getElement().getStyle().setMarginBottom(0.5, Unit.EM);
            sp.getElement().getStyle().setPaddingLeft(0, Unit.EM);
            sp.setWidth("100%");
            unitDetailPanel.add(sp);

            unitDetailPanel.setVisible(false);
            lineupWidth(unitDetailPanel);
            content.add(unitDetailPanel);
        }

        /*
         * Here is the workaround of the problem: our ViewHeaderDecorator has padding 1em
         * in the CSS style, so in order to set all other internal widgets intended to be
         * whole-width-wide by means of percentage width it's necessary to add than left
         * padding value!
         */
        private void lineupWidth(Widget w) {
            w.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            w.setWidth("100%");
        }

        private double minRentValue(com.propertyvista.portal.domain.Unit unit) {
            double rent = Double.MAX_VALUE;
            for (MarketRent mr : unit.marketRent())
                rent = Math.min(rent, mr.rent().amount().getValue());
            return rent;
        }

        private double minRentValue(IList<com.propertyvista.portal.domain.Unit> units) {
            double rent = Double.MAX_VALUE;
            for (com.propertyvista.portal.domain.Unit u : units)
                rent = Math.min(rent, minRentValue(u));
            return rent;
        }
    }
}
