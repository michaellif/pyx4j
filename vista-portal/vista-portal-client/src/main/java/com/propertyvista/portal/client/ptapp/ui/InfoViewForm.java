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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.EmergencyContact;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Vehicle;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

@Singleton
public class InfoViewForm extends BaseEntityForm<PotentialTenantInfo> {

    public InfoViewForm() {
        super(PotentialTenantInfo.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();

        main.add(new ViewHeaderDecorator(new HTML("<h4>Contact Details</h4>")));

        DecorationData decorData = new DecorationData();
        decorData.editable = false;
        decorData.componentWidth = 12;
        main.add(new VistaWidgetDecorator(inject(proto().firstName()), decorData));

        main.add(new HTML());

        decorData = new DecorationData();
        decorData.componentWidth = 12;
        main.add(new VistaWidgetDecorator(inject(proto().middleName()), decorData));
        main.add(new HTML());

        decorData = new DecorationData();
        decorData.editable = false;
        decorData.componentWidth = 20;
        main.add(new VistaWidgetDecorator(inject(proto().lastName()), decorData));

        main.add(new HTML());

        decorData = new DecorationData();
        decorData.componentWidth = 15;
        main.add(new VistaWidgetDecorator(inject(proto().homePhone()), decorData));
        main.add(new HTML());

        decorData = new DecorationData();
        decorData.componentWidth = 15;
        main.add(new VistaWidgetDecorator(inject(proto().mobilePhone()), decorData));
        main.add(new HTML());

        decorData = new DecorationData();
        decorData.componentWidth = 15;
        main.add(new VistaWidgetDecorator(inject(proto().workPhone()), decorData));
        main.add(new HTML());

        decorData = new DecorationData();
        decorData.editable = false;
        decorData.componentWidth = 25;
        main.add(new VistaWidgetDecorator(inject(proto().email()), decorData));

        main.add(new ViewHeaderDecorator(new HTML("<h4>Secure Information</h4>")));
        decorData = new DecorationData();
        decorData.componentWidth = 20;
        main.add(new VistaWidgetDecorator(inject(proto().driversLicense()), decorData));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(inject(proto().driversLicenseState())));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 11;
        main.add(new VistaWidgetDecorator(inject(proto().secureIdentifier()), decorData));
        main.add(new HTML());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Current Address</h4>")));
        main.add(inject(proto().currentAddress()));

        main.add(new ViewHeaderDecorator(new HTML("<h4>Previous Address</h4>")));
        main.add(inject(proto().previousAddress()));

        main.add(new ViewHeaderDecorator(new HTML("<h4>Vehicles</h4>")));
        main.add(inject(proto().vehicles()));

        main.add(new ViewHeaderDecorator(new HTML("<h4>General Questions</h4>")));

        VistaWidgetDecorator.DecorationData decor = new VistaWidgetDecorator.DecorationData(450, 100);
        decor.labelAlignment = HasHorizontalAlignment.ALIGN_LEFT;

        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().suedForRent()), decor));
        main.add(new ViewLineSeparator());
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().suedForDamages()), decor));
        main.add(new ViewLineSeparator());
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().everEvicted()), decor));
        main.add(new ViewLineSeparator());
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().defaultedOnLease()), decor));
        main.add(new ViewLineSeparator());
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().convictedOfFelony()), decor));
        main.add(new ViewLineSeparator());
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().legalTroubles()), decor));
        main.add(new ViewLineSeparator());
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().filedBankruptcy()), decor));
        main.add(new HTML());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Emergency Contacts</h4>")));

        main.add(new HTML("<p/><h6>Contact1</h6>"));
        main.add(inject(proto().emergencyContact1()));

        main.add(new HTML("<p/><h6>Contact2</h6>"));
        main.add(inject(proto().emergencyContact2()));

        return main;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass().equals(Address.class)) {
            return createAddressEditor();
        } else if (member.getValueClass().equals(EmergencyContact.class)) {
            return createEmergencyContactEditor();
        } else if (member == proto().vehicles()) {
            return createVehicleFolderEditorColumns();
        } else {
            return super.create(member);
        }
    }

    private CEntityEditableComponent<Address> createAddressEditor() {
        return new CEntityEditableComponent<Address>(Address.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel main = new FlowPanel();
                injectIAddress(main, proto(), this);
                DecorationData decorData = new DecorationData();
                decorData = new DecorationData();
                decorData.componentWidth = 10;
                main.add(new VistaWidgetDecorator(inject(proto().moveInDate()), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 10;
                main.add(new VistaWidgetDecorator(inject(proto().moveOutDate()), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 8;
                main.add(new VistaWidgetDecorator(inject(proto().payment()), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 15;
                main.add(new VistaWidgetDecorator(inject(proto().phone()), decorData));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(inject(proto().rented())));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 30;
                main.add(new VistaWidgetDecorator(inject(proto().managerName()), decorData));
                main.add(new HTML());
                return main;
            }
        };
    }

    private CEntityEditableComponent<EmergencyContact> createEmergencyContactEditor() {
        return new CEntityEditableComponent<EmergencyContact>(EmergencyContact.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel main = new FlowPanel();
                DecorationData decorData = new DecorationData();
                decorData.componentWidth = 12;
                main.add(new VistaWidgetDecorator(inject(proto().firstName()), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 12;
                main.add(new VistaWidgetDecorator(inject(proto().middleName()), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 20;
                main.add(new VistaWidgetDecorator(inject(proto().lastName()), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 15;
                main.add(new VistaWidgetDecorator(inject(proto().homePhone()), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 15;
                main.add(new VistaWidgetDecorator(inject(proto().mobilePhone()), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 40;
                main.add(new VistaWidgetDecorator(inject(proto().address().street1()), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 40;
                main.add(new VistaWidgetDecorator(inject(proto().address().street2()), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 15;
                main.add(new VistaWidgetDecorator(inject(proto().address().city()), decorData));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(inject(proto().address().province())));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 7;
                main.add(new VistaWidgetDecorator(inject(proto().address().postalCode()), decorData));
                main.add(new HTML());
                return main;

            }
        };
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
                return new TableFolderDecorator<Vehicle>(columns, SiteImages.INSTANCE.addRow(), "Add a vehicle");
            }

            @Override
            protected CEntityFolderItem<Vehicle> createItem() {
                return createEmployeeRowEditor(columns);
            }

            private CEntityFolderItem<Vehicle> createEmployeeRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<Vehicle>(Vehicle.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), "Remove vehicle");
                    }

                };
            }

        };

    }
}
