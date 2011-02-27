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

@Singleton
public class InfoViewForm extends BaseEntityForm<PotentialTenantInfo> {

    public InfoViewForm() {
        super(PotentialTenantInfo.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();

        main.add(new ViewHeaderDecorator(new HTML("<h4>Contact Details</h4>")));

        DecorationData decorData = new DecorationData();
        decorData.editable = false;
        decorData.componentWidth = 12;
        main.add(new VistaWidgetDecorator(create(proto().firstName(), this), decorData));

        main.add(new HTML());

        decorData = new DecorationData();
        decorData.componentWidth = 12;
        main.add(new VistaWidgetDecorator(create(proto().middleName(), this), decorData));
        main.add(new HTML());

        decorData = new DecorationData();
        decorData.editable = false;
        decorData.componentWidth = 20;
        main.add(new VistaWidgetDecorator(create(proto().lastName(), this), decorData));

        main.add(new HTML());

        decorData = new DecorationData();
        decorData.componentWidth = 15;
        main.add(new VistaWidgetDecorator(create(proto().homePhone(), this), decorData));
        main.add(new HTML());

        decorData = new DecorationData();
        decorData.componentWidth = 15;
        main.add(new VistaWidgetDecorator(create(proto().mobilePhone(), this), decorData));
        main.add(new HTML());

        decorData = new DecorationData();
        decorData.componentWidth = 15;
        main.add(new VistaWidgetDecorator(create(proto().workPhone(), this), decorData));
        main.add(new HTML());

        decorData = new DecorationData();
        decorData.editable = false;
        decorData.componentWidth = 25;
        main.add(new VistaWidgetDecorator(create(proto().email(), this), decorData));

        main.add(new ViewHeaderDecorator(new HTML("<h4>Secure Information</h4>")));
        decorData = new DecorationData();
        decorData.componentWidth = 20;
        main.add(new VistaWidgetDecorator(create(proto().driversLicense(), this), decorData));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto().driversLicenseState(), this)));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 11;
        main.add(new VistaWidgetDecorator(create(proto().secureIdentifier(), this), decorData));
        main.add(new HTML());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Current Address</h4>")));
        main.add(create(proto().currentAddress(), this));

        main.add(new ViewHeaderDecorator(new HTML("<h4>Previous Address</h4>")));
        main.add(create(proto().previousAddress(), this));

        main.add(new ViewHeaderDecorator(new HTML("<h4>Vehicles</h4>")));
        main.add(create(proto().vehicles(), this));

        main.add(new ViewHeaderDecorator(new HTML("<h4>General Questions</h4>")));

        VistaWidgetDecorator.DecorationData decor = new VistaWidgetDecorator.DecorationData(450, 100);
        decor.labelAlignment = HasHorizontalAlignment.ALIGN_LEFT;

        main.add(new VistaWidgetDecorator(create(proto().legalQuestions().suedForRent(), this), decor));
        main.add(new ViewLineSeparator());
        main.add(new VistaWidgetDecorator(create(proto().legalQuestions().suedForDamages(), this), decor));
        main.add(new ViewLineSeparator());
        main.add(new VistaWidgetDecorator(create(proto().legalQuestions().everEvicted(), this), decor));
        main.add(new ViewLineSeparator());
        main.add(new VistaWidgetDecorator(create(proto().legalQuestions().defaultedOnLease(), this), decor));
        main.add(new ViewLineSeparator());
        main.add(new VistaWidgetDecorator(create(proto().legalQuestions().convictedOfFelony(), this), decor));
        main.add(new ViewLineSeparator());
        main.add(new VistaWidgetDecorator(create(proto().legalQuestions().legalTroubles(), this), decor));
        main.add(new ViewLineSeparator());
        main.add(new VistaWidgetDecorator(create(proto().legalQuestions().filedBankruptcy(), this), decor));
        main.add(new HTML());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Emergency Contacts</h4>")));

        main.add(new HTML("<p/><h6>Contact1</h6>"));
        main.add(create(proto().emergencyContact1(), this));

        main.add(new HTML("<p/><h6>Contact2</h6>"));
        main.add(create(proto().emergencyContact2(), this));

        setWidget(main);
    }

    @Override
    protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
        if (member.getValueClass().equals(Address.class)) {
            return createAddressEditor();
        } else if (member.getValueClass().equals(EmergencyContact.class)) {
            return createEmergencyContactEditor();
        } else {
            return super.createMemberEditor(member);
        }
    }

    private CEntityEditableComponent<Address> createAddressEditor() {
        return new CEntityEditableComponent<Address>(Address.class) {
            @Override
            public void createContent() {
                FlowPanel main = new FlowPanel();
                createIAddress(main, proto(), this);
                DecorationData decorData = new DecorationData();
                decorData = new DecorationData();
                decorData.componentWidth = 10;
                main.add(new VistaWidgetDecorator(create(proto().moveInDate(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 10;
                main.add(new VistaWidgetDecorator(create(proto().moveOutDate(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 8;
                main.add(new VistaWidgetDecorator(create(proto().payment(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 15;
                main.add(new VistaWidgetDecorator(create(proto().phone(), this), decorData));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().rented(), this)));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 30;
                main.add(new VistaWidgetDecorator(create(proto().managerName(), this), decorData));
                main.add(new HTML());
                setWidget(main);
            }
        };
    }

    private CEntityEditableComponent<EmergencyContact> createEmergencyContactEditor() {
        return new CEntityEditableComponent<EmergencyContact>(EmergencyContact.class) {
            @Override
            public void createContent() {
                FlowPanel main = new FlowPanel();
                DecorationData decorData = new DecorationData();
                decorData.componentWidth = 12;
                main.add(new VistaWidgetDecorator(create(proto().firstName(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 12;
                main.add(new VistaWidgetDecorator(create(proto().middleName(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 20;
                main.add(new VistaWidgetDecorator(create(proto().lastName(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 15;
                main.add(new VistaWidgetDecorator(create(proto().homePhone(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 15;
                main.add(new VistaWidgetDecorator(create(proto().mobilePhone(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 40;
                main.add(new VistaWidgetDecorator(create(proto().address().street1(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 40;
                main.add(new VistaWidgetDecorator(create(proto().address().street2(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 15;
                main.add(new VistaWidgetDecorator(create(proto().address().city(), this), decorData));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().address().province(), this)));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 7;
                main.add(new VistaWidgetDecorator(create(proto().address().postalCode(), this), decorData));
                main.add(new HTML());
                setWidget(main);
            }
        };
    }

    @Override
    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        if (member.equals(proto().vehicles())) {
            return createVehicleFolderEditorColumns();
        } else {
            return super.createMemberFolderEditor(member);
        }
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
                return new CEntityFolderRow<Vehicle>(Vehicle.class, columns, InfoViewForm.this) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), "Remove vehicle");
                    }

                };
            }

        };

    }
}
