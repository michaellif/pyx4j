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
package com.propertyvista.common.client.ui.components.editors.dto;

import java.util.Date;
import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.ApplicationDocumentsFolderUploader;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEmailLabel;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.editors.PriorAddressEditor;
import com.propertyvista.common.client.ui.components.folders.EmergencyContactFolder;
import com.propertyvista.common.client.ui.validators.CanadianSinValidator;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.media.ApplicationDocument.DocumentType;
import com.propertyvista.domain.person.Name;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.misc.BusinessRules;

public class InfoViewForm extends CEntityDecoratableEditor<TenantInfoDTO> {

    private static I18n i18n = I18n.get(InfoViewForm.class);

    private final FormFlexPanel previousAddress = new FormFlexPanel() {
        @Override
        public void setVisible(boolean visible) {
            get(proto().previousAddress()).setVisible(visible);
            super.setVisible(visible);
        }
    };

    private ApplicationDocumentsFolderUploader fileUpload;

    public InfoViewForm() {
        this(new VistaEditorsComponentFactory());
    }

    public InfoViewForm(IEditableComponentFactory factory) {
        super(TenantInfoDTO.class, factory);
        setEditable(factory instanceof VistaEditorsComponentFactory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Contact Details"));
        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().namePrefix()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().firstName()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().middleName()), 10).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name().lastName()), 20).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().name(), new CEntityLabel<Name>()), 25).customLabel(i18n.tr("Person")).build());
            get(proto().person().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
        }
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().sex()), 7).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().birthDate(), new CDateLabel()), 9).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().homePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().mobilePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().workPhone()), 15).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().email(), new CEmailLabel()), 25).build());

        main.setH1(++row, 0, 1, i18n.tr("Secure Information"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().driversLicense()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().driversLicenseState()), 17).build());

        final CComponent<?, ?> sin = inject(proto().secureIdentifier());
        main.setWidget(++row, 0, new DecoratorBuilder(sin, 7).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().notCanadianCitizen()), 3).build());

        main.setWidget(
                ++row,
                0,
                new DecoratorBuilder(inject(proto().documents(), fileUpload = new ApplicationDocumentsFolderUploader(DocumentType.securityInfo))).customLabel(
                        i18n.tr("Please Attach proof Of Citizenship")).build());
        fileUpload.asWidget().getElement().getStyle().setMarginTop(1, Unit.EM);
        fileUpload.asWidget().getElement().getStyle().setMarginBottom(1, Unit.EM);
        fileUpload.asWidget().setWidth("40em");
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

        main.setH1(++row, 0, 1, proto().currentAddress().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().currentAddress(), new PriorAddressEditor()));

        previousAddress.setH1(0, 0, 1, proto().previousAddress().getMeta().getCaption());
        previousAddress.setWidget(1, 0, inject(proto().previousAddress(), new PriorAddressEditor()));
        main.setWidget(++row, 0, previousAddress);

        main.setH1(++row, 0, 1, proto().legalQuestions().getMeta().getCaption());
        main.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().legalQuestions().suedForRent()), 10, 54).labelAlignment(Alignment.left).useLabelSemicolon(false).build());
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().legalQuestions().suedForDamages()), 10, 54).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().legalQuestions().everEvicted()), 10, 54).labelAlignment(Alignment.left).useLabelSemicolon(false).build());
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().legalQuestions().defaultedOnLease()), 10, 54).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().legalQuestions().convictedOfFelony()), 10, 54).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().legalQuestions().legalTroubles()), 10, 54).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().legalQuestions().filedBankruptcy()), 10, 54).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());

        main.setH1(++row, 0, 1, proto().emergencyContacts().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().emergencyContacts(), new EmergencyContactFolder(isEditable(), true)));

        addValidations();

        return main;
    }

    @Override
    public void addValidations() {
        @SuppressWarnings("unchecked")
        CEntityEditor<PriorAddress> currentAddressForm = ((CEntityEditor<PriorAddress>) get(proto().currentAddress()));
        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {

            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                enablePreviousAddress();
            }
        });

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                return (value != null) && value.before(TimeUtils.today()) ? null : new ValidationFailure(i18n
                        .tr("The Date Chosen Cannot Be Today's Date Or A Date In The Future"));
            }

        });

        currentAddressForm.get(currentAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                return (value != null) && value.after(TimeUtils.today()) ? null : new ValidationFailure(i18n
                        .tr("The Date Chosen Cannot Be Today's Date Or A Date In The Past"));
            }

        });

        // ------------------------------------------------------------------------------------------------

        @SuppressWarnings("unchecked")
        final CEntityEditor<PriorAddress> previousAddressForm = ((CEntityEditor<PriorAddress>) get(proto().previousAddress()));
        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                return (value != null) && value.before(TimeUtils.today()) ? null : new ValidationFailure(i18n
                        .tr("The Date Chosen Cannot Be Today's Date Or A Date In The Future"));
            }

        });

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                if (getValue() == null || getValue().isEmpty()) {
                    return null;
                }

                IPrimitive<LogicalDate> date = getValue().previousAddress().moveOutDate();
                return (date.isNull() || value.before(date.getValue())) ? null : new ValidationFailure(i18n
                        .tr("Move Out Date should be greater than Move In Date"));
            }

        });

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveOutDate())));

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(currentAddressForm.get(currentAddressForm.proto().moveInDate())));

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveInDate())));

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(currentAddressForm.get(currentAddressForm.proto().moveInDate())));

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveOutDate())));

        // ------------------------------------------------------------------------------------------------

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                return (value != null) && value.before(TimeUtils.today()) ? null : new ValidationFailure(i18n
                        .tr("The Date Chosen Cannot Be Today's Date Or A Date In The Future"));
            }

        });

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                if (getValue() == null || getValue().isEmpty()) {
                    return null;
                }

                IPrimitive<LogicalDate> date = getValue().previousAddress().moveInDate();
                return (date.isNull() || value.after(date.getValue())) ? null : new ValidationFailure(i18n
                        .tr("Move Out Date should be greater than Move In Date"));
            }

        });

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                if (getValue() == null || getValue().isEmpty()) {
                    return null;
                }

                IPrimitive<LogicalDate> date = getValue().previousAddress().moveInDate();
                return (date.isNull() || value.after(date.getValue()) || TimeUtils.isOlderThan(value, 3)) ? null : new ValidationFailure(i18n
                        .tr("Current Move In date should be greater than Previous Move In date"));
            }

        });

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                if (getValue() == null || getValue().isEmpty()) {
                    return null;
                }

                IPrimitive<LogicalDate> date = getValue().currentAddress().moveInDate();
                return (date.isNull() || value.before(date.getValue())) ? null : new ValidationFailure(i18n
                        .tr("Current Move In date should be greater than Previous Move In date"));
            }

        });

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                if (getValue() == null || getValue().isEmpty()) {
                    return null;
                }

                IPrimitive<LogicalDate> date = getValue().currentAddress().moveInDate();
                return (date.isNull() || !value.before(date.getValue())) ? null : new ValidationFailure(i18n
                        .tr("Current Move In date should not be greater than Previous Move Out date"));
            }

        });

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                if (getValue() == null || getValue().isEmpty()) {
                    return null;
                }

                IPrimitive<LogicalDate> date = getValue().previousAddress().moveOutDate();
                return (date.isNull() || value.equals(date.getValue()) || value.before(date.getValue())) ? null : new ValidationFailure(i18n
                        .tr("Current Move In date should not be greater than Previous Move Out date"));
            }

        });

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveInDate())));

        // ------------------------------------------------------------------------------------------------

        get(proto().secureIdentifier()).addValueValidator(new CanadianSinValidator());

        get(proto().emergencyContacts()).addValueValidator(new EditableValueValidator<List<EmergencyContact>>() {

            @Override
            public ValidationFailure isValid(CComponent<List<EmergencyContact>, ?> component, List<EmergencyContact> value) {
                if (value == null || getValue() == null) {
                    return null;
                }

                return !EntityGraph.hasBusinessDuplicates(getValue().emergencyContacts()) ? null : new ValidationFailure(i18n
                        .tr("Duplicate contacts specified"));
            }
        });
    }

    private void enablePreviousAddress() {
        previousAddress.setVisible(BusinessRules.infoPageNeedPreviousAddress(getValue().currentAddress().moveInDate().getValue()));
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        enablePreviousAddress();

        get(proto().secureIdentifier()).setEnabled(!getValue().notCanadianCitizen().isBooleanTrue());
        fileUpload.setVisible(getValue().notCanadianCitizen().isBooleanTrue());
        if (getValue() != null) {
            fileUpload.setTenantID(((IEntity) getValue()).getPrimaryKey());
        }
    }
}
