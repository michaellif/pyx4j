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

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Overflow;
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

        VistaWidgetDecorator dateFrom = new VistaWidgetDecorator(create(proto().selectionCriteria().availableFrom(), this), 0, 7.2, 1);
        dateFrom.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        dateFrom.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        header.add(dateFrom);

        VistaWidgetDecorator dateTo = new VistaWidgetDecorator(create(proto().selectionCriteria().availableTo(), this), 0, 7.2, 1);
        dateTo.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        dateTo.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        header.add(dateTo);

        Button changeBtn = new Button("Change");
        changeBtn.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        changeBtn.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        changeBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.selectByDates(getValue());
            }
        });
        header.add(changeBtn);

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
        main.add(new ViewLineSeparator(0, Unit.PCT, 1, Unit.EM, 1, Unit.EM));

        caption = new HTML("<h3>Start Rent Date</h3>");
        caption.getElement().getStyle().setFloat(Float.LEFT);
        caption.getElement().getStyle().setMarginTop(3, Unit.PX);
        main.add(caption);
        main.add(new VistaWidgetDecorator(create(proto().rentStart(), this), 0, 7.2));

        setWidget(main);
    }

    @Override
    public void populate(UnitSelection entity) {
        //        System.out.println(">>>" + entity.availableUnits().units().toString());
        availableUnitsTable.populate(entity);
    }

    @Override
    public UnitSelection getValue() {
        UnitSelection us = super.getValue();
        if (us != null) {
            us.selectedUnit().set(selectedUnit);
            us.selectedUnitLeaseTerm().setValue(selectedTerm);
        }
        return us;

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
            //           addCell("From " + availableUnits.rent().getValue().getA() + "$" + "to " + availableUnits.rent().getValue().getB() + "$", tableLayout.get("Rent"), floorplan);
            //            System.out.println(">>>" + availableUnits.rent().toString());

            addCell("&nbsp", tableLayout.get("Deposit"), floorplan);
            addCell("&nbsp", tableLayout.get("Beds"), floorplan);
            addCell("&nbsp", tableLayout.get("Baths"), floorplan);
            addCell(availableUnits.floorplan().area().getStringView(), tableLayout.get("Sq F"), floorplan);
            addCell("&nbsp", tableLayout.get("Available"), floorplan);

            lineupWidth(floorplan);
            content.add(floorplan);

            content.add(new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 0.5, Unit.EM));
        }

        private void populateUnits(AvailableUnitsByFloorplan availableUnits) {

            for (final com.propertyvista.portal.domain.Unit unit : availableUnits.units()) {
                final FlowPanel unitRowPanel = new FlowPanel();
                unitRowPanel.setStyleName(UNIT_ROW_PANEL_STYLENAME);
                unitRowPanel.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                unitRowPanel.addDomHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {

                        selectedUnit = unit; // update user-selected unit...

                        // tweak selected unit data view:
                        for (Widget w : content) {
                            // hide all detail panels:
                            if (w.getStyleName().equals(UNIT_DETAIL_PANEL_STYLENAME)) {
                                w.setVisible(false);
                                w.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
                            }
                            // clear selected background:
                            if (w.getStyleName().equals(UNIT_ROW_PANEL_STYLENAME)) {
                                w.getElement().getStyle().setBackgroundColor("");
                                w.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
                            }
                        }

                        // show current selected row with details + their decorations: 
                        unitRowPanel.getElement().getStyle().setBackgroundColor("lightGray");
                        unitRowPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
                        unitRowPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);
                        unitRowPanel.getElement().getStyle().setBorderColor("black");
                        unitRowPanel.getElement().getStyle().setProperty("borderBottom", " none");

                        Widget detailPanel = content.getWidget(content.getWidgetIndex(unitRowPanel) + 1);
                        detailPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
                        detailPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);
                        detailPanel.getElement().getStyle().setBorderColor("black");
                        detailPanel.getElement().getStyle().setProperty("borderTop", " none");
                        detailPanel.setVisible(true);
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
            unitDetailPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);

            FlowPanel infoPanel = new FlowPanel();
            infoPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            infoPanel.add(new HTML("<h3>Info</h3>"));
            infoPanel.add(new HTML(unit.infoDetails().getStringView()));
            infoPanel.setWidth("33%");
            unitDetailPanel.add(infoPanel);

            FlowPanel amenitiesPanel = new FlowPanel();
            amenitiesPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            amenitiesPanel.add(new HTML("<h3>Amenities/Utilities:</h3>"));
            amenitiesPanel.add(new HTML(unit.amenities().getStringView()));
            amenitiesPanel.add(new HTML(unit.utilities().getStringView()));
            amenitiesPanel.setWidth("33%");
            unitDetailPanel.add(amenitiesPanel);

            FlowPanel concessionPanel = new FlowPanel();
            concessionPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            concessionPanel.add(new HTML("<h3>Concession</h3>"));
            concessionPanel.add(new HTML(unit.concessions().getStringView()));
            concessionPanel.setWidth("33%");
            unitDetailPanel.add(concessionPanel);

            Widget sp = new ViewLineSeparator(98, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPaddingLeft(0, Unit.EM);
            unitDetailPanel.add(sp);

            FlowPanel addonsPanel = new FlowPanel();
            addonsPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            addonsPanel.add(new HTML("<h3>Available add-ons</h3>"));
            addonsPanel.setWidth("33%");
            unitDetailPanel.add(addonsPanel);
            addonsPanel.add(new HTML(unit.concessions().getStringView()));

            sp = new ViewLineSeparator(98, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPaddingLeft(0, Unit.EM);
            unitDetailPanel.add(sp);

            // lease term:
            unitDetailPanel.add(new HTML());
            unitDetailPanel.add(new HTML("<h3>Lease Terms</h3>"));
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

            unitDetailPanel.getElement().getStyle().setPadding(1, Unit.EM);
            unitDetailPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
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
            w.getElement().getStyle().setPaddingRight(1, Unit.EM);
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
