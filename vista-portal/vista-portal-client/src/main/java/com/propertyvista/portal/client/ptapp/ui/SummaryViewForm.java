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
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteResources;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaTextPairDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.domain.pt.Pet;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Summary;

import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.widgets.client.Button;

@Singleton
public class SummaryViewForm extends BaseEntityForm<Summary> {

    private FlowPanel main;

    private ApartmentView apartmentView;

    private LeaseTermView leaseTermView;

    private TenantsTable tenantsTable;

    private TenantsView tenantsView;

    private PetsTable petsTable;

    private LeaseTermsCheck leaseTermsCheck;

    private SignatureView signatureView;

    public SummaryViewForm() {
        super(Summary.class);
    }

    @Override
    public void createContent() {
        main = new FlowPanel();

        main.add(new ViewHeaderDecorator(new HTML("<h4>Apartment</h4>")));
        main.add(apartmentView = new ApartmentView());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Lease Term</h4>")));
        main.add(leaseTermView = new LeaseTermView());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Tenants</h4>")));
        main.add(tenantsTable = new TenantsTable());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Info</h4>")));
        main.add(tenantsView = new TenantsView());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Financial</h4>")));

        main.add(new ViewHeaderDecorator(new HTML("<h4>Pets</h4>")));
        main.add(petsTable = new PetsTable());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Lease Terms</h4>")));
        main.add(leaseTermsCheck = new LeaseTermsCheck());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Monthtly Charges</h4>")));

        main.add(new ViewHeaderDecorator(new HTML("<h4>Digital Signature</h4>")));
        main.add(signatureView = new SignatureView());

        setWidget(main);
    }

    @Override
    public void populate(Summary value) {
        super.populate(value);

        // populate internal views:
        apartmentView.populate(value);
        leaseTermView.populate(value);
        tenantsTable.populate(value);
        tenantsView.populate(value);
        petsTable.populate(value);
        leaseTermsCheck.populate(value);
        signatureView.populate(value);
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
     * Selected Apartment information view implementation
     */
    private class ApartmentView extends FlowPanel {

        private final Map<String, String> tableLayout = new LinkedHashMap<String, String>();

        private final FlowPanel content;

        public ApartmentView() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);

            tableLayout.put("Type", "20%");
            tableLayout.put("Unit", "10%");
            tableLayout.put("Rent", "10%");
            tableLayout.put("Deposit", "10%");
            tableLayout.put("Beds", "10%");
            tableLayout.put("Baths", "10%");
            tableLayout.put("Sq F", "10%");
            tableLayout.put("Available", "20%");

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

            content.clear();

        }

