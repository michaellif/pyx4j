/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import static com.pyx4j.commons.HtmlUtils.h3;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaReadOnlyDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.EmergencyContact;
import com.propertyvista.portal.domain.pt.IPerson;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Vehicle;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.widgets.client.Button;

public class SummaryViewTenantInfo extends CEntityFolderItem<PotentialTenantInfo> {

    private final I18n i18n = I18nFactory.getI18n(SummaryViewTenantInfo.class);

    private final String LEFT_COLUMN_WIDTH = "40%";

    private final String GAP_COLUMN_WIDTH = "10%";

    private final String RIGHT_COLUMN_WIDTH = "40%";

    private final FlowPanel fullViewPanel = new FlowPanel();

    public SummaryViewTenantInfo(Class<PotentialTenantInfo> clazz) {
        super(clazz);
    }

    @Override
    public FolderItemDecorator createFolderItemDecorator() {
        return new BoxReadOnlyFolderItemDecorator(false);
    }

    @Override
    public IsWidget createContent() {

        FlowPanel main = new FlowPanel();

        SimplePanel padder = new SimplePanel();
        padder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        padder.getElement().getStyle().setBackgroundColor("white");

        padder.getElement().getStyle().setBorderWidth(1, Unit.PX);
        padder.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        padder.getElement().getStyle().setBorderColor("black");
        padder.getElement().getStyle().setMarginBottom(0.5, Unit.EM);

        main.getElement().getStyle().setPaddingTop(0.5, Unit.EM);
        main.getElement().getStyle().setPaddingBottom(0.5, Unit.EM);
        main.getElement().getStyle().setPaddingLeft(15, Unit.PX);
        main.getElement().getStyle().setPaddingRight(15, Unit.PX);
        main.setWidth("670");

        main.add(bindCompactView());
        main.add(bindFullView());

        padder.setWidget(main);
        padder.setWidth("700px");
        return padder;
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

    public Widget bindCompactView() {

        HorizontalPanel panel = new HorizontalPanel();

        Widget sw;
        panel.add(sw = addViewSwitcher());
        panel.setCellVerticalAlignment(sw, HasVerticalAlignment.ALIGN_MIDDLE);

        FlowPanel tenant = formFullName(proto());
        tenant.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        tenant.getElement().getStyle().setFontSize(1.5, Unit.EM);
        tenant.getElement().getStyle().setPaddingTop(0.2, Unit.EM);
        tenant.getElement().getStyle().setPaddingBottom(0.3, Unit.EM);
        tenant.getElement().getStyle().setMarginLeft(2, Unit.EM);
        panel.add(tenant);
        panel.setCellVerticalAlignment(tenant, HasVerticalAlignment.ALIGN_MIDDLE);

        return panel;
    }

    public Widget bindFullView() {

        DecorationData dd2ColumnsTable = new DecorationData(50, Unit.PCT, HasHorizontalAlignment.ALIGN_LEFT, 50, Unit.PCT, HasHorizontalAlignment.ALIGN_RIGHT);

        // ----------------------------------------------------------------------
        // Person:

        HorizontalPanel subviewPanel = new HorizontalPanel();
        subviewPanel.getElement().getStyle().setMarginTop(0.3, Unit.EM);

        FlowPanel panel = new FlowPanel();
        panel.add(new VistaReadOnlyDecorator(inject(proto().homePhone()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().mobilePhone()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().workPhone()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().email()), dd2ColumnsTable));
        subviewPanel.add(panel);
        subviewPanel.setCellWidth(panel, LEFT_COLUMN_WIDTH);

        panel = new FlowPanel();
        subviewPanel.add(panel);
        subviewPanel.setCellWidth(panel, GAP_COLUMN_WIDTH);

        panel = new FlowPanel();
        panel.add(new VistaReadOnlyDecorator(inject(proto().driversLicense()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().driversLicenseState()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().secureIdentifier()), dd2ColumnsTable));
        subviewPanel.add(panel);
        subviewPanel.setCellWidth(panel, RIGHT_COLUMN_WIDTH);

        subviewPanel.add(new FlowPanel()); // add empty cell just for proper resizing of the previous two ;)
        subviewPanel.setWidth("100%");
        fullViewPanel.add(subviewPanel);

        Widget sp = new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        fullViewPanel.add(sp);

        // ----------------------------------------------------------------------
        // Addresses:

        subviewPanel = new HorizontalPanel();

        subviewPanel.add(panel = bindAddress(proto().currentAddress(), dd2ColumnsTable));
        subviewPanel.setCellWidth(panel, LEFT_COLUMN_WIDTH);

        panel = new FlowPanel();
        subviewPanel.add(panel);
        subviewPanel.setCellWidth(panel, GAP_COLUMN_WIDTH);

        subviewPanel.add(panel = bindAddress(proto().previousAddress(), dd2ColumnsTable));
        subviewPanel.setCellWidth(panel, RIGHT_COLUMN_WIDTH);

        subviewPanel.add(new FlowPanel()); // add empty cell just for proper resizing of the previous two ;)
        subviewPanel.setWidth("100%");
        fullViewPanel.add(subviewPanel);

        sp = new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        fullViewPanel.add(sp);

        // ----------------------------------------------------------------------
        // Vehicles:

        fullViewPanel.add(new HTML(h3(proto().vehicles().getMeta().getCaption())));

        bind(createVehicleFolderEditorColumns(), proto().vehicles());
        fullViewPanel.add(get(proto().vehicles()));

        sp = new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        fullViewPanel.add(sp);

        // ----------------------------------------------------------------------
        // legal Questions:

        DecorationData ddQuestionay = new DecorationData(80, Unit.PCT, 10, Unit.PCT);
        ddQuestionay.labelAlignment = HasHorizontalAlignment.ALIGN_LEFT;
        ddQuestionay.componentAlignment = HasHorizontalAlignment.ALIGN_RIGHT;
        ddQuestionay.componentVerticalAlignment = VerticalAlign.MIDDLE;

        panel = new FlowPanel();
        panel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        panel.add(new HTML(h3(proto().legalQuestions().getMeta().getCaption())));
        panel.add(new VistaReadOnlyDecorator(inject(proto().legalQuestions().suedForRent()), ddQuestionay));
        panel.add(new VistaReadOnlyDecorator(inject(proto().legalQuestions().suedForDamages()), ddQuestionay));
        panel.add(new VistaReadOnlyDecorator(inject(proto().legalQuestions().everEvicted()), ddQuestionay));
        panel.add(new VistaReadOnlyDecorator(inject(proto().legalQuestions().defaultedOnLease()), ddQuestionay));
        panel.add(new VistaReadOnlyDecorator(inject(proto().legalQuestions().convictedOfFelony()), ddQuestionay));
        panel.add(new VistaReadOnlyDecorator(inject(proto().legalQuestions().legalTroubles()), ddQuestionay));
        panel.add(new VistaReadOnlyDecorator(inject(proto().legalQuestions().filedBankruptcy()), ddQuestionay));
        panel.setWidth("700px");
        fullViewPanel.add(panel);

        sp = new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        fullViewPanel.add(sp);

        // ----------------------------------------------------------------------
        // Emergency:

        fullViewPanel.add(new HTML(h3(i18n.tr("Emergency Contacts"))));

        subviewPanel = new HorizontalPanel();
        subviewPanel.getElement().getStyle().setMarginTop(0.3, Unit.EM);
        subviewPanel.getElement().getStyle().setMarginBottom(0.5, Unit.EM);

        subviewPanel.add(panel = bindEmergencyContact(proto().emergencyContact1(), dd2ColumnsTable));
        subviewPanel.setCellWidth(panel, LEFT_COLUMN_WIDTH);

        panel = new FlowPanel();
        subviewPanel.add(panel);
        subviewPanel.setCellWidth(panel, GAP_COLUMN_WIDTH);

        subviewPanel.add(panel = bindEmergencyContact(proto().emergencyContact2(), dd2ColumnsTable));
        subviewPanel.setCellWidth(panel, RIGHT_COLUMN_WIDTH);

        subviewPanel.add(new FlowPanel()); // add empty cell just for proper resizing of the previous two ;)
        subviewPanel.setWidth("100%");
        fullViewPanel.add(subviewPanel);

        fullViewPanel.setVisible(false);
        return fullViewPanel;
    }

    private FlowPanel bindAddress(Address currentAddress, DecorationData dd2ColumnsTable) {
        FlowPanel addressPanel = new FlowPanel();

        addressPanel.add(new HTML(h3(currentAddress.getMeta().getCaption())));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.postalCode()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.city()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.province()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.street1()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.street2()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.moveInDate()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.moveOutDate()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.payment()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.phone()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.rented()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.managerName()), dd2ColumnsTable));

        return addressPanel;
    }

    private FlowPanel bindEmergencyContact(EmergencyContact emergencyContact, DecorationData dd2ColumnsTable) {
        FlowPanel contactPanel = new FlowPanel();

        FlowPanel person = formFullName(emergencyContact);
        person.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        person.getElement().getStyle().setFontSize(1.1, Unit.EM);
        contactPanel.add(person);
        contactPanel.add(new VistaReadOnlyDecorator(inject(emergencyContact.homePhone()), dd2ColumnsTable));
        contactPanel.add(new VistaReadOnlyDecorator(inject(emergencyContact.mobilePhone()), dd2ColumnsTable));
        contactPanel.add(new VistaReadOnlyDecorator(inject(emergencyContact.workPhone()), dd2ColumnsTable));
        contactPanel.add(new VistaReadOnlyDecorator(inject(emergencyContact.address().street1()), dd2ColumnsTable));
        contactPanel.add(new VistaReadOnlyDecorator(inject(emergencyContact.address().street2()), dd2ColumnsTable));
        contactPanel.add(new VistaReadOnlyDecorator(inject(emergencyContact.address().city()), dd2ColumnsTable));
        contactPanel.add(new VistaReadOnlyDecorator(inject(emergencyContact.address().province()), dd2ColumnsTable));
        contactPanel.add(new VistaReadOnlyDecorator(inject(emergencyContact.address().postalCode()), dd2ColumnsTable));

        return contactPanel;
    }

    private Widget addViewSwitcher() {

        Button switcher = new Button("v");
        switcher.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (fullViewPanel.isVisible()) {
                    fullViewPanel.setVisible(false);
                } else {
                    fullViewPanel.setVisible(true);
                }
            }
        });

        return switcher;
    }

    private CEntityFolder<Vehicle> createVehicleFolderEditorColumns() {
        return new CEntityFolder<Vehicle>() {

            private List<EntityFolderColumnDescriptor> columns;

            {
                Vehicle proto = EntityFactory.getEntityPrototype(Vehicle.class);
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto.plateNumber(), "120px"));
                columns.add(new EntityFolderColumnDescriptor(proto.year(), "120px"));
                columns.add(new EntityFolderColumnDescriptor(proto.make(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.model(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.province(), "100px"));
            }

            @Override
            protected FolderDecorator<Vehicle> createFolderDecorator() {
                return new BoxReadOnlyFolderDecorator<Vehicle>();
            }

            @Override
            protected CEntityFolderItem<Vehicle> createItem() {
                return createVechileRowEditor(columns);
            }

            private CEntityFolderItem<Vehicle> createVechileRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<Vehicle>(Vehicle.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new BoxReadOnlyFolderItemDecorator(false);
                    }

                };
            }

        };

    }
}
