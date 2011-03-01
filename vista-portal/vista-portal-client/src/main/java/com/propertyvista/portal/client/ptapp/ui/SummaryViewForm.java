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
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteResources;
import com.propertyvista.portal.client.ptapp.ui.components.ReadOnlyComponentFactory;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaTextPairDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.ChargeLineSelectable;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.IPerson;
import com.propertyvista.portal.domain.pt.IncomeSource;
import com.propertyvista.portal.domain.pt.Pet;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.TenantAsset;
import com.propertyvista.portal.domain.pt.TenantCharge;
import com.propertyvista.portal.domain.pt.TenantGuarantor;
import com.propertyvista.portal.domain.pt.TenantIncome;
import com.propertyvista.portal.domain.pt.Vehicle;
import com.propertyvista.portal.rpc.pt.SiteMap;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button;

@Singleton
public class SummaryViewForm extends BaseEntityForm<Summary> {

    private SummaryViewPresenter presenter;

    private FlowPanel main;

    private ApartmentView apartmentView;

    private LeaseTermView leaseTermView;

    private TenantsTable tenantsTable;

    private TenantsView tenantsView;

    private FinancialView financialView;

    private PetsTable petsTable;

    private ChargesView chargesView;

    private LeaseTermsCheck leaseTermsCheck;

    private SignatureView signatureView;

    public SummaryViewForm() {
        super(Summary.class, new ReadOnlyComponentFactory());
    }

    public SummaryViewPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(SummaryViewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {

        /**
         * create view processing object first! Note!!! because createContent has being
         * called in super constructor and SummaryViewForm.createMemberFolderEditor
         * callback processor is common for all those objects - it's necessary to create
         * them BEFORE call to any of theirs createContents!!!
         */
        financialView = new FinancialView(this);
        chargesView = new ChargesView(this);

        // fill main form:
        main = new FlowPanel();

        main.add(new ViewHeaderDecorator(new HTML("<h4>Apartment</h4>")));
        main.add(apartmentView = new ApartmentView());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Lease Term</h4>")));
        main.add(leaseTermView = new LeaseTermView());

        main.add(createHeaderWithEditLink("Tenants", new SiteMap.Tenants()));
        main.add(tenantsTable = new TenantsTable());

        main.add(createHeaderWithEditLink("Info", new SiteMap.Info()));
        main.add(tenantsView = new TenantsView());

        main.add(createHeaderWithEditLink("Financial", new SiteMap.Financial()));
        financialView.createContent(main, proto().financial());

        main.add(createHeaderWithEditLink("Pets", new SiteMap.Pets()));
        main.add(petsTable = new PetsTable());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Lease Terms</h4>")));
        main.add(leaseTermsCheck = new LeaseTermsCheck());

        chargesView.createContent(main, proto().charges());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Digital Signature</h4>")));
        main.add(signatureView = new SignatureView());

        return main;
    }

    @Override
    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        CEntityFolder<?> editor = null;

        if (editor == null)
            editor = chargesView.createMemberFolderEditor(member);
        if (editor == null)
            editor = financialView.createMemberFolderEditor(member);

        return (editor != null ? editor : super.createMemberFolderEditor(member));
    }

    @Override
    public void populate(Summary value) {
        super.populate(value);

        // populate internal views:
        apartmentView.populate(value);
        leaseTermView.populate(value);
        tenantsTable.populate(value);
        tenantsView.populate(value);
        //        financialView.populate(value);
        petsTable.populate(value);
        chargesView.populate(value);
        leaseTermsCheck.populate(value);
        signatureView.populate(value);
    }