        private void addCell(String cellName, String cellContent) {
            HTML label = new HTML(cellContent);
            label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            label.setWidth(tableLayout.get(cellName));
            content.add(label);
        }
    }

    /*
     * Selected Apartment information view implementation
     */
    private class LeaseTermView extends FlowPanel {

        public LeaseTermView() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);

            HTML availabilityAndPricing = new HTML(SiteResources.INSTANCE.availabilityAndPricing().getText());
            add(availabilityAndPricing);
        }

        public void populate(Summary value) {

        }
    }

    /*
     * Tenants information table implementation
     */
    private class TenantsTable extends FlowPanel {

        private final Map<String, String> tableLayout = new LinkedHashMap<String, String>();

        private final FlowPanel content;

        public TenantsTable() {

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

            content.clear();

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
     * Tenants detailed information view implementation
     */
    private class TenantsView extends FlowPanel {

        private final FlowPanel content;

        public TenantsView() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);

            // add table content panel:
            add(innerLevelElementElignment(content = new FlowPanel()));
        }

        public void populate(Summary value) {

            content.clear();

            for (PotentialTenantInfo pti : value.tenants().tenants()) {
                content.add(new TenantInfo(pti));
            }
        }

        private class TenantInfo extends FlowPanel {

            private final PotentialTenantInfo pti;

            private boolean fullView;

            public TenantInfo(PotentialTenantInfo pti) {

                getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

                this.pti = pti;
                showCompact();
            }

            public void showCompact() {

                clear();

                addViewSwitcher();
                HTML tenant = new HTML("<h4>" + pti.firstName().getStringView() + "&nbsp" + pti.lastName().getStringView() + "</h4>");
                tenant.getElement().getStyle().setMarginLeft(4, Unit.EM);
                tenant.getElement().getStyle().setMarginBottom(1, Unit.EM);
                setWidth("100%");
                add(tenant);

                fullView = false;
            }

            public void showFull() {

                showCompact();

                DecorationData dd = new DecorationData();
                dd.labelWidth = 50;
                dd.labelUnit = Unit.PCT;
                dd.labelAlignment = HasHorizontalAlignment.ALIGN_LEFT;
                dd.componentWidth = 50;
                dd.componentUnit = Unit.PCT;
                dd.componentAlignment = HasHorizontalAlignment.ALIGN_RIGHT;

                FlowPanel panel = new FlowPanel();
                panel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                panel.add(new VistaTextPairDecorator(pti.homePhone().getMeta().getCaption(), pti.homePhone().getStringView(), dd));
                panel.add(new VistaTextPairDecorator(pti.mobilePhone().getMeta().getCaption(), pti.mobilePhone().getStringView(), dd));
                panel.add(new VistaTextPairDecorator(pti.email().getMeta().getCaption(), pti.email().getStringView(), dd));
                panel.setWidth("40%");
                add(panel);

                panel = new FlowPanel();
                panel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                panel.getElement().getStyle().setMarginLeft(10, Unit.PCT);
                panel.add(new VistaTextPairDecorator(pti.driversLicense().getMeta().getCaption(), pti.driversLicense().getStringView(), dd));
                panel.add(new VistaTextPairDecorator(pti.driversLicenseState().getMeta().getCaption(), pti.driversLicenseState().getStringView(), dd));
                panel.add(new VistaTextPairDecorator(pti.secureIdentifier().getMeta().getCaption(), pti.secureIdentifier().getStringView(), dd));
                panel.setWidth("40%");
                add(panel);

                Widget sp = new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
                sp.getElement().getStyle().setPadding(0, Unit.EM);
                add(sp);

                fullView = true;
            }

            private void addViewSwitcher() {

                Button switcher = new Button("v");
                switcher.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                switcher.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        if (fullView) {
                            showCompact();
                        } else {
                            showFull();
                        }
                    }
                });
                add(switcher);
            }
        }
    }

    /*
     * Pets information table implementation
     */
    private class PetsTable extends FlowPanel {
        private final Map<String, String> tableLayout = new LinkedHashMap<String, String>();

        private final FlowPanel content;

        public PetsTable() {

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

            content.clear();

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

    /*
     * Lease Terms view implementation
     */
    private class LeaseTermsCheck extends FlowPanel {

        private final CheckBox agreeCheck;

        public LeaseTermsCheck() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);

            // add table content panel:
            TextArea leaseTerms = new TextArea();
            leaseTerms.setReadOnly(true);
            leaseTerms.setText(SiteResources.INSTANCE.leaseTerms().getText());
            leaseTerms.setVisibleLines(10);

            leaseTerms.setWidth("100%");
            add(leaseTerms);

            agreeCheck = new CheckBox("I Agree");
            agreeCheck.getElement().getStyle().setMarginLeft(40, Unit.PCT);
            agreeCheck.getElement().getStyle().setMarginBottom(1, Unit.EM);
            add(agreeCheck);
        }

        public void populate(Summary value) {
        }
    }

    /*
     * Digital Signature view implementation
     */
    private class SignatureView extends FlowPanel {

        public SignatureView() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);

            HTML signatureTerms = new HTML(SiteResources.INSTANCE.digitalSignature().getText());
            add(signatureTerms);

            VistaWidgetDecorator signature = new VistaWidgetDecorator(new CTextField("Type Your Full Name"));
            signature.getElement().getStyle().setBackgroundColor("darkGray");
            signature.getElement().getStyle().setPaddingTop(1, Unit.EM);
            signature.setHeight("3em");
            add(signature);
        }

        public void populate(Summary value) {
        }
    }
}
