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
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.IPerson;
import com.propertyvista.portal.domain.pt.Pets;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.Vehicle;
import com.propertyvista.portal.rpc.pt.SiteMap;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button;

@Singleton
public class SummaryViewForm extends BaseEntityForm<Summary> {

    private SummaryViewPresenter presenter;

    private TenantsTable tenantsTable;

    private TenantsView tenantsView;

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
        FlowPanel main = new FlowPanel();

        main.add(new ViewHeaderDecorator(new HTML("<h4>Apartment</h4>")));
        main.add(new ApartmentView());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Lease Term</h4>")));
        main.add(new LeaseTermView());

        main.add(createHeaderWithEditLink("Tenants", new SiteMap.Tenants()));
        main.add(tenantsTable = new TenantsTable());
        main.add(inject(proto().tenants().tenants()));

        main.add(createHeaderWithEditLink("Info", new SiteMap.Info()));
        main.add(tenantsView = new TenantsView());

        main.add(createHeaderWithEditLink("Financial", new SiteMap.Financial()));
        main.add(inject(proto().financial()));

        main.add(createHeaderWithEditLink("Pets", new SiteMap.Pets()));
        main.add(inject(proto().pets()));

        main.add(new ViewHeaderDecorator(new HTML("<h4>Lease Terms</h4>")));
        main.add(new LeaseTermsCheck());

        main.add(inject(proto().charges()));

        //bind(new ChargesViewForm(this), proto().charges());
        //main.add(get(proto().charges()));

        main.add(new ViewHeaderDecorator(new HTML("<h4>Digital Signature</h4>")));
        main.add(new SignatureView());

        return main;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {

        if (member == proto().tenants().tenants()) {
            return tenantsTable.CreateTenantFolder();
        } else if (member.getValueClass().equals(PotentialTenantFinancial.class)) {
            return new FinancialViewForm(this);
        } else if (member.getValueClass().equals(Pets.class)) {
            return new PetsViewForm(this);
        } else if (member.getValueClass().equals(Charges.class)) {
            return new ChargesViewForm(this);
        } else {
            return super.create(member);
        }

    }

    @Override
    public void populate(Summary value) {
        super.populate(value);

        // populate internal views:
        tenantsView.populate(value);
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

        public ApartmentView() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);

            Map<String, String> tableLayout = new LinkedHashMap<String, String>();
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
            FlowPanel content;
            add(innerLevelElementElignment(content = new FlowPanel()));