    private Widget createHeaderWithEditLink(String captionTxt, final AppPlace link) {

        FlowPanel header = new FlowPanel();
        HTML caption = new HTML("<h4>" + captionTxt + "</h4>");
        caption.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        caption.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        caption.getElement().getStyle().setMarginRight(4, Unit.EM);
        caption.getElement().getStyle().setPaddingBottom(0, Unit.EM);
        header.add(caption);

        Button edit = new Button("Edit");
        edit.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        edit.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        edit.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                getPresenter().goToPlace(link);
            }
        });
        header.add(edit);
        return new ViewHeaderDecorator(header);
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
        e.setWidth("100%");
        return e;
    }

    // this block of styles alight width and left position: 
    private Widget innerElement2upperElignment(Widget e) {
        e.getElement().getStyle().setPosition(Position.RELATIVE);
        e.getElement().getStyle().setLeft(-1, Unit.EM);
        e.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        e.getElement().getStyle().setPaddingRight(1, Unit.EM);
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
            tableLayout.put("Unit", "20%");
            //            tableLayout.put("Rent", "10%");
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

            addCell("Type", value.unitSelection().selectedUnit().floorplan().name().getStringView());
            addCell("Unit", value.unitSelection().selectedUnit().unitType().getStringView());
            //            addCell("Rent", value.unitSelection().selectedUnit().marketRent().getStringView());
            addCell("Deposit", value.unitSelection().selectedUnit().requiredDeposit().getStringView());
            addCell("Beds", value.unitSelection().selectedUnit().bedrooms().getStringView());
            addCell("Baths", value.unitSelection().selectedUnit().bathrooms().getStringView());
            addCell("Sq F", value.unitSelection().selectedUnit().area().getStringView());
            addCell("Available", value.unitSelection().selectedUnit().avalableForRent().getStringView());

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

        private final FlowPanel content;

        public LeaseTermView() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);

            // add table content panel:
            add(innerLevelElementElignment(content = new FlowPanel()));
            content.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            content.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);

            content.setHeight("100%");
            content.setWidth("30%");

            // add static lease term blah-blah:
            HTML availabilityAndPricing = new HTML(SiteResources.INSTANCE.availabilityAndPricing().getText());
            availabilityAndPricing.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            availabilityAndPricing.setWidth("70%");
            add(availabilityAndPricing);
        }

        public void populate(Summary value) {

            content.clear();

            HTML label = new HTML(value.unitSelection().markerRent().leaseTerm().getStringView() + "&nbsp month Rent");
            label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            content.add(label);

            label = new HTML(value.unitSelection().markerRent().rent().currency().getStringView()
                    + value.unitSelection().markerRent().rent().amount().getStringView() + " / month");
            label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            label.getElement().getStyle().setMarginTop(1, Unit.EM);
            content.add(label);
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

            tableLayout.put("Name", "30%");
            tableLayout.put("Date of Birht", "20%");
            tableLayout.put("Email", "25%");
            tableLayout.put("Relationship", "15%");
            tableLayout.put("Dependant", "10%");

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
                addCell("Name", ti.firstName().getStringView() + " &nbsp " + ti.lastName().getStringView());
                addCell("Date of Birht", ti.birthDate().getStringView());
                addCell("Email", ti.email().getStringView());
                addCell("Relationship", ti.relationship().getStringView());
                addCell("Dependant", ti.dependant().getStringView());
            }
        }

        private void addCell(String cellName, String cellContent) {
            HTML label = new HTML(cellContent);
            label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            label.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);
            label.setWidth(tableLayout.get(cellName));
            content.add(label);
        }
    }

    /*
     * Tenants detailed information view implementation
     */
    private class TenantsView extends FlowPanel {

        private final VerticalPanel content;

        public TenantsView() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);

            // add table content panel:
            add(innerLevelElementElignment(content = new VerticalPanel()));
        }

        public void populate(Summary value) {

            content.clear();

            for (PotentialTenantInfo pti : value.tenants().tenants()) {
                content.add(new TenantInfo(pti));
            }
        }

        private class TenantInfo extends FlowPanel {

            private final PotentialTenantInfo pti;

            private final String LEFT_COLUMN_WIDTH = "40%";

            private final String GAP_COLUMN_WIDTH = "10%";

            private final String RIGHT_COLUMN_WIDTH = "40%";

            private boolean fullView;

            public TenantInfo(PotentialTenantInfo pti) {

                getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                innerElement2upperElignment(this);

                getElement().getStyle().setBackgroundColor("white");

                getElement().getStyle().setBorderWidth(1, Unit.PX);
                getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
                getElement().getStyle().setBorderColor("black");

                //                getElement().getStyle().setPaddingRight(1, Unit.EM);
                getElement().getStyle().setPaddingTop(0.5, Unit.EM);
                getElement().getStyle().setPaddingBottom(0.5, Unit.EM);

                getElement().getStyle().setMarginBottom(0.5, Unit.EM);
                setWidth("100%");

                this.pti = pti;
                showCompact();
            }

            public void showCompact() {

                clear();

                HorizontalPanel panel = new HorizontalPanel();

                addViewSwitcher(panel);
                panel.setCellVerticalAlignment(panel.getWidget(panel.getWidgetCount() - 1), HasVerticalAlignment.ALIGN_MIDDLE);

                HTML tenant = new HTML("<h2>" + pti.firstName().getStringView() + " &nbsp " + pti.lastName().getStringView() + "</h2>");
                tenant.getElement().getStyle().setMarginLeft(4, Unit.EM);
                panel.add(tenant);
                panel.setCellVerticalAlignment(tenant, HasVerticalAlignment.ALIGN_MIDDLE);

                add(panel);

                fullView = false;
            }

            public void showFull() {

                showCompact();

                DecorationData dd2ColumnsTable = new DecorationData(50, Unit.PCT, 50, Unit.PCT);
                dd2ColumnsTable.labelAlignment = HasHorizontalAlignment.ALIGN_LEFT;
                dd2ColumnsTable.componentAlignment = HasHorizontalAlignment.ALIGN_RIGHT;

                // ----------------------------------------------------------------------

                HorizontalPanel subviewPanel = new HorizontalPanel();

                FlowPanel panel = new FlowPanel();
                panel.add(new VistaTextPairDecorator(pti.homePhone().getMeta().getCaption(), pti.homePhone().getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.mobilePhone().getMeta().getCaption(), pti.mobilePhone().getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.workPhone().getMeta().getCaption(), pti.workPhone().getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.email().getMeta().getCaption(), pti.email().getStringView(), dd2ColumnsTable));
                subviewPanel.add(panel);
                subviewPanel.setCellWidth(panel, LEFT_COLUMN_WIDTH);

                panel = new FlowPanel();
                subviewPanel.add(panel);
                subviewPanel.setCellWidth(panel, GAP_COLUMN_WIDTH);

                panel = new FlowPanel();
                panel.add(new VistaTextPairDecorator(pti.driversLicense().getMeta().getCaption(), pti.driversLicense().getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.driversLicenseState().getMeta().getCaption(), pti.driversLicenseState().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.secureIdentifier().getMeta().getCaption(), pti.secureIdentifier().getStringView(), dd2ColumnsTable));
                subviewPanel.add(panel);
                subviewPanel.setCellWidth(panel, RIGHT_COLUMN_WIDTH);

                // add empty cell just for proper resizing of the previous two ;)
                subviewPanel.add(new FlowPanel());

                subviewPanel.setWidth("100%");
                add(subviewPanel);

                Widget sp = new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
                sp.getElement().getStyle().setPadding(0, Unit.EM);
                add(sp);

                // ----------------------------------------------------------------------

                subviewPanel = new HorizontalPanel();

                panel = new FlowPanel();
                panel.add(new HTML("<h3>" + pti.currentAddress().getMeta().getCaption() + "</h3>"));
                panel.add(new VistaTextPairDecorator(pti.currentAddress().postalCode().getMeta().getCaption(), pti.currentAddress().postalCode()
                        .getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.currentAddress().city().getMeta().getCaption(), pti.currentAddress().city().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.currentAddress().province().getMeta().getCaption(), pti.currentAddress().province().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.currentAddress().street1().getMeta().getCaption(), pti.currentAddress().street1().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.currentAddress().street2().getMeta().getCaption(), pti.currentAddress().street2().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.currentAddress().moveInDate().getMeta().getCaption(), pti.currentAddress().moveInDate()
                        .getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.currentAddress().moveOutDate().getMeta().getCaption(), pti.currentAddress().moveOutDate()
                        .getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.currentAddress().payment().getMeta().getCaption(), pti.currentAddress().payment().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.currentAddress().phone().getMeta().getCaption(), pti.currentAddress().phone().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.currentAddress().rented().getMeta().getCaption(), pti.currentAddress().rented().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.currentAddress().managerName().getMeta().getCaption(), pti.currentAddress().managerName()
                        .getStringView(), dd2ColumnsTable));
                subviewPanel.add(panel);
                subviewPanel.setCellWidth(panel, LEFT_COLUMN_WIDTH);

                panel = new FlowPanel();
                subviewPanel.add(panel);
                subviewPanel.setCellWidth(panel, GAP_COLUMN_WIDTH);

                panel = new FlowPanel();

                panel.add(new HTML("<h3>" + pti.previousAddress().getMeta().getCaption() + "</h3>"));
                panel.add(new VistaTextPairDecorator(pti.previousAddress().postalCode().getMeta().getCaption(), pti.previousAddress().postalCode()
                        .getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.previousAddress().city().getMeta().getCaption(), pti.previousAddress().city().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.previousAddress().province().getMeta().getCaption(), pti.previousAddress().province().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.previousAddress().street1().getMeta().getCaption(), pti.previousAddress().street1().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.previousAddress().street2().getMeta().getCaption(), pti.previousAddress().street2().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.previousAddress().moveInDate().getMeta().getCaption(), pti.previousAddress().moveInDate()
                        .getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.previousAddress().moveOutDate().getMeta().getCaption(), pti.previousAddress().moveOutDate()
                        .getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.previousAddress().payment().getMeta().getCaption(), pti.previousAddress().payment().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.previousAddress().phone().getMeta().getCaption(), pti.previousAddress().phone().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.previousAddress().rented().getMeta().getCaption(), pti.previousAddress().rented().getStringView(),
                        dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.previousAddress().managerName().getMeta().getCaption(), pti.previousAddress().managerName()
                        .getStringView(), dd2ColumnsTable));
                subviewPanel.add(panel);
                subviewPanel.setCellWidth(panel, RIGHT_COLUMN_WIDTH);

                // add empty cell just for proper resizing of the previous two ;)
                subviewPanel.add(new FlowPanel());

                subviewPanel.setWidth("100%");
                add(subviewPanel);

                sp = new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
                sp.getElement().getStyle().setPadding(0, Unit.EM);
                add(sp);

                // ----------------------------------------------------------------------

                add(new HTML("<h3>" + pti.vehicles().getMeta().getCaption() + "</h3>"));

                Widget w;
                for (Vehicle vhcl : pti.vehicles()) {
                    HorizontalPanel vehiclePanel = new HorizontalPanel();

                    vehiclePanel.add(w = new HTML(vhcl.plateNumber().getStringView()));
                    vehiclePanel.setCellWidth(w, "20%");

                    vehiclePanel.add(w = new HTML(vhcl.year().getStringView()));
                    vehiclePanel.setCellWidth(w, "20%");

                    vehiclePanel.add(w = new HTML(vhcl.make().getStringView()));
                    vehiclePanel.setCellWidth(w, "20%");

                    vehiclePanel.add(w = new HTML(vhcl.model().getStringView()));
                    vehiclePanel.setCellWidth(w, "20%");

                    vehiclePanel.add(w = new HTML(vhcl.province().getStringView()));
                    vehiclePanel.setCellWidth(w, "20%");

                    vehiclePanel.setWidth("100%");
                    add(vehiclePanel);
                }

                sp = new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
                sp.getElement().getStyle().setPadding(0, Unit.EM);
                add(sp);

                // ----------------------------------------------------------------------

                DecorationData ddQuestionay = new DecorationData(80, Unit.PCT, 10, Unit.PCT);
                ddQuestionay.labelAlignment = HasHorizontalAlignment.ALIGN_LEFT;
                ddQuestionay.componentAlignment = HasHorizontalAlignment.ALIGN_RIGHT;
                ddQuestionay.componentVerticalAlignment = VerticalAlign.MIDDLE;

                panel = new FlowPanel();
                panel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

                panel.add(new HTML("<h3>" + pti.legalQuestions().getMeta().getCaption() + "</h3>"));
                panel.add(new VistaTextPairDecorator(pti.legalQuestions().suedForRent().getMeta().getCaption(), pti.legalQuestions().suedForRent()
                        .getStringView(), ddQuestionay));
                panel.add(new VistaTextPairDecorator(pti.legalQuestions().suedForDamages().getMeta().getCaption(), pti.legalQuestions().suedForDamages()
                        .getStringView(), ddQuestionay));
                panel.add(new VistaTextPairDecorator(pti.legalQuestions().everEvicted().getMeta().getCaption(), pti.legalQuestions().everEvicted()
                        .getStringView(), ddQuestionay));
                panel.add(new VistaTextPairDecorator(pti.legalQuestions().defaultedOnLease().getMeta().getCaption(), pti.legalQuestions().defaultedOnLease()
                        .getStringView(), ddQuestionay));
                panel.add(new VistaTextPairDecorator(pti.legalQuestions().convictedOfFelony().getMeta().getCaption(), pti.legalQuestions().convictedOfFelony()
                        .getStringView(), ddQuestionay));
                panel.add(new VistaTextPairDecorator(pti.legalQuestions().legalTroubles().getMeta().getCaption(), pti.legalQuestions().legalTroubles()
                        .getStringView(), ddQuestionay));
                panel.add(new VistaTextPairDecorator(pti.legalQuestions().filedBankruptcy().getMeta().getCaption(), pti.legalQuestions().filedBankruptcy()
                        .getStringView(), ddQuestionay));
                panel.setWidth("70%");
                add(panel);

                sp = new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
                sp.getElement().getStyle().setPadding(0, Unit.EM);
                add(sp);

                // ----------------------------------------------------------------------

                add(new HTML("<h3>" + "Emergency Contacts" + "</h3>"));

                subviewPanel = new HorizontalPanel();

                panel = new FlowPanel();
                panel.add(new HTML("<h2>" + pti.emergencyContact1().firstName().getStringView() + " &nbsp "
                        + pti.emergencyContact1().lastName().getStringView() + "</h2>"));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact1().homePhone().getMeta().getCaption(), pti.emergencyContact1().homePhone()
                        .getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact1().mobilePhone().getMeta().getCaption(), pti.emergencyContact1().mobilePhone()
                        .getStringView(), dd2ColumnsTable));
                //                panel.add(new VistaTextPairDecorator(pti.emergencyContact1().workPhone().getMeta().getCaption(), pti.emergencyContact1().workPhone().getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact1().address().street1().getMeta().getCaption(), pti.emergencyContact1().address()
                        .street1().getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact1().address().street2().getMeta().getCaption(), pti.emergencyContact1().address()
                        .street2().getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact1().address().city().getMeta().getCaption(), pti.emergencyContact1().address().city()
                        .getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact1().address().province().getMeta().getCaption(), pti.emergencyContact1().address()
                        .province().getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact1().address().postalCode().getMeta().getCaption(), pti.emergencyContact1().address()
                        .postalCode().getStringView(), dd2ColumnsTable));
                subviewPanel.add(panel);
                subviewPanel.setCellWidth(panel, LEFT_COLUMN_WIDTH);

                panel = new FlowPanel();
                subviewPanel.add(panel);
                subviewPanel.setCellWidth(panel, GAP_COLUMN_WIDTH);

                panel = new FlowPanel();
                panel.add(new HTML("<h2>" + pti.emergencyContact2().firstName().getStringView() + " &nbsp "
                        + pti.emergencyContact2().lastName().getStringView() + "</h2>"));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact2().homePhone().getMeta().getCaption(), pti.emergencyContact2().homePhone()
                        .getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact2().mobilePhone().getMeta().getCaption(), pti.emergencyContact2().mobilePhone()
                        .getStringView(), dd2ColumnsTable));
                //                panel.add(new VistaTextPairDecorator(pti.emergencyContact2().workPhone().getMeta().getCaption(), pti.emergencyContact2().workPhone().getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact2().address().street1().getMeta().getCaption(), pti.emergencyContact2().address()
                        .street1().getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact2().address().street2().getMeta().getCaption(), pti.emergencyContact2().address()
                        .street2().getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact2().address().city().getMeta().getCaption(), pti.emergencyContact2().address().city()
                        .getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact2().address().province().getMeta().getCaption(), pti.emergencyContact2().address()
                        .province().getStringView(), dd2ColumnsTable));
                panel.add(new VistaTextPairDecorator(pti.emergencyContact2().address().postalCode().getMeta().getCaption(), pti.emergencyContact2().address()
                        .postalCode().getStringView(), dd2ColumnsTable));
                subviewPanel.add(panel);
                subviewPanel.setCellWidth(panel, RIGHT_COLUMN_WIDTH);

                // add empty cell just for proper resizing of the previous two ;)
                subviewPanel.add(new FlowPanel());

                subviewPanel.setWidth("100%");
                add(subviewPanel);

                fullView = true;
            }

            private void addViewSwitcher(HorizontalPanel panel) {

                Button switcher = new Button("v");
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
                panel.add(switcher);
            }
        }
    }

    /*
     * Financial detailed information view implementation
     */
    private class FinancialView {

        private final FlowPanel content;

        private final String LEFT_COLUMN_WIDTH = "30%";

        private final String RIGHT_COLUMN_WIDTH = "30%";

        final CEntityForm<?> masterForm;

        public FinancialView(CEntityForm<?> masterForm) {
            this.masterForm = masterForm;

            // add table content panel:
            upperLevelElementElignment(content = new FlowPanel());
            content.getElement().getStyle().setBackgroundColor("white");
            content.getElement().getStyle().setBorderWidth(1, Unit.PX);
            content.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
            content.getElement().getStyle().setBorderColor("black");
        }

        public void createContent(FlowPanel main, PotentialTenantFinancial member) {

            FlowPanel fullname = createFullName(member.tenant());
            fullname.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            fullname.getElement().getStyle().setFontSize(1.5, Unit.EM);
            fullname.getElement().getStyle().setMarginLeft(3, Unit.EM);
            content.add(fullname);

            content.add(createHeader2(member.incomes()));
            content.add(masterForm.create(member.incomes(), masterForm));
            content.add(createHeader2(member.assets()));
            content.add(masterForm.create(member.assets(), masterForm));
            content.add(createHeader2(member.guarantors()));
            content.add(masterForm.create(member.guarantors(), masterForm));

            main.add(content);
        }

        private FlowPanel createFullName(IPerson person) {
            FlowPanel fullname = new FlowPanel();
            fullname.add(DecorationUtils.inline(masterForm.create(person.firstName(), masterForm), "20%", null));
            fullname.add(DecorationUtils.inline(masterForm.create(person.middleName(), masterForm), "5%", null));
            fullname.add(DecorationUtils.inline(masterForm.create(person.lastName(), masterForm), "30%", null));
            fullname.setWidth("50%");
            return fullname;
        }

        private Widget createHeader(IObject<?> member) {

            return new HTML("<h4>" + member.getMeta().getCaption() + "</h4>");
        }

        private Widget createHeader2(IObject<?> member) {

            HTML h = new HTML("<h5>" + member.getMeta().getCaption() + "</h5>");
            h.getElement().getStyle().setMarginTop(0.5, Unit.EM);
            h.getElement().getStyle().setMarginLeft(1, Unit.EM);
            return h;
        }

        protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
            if (member.getValueClass().equals(TenantIncome.class)) {
                return new TenantIncomeListFolder();
            } else if (member.getValueClass().equals(TenantAsset.class)) {
                return new TenantAssetListFolder();
            } else if (member.getValueClass().equals(TenantGuarantor.class)) {
                return new TenantGuarantorListFolder();
            } else {
                return null;
            }
        }

        public void populate(Summary value) {
        }

        // 
        private class TenantIncomeListFolder extends CEntityFolder<TenantIncome> {

            @Override
            protected FolderDecorator<TenantIncome> createFolderDecorator() {
                return new BoxReadOnlyFolderDecorator<TenantIncome>();
            }

            @Override
            protected CEntityFolderItem<TenantIncome> createItem() {

                return new CEntityFolderItem<TenantIncome>(TenantIncome.class) {

                    private FlowPanel employer;

                    private FlowPanel selfEmployed;

                    private FlowPanel seasonallyEmployed;

                    private FlowPanel socialservices;

                    private FlowPanel student;

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new BoxReadOnlyFolderItemDecorator(false);
                    }

                    @Override
                    public IsWidget createContent() {
                        FlowPanel main = new FlowPanel();

                        Widget income = formRow(proto().incomeSource());
                        income.getElement().getStyle().setFontWeight(FontWeight.BOLD);
                        main.add(income);

                        main.add(employer = createEmployerPanel());
                        main.add(seasonallyEmployed = createSeasonallyEmployedPanel());
                        main.add(selfEmployed = createSelfemployedPanel());
                        main.add(student = createStudentPanel());
                        main.add(socialservices = createSocialPanel());

                        main.getElement().getStyle().setPaddingLeft(2, Unit.EM);
                        main.getElement().getStyle().setMarginBottom(0.5, Unit.EM);
                        main.setWidth("100%");
                        return main;
                    }

                    @Override
                    public void populate(TenantIncome value) {
                        super.populate(value);
                        setVisibility(value.incomeSource().getValue());
                    }

                    private void setVisibility(IncomeSource value) {
                        employer.setVisible(false);
                        seasonallyEmployed.setVisible(false);
                        selfEmployed.setVisible(false);
                        student.setVisible(false);
                        socialservices.setVisible(false);
                        if (value != null) {
                            switch (value) {
                            case fulltime:
                            case parttime:
                                employer.setVisible(true);
                                break;
                            case selfemployed:
                                selfEmployed.setVisible(true);
                                break;
                            case seasonallyEmployed:
                                seasonallyEmployed.setVisible(true);
                                break;
                            case socialServices:
                                socialservices.setVisible(true);
                                break;
                            case student:
                                student.setVisible(true);
                                break;
                            }
                        }
                    }

                    Widget formRow(IObject<?> value) {

                        FlowPanel rowContent = new FlowPanel();
                        rowContent.add(DecorationUtils.inline(new HTML(value.getMeta().getCaption()), LEFT_COLUMN_WIDTH, null));

                        CLabel wrappper = new CLabel();
                        bind(wrappper, value);
                        rowContent.add(DecorationUtils.inline(wrappper, RIGHT_COLUMN_WIDTH, "right"));
                        return rowContent;
                    }

                    private FlowPanel createEmployerPanel() {
                        FlowPanel panel = new FlowPanel();

                        panel.add(new HTML(proto().employer().getMeta().getCaption()));
                        panel.add(formRow(proto().employer().name()));
                        panel.add(formRow(proto().employer().employedForYears()));
                        panel.add(formRow(proto().employer().street1()));
                        panel.add(formRow(proto().employer().street2()));
                        panel.add(formRow(proto().employer().city()));
                        panel.add(formRow(proto().employer().province()));
                        panel.add(formRow(proto().employer().postalCode()));
                        panel.add(formRow(proto().employer().supervisorName()));
                        panel.add(formRow(proto().employer().supervisorPhone()));
                        panel.add(formRow(proto().employer().monthlySalary()));
                        panel.add(formRow(proto().employer().position()));

                        panel.getElement().getStyle().setMarginBottom(0.5, Unit.EM);
                        return panel;
                    }

                    private FlowPanel createSelfemployedPanel() {
                        FlowPanel panel = new FlowPanel();

                        panel.add(new HTML(proto().employer().getMeta().getCaption()));
                        panel.add(formRow(proto().selfEmployed().companyName()));
                        panel.add(formRow(proto().selfEmployed().yearsInBusiness()));
                        panel.add(formRow(proto().selfEmployed().fullyOwned()));
                        panel.add(formRow(proto().selfEmployed().street1()));
                        panel.add(formRow(proto().selfEmployed().street2()));
                        panel.add(formRow(proto().selfEmployed().city()));
                        panel.add(formRow(proto().selfEmployed().province()));
                        panel.add(formRow(proto().selfEmployed().postalCode()));
                        panel.add(formRow(proto().selfEmployed().monthlyRevenue()));
                        panel.add(formRow(proto().selfEmployed().monthlySalary()));
                        panel.add(formRow(proto().selfEmployed().numberOfEmployees()));

                        panel.getElement().getStyle().setMarginBottom(0.5, Unit.EM);
                        return panel;
                    }

                    private FlowPanel createSeasonallyEmployedPanel() {
                        FlowPanel panel = new FlowPanel();

                        panel.add(new HTML(proto().seasonallyEmployed().getMeta().getCaption()));
                        panel.add(formRow(proto().seasonallyEmployed().street1()));
                        panel.add(formRow(proto().seasonallyEmployed().street2()));
                        panel.add(formRow(proto().seasonallyEmployed().city()));
                        panel.add(formRow(proto().seasonallyEmployed().province()));
                        panel.add(formRow(proto().seasonallyEmployed().postalCode()));
                        panel.add(formRow(proto().seasonallyEmployed().monthlySalary()));

                        panel.getElement().getStyle().setMarginBottom(0.5, Unit.EM);
                        return panel;
                    }

                    private FlowPanel createSocialPanel() {
                        FlowPanel panel = new FlowPanel();

                        panel.add(new HTML(proto().socialServices().getMeta().getCaption()));
                        panel.add(formRow(proto().socialServices().agency()));
                        panel.add(formRow(proto().socialServices().street1()));
                        panel.add(formRow(proto().socialServices().street2()));
                        panel.add(formRow(proto().socialServices().city()));
                        panel.add(formRow(proto().socialServices().province()));
                        panel.add(formRow(proto().socialServices().postalCode()));
                        panel.add(formRow(proto().socialServices().worker()));
                        panel.add(formRow(proto().socialServices().workerPhone()));
                        panel.add(formRow(proto().socialServices().monthlyAmount()));
                        panel.add(formRow(proto().socialServices().yearsReceiving()));

                        panel.getElement().getStyle().setMarginBottom(0.5, Unit.EM);
                        return panel;
                    }

                    private FlowPanel createStudentPanel() {
                        FlowPanel panel = new FlowPanel();

                        panel.add(new HTML(proto().studentIncome().getMeta().getCaption()));
                        panel.add(formRow(proto().studentIncome().school()));
                        panel.add(formRow(proto().studentIncome().street1()));
                        panel.add(formRow(proto().studentIncome().street2()));
                        panel.add(formRow(proto().studentIncome().city()));
                        panel.add(formRow(proto().studentIncome().province()));
                        panel.add(formRow(proto().studentIncome().postalCode()));
                        panel.add(formRow(proto().studentIncome().graduate()));
                        panel.add(formRow(proto().studentIncome().fieldOfStudy()));
                        panel.add(formRow(proto().studentIncome().fundingChoices()));
                        panel.add(formRow(proto().studentIncome().employedForYears()));
                        panel.add(formRow(proto().studentIncome().supervisorName()));
                        panel.add(formRow(proto().studentIncome().supervisorPhone()));
                        panel.add(formRow(proto().studentIncome().monthlySalary()));

                        panel.getElement().getStyle().setMarginBottom(0.5, Unit.EM);
                        return panel;
                    }
                };
            }
        }

        private class TenantAssetListFolder extends CEntityFolder<TenantAsset> {

            @Override
            protected FolderDecorator<TenantAsset> createFolderDecorator() {
                return new BoxReadOnlyFolderDecorator<TenantAsset>();
            }

            @Override
            protected CEntityFolderItem<TenantAsset> createItem() {

                return new CEntityFolderItem<TenantAsset>(TenantAsset.class) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new BoxReadOnlyFolderItemDecorator(false);
                    }

                    @Override
                    public IsWidget createContent() {
                        FlowPanel main = new FlowPanel();

                        main.add(formRow(proto().assetType()));
                        main.add(formRow(proto().percent()));
                        main.add(formRow(proto().assetValue()));

                        main.getElement().getStyle().setPaddingLeft(2, Unit.EM);
                        main.setWidth("100%");
                        return main;
                    }

                    Widget formRow(IObject<?> value) {

                        FlowPanel rowContent = new FlowPanel();
                        rowContent.add(DecorationUtils.inline(new HTML(value.getMeta().getCaption()), LEFT_COLUMN_WIDTH, null));

                        CNumberLabel fixedPrc = new CNumberLabel();
                        bind(fixedPrc, value);
                        rowContent.add(DecorationUtils.inline(fixedPrc, RIGHT_COLUMN_WIDTH, "right"));
                        return rowContent;
                    }

                };
            }
        }

        private class TenantGuarantorListFolder extends CEntityFolder<TenantGuarantor> {

            @Override
            protected FolderDecorator<TenantGuarantor> createFolderDecorator() {
                return new BoxReadOnlyFolderDecorator<TenantGuarantor>();
            }

            @Override
            protected CEntityFolderItem<TenantGuarantor> createItem() {

                return new CEntityFolderItem<TenantGuarantor>(TenantGuarantor.class) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new BoxReadOnlyFolderItemDecorator(false);
                    }

                    @Override
                    public IsWidget createContent() {
                        VerticalPanel main = new VerticalPanel();

                        main.add(createFullName(proto()));
                        main.add(formRow(proto().relationship()));
                        main.add(formRow(proto().email()));

                        main.getElement().getStyle().setPaddingLeft(2, Unit.EM);
                        main.setWidth("100%");
                        return main;
                    }

                    Widget formRow(IObject<?> value) {

                        FlowPanel rowContent = new FlowPanel();
                        rowContent.add(DecorationUtils.inline(new HTML(value.getMeta().getCaption()), LEFT_COLUMN_WIDTH, null));

                        CNumberLabel fixedPrc = new CNumberLabel();
                        bind(fixedPrc, value);
                        rowContent.add(DecorationUtils.inline(fixedPrc, RIGHT_COLUMN_WIDTH, "right"));
                        return rowContent;
                    }
                };
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

            tableLayout.put("Type", "10%");
            tableLayout.put("Name", "25%");
            tableLayout.put("Color", "10%");
            tableLayout.put("Breed", "20%");
            tableLayout.put("Weight", "10%");
            tableLayout.put("Units", "5%");
            tableLayout.put("Date of Birht", "20%");

            //  It seems that header doesn't need for pets, but leave it till now... 
            //
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
            label.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);
            label.setWidth(tableLayout.get(cellName));
            content.add(label);
        }
    }

    /*
     * Charges view implementation
     */
    public class ChargesView {

        final CEntityForm<?> masterForm;

        Widget upgradesHeader;

        public ChargesView(CEntityForm<?> masterForm) {
            this.masterForm = masterForm;
        }

        public void createContent(FlowPanel main, Charges member) {

            main.add(createHeader(member.monthlyCharges()));
            main.add(masterForm.create(member.monthlyCharges().charges(), masterForm));

            main.add(upgradesHeader = createHeader2(member.monthlyCharges().upgradeCharges()));
            main.add(masterForm.create(member.monthlyCharges().upgradeCharges(), masterForm));

            main.add(createTotal(member.monthlyCharges().total()));

            main.add(createHeader(member.proRatedCharges()));
            main.add(masterForm.create(member.proRatedCharges().charges(), masterForm));
            main.add(createTotal(member.proRatedCharges().total()));

            main.add(createHeader(member.applicationCharges()));
            main.add(masterForm.create(member.applicationCharges().charges(), masterForm));
            main.add(createTotal(member.applicationCharges().total()));

            main.add(createHeader(member.paymentSplitCharges()));
            main.add(masterForm.create(member.paymentSplitCharges().charges(), masterForm));
            main.add(createTotal(member.paymentSplitCharges().total()));
        }

        private Widget createHeader(IObject<?> member) {

            return new ViewHeaderDecorator(new HTML("<h4>" + member.getMeta().getCaption() + "</h4>"));
        }

        private Widget createHeader2(IObject<?> member) {

            HTML h = new HTML("<h5>" + member.getMeta().getCaption() + "</h5>");
            h.getElement().getStyle().setMarginTop(0.5, Unit.EM);
            h.getElement().getStyle().setMarginLeft(1, Unit.EM);
            return h;
        }

        private Widget createTotal(Money member) {
            FlowPanel totalRow = new FlowPanel();

            Widget sp = new ViewLineSeparator(0, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setProperty("border", "1px dotted black");
            totalRow.add(sp);

            HTML total = new HTML("<b>" + member.getMeta().getCaption() + "</b>");
            totalRow.add(DecorationUtils.inline(total, "60%", null));
            totalRow.add(DecorationUtils.inline(masterForm.create(member, masterForm), "10%", "right"));
            totalRow.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            return totalRow;
        }

        protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
            if (member.getValueClass().equals(ChargeLine.class)) {
                return new ChargeLineFolder(masterForm);
            } else if (member.getValueClass().equals(ChargeLineSelectable.class)) {
                return new ChargeLineSelectableFolder(masterForm, null);
            } else if (member.getValueClass().equals(TenantCharge.class)) {
                return new ChargeSplitListFolder(masterForm, null);
            } else {
                return null;
            }
        }

        public void populate(Summary value) {
            upgradesHeader.setVisible(!value.charges().monthlyCharges().upgradeCharges().isEmpty());
        }
    }

    /*
     * Lease Terms view implementation
     */
    private class LeaseTermsCheck extends FlowPanel {

        private final HTML leaseTermContent = new HTML();

        public LeaseTermsCheck() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);

            // add table content panel:

            ScrollPanel leaseTerms = new ScrollPanel(leaseTermContent);
            leaseTerms.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
            leaseTerms.getElement().getStyle().setBorderWidth(1, Unit.PX);
            leaseTerms.getElement().getStyle().setBorderColor("black");
            leaseTerms.getElement().getStyle().setBackgroundColor("white");
            leaseTerms.getElement().getStyle().setColor("black");

            innerElement2upperElignment(leaseTerms);
            leaseTerms.setHeight("20em");
            add(leaseTerms);

            VistaWidgetDecorator agree = new VistaWidgetDecorator(create(proto().agree(), SummaryViewForm.this), new DecorationData(0, Unit.EM, 0, Unit.EM));
            agree.asWidget().getElement().getStyle().setMarginLeft(40, Unit.PCT);
            agree.asWidget().getElement().getStyle().setMarginTop(0.5, Unit.EM);
            agree.asWidget().getElement().getStyle().setMarginBottom(0.5, Unit.EM);
            add(agree);
        }

        public void populate(Summary value) {
            leaseTermContent.setHTML(value.leaseTerms().text().getStringView());
            //            leaseTermContent.setText(SiteResources.INSTANCE.leaseTerms().getText());
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

            CTextField edit = new CTextField();
            bind(edit, proto().fullName());
            VistaWidgetDecorator signature = new VistaWidgetDecorator(edit);
            signature.getElement().getStyle().setBackgroundColor("darkGray");
            signature.getElement().getStyle().setPaddingTop(1, Unit.EM);
            signature.setHeight("3em");
            add(signature);
        }

        public void populate(Summary value) {
        }
    }
}
