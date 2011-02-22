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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.domain.pt.AvailableUnitsByFloorplan;
import com.propertyvista.portal.domain.pt.UnitSelection;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.widgets.client.Button;

public class ApartmentViewForm extends CEntityForm<UnitSelection> {

    private AvailableUnitsTable availableUnitsTable;

    public ApartmentViewForm() {
        super(UnitSelection.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();

        // Form first table header: 
        FlowPanel header = new FlowPanel();
        HTML caption = new HTML("<h2>Available Units</h2>");
        caption.getElement().getStyle().setFloat(Float.LEFT);
        caption.getElement().getStyle().setMarginTop(3, Unit.PX);
        caption.getElement().getStyle().setMarginRight(8, Unit.EM);
        caption.getElement().getStyle().setMarginBottom(0, Unit.EM);
        caption.getElement().getStyle().setVerticalAlign(VerticalAlign.BOTTOM);
        header.add(caption);

        Widget w = new VistaWidgetDecorator(create(proto().availableFrom(), this), 0, 7.1);
        w.getElement().getStyle().setFloat(Float.LEFT);
        header.add(w);

        w = new VistaWidgetDecorator(create(proto().availableTo(), this), 0, 7.1);
        w.getElement().getStyle().setFloat(Float.LEFT);
        header.add(w);

        w = new Button("Change");
        //        w.getElement().getStyle().setFloat(Float.RIGHT);
        header.add(w);
        w = new ViewHeaderDecorator(header);
        w.getElement().getStyle().setMarginBottom(0, Unit.EM);
        main.add(w);

        // units table:
        main.add(new HTML());
        availableUnitsTable = new AvailableUnitsTable();
        main.add(availableUnitsTable);

        // lease term:
        main.add(new HTML());
        main.add(new ViewHeaderDecorator(new HTML("<h3>Lease Terms</h3>")));

        // start date:
        main.add(new ViewLineSeparator());
        caption = new HTML("<h3>Start Rent Date</h3>");
        caption.getElement().getStyle().setFloat(Float.LEFT);
        //        caption.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        caption.getElement().getStyle().setMarginTop(3, Unit.PX);
        main.add(caption);
        main.add(new VistaWidgetDecorator(create(proto().moveInDate(), this), 0, 7.1));

        setWidget(main);
    }

    @Override
    public void populate(UnitSelection entity) {
        //        System.out.println("+++++++++++++++++++" + entity.availableUnits().units().toString());
        availableUnitsTable.populate(entity);
    }

    class AvailableUnitsTable extends FlowPanel {

        private final UnitSelection proto = EntityFactory.getEntityPrototype(UnitSelection.class);

        private final FlowPanel header;

        private final FlowPanel content;

        Map<String, String> tableLayout = new LinkedHashMap<String, String>();

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

            header = new FlowPanel();
            header.setWidth("100%");
            header.setHeight("2em");
            header.getElement().getStyle().setBackgroundColor("lightGray");
            header.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            header.getElement().getStyle().setMarginBottom(1, Unit.EM);
            // fill header:
            for (Entry<String, String> e : tableLayout.entrySet()) {
                HTML label = new HTML(e.getKey());
                label.asWidget().getElement().getStyle().setFloat(Float.LEFT);
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

            addCell(" ", tableLayout.get("Plan"), floorplan);
            addCell(" ", tableLayout.get("Type"), floorplan);
            addCell(availableUnits.rent().getStringView(), tableLayout.get("Rent"), floorplan);
            addCell(" ", tableLayout.get("Deposit"), floorplan);
            addCell(" ", tableLayout.get("Beds"), floorplan);
            addCell(" ", tableLayout.get("Baths"), floorplan);
            addCell(availableUnits.floorplan().area().getStringView(), tableLayout.get("Sq F"), floorplan);
            addCell(" ", tableLayout.get("Available"), floorplan);

            floorplan.setWidth("100%");
            content.add(floorplan);

            Widget w = new ViewLineSeparator();
            w.setWidth("100%");
            content.add(w);
        }

        private void populateUnits(AvailableUnitsByFloorplan availableUnits) {
            FlowPanel unitsTable = new FlowPanel();
            unitsTable.getElement().getStyle().setPaddingLeft(1, Unit.EM);

            for (com.propertyvista.portal.domain.Unit unit : availableUnits.units()) {
                addCell(" ", tableLayout.get("Plan"), unitsTable);
                addCell(unit.unitType().getStringView(), tableLayout.get("Type"), unitsTable);
                addCell(unit.marketRent().getStringView(), tableLayout.get("Rent"), unitsTable);
                addCell(unit.requiredDeposit().getStringView(), tableLayout.get("Deposit"), unitsTable);
                addCell(unit.bedrooms().getStringView(), tableLayout.get("Beds"), unitsTable);
                addCell(unit.bathrooms().getStringView(), tableLayout.get("Baths"), unitsTable);
                addCell(unit.area().getStringView(), tableLayout.get("Sq F"), unitsTable);
                addCell(unit.avalableForRent().getStringView(), tableLayout.get("Available"), unitsTable);
            }

            unitsTable.setWidth("100%");
            content.add(unitsTable);
        }

        private void addCell(String text, String width, FlowPanel container) {
            HTML label = new HTML(text);
            label.getElement().getStyle().setFloat(Float.LEFT);
            label.setWidth(width);
            container.add(label);
        }
    }
}