            addCell(tableLayout, content, "Type", inject(proto().unitSelection().selectedUnit().floorplan().name()).asWidget());
            addCell(tableLayout, content, "Unit", inject(proto().unitSelection().selectedUnit().unitType()).asWidget());
            //            addCell(tableLayout, content, "Rent", inject(proto().unitSelection().selectedUnit().marketRent()).asWidget());
            addCell(tableLayout, content, "Deposit", inject(proto().unitSelection().selectedUnit().requiredDeposit()).asWidget());
            addCell(tableLayout, content, "Beds", inject(proto().unitSelection().selectedUnit().bedrooms()).asWidget());
            addCell(tableLayout, content, "Baths", inject(proto().unitSelection().selectedUnit().bathrooms()).asWidget());
            addCell(tableLayout, content, "Sq F", inject(proto().unitSelection().selectedUnit().area()).asWidget());
            addCell(tableLayout, content, "Available", inject(proto().unitSelection().selectedUnit().avalableForRent()).asWidget());
        }

        private void addCell(Map<String, String> tableLayout, FlowPanel content, String cellName, Widget cellContent) {
            cellContent.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            cellContent.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);
            cellContent.setWidth(tableLayout.get(cellName));
            content.add(cellContent);
        }
    }

    /*
     * Selected Apartment information view implementation
     */
    private class LeaseTermView extends FlowPanel {

        public LeaseTermView() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);

            // add lease term/price:
            FlowPanel content = new FlowPanel();
            content.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            content.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);

            Widget label = inject(proto().unitSelection().markerRent().leaseTerm()).asWidget();
            label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            content.add(DecorationUtils.inline(label, "auto"));
            label = new HTML("&nbsp;month Rent");
            label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            content.add(DecorationUtils.inline(label));

            content.add(DecorationUtils.block(new HTML()));

            label = inject(proto().unitSelection().markerRent().rent()).asWidget();
            label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            content.add(DecorationUtils.inline(label, "auto"));
            label = new HTML("&nbsp;/ month");
            label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            content.add(DecorationUtils.inline(label));

            add(innerLevelElementElignment(content));
            content.setWidth("30%");

            // add static lease terms blah-blah:
            HTML availabilityAndPricing = new HTML(SiteResources.INSTANCE.availabilityAndPricing().getText());
            availabilityAndPricing.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            availabilityAndPricing.setWidth("70%");
            add(availabilityAndPricing);
        }
    }

    /*
     * Tenants information table implementation
     */
    private class TenantsTable extends FlowPanel {

        private final Map<String, String> tableLayout = new LinkedHashMap<String, String>();

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
        }

        private void addCell(Map<String, String> tableLayout, FlowPanel content, String cellName, Widget cellContent) {
            cellContent.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            cellContent.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);
            cellContent.setWidth(tableLayout.get(cellName));
            content.add(cellContent);
        }

        public CEntityFolder<PotentialTenantInfo> CreateTenantFolder() {

            return new CEntityFolder<PotentialTenantInfo>() {

                @Override
                protected CEntityFolderItem<PotentialTenantInfo> createItem() {
                    return new CEntityFolderItem<PotentialTenantInfo>(PotentialTenantInfo.class) {

                        @Override
                        public IsWidget createContent() {
                            FlowPanel content = new FlowPanel();
                            addCell(tableLayout, content, "Name", formFullName(proto()));
                            addCell(tableLayout, content, "Date of Birht", inject(proto().birthDate()).asWidget());
                            addCell(tableLayout, content, "Email", inject(proto().email()).asWidget());
                            addCell(tableLayout, content, "Relationship", inject(proto().relationship()).asWidget());
                            addCell(tableLayout, content, "Dependant", inject(proto().dependant()).asWidget());
                            upperLevelElementElignment(content);
                            return content;
                        }

                        @Override
                        public FolderItemDecorator createFolderItemDecorator() {
                            return new BoxReadOnlyFolderItemDecorator(false);
                        }

                        private FlowPanel formFullName(IPerson person) {
                            FlowPanel fullname = new FlowPanel();
                            fullname.add(DecorationUtils.inline(inject(person.firstName()), "auto"));
                            fullname.add(DecorationUtils.inline(new HTML("&nbsp;")));
                            fullname.add(DecorationUtils.inline(inject(person.middleName()), "auto"));
                            fullname.add(DecorationUtils.inline(new HTML("&nbsp;")));
                            fullname.add(DecorationUtils.inline(inject(person.lastName()), "auto"));
                            return fullname;
                        }
                    };
                }

                @Override
                protected FolderDecorator<PotentialTenantInfo> createFolderDecorator() {
                    return new BoxReadOnlyFolderDecorator<PotentialTenantInfo>();
                }
            };
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
     * Lease Terms view implementation
     */
    private class LeaseTermsCheck extends FlowPanel {

        public LeaseTermsCheck() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);

            // add terms content:
            CLabel leaseTermContent = new CLabel();
            leaseTermContent.setAllowHtml(true);
            leaseTermContent.setWordWrap(true);
            bind(leaseTermContent, proto().leaseTerms().text());

            ScrollPanel leaseTerms = new ScrollPanel(leaseTermContent.asWidget());
            leaseTerms.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
            leaseTerms.getElement().getStyle().setBorderWidth(1, Unit.PX);
            leaseTerms.getElement().getStyle().setBorderColor("black");
            leaseTerms.getElement().getStyle().setBackgroundColor("white");
            leaseTerms.getElement().getStyle().setColor("black");

            innerElement2upperElignment(leaseTerms);
            leaseTerms.setHeight("20em");
            add(leaseTerms);

            // "I Agree" check-box:
            CCheckBox check = new CCheckBox();
            bind(check, proto().agree());
            VistaWidgetDecorator agree = new VistaWidgetDecorator(check, new DecorationData(0, Unit.EM, 0, Unit.EM));
            agree.asWidget().getElement().getStyle().setMarginLeft(40, Unit.PCT);
            agree.asWidget().getElement().getStyle().setMarginTop(0.5, Unit.EM);
            agree.asWidget().getElement().getStyle().setMarginBottom(0.5, Unit.EM);
            add(agree);
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
    }
}
