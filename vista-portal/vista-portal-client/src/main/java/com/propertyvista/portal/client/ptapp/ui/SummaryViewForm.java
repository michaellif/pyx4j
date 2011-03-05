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

import static com.pyx4j.commons.HtmlUtils.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteResources;
import com.propertyvista.portal.client.ptapp.ui.components.ReadOnlyComponentFactory;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.IPerson;
import com.propertyvista.portal.domain.pt.Pets;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.rpc.pt.SiteMap;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button;

@Singleton
public class SummaryViewForm extends BaseEntityForm<Summary> {

    private static I18n i18n = I18nFactory.getI18n(SummaryViewForm.class);

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

        main.add(new ViewHeaderDecorator(new HTML(h4(i18n.tr("Apartment")))));

        main.add(new ApartmentView());

        main.add(new ViewHeaderDecorator(new HTML(h4(i18n.tr("Lease Term")))));
        main.add(new LeaseTermView());

        main.add(createHeaderWithEditLink(i18n.tr("Tenants"), new SiteMap.Tenants()));
        main.add(tenantsTable = new TenantsTable());
        main.add(inject(proto().tenants().tenants()));

        main.add(createHeaderWithEditLink(i18n.tr("Info"), new SiteMap.Info()));
        main.add(tenantsView = new TenantsView());
        main.add(inject(proto().tenants2().tenants()));

        main.add(createHeaderWithEditLink(i18n.tr("Financial"), new SiteMap.Financial()));
        main.add(inject(proto().financial()));

        main.add(createHeaderWithEditLink(i18n.tr("Pets"), new SiteMap.Pets()));
        main.add(inject(proto().pets()));

        main.add(new ViewHeaderDecorator(new HTML(h4(i18n.tr("Lease Terms")))));
        main.add(new LeaseTermsCheck());

        main.add(inject(proto().charges()));

        // Another way of data binding:  
        //bind(new ChargesViewForm(this), proto().charges());
        //main.add(get(proto().charges()));

        main.add(new ViewHeaderDecorator(new HTML(h4(i18n.tr("Digital Signature")))));
        main.add(new SignatureView());

        return main;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {

        if (member == proto().tenants().tenants()) {
            return tenantsTable.CreateTenantFolder();
        } else if (member == proto().tenants2().tenants()) {
            return tenantsView.CreateTenantFolder();
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
    }

    private Widget createHeaderWithEditLink(String captionTxt, final AppPlace link) {

        FlowPanel header = new FlowPanel();
        HTML caption = new HTML(h4(captionTxt));
        caption.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        caption.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        caption.getElement().getStyle().setMarginRight(4, Unit.EM);
        caption.getElement().getStyle().setPaddingBottom(0, Unit.EM);
        header.add(caption);

        Button edit = new Button(i18n.tr("Edit"));
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
        e.setWidth("700px");
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
            tableLayout.put("Type", "25%");
            tableLayout.put("Unit", "20%");
            tableLayout.put("Deposit", "10%");
            tableLayout.put("Beds", "10%");
            tableLayout.put("Baths", "10%");
            tableLayout.put("Sq F", "10%");
            tableLayout.put("Available", "15%");

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
            label = new HTML("&nbsp;" + i18n.tr("month Rent"));
            label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            content.add(DecorationUtils.inline(label));

            content.add(DecorationUtils.block(new HTML()));

            label = inject(proto().unitSelection().markerRent().rent()).asWidget();
            label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            content.add(DecorationUtils.inline(label, "auto"));
            label = new HTML("&nbsp;/ " + i18n.tr("month"));
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

        public TenantsView() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            upperLevelElementElignment(this);
        }

        public CEntityFolder<PotentialTenantInfo> CreateTenantFolder() {

            return new CEntityFolder<PotentialTenantInfo>() {

                @Override
                protected CEntityFolderItem<PotentialTenantInfo> createItem() {
                    return new SummaryViewTenantInfo(PotentialTenantInfo.class);
                }

                @Override
                protected FolderDecorator<PotentialTenantInfo> createFolderDecorator() {
                    return new BoxReadOnlyFolderDecorator<PotentialTenantInfo>();
                }
            };
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
