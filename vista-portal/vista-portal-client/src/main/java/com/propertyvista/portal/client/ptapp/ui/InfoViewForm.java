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

import static com.pyx4j.commons.HtmlUtils.h4;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.client.ptapp.ui.validators.CanadianSinValidator;
import com.propertyvista.portal.client.ptapp.ui.validators.ZipCodeValueValidator;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.Address.OwnedRented;
import com.propertyvista.portal.domain.pt.EmergencyContact;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Vehicle;

import com.pyx4j.commons.TimeUtils;
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

    private static I18n i18n = I18nFactory.getI18n(SummaryViewForm.class);

    private static Date needPreviousAddress;

    private Widget previousAddressHeader;

    @SuppressWarnings("deprecation")
    public InfoViewForm() {
        super(PotentialTenantInfo.class);
        Date now = new Date();
        needPreviousAddress = TimeUtils.createDate(now.getYear() - 3, now.getMonth(), now.getDate());
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new ViewHeaderDecorator(i18n.tr("Contact Details")));

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

        main.add(new ViewHeaderDecorator(i18n.tr("Secure Information")));
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

        main.add(new ViewHeaderDecorator(proto().currentAddress()));
        main.add(inject(proto().currentAddress()));

        main.add(previousAddressHeader = new ViewHeaderDecorator(proto().previousAddress()));
        main.add(inject(proto().previousAddress()));

        main.add(new ViewHeaderDecorator(proto().vehicles()));
        main.add(inject(proto().vehicles(), createVehicleFolderEditorColumns()));

        main.add(new ViewHeaderDecorator(proto().legalQuestions()));

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

        main.add(new ViewHeaderDecorator(h4(i18n.tr("Emergency Contact1"))));
        main.add(inject(proto().emergencyContact1()));

        main.add(new ViewHeaderDecorator(h4(i18n.tr("Emergency Contact2"))));
        main.add(inject(proto().emergencyContact2()));

        main.setWidth("700px");

        addValidations();

        return main;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass().equals(Address.class)) {
            return createAddressEditor();
        } else if (member.getValueClass().equals(EmergencyContact.class)) {
            return createEmergencyContactEditor();
        } else {
            return super.create(member);
        }
    }

    private void addValidations() {
        @SuppressWarnings("unchecked")
        CEntityEditableComponent<Address> currentAddressForm = ((CEntityEditableComponent<Address>) getRaw(proto().currentAddress()));
        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueChangeHandler(new ValueChangeHandler<Date>() {

            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                enablePreviousAddress();
            }
        });

        get(proto().secureIdentifier()).addValueValidator(new CanadianSinValidator());
    }

    private void enablePreviousAddress() {
        boolean enabled = false;
        if (!getValue().currentAddress().moveInDate().isNull()) {
            enabled = needPreviousAddress.before(getValue().currentAddress().moveInDate().getValue());
        }
        get(proto().previousAddress()).setVisible(enabled);
        previousAddressHeader.setVisible(enabled);
    }

    @Override
    public void populate(PotentialTenantInfo value) {
        super.populate(value);
        enablePreviousAddress();
    }

    private CEntityEditableComponent<Address> createAddressEditor() {
        return new CEntityEditableComponent<Address>(Address.class) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
                injectIAddress(main, proto(), this);
                main.add(inject(proto().moveInDate()), 10);
                main.add(inject(proto().moveOutDate()), 10);
                main.add(inject(proto().phone()), 15);

                CEditableComponent<?, ?> rentedComponent = inject(proto().rented());
                rentedComponent.addValueChangeHandler(new ValueChangeHandler() {
                    @Override
                    public void onValueChange(ValueChangeEvent event) {
                        setVizibility(getValue());
                    }
                });
                main.add(rentedComponent, 15);
                main.add(inject(proto().payment()), 8);
                main.add(inject(proto().managerName()), 30);
                main.add(new HTML());
                return main;
            }

            @Override
            public void populate(Address value) {
                super.populate(value);
                setVizibility(value);
            }

            private void setVizibility(Address value) {
                boolean rented = OwnedRented.Rented.equals(value.rented().getValue());
                get(proto().payment()).setVisible(rented);
                get(proto().managerName()).setVisible(rented);
            }

        };

    }

    private CEntityEditableComponent<EmergencyContact> createEmergencyContactEditor() {
        return new CEntityEditableComponent<EmergencyContact>(EmergencyContact.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
                main.add(inject(proto().firstName()), 12);
                main.add(inject(proto().middleName()), 12);
                main.add(inject(proto().lastName()), 20);
                main.add(inject(proto().homePhone()), 15);
                main.add(inject(proto().mobilePhone()), 15);
                main.add(inject(proto().workPhone()), 15);
                main.add(inject(proto().address().street1()), 20);
                main.add(inject(proto().address().street2()), 20);
                main.add(inject(proto().address().city()), 15);
                main.add(inject(proto().address().province()), 10);
                main.add(inject(proto().address().postalCode()), 7);
                main.add(new HTML());

                get(proto().address().postalCode()).addValueValidator(new ZipCodeValueValidator());

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
                columns.add(new EntityFolderColumnDescriptor(proto.plateNumber(), "5em", "1em"));
                columns.add(new EntityFolderColumnDescriptor(proto.year(), "5em", "1em"));
                columns.add(new EntityFolderColumnDescriptor(proto.make(), "5em", "1em"));
                columns.add(new EntityFolderColumnDescriptor(proto.model(), "5em", "1em"));
                columns.add(new EntityFolderColumnDescriptor(proto.province(), "10em", "1em"));
            }

            @Override
            protected FolderDecorator<Vehicle> createFolderDecorator() {
                return new TableFolderDecorator<Vehicle>(columns, SiteImages.INSTANCE.addRow(), i18n.tr("Add a vehicle"));
            }

            @Override
            protected CEntityFolderItem<Vehicle> createItem() {
                return createEmployeeRowEditor(columns);
            }

            private CEntityFolderItem<Vehicle> createEmployeeRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<Vehicle>(Vehicle.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), i18n.tr("Remove vehicle"));
                    }

                };
            }

        };

    }
}
