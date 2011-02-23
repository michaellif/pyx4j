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
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
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
                // This is owned entity, it can't be serialised by itself because all values are in owner. So we do clone
                // TODO fix the RPC services to know about this case.
                presenter.selectByDates((UnitSelectionCriteria) getValue().selectionCriteria().cloneEntity());
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

    /*
     * Here is the workaround of the problem: our ViewHeaderDecorator has padding 1em on
     * both ends in the CSS style, so in order to set all other internal widgets intended
     * to be whole-width-wide by means of percentage width it's necessary to add those
     * padding values!
     */
    private Widget upperLevelElementElignment(Widget e) {
        e.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        e.getElement().getStyle().setPaddingRight(1, Unit.EM);
        e.setWidth("70%");
        return e;
    }

    private Widget innerLevelElementElignment(Widget e) {
        upperLevelElementElignment(e);
        e.setWidth("100%");
        return e;
    }

    class AvailableUnitsTable extends FlowPanel {

        private final FlowPanel content;

        private final Map<String, String> tableLayout = new LinkedHashMap<String, String>();

        private static final String UNIT_ROW_PANEL_STYLENAME = "unitRowPanel";

        private static final String UNIT_DETAIL_PANEL_STYLENAME = "unitDetailPanel";

        public AvailableUnitsTable() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            //            upperLevelElementElignment(this);
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
            innerLevelElementElignment(header);

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

            addCell("Plan", "&nbsp", floorplan);
            addCell("Type", availableUnits.floorplan().name().getStringView(), floorplan);
            addCell("Rent", "From " + minRentValue(availableUnits.units()) + "$", floorplan);
            //                       addCell("From " + availableUnits.rent().getValue().getA() + "$" + "to " + availableUnits.rent().getValue().getB() + "$", tableLayout.get("Rent"), floorplan);
            //            System.out.println(">>>" + availableUnits.rent().toString());

            addCell("Deposit", "&nbsp", floorplan);
            addCell("Beds", "&nbsp", floorplan);
            addCell("Baths", "&nbsp", floorplan);
            addCell("Sq F", availableUnits.floorplan().area().getStringView(), floorplan);
            addCell("Available", "&nbsp", floorplan);

            content.add(innerLevelElementElignment(floorplan));
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

                addCell("Plan", "&nbsp", unitRowPanel);
                addCell("Type", unit.unitType().getStringView(), unitRowPanel);
                addCell("Rent", Double.toString(minRentValue(unit)) + "$", unitRowPanel);
                addCell("Deposit", unit.requiredDeposit().getStringView() + "$", unitRowPanel);
                addCell("Beds", unit.bedrooms().getStringView(), unitRowPanel);
                addCell("Baths", unit.bathrooms().getStringView(), unitRowPanel);
                addCell("Sq F", unit.area().getStringView(), unitRowPanel);
                addCell("Available", unit.avalableForRent().getStringView(), unitRowPanel);

                content.add(innerLevelElementElignment(unitRowPanel));

                populateUnitDetail(unit);
            }
        }

        private void addCell(String cellName, String cellContent, FlowPanel container) {
            HTML label = new HTML(cellContent);
            label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            label.setWidth(tableLayout.get(cellName));
            container.add(label);
        }

        private void populateUnitDetail(com.propertyvista.portal.domain.Unit unit) {
            FlowPanel unitDetailPanel = new FlowPanel();
            unitDetailPanel.setStyleName(UNIT_DETAIL_PANEL_STYLENAME);
            unitDetailPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);

            FlowPanel infoPanel = new FlowPanel();
            infoPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            infoPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            infoPanel.add(new HTML("<h3>Info</h3>"));
            infoPanel.add(new HTML(unit.infoDetails().getStringView()));
            infoPanel.setWidth("33%");
            unitDetailPanel.add(infoPanel);

            FlowPanel amenitiesPanel = new FlowPanel();
            amenitiesPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            amenitiesPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            amenitiesPanel.add(new HTML("<h3>Amenities/Utilities:</h3>"));
            amenitiesPanel.add(new HTML(unit.amenities().getStringView()));
            amenitiesPanel.add(new HTML(unit.utilities().getStringView()));
            amenitiesPanel.setWidth("33%");
            unitDetailPanel.add(amenitiesPanel);

            FlowPanel concessionPanel = new FlowPanel();
            concessionPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            concessionPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
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
            content.add(innerLevelElementElignment(unitDetailPanel));
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
