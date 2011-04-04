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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.propertyvista.common.client.ui.ViewLineSeparator;
import com.propertyvista.common.client.ui.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.components.AddressUtils;
import com.propertyvista.portal.client.ptapp.ui.components.ApplicationDocumentsFolderUploader;
import com.propertyvista.portal.client.ptapp.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.portal.client.ptapp.ui.validators.CanadianSinValidator;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.Address.OwnedRented;
import com.propertyvista.portal.domain.pt.ApplicationDocument.DocumentType;
import com.propertyvista.portal.domain.pt.EmergencyContact;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Vehicle;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.ui.flex.BoxFolderDecorator;
import com.pyx4j.entity.client.ui.flex.BoxFolderItemDecorator;
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
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

@Singleton
public class InfoViewForm extends CEntityForm<PotentialTenantInfo> {

    private static I18n i18n = I18nFactory.getI18n(SummaryViewForm.class);

    private static Date needPreviousAddress;

    private Widget previousAddressHeader;

    private ApplicationDocumentsFolderUploader fileUpload;

    @SuppressWarnings("deprecation")
    public InfoViewForm() {
        super(PotentialTenantInfo.class, new VistaEditorsComponentFactory());
        Date now = new Date();
        needPreviousAddress = TimeUtils.createDate(now.getYear() - 3, now.getMonth(), now.getDate());
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new ViewHeaderDecorator(i18n.tr("Contact Details")));

        DecorationData decorData = new DecorationData(14d, 12);
        decorData.editable = false;
        main.add(new VistaWidgetDecorator(inject(proto().firstName()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().middleName()), new DecorationData(14d, 12)));

        decorData = new DecorationData(14d, 20);
        decorData.editable = false;
        main.add(new VistaWidgetDecorator(inject(proto().lastName()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().homePhone()), new DecorationData(14d, 15)));
        main.add(new VistaWidgetDecorator(inject(proto().mobilePhone()), new DecorationData(14d, 15)));
        main.add(new VistaWidgetDecorator(inject(proto().workPhone()), new DecorationData(14d, 15)));

        decorData = new DecorationData(14d, 25);
        decorData.editable = false;
        main.add(new VistaWidgetDecorator(inject(proto().email()), decorData));

        main.add(new ViewHeaderDecorator(i18n.tr("Secure Information")));
        main.add(new VistaWidgetDecorator(inject(proto().driversLicense()), new DecorationData(14d, 20)));
        main.add(new VistaWidgetDecorator(inject(proto().driversLicenseState()), new DecorationData(14d, 17)));
        final CEditableComponent<?, ?> sin = inject(proto().secureIdentifier());
        main.add(new VistaWidgetDecorator(sin, new DecorationData(14d, 7)));

        main.add(new VistaWidgetDecorator(inject(proto().notCanadianCitizen()), new DecorationData(14d, 3)));

        main.add(inject(proto().documents(), fileUpload = new ApplicationDocumentsFolderUploader(DocumentType.securityInfo)));
        fileUpload.asWidget().getElement().getStyle().setMarginLeft(14, Unit.EM);
        fileUpload.asWidget().getElement().getStyle().setMarginTop(1, Unit.EM);
        fileUpload.asWidget().getElement().getStyle().setMarginBottom(1, Unit.EM);
        fileUpload.setVisible(false); // show it in case on not a Canadian citizen!..

