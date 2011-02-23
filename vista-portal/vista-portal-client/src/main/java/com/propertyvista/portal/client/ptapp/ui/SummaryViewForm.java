/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.domain.pt.Pet;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Summary;

@Singleton
public class SummaryViewForm extends BaseEntityForm<Summary> {

    private FlowPanel main;

    private TenantsTable tenantsTable;

    private PetsTable petsTable;

    public SummaryViewForm() {
        super(Summary.class);
    }

    @Override
    public void createContent() {
        main = new FlowPanel();

        main.add(new ViewHeaderDecorator(new HTML("<h4>Apartment</h4>")));
        main.add(new ViewHeaderDecorator(new HTML("<h4>Lease Term</h4>")));

        main.add(new ViewHeaderDecorator(new HTML("<h4>Tenants</h4>")));
        main.add(tenantsTable = new TenantsTable());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Info</h4>")));
        main.add(new ViewHeaderDecorator(new HTML("<h4>Financial</h4>")));

        main.add(new ViewHeaderDecorator(new HTML("<h4>Pets</h4>")));
        main.add(petsTable = new PetsTable());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Lease Terms</h4>")));
        main.add(new ViewHeaderDecorator(new HTML("<h4>Monthtly Charges</h4>")));
        main.add(new ViewHeaderDecorator(new HTML("<h4>Charges Payable Upon Approval</h4>")));
        main.add(new ViewHeaderDecorator(new HTML("<h4>Digital Signature</h4>")));

        setWidget(main);
    }

    @Override
    public void populate(Summary value) {
        super.populate(value);

        tenantsTable.populate(value);
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

    /*
     * Tenants information table implementation
     */
    class TenantsTable extends FlowPanel {
        private final Map<String, String> tableLayout = new LinkedHashMap<String, String>();

        private final FlowPanel content;

        TenantsTable() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);

            tableLayout.put("Name", "20%");
            tableLayout.put("Date of Birht", "20%");
            tableLayout.put("Email", "20%");
            tableLayout.put("Relationship", "20%");
            tableLayout.put("Dependant", "20%");

            // fill header:
            for (Entry<String, String> e : tableLayout.entrySet()) {
                HTML label = new HTML(e.getKey());
                label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                label.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
                label.setWidth(e.getValue());
                add(label);
            }

            Widget sp = new ViewLineSeparator(100, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            add(sp);

            // add table content panel:
            add(innerLevelElementElignment(content = new FlowPanel()));
        }

        public void populate(Summary value) {
            for (PotentialTenantInfo ti : value.tenants().tenants()) {
                addCell("Name", ti.firstName().getStringView() + "&nbsp" + ti.lastName().getStringView());
                addCell("Date of Birht", ti.birthDate().getStringView());
                addCell("Email", ti.email().getStringView());
                addCell("Relationship", ti.relationship().getStringView());
                addCell("Dependant", ti.dependant().getStringView());
            }
        }

        private void addCell(String cellName, String cellContent) {
            HTML label = new HTML(cellContent);
            label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            label.setWidth(tableLayout.get(cellName));
            content.add(label);
        }
    }

    /*
     * Pets information table implementation
     */
    class PetsTable extends FlowPanel {
        private final Map<String, String> tableLayout = new LinkedHashMap<String, String>();

        private final FlowPanel content;

        PetsTable() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);

            tableLayout.put("Type", "20%");
            tableLayout.put("Name", "20%");
            tableLayout.put("Color", "10%");
            tableLayout.put("Breed", "20%");
            tableLayout.put("Weight", "10%");
            tableLayout.put("Units", "5%");
            tableLayout.put("Date of Birht", "15%");

            //            // fill header:
            //            for (Entry<String, String> e : tableLayout.entrySet()) {
            //                HTML label = new HTML(e.getKey());
            //                label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            //                label.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            //                label.setWidth(e.getValue());
            //                add(label);
            //            }
            //
            //            Widget sp = new ViewLineSeparator(100, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            //            sp.getElement().getStyle().setPadding(0, Unit.EM);
            //            add(sp);

            // add table content panel:
            add(innerLevelElementElignment(content = new FlowPanel()));
        }

        public void populate(Summary value) {
            for (Pet pet : value.pets().pets()) {
                addCell("Type", pet.type().getStringView());
                addCell("Name", pet.name().getStringView());
                addCell("Color", pet.color().getStringView());
                addCell("Breed", pet.breed().getStringView());
                addCell("Weight", pet.weight().getStringView());
                addCell("Units", pet.weightUnit().getStringView());
                addCell("Date of Birht", pet.birthDate().getStringView());
            }
        }

        private void addCell(String cellName, String cellContent) {
            HTML label = new HTML(cellContent);
            label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            label.setWidth(tableLayout.get(cellName));
            content.add(label);
        }
    }
}
