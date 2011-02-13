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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.EmergencyContact;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Vehicle;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.decorators.BasicWidgetDecorator;

@Singleton
public class InfoViewForm extends CEntityForm<PotentialTenantInfo> {

    public InfoViewForm() {
        super(PotentialTenantInfo.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();
        main.add(new HTML("<h4>Contact Details</h4>"));
        main.add(new BasicWidgetDecorator(create(proto().firstName(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(proto().middleName(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(proto().lastName(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(proto().homePhone(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(proto().mobilePhone(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(proto().email(), this)));

        main.add(new HTML("<p/><h4>Secure Information</h4>"));
        main.add(new BasicWidgetDecorator(create(proto().driversLicense(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(proto().driversLicenseState(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(proto().birthDate(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(proto().secureIdentifier(), this)));
        main.add(new HTML());

        main.add(new HTML("<p/><h4>Current Address</h4>"));
        main.add(create(proto().currentAddress(), this));

        main.add(new HTML("<p/><h4>Previous Address</h4>"));
        main.add(create(proto().previousAddress(), this));

        main.add(new HTML("<p/><h4>Vehicles</h4>"));
        main.add(create(proto().vehicles(), this));

        main.add(new HTML("<p/><h4>General Questions</h4>"));
        main.add(create(proto().legalQuestions().suedForRent(), this));
        main.add(new HTML());
        main.add(create(proto().legalQuestions().suedForDamages(), this));
        main.add(new HTML());
        main.add(create(proto().legalQuestions().everEvicted(), this));
        main.add(new HTML());
        main.add(create(proto().legalQuestions().defaultedOnLease(), this));
        main.add(new HTML());
        main.add(create(proto().legalQuestions().convictedOfFelony(), this));
        main.add(new HTML());
        main.add(create(proto().legalQuestions().legalTroubles(), this));
        main.add(new HTML());

        main.add(new HTML("<p/><h4>Emergency Contacts</h4>"));

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
                main.add(new BasicWidgetDecorator(create(proto().street1(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().street2(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().city(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().province(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().postalCode(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().moveInDate(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().moveOutDate(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().payment(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().phone(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().rented(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().managerName(), this)));
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
                main.add(new BasicWidgetDecorator(create(proto().firstName(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().middleName(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().lastName(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().home(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().mobile(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().address().street1(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().address().street2(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().address().city(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().address().province(), this)));
                main.add(new HTML());
                main.add(new BasicWidgetDecorator(create(proto().address().postalCode(), this)));
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
            protected FolderDecorator createFolderDecorator() {
                return new TableFolderDecorator(columns, SiteImages.INSTANCE.addRow());
            }

            @Override
            protected CEntityFolderItem<Vehicle> createItem() {
                return createEmployeeRowEditor(columns);
            }

            private CEntityFolderItem<Vehicle> createEmployeeRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<Vehicle>(Vehicle.class, columns, InfoViewForm.this) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow());
                    }

                };
            }

        };

    }
}