        get(proto().notCanadianCitizen()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                sin.asWidget().setEnabled(!event.getValue());
                fileUpload.setVisible(event.getValue());
            }
        });

        main.add(new ViewHeaderDecorator(proto().currentAddress()));
        main.add(inject(proto().currentAddress()));

        main.add(previousAddressHeader = new ViewHeaderDecorator(proto().previousAddress()));
        main.add(inject(proto().previousAddress()));

        main.add(new ViewHeaderDecorator(proto().vehicles()));
        main.add(inject(proto().vehicles(), createVehicleFolderEditorColumns()));

        main.add(new ViewHeaderDecorator(proto().legalQuestions()));

        VistaWidgetDecorator.DecorationData decor = new VistaWidgetDecorator.DecorationData(43d, HasHorizontalAlignment.ALIGN_LEFT, 8);
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().suedForRent()), decor));
        main.add(new ViewLineSeparator(50, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().suedForDamages()), decor));
        main.add(new ViewLineSeparator(50, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().everEvicted()), decor));
        main.add(new ViewLineSeparator(50, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().defaultedOnLease()), decor));
        main.add(new ViewLineSeparator(50, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().convictedOfFelony()), decor));
        main.add(new ViewLineSeparator(50, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().legalTroubles()), decor));
        main.add(new ViewLineSeparator(50, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().filedBankruptcy()), decor));

        main.add(new ViewHeaderDecorator(proto().emergencyContacts()));
        main.add(inject(proto().emergencyContacts(), createIncomeFolderEditor()));

        main.setWidth("700px");

        addValidations();

        return main;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass().equals(Address.class)) {
            return createAddressEditor();
        } else {
            return super.create(member);
        }
    }

    @Override
    public void addValidations() {
        @SuppressWarnings("unchecked")
        CEntityEditableComponent<Address> currentAddressForm = ((CEntityEditableComponent<Address>) getRaw(proto().currentAddress()));
        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueChangeHandler(new ValueChangeHandler<Date>() {

            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                enablePreviousAddress();
            }
        });

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                return (value != null) && value.before(new Date());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The date can not be equal today or in the future.");
            }
        });

        currentAddressForm.get(currentAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                return (value != null) && value.after(new Date());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The date can not be equal today or in the past.");
            }
        });

        @SuppressWarnings("unchecked")
        CEntityEditableComponent<Address> previousAddressForm = ((CEntityEditableComponent<Address>) getRaw(proto().previousAddress()));
        previousAddressForm.get(currentAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                return (value != null) && value.before(new Date());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The date can not be equal today or in the future.");
            }
        });

        previousAddressForm.get(currentAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                return (value != null) && value.before(new Date());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The date can not be equal today or in the future.");
            }
        });

        get(proto().secureIdentifier()).addValueValidator(new CanadianSinValidator());

        get(proto().vehicles()).addValueValidator(new EditableValueValidator<List<Map<String, Object>>>() {

            @Override
            public boolean isValid(CEditableComponent<List<Map<String, Object>>, ?> component, List<Map<String, Object>> value) {
                return !EntityGraph.hasBusinessDuplicates(getValue().vehicles());
            }

            @Override
            public String getValidationMessage(CEditableComponent<List<Map<String, Object>>, ?> component, List<Map<String, Object>> value) {
                return i18n.tr("Duplicate vehicles hspecified");
            }
        });

        get(proto().emergencyContacts()).addValueValidator(new EditableValueValidator<List<Map<String, Object>>>() {

            @Override
            public boolean isValid(CEditableComponent<List<Map<String, Object>>, ?> component, List<Map<String, Object>> value) {
                return !EntityGraph.hasBusinessDuplicates(getValue().emergencyContacts());
            }

            @Override
            public String getValidationMessage(CEditableComponent<List<Map<String, Object>>, ?> component, List<Map<String, Object>> value) {
                return i18n.tr("Duplicate contacts specified");
            }
        });
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

        get(proto().secureIdentifier()).setEnabled(!value.notCanadianCitizen().isBooleanTrue());
        fileUpload.setVisible(value.notCanadianCitizen().isBooleanTrue());
    }

    private CEntityEditableComponent<Address> createAddressEditor() {
        return new CEntityEditableComponent<Address>(Address.class) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
                AddressUtils.injectIAddress(main, proto(), this);
                main.add(inject(proto().moveInDate()), 8.2);
                main.add(inject(proto().moveOutDate()), 8.2);
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

    private CEntityFolder<Vehicle> createVehicleFolderEditorColumns() {
        return new CEntityFolder<Vehicle>(Vehicle.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().plateNumber(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().year(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().make(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().model(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().country(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().province(), "17em"));
            }

            @Override
            protected FolderDecorator<Vehicle> createFolderDecorator() {
                return new TableFolderDecorator<Vehicle>(columns, SiteImages.INSTANCE.addRow(), SiteImages.INSTANCE.addRowHover(), i18n.tr("Add a vehicle"));
            }

            @Override
            protected CEntityFolderItem<Vehicle> createItem() {
                return createEmployeeRowEditor(columns);
            }

            private CEntityFolderItem<Vehicle> createEmployeeRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<Vehicle>(Vehicle.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.delRow(), SiteImages.INSTANCE.delRowHover(), i18n.tr("Remove vehicle"));
                    }

                };
            }

        };

    }

    private CEntityFolder<EmergencyContact> createIncomeFolderEditor() {

        return new CEntityFolder<EmergencyContact>(EmergencyContact.class) {

            @Override
            protected FolderDecorator<EmergencyContact> createFolderDecorator() {
                return new BoxFolderDecorator<EmergencyContact>(SiteImages.INSTANCE.addRow(), SiteImages.INSTANCE.addRowHover(),
                        i18n.tr("Add one more contact"));
            }

            @Override
            protected CEntityFolderItem<EmergencyContact> createItem() {
                return createEmergencyContactItem();
            }

            @Override
            public void populate(IList<EmergencyContact> value) {
                super.populate(value);
                if (value.isEmpty()) {
                    addItem(); // at least one Emergency Contact should be present!..
                }
            }
        };
    }

    private CEntityFolderItem<EmergencyContact> createEmergencyContactItem() {

        return new CEntityFolderItem<EmergencyContact>(EmergencyContact.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
                main.add(inject(proto().firstName()), 12);
                main.add(inject(proto().middleName()), 12);
                main.add(inject(proto().lastName()), 20);
                main.add(inject(proto().homePhone()), 15);
                main.add(inject(proto().mobilePhone()), 15);
                main.add(inject(proto().workPhone()), 15);
                AddressUtils.injectIAddress(main, proto().address(), this);
                main.add(new HTML());
                return main;
            }

            @Override
            public FolderItemDecorator createFolderItemDecorator() {
                return new BoxFolderItemDecorator(SiteImages.INSTANCE.delRow(), SiteImages.INSTANCE.delRowHover(), i18n.tr("Remove contact"), !isFirst());
            }
        };
    }
}
