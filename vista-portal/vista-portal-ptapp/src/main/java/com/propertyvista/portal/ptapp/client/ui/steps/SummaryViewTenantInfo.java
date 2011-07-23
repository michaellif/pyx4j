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
package com.propertyvista.portal.ptapp.client.ui.steps;

import static com.pyx4j.commons.HtmlUtils.h3;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;

import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.Vehicle;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.ptapp.client.ui.decorations.VistaReadOnlyDecorator;

public class SummaryViewTenantInfo extends SummaryViewTenantListBase<PotentialTenantInfo> {

    private final String LEFT_COLUMN_WIDTH = "40%";

    private final String GAP_COLUMN_WIDTH = "10%";

    private final String RIGHT_COLUMN_WIDTH = "40%";

    public SummaryViewTenantInfo() {
        super(PotentialTenantInfo.class);
    }

    @Override
    public IsWidget getTenantFullName() {
        return DecorationUtils.formFullName(this, proto().person());
    }

    @Override
    public IsWidget bindFullView() {

        FlowPanel fullViewPanel = new FlowPanel();

        DecorationData dd2ColumnsTable = new DecorationData(50, Unit.PCT, HasHorizontalAlignment.ALIGN_LEFT, 50, Unit.PCT, HasHorizontalAlignment.ALIGN_RIGHT);

        // ----------------------------------------------------------------------
        // Person:

        HorizontalPanel subviewPanel = new HorizontalPanel();
        subviewPanel.getElement().getStyle().setMarginTop(0.3, Unit.EM);

        FlowPanel panel = new FlowPanel();
        panel.add(new VistaReadOnlyDecorator(inject(proto().person().homePhone()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().person().mobilePhone()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().person().workPhone()), dd2ColumnsTable));
        panel.add(new VistaReadOnlyDecorator(inject(proto().person().email()), dd2ColumnsTable));
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

        Widget sp = new VistaLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
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

        sp = new VistaLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
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
        panel.setWidth("100%");
        fullViewPanel.add(panel);

        sp = new VistaLineSeparator(100, Unit.PCT, 1, Unit.EM, 1, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        fullViewPanel.add(sp);

        // ----------------------------------------------------------------------
        // Emergency:

        fullViewPanel.add(new HTML(h3(proto().emergencyContacts().getMeta().getCaption())));

        bind(createEmergencyContactsFolder(), proto().emergencyContacts());
        fullViewPanel.add(get(proto().emergencyContacts()));
        return fullViewPanel;
    }

    private FlowPanel bindAddress(PriorAddress currentAddress, DecorationData dd2ColumnsTable) {
        FlowPanel addressPanel = new FlowPanel();

        addressPanel.add(new HTML(h3(currentAddress.getMeta().getCaption())));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.country()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.postalCode()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.city()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.county()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.province()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.unitNumber()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.streetNumber()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.streetNumberSuffix()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.streetType()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.streetDirection()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.moveInDate()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.moveOutDate()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.payment()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.phone()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.rented()), dd2ColumnsTable));
        addressPanel.add(new VistaReadOnlyDecorator(inject(currentAddress.managerName()), dd2ColumnsTable));

        return addressPanel;
    }

    private CEntityFolderEditor<Vehicle> createVehicleFolderEditorColumns() {
        return new CEntityFolderEditor<Vehicle>(Vehicle.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().plateNumber(), "120px"));
                columns.add(new EntityFolderColumnDescriptor(proto().year(), "120px"));
                columns.add(new EntityFolderColumnDescriptor(proto().make(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto().model(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto().province(), "100px"));
            }

            @Override
            protected IFolderEditorDecorator<Vehicle> createFolderDecorator() {
                return new BoxReadOnlyFolderDecorator<Vehicle>();
            }

            @Override
            protected CEntityFolderItemEditor<Vehicle> createItem() {
                return createVechileRowEditor(columns);
            }

            private CEntityFolderItemEditor<Vehicle> createVechileRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRowEditor<Vehicle>(Vehicle.class, columns) {

                    @Override
                    public IFolderItemEditorDecorator<Vehicle> createFolderItemDecorator() {
                        return new BoxReadOnlyFolderItemDecorator<Vehicle>(false);
                    }

                };
            }

        };

    }

    private CEntityFolderEditor<EmergencyContact> createEmergencyContactsFolder() {

        return new CEntityFolderEditor<EmergencyContact>(EmergencyContact.class) {

            @Override
            protected IFolderEditorDecorator<EmergencyContact> createFolderDecorator() {
                return new BoxReadOnlyFolderDecorator<EmergencyContact>();
            }

            @Override
            protected CEntityFolderItemEditor<EmergencyContact> createItem() {
                return createEmergencyContactItem();
            }

            private CEntityFolderItemEditor<EmergencyContact> createEmergencyContactItem() {

                return new CEntityFolderItemEditor<EmergencyContact>(EmergencyContact.class) {
                    @Override
                    public IsWidget createContent() {
                        FlowPanel contactPanel = new FlowPanel();

                        FlowPanel person = DecorationUtils.formFullName(this, proto());
                        person.getElement().getStyle().setFontWeight(FontWeight.BOLD);
                        person.getElement().getStyle().setFontSize(1.1, Unit.EM);
                        contactPanel.add(person);

                        DecorationData dd = new DecorationData(40, Unit.PCT, HasHorizontalAlignment.ALIGN_LEFT, 60, Unit.PCT,
                                HasHorizontalAlignment.ALIGN_RIGHT);
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().homePhone()), dd));
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().mobilePhone()), dd));
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().workPhone()), dd));
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().address().unitNumber()), dd));
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().address().streetNumber()), dd));
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().address().streetNumberSuffix()), dd));
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().address().streetName()), dd));
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().address().streetType()), dd));
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().address().streetDirection()), dd));
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().address().city()), dd));
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().address().county()), dd));
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().address().province()), dd));
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().address().country()), dd));
                        contactPanel.add(new VistaReadOnlyDecorator(inject(proto().address().postalCode()), dd));

                        return contactPanel;
                    }

                    @Override
                    public IFolderItemEditorDecorator<EmergencyContact> createFolderItemDecorator() {
                        return new BoxReadOnlyFolderItemDecorator<EmergencyContact>(!isFirst());
                    }
                };
            }

        };
    }
}
