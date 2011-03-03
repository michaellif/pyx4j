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
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaReadOnlyDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;
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

        main.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        upperLevelElementElignment(main);

        main.getElement().getStyle().setBackgroundColor("white");
        main.getElement().getStyle().setBorderWidth(1, Unit.PX);
        main.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        main.getElement().getStyle().setBorderColor("black");

        //                getElement().getStyle().setPaddingRight(1, Unit.EM);
        main.getElement().getStyle().setPaddingTop(0.5, Unit.EM);
        main.getElement().getStyle().setPaddingBottom(0.5, Unit.EM);

        main.add(bindCompactView());

        main.add(bindFullView());

        return main;
    }

    private Widget upperLevelElementElignment(Widget e) {
        e.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        e.getElement().getStyle().setPaddingRight(1, Unit.EM);
        e.setWidth("70%");
        return e;
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

        addViewSwitcher(panel);
        panel.setCellVerticalAlignment(panel.getWidget(panel.getWidgetCount() - 1), HasVerticalAlignment.ALIGN_MIDDLE);

        //                HTML tenant = new HTML(h2(proto().firstName()) + " &nbsp " + proto().lastName())));
        FlowPanel tenant = formFullName(proto());
        tenant.getElement().getStyle().setMarginLeft(4, Unit.EM);
        panel.add(tenant);
        panel.setCellVerticalAlignment(tenant, HasVerticalAlignment.ALIGN_MIDDLE);

        return panel;
    }

    public Widget bindFullView() {

        DecorationData dd2ColumnsTable = new DecorationData(50, Unit.PCT, 50, Unit.PCT);
        dd2ColumnsTable.labelAlignment = HasHorizontalAlignment.ALIGN_LEFT;
        dd2ColumnsTable.componentAlignment = HasHorizontalAlignment.ALIGN_RIGHT;

        // ----------------------------------------------------------------------

        HorizontalPanel subviewPanel = new HorizontalPanel();

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

        // add empty cell just for proper resizing of the previous two ;)
        subviewPanel.add(new FlowPanel());

        subviewPanel.setWidth("100%");
        fullViewPanel.add(subviewPanel);

        Widget sp = new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        fullViewPanel.add(sp);

        // ----------------------------------------------------------------------

        subviewPanel = new HorizontalPanel();

        panel = new FlowPanel();
        panel.add(new HTML(h3(proto().currentAddress().getMeta().getCaption())));
        panel.add(new VistaReadOnlyDecorator(inject(proto().currentAddress().postalCode()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().currentAddress().city()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().currentAddress().province()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().currentAddress().street1()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().currentAddress().street2()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().currentAddress().moveInDate()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().currentAddress().moveOutDate()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().currentAddress().payment()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().currentAddress().phone()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().currentAddress().rented()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().currentAddress().managerName()), dd2ColumnsTable));
        subviewPanel.add(panel);
        subviewPanel.setCellWidth(panel, LEFT_COLUMN_WIDTH);

        panel = new FlowPanel();
        subviewPanel.add(panel);
        subviewPanel.setCellWidth(panel, GAP_COLUMN_WIDTH);

        panel = new FlowPanel();

        panel.add(new HTML("<h3>" + proto().previousAddress().getMeta().getCaption() + "</h3>"));
        panel.add(new VistaReadOnlyDecorator(inject(proto().previousAddress().postalCode()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().previousAddress().city()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().previousAddress().province()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().previousAddress().street1()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().previousAddress().street2()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().previousAddress().moveInDate()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().previousAddress().moveOutDate()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().previousAddress().payment()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().previousAddress().phone()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().previousAddress().rented()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().previousAddress().managerName()), dd2ColumnsTable));
        subviewPanel.add(panel);
        subviewPanel.setCellWidth(panel, RIGHT_COLUMN_WIDTH);

        // add empty cell just for proper resizing of the previous two ;)
        subviewPanel.add(new FlowPanel());

        subviewPanel.setWidth("100%");
        fullViewPanel.add(subviewPanel);

        sp = new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        fullViewPanel.add(sp);

        // ----------------------------------------------------------------------

        fullViewPanel.add(new HTML(h3(proto().vehicles().getMeta().getCaption())));

        bind(createVehicleFolderEditorColumns(), proto().vehicles());
        fullViewPanel.add(get(proto().vehicles()));

        //            Widget w;
        //            for (Vehicle vhcl : proto().vehicles()) {
        //                HorizontalPanel vehiclePanel = new HorizontalPanel();
        //
        //                vehiclePanel.add(w = new HTML(vhcl.plateNumber())));
        //                vehiclePanel.setCellWidth(w, "20%");
        //
        //                vehiclePanel.add(w = new HTML(vhcl.year())));
        //                vehiclePanel.setCellWidth(w, "20%");
        //
        //                vehiclePanel.add(w = new HTML(vhcl.make())));
        //                vehiclePanel.setCellWidth(w, "20%");
        //
        //                vehiclePanel.add(w = new HTML(vhcl.model())));
        //                vehiclePanel.setCellWidth(w, "20%");
        //
        //                vehiclePanel.add(w = new HTML(vhcl.province())));
        //                vehiclePanel.setCellWidth(w, "20%");
        //
        //                vehiclePanel.setWidth("100%");
        //                add(vehiclePanel);
        //            }

        sp = new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        fullViewPanel.add(sp);

        // ----------------------------------------------------------------------

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
        panel.setWidth("70%");
        fullViewPanel.add(panel);

        sp = new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        fullViewPanel.add(sp);

        // ----------------------------------------------------------------------

        fullViewPanel.add(new HTML(h3(i18n.tr("Emergency Contacts"))));

        subviewPanel = new HorizontalPanel();

        panel = new FlowPanel();
        panel.add(formFullName(proto().emergencyContact1()));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact1().homePhone()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact1().mobilePhone()), dd2ColumnsTable));
        //                panel.add(new VistaTextPairDecorator(proto().emergencyContact1().workPhone().getMeta().getCaption(), inject(proto().emergencyContact1().workPhone()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact1().address().street1()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact1().address().street2()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact1().address().city()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact1().address().province()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact1().address().postalCode()), dd2ColumnsTable));
        subviewPanel.add(panel);
        subviewPanel.setCellWidth(panel, LEFT_COLUMN_WIDTH);

        panel = new FlowPanel();
        subviewPanel.add(panel);
        subviewPanel.setCellWidth(panel, GAP_COLUMN_WIDTH);

        panel = new FlowPanel();
        panel.add(formFullName(proto().emergencyContact2()));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact2().homePhone()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact2().mobilePhone()), dd2ColumnsTable));
        //                panel.add(new VistaTextPairDecorator(proto().emergencyContact2().workPhone().getMeta().getCaption(), inject(proto().emergencyContact2().workPhone()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact2().address().street1()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact2().address().street2()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact2().address().city()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact2().address().province()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().emergencyContact2().address().postalCode()), dd2ColumnsTable));
        subviewPanel.add(panel);
        subviewPanel.setCellWidth(panel, RIGHT_COLUMN_WIDTH);

        // add empty cell just for proper resizing of the previous two ;)
        subviewPanel.add(new FlowPanel());

        subviewPanel.setWidth("100%");
        fullViewPanel.add(subviewPanel);

        fullViewPanel.setVisible(false);
        return fullViewPanel;
    }

    private void addViewSwitcher(HorizontalPanel panel) {

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
        panel.add(switcher);
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
                return new BoxReadOnlyFolderDecorator<Vehicle>() {
                    @Override
                    public void setFolder(CEntityFolder<?> w) {
                        super.setFolder(w);
                        this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                    }
                };
            }

            @Override
            protected CEntityFolderItem<Vehicle> createItem() {
                return createEmployeeRowEditor(columns);
            }

            private CEntityFolderItem<Vehicle> createEmployeeRowEditor(final List<EntityFolderColumnDescriptor> columns) {
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
