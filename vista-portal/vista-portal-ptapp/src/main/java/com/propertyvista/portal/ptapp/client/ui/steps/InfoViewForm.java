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
package com.propertyvista.portal.ptapp.client.ui.steps;

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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.editor.BoxFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.BoxFolderItemEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.components.ApplicationDocumentsFolderUploader;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.validators.CanadianSinValidator;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.domain.ApplicationDocument.DocumentType;
import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.PriorAddress.OwnedRented;
import com.propertyvista.misc.BusinessRules;
import com.propertyvista.portal.domain.ptapp.dto.TenantInfoEditorDTO;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;

public class InfoViewForm extends CEntityForm<TenantInfoEditorDTO> {

    private static I18n i18n = I18nFactory.getI18n(InfoViewForm.class);

    private Widget previousAddressHeader;

    private ApplicationDocumentsFolderUploader fileUpload;

    public InfoViewForm() {
        super(TenantInfoEditorDTO.class, new VistaEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new VistaHeaderBar(i18n.tr("Contact Details")));

        DecorationData decorData = new DecorationData(14d, 12);
        decorData.editable = false;
        main.add(new VistaWidgetDecorator(inject(proto().person().name().firstName()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().person().name().middleName()), new DecorationData(14d, 12)));

        decorData = new DecorationData(14d, 20);
        decorData.editable = false;
        main.add(new VistaWidgetDecorator(inject(proto().person().name().lastName()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().person().homePhone()), new DecorationData(14d, 15)));
        main.add(new VistaWidgetDecorator(inject(proto().person().mobilePhone()), new DecorationData(14d, 15)));
        main.add(new VistaWidgetDecorator(inject(proto().person().workPhone()), new DecorationData(14d, 15)));

        decorData = new DecorationData(14d, 25);
        decorData.editable = false;
        main.add(new VistaWidgetDecorator(inject(proto().person().email()), decorData));

        main.add(new VistaHeaderBar(i18n.tr("Secure Information")));
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
                if (event.getValue()) {
                    sin.setValue(null);
                }
                sin.asWidget().setEnabled(!event.getValue());
                fileUpload.setVisible(event.getValue());
            }
        });

        main.add(new VistaHeaderBar(proto().currentAddress()));
        main.add(inject(proto().currentAddress()));

        main.add(previousAddressHeader = new VistaHeaderBar(proto().previousAddress()));
        main.add(inject(proto().previousAddress()));

        main.add(new VistaHeaderBar(proto().legalQuestions()));

        DecorationData decor = new DecorationData(43d, HasHorizontalAlignment.ALIGN_LEFT, 8);
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().suedForRent()), decor));
        main.add(new VistaLineSeparator(50, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().suedForDamages()), decor));
        main.add(new VistaLineSeparator(50, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().everEvicted()), decor));
        main.add(new VistaLineSeparator(50, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().defaultedOnLease()), decor));
        main.add(new VistaLineSeparator(50, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().convictedOfFelony()), decor));
        main.add(new VistaLineSeparator(50, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().legalTroubles()), decor));
        main.add(new VistaLineSeparator(50, Unit.EM));
        main.add(new VistaWidgetDecorator(inject(proto().legalQuestions().filedBankruptcy()), decor));

        main.add(new VistaHeaderBar(proto().emergencyContacts()));
        main.add(inject(proto().emergencyContacts(), createEmergencyContactFolderEditor()));

        main.setWidth("800px");

        addValidations();

        return main;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass().equals(PriorAddress.class)) {
            return createAddressEditor();
        } else {
            return super.create(member);
        }
    }

    @Override
    public void addValidations() {
        @SuppressWarnings("unchecked")
        CEntityEditor<PriorAddress> currentAddressForm = ((CEntityEditor<PriorAddress>) getRaw(proto().currentAddress()));
        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {

            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                enablePreviousAddress();
            }
        });

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                return (value != null) && value.before(TimeUtils.today());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The date can not be equal today or in the future.");
            }
        });

        currentAddressForm.get(currentAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                return (value != null) && value.after(TimeUtils.today());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The date can not be equal today or in the past.");
            }
        });

        // ------------------------------------------------------------------------------------------------        

        @SuppressWarnings("unchecked")
        final CEntityEditor<PriorAddress> previousAddressForm = ((CEntityEditor<PriorAddress>) getRaw(proto().previousAddress()));
        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                return (value != null) && value.before(TimeUtils.today());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The date can not be equal today or in the future.");
            }
        });

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                IPrimitive<LogicalDate> date = getValue().previousAddress().moveOutDate();
                return (date.isNull() || value.before(date.getValue()));
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The move out date can not be equal or after move out one.");
            }
        });

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveOutDate())));

        // ------------------------------------------------------------------------------------------------        

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                return (value != null) && value.before(TimeUtils.today());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The date can not be equal today or in the future.");
            }
        });

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                IPrimitive<LogicalDate> date = getValue().previousAddress().moveInDate();
                return (date.isNull() || value.after(date.getValue()));
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The move out date can not be before or equal move in one.");
            }
        });

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveInDate())));

        // ------------------------------------------------------------------------------------------------

        get(proto().secureIdentifier()).addValueValidator(new CanadianSinValidator());

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
        boolean enabled = BusinessRules.infoPageNeedPreviousAddress(getValue().currentAddress().moveInDate().getValue());
        get(proto().previousAddress()).setVisible(enabled);
        previousAddressHeader.setVisible(enabled);
    }

    @Override
    public void populate(TenantInfoEditorDTO value) {
        super.populate(value);

        enablePreviousAddress();

        get(proto().secureIdentifier()).setEnabled(!value.notCanadianCitizen().isBooleanTrue());
        fileUpload.setVisible(value.notCanadianCitizen().isBooleanTrue());
        if (value != null) {
            fileUpload.setTenantID(((IEntity) value).getPrimaryKey());
        }
    }

    private CEntityEditor<PriorAddress> createAddressEditor() {
        return new CEntityEditor<PriorAddress>(PriorAddress.class) {
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
            public void populate(PriorAddress value) {
                super.populate(value);
                setVizibility(value);
            }

            private void setVizibility(PriorAddress value) {
                boolean rented = OwnedRented.rented.equals(value.rented().getValue());
                get(proto().payment()).setVisible(rented);
                get(proto().managerName()).setVisible(rented);
            }

        };

    }

    private CEntityFolderEditor<EmergencyContact> createEmergencyContactFolderEditor() {

        return new CEntityFolderEditor<EmergencyContact>(EmergencyContact.class) {

            @Override
            protected IFolderEditorDecorator<EmergencyContact> createFolderDecorator() {
                return new BoxFolderEditorDecorator<EmergencyContact>(PortalImages.INSTANCE.addRow(), PortalImages.INSTANCE.addRowHover(),
                        i18n.tr("Add one more contact"));
            }

            @Override
            protected CEntityFolderItemEditor<EmergencyContact> createItem() {
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

    private CEntityFolderItemEditor<EmergencyContact> createEmergencyContactItem() {

        return new CEntityFolderItemEditor<EmergencyContact>(EmergencyContact.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
                main.add(inject(proto().name().firstName()), 12);
                main.add(inject(proto().name().middleName()), 12);
                main.add(inject(proto().name().lastName()), 20);
                main.add(inject(proto().homePhone()), 15);
                main.add(inject(proto().mobilePhone()), 15);
                main.add(inject(proto().workPhone()), 15);
                AddressUtils.injectIAddress(main, proto().address(), this);
                main.add(new HTML());
                return main;
            }

            @Override
            public IFolderItemEditorDecorator<EmergencyContact> createFolderItemDecorator() {
                return new BoxFolderItemEditorDecorator<EmergencyContact>(PortalImages.INSTANCE.delRow(), PortalImages.INSTANCE.delRowHover(),
                        i18n.tr("Remove contact"), !isFirst());
            }
        };
    }
}
