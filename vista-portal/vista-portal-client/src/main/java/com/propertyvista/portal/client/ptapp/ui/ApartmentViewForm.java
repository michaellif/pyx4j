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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.propertyvista.portal.domain.pt.AvailableUnitsByFloorplan;
import com.propertyvista.portal.domain.pt.UnitSelection;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.decorators.BasicWidgetDecorator;
import com.pyx4j.widgets.client.Button;

public class ApartmentViewForm extends CEntityForm<UnitSelection> {

    private AvailableUnitsTable availableUnitsTable;

    public ApartmentViewForm() {
        super(UnitSelection.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();

        HTML caption = new HTML("<h4>Available Units</h4>");
        caption.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        main.add(caption);
        main.add(new BasicWidgetDecorator(create(proto().availableFrom(), this), 40, 100));
        main.add(new BasicWidgetDecorator(create(proto().availableTo(), this), 40, 100));
        main.add(new Button("Change"));
        main.add(new HTML());
        availableUnitsTable = new AvailableUnitsTable();
        main.add(availableUnitsTable);
        main.add(new HTML());
        main.add(new HTML("<h4>Lease Terms</h4>"));
        caption = new HTML("<h4>Desired Move In Date</h4>");
        caption.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        main.add(caption);
        main.add(new BasicWidgetDecorator(create(proto().moveInDate(), this), 40, 100));

        setWidget(main);
    }

    @Override
    public void populate(UnitSelection entity) {
        availableUnitsTable.populate(entity);
    }

    class AvailableUnitsTable extends FlowPanel {

        private final UnitSelection proto = EntityFactory.getEntityPrototype(UnitSelection.class);

        private final FlowPanel header;

        private final FlowPanel content;

        public AvailableUnitsTable() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            header = new FlowPanel();
            header.setWidth("100%");
            header.setHeight("2em");
            header.getElement().getStyle().setBackgroundColor("lightGray");
            header.getElement().getStyle().setPaddingLeft(10, Unit.PX);
            header.getElement().getStyle().setPaddingRight(10, Unit.PX);
            header.getElement().getStyle().setMarginTop(10, Unit.PX);
            header.getElement().getStyle().setMarginBottom(10, Unit.PX);

            addHeaderCell("Plan", "80px", header);
            addHeaderCell("Type", "80px", header);
            addHeaderCell("Rent", "80px", header);
            addHeaderCell("Deposit", "80px", header);
            addHeaderCell("Beds", "80px", header);
            addHeaderCell("Baths", "80px", header);
            addHeaderCell("Sq F", "80px", header);
            addHeaderCell("Available", "80px", header);

            add(header);

            content = new FlowPanel();
            add(content);

        }

        private void addHeaderCell(String text, String width, FlowPanel container) {
            HTML label = new HTML(text);
            label.setWidth(width);
            label.asWidget().getElement().getStyle().setFloat(Float.LEFT);
            label.asWidget().getElement().getStyle().setProperty("lineHeight", "2em");
            container.add(label);
        }

        protected void populate(UnitSelection units) {
            ApartmentViewForm.super.populate(units);
            content.clear();

            populateFloorplan(units.availableUnits());
            populateUnits(units.availableUnits());

        }

        private void populateFloorplan(AvailableUnitsByFloorplan availableUnits) {
            FlowPanel floorplan = new FlowPanel();
            floorplan.getElement().getStyle().setPaddingLeft(10, Unit.PX);
            floorplan.getElement().getStyle().setPaddingRight(10, Unit.PX);
            addCell("Plan", "80px", floorplan);
            addCell(availableUnits.floorplan().name().getStringView(), "80px", floorplan);
            addCell("From " + availableUnits.units().get(0).marketRent().get(0).rent().amount().getStringView(), "80px", floorplan);
            addCell("From " + availableUnits.units().get(0).requiredDeposit().getStringView(), "80px", floorplan);
            addCell(availableUnits.units().get(0).bedrooms().getStringView(), "80px", floorplan);
            addCell(availableUnits.units().get(0).bathrooms().getStringView(), "80px", floorplan);
            addCell(availableUnits.units().get(0).area().getStringView(), "80px", floorplan);
            addCell("", "80px", floorplan);
            content.add(floorplan);
        }

        private void addCell(String text, String width, FlowPanel container) {
            HTML label = new HTML(text);
            label.setWidth(width);
            label.asWidget().getElement().getStyle().setFloat(Float.LEFT);
            container.add(label);

        }

        private void populateUnits(AvailableUnitsByFloorplan availableUnits) {
            // TODO Auto-generated method stub

        }

    }

}
