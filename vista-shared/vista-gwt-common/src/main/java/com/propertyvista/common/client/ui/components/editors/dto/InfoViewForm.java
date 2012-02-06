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
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.ui.components.ApplicationDocumentUploaderFolder;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.editors.PriorAddressEditor;
import com.propertyvista.common.client.ui.components.folders.EmergencyContactFolder;
import com.propertyvista.common.client.ui.validators.CanadianSinValidator;
import com.propertyvista.common.client.ui.validators.FutureDateValidation;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.misc.BusinessRules;

public class InfoViewForm extends CEntityDecoratableEditor<TenantInfoDTO> {

    private static final I18n i18n = I18n.get(InfoViewForm.class);

    private final FormFlexPanel previousAddress;

    private ApplicationDocumentUploaderFolder fileUpload;

    private boolean ptAppMode = false;

    public InfoViewForm() {
        super(TenantInfoDTO.class, new VistaEditorsComponentFactory());

        previousAddress = new FormFlexPanel() {
            @Override
            public void setVisible(boolean visible) {
                get(proto().previousAddress()).setVisible(visible);
                super.setVisible(visible);
            }
        };
    }

    public InfoViewForm(boolean ptAppMode) {
        this();
        this.ptAppMode = ptAppMode;
    }

    public boolean isShowEditable() {
        return (super.isEditable() && !ptAppMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Contact Details"));
        if (isShowEditable()) {
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

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().email()/* , new CEmailLabel() */), 25).build());

        main.setH1(++row, 0, 1, i18n.tr("Secure Information"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().driversLicense()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().driversLicenseState()), 17).build());

        final CComponent<?, ?> sin = inject(proto().secureIdentifier());
        main.setWidget(++row, 0, new DecoratorBuilder(sin, 7).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().notCanadianCitizen()), 3).build());

        main.setWidget(
                ++row,
                0,
                new DecoratorBuilder(inject(proto().documents(), fileUpload = new ApplicationDocumentUploaderFolder())).customLabel(
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
                sin.setEnabled(!event.getValue());
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

        if (!SecurityController.checkBehavior(VistaTenantBehavior.Guarantor)) {
            main.setH1(++row, 0, 1, proto().emergencyContacts().getMeta().getCaption());
            main.setWidget(++row, 0, inject(proto().emergencyContacts(), new EmergencyContactFolder(isShowEditable(), true)));
        }

        addValidations();

        return main;
    }

    @Override
    public void addValidations() {
        @SuppressWarnings("unchecked")
        CEntityEditor<PriorAddress> currentAddressForm = ((CEntityEditor<PriorAddress>) get(proto().currentAddress()));
        @SuppressWarnings("unchecked")
        final CEntityEditor<PriorAddress> previousAddressForm = ((CEntityEditor<PriorAddress>) get(proto().previousAddress()));
        CComponent<LogicalDate, ?> c1 = currentAddressForm.get(currentAddressForm.proto().moveInDate());
        CComponent<LogicalDate, ?> c2 = currentAddressForm.get(currentAddressForm.proto().moveOutDate());
        CComponent<LogicalDate, ?> p1 = previousAddressForm.get(previousAddressForm.proto().moveInDate());
        CComponent<LogicalDate, ?> p2 = previousAddressForm.get(previousAddressForm.proto().moveOutDate());

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {

            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                enablePreviousAddress();
            }
        });

        new PastDateValidation(c1);
        new PastDateValidation(p1);
        new FutureDateValidation(c2);
        new StartEndDateValidation(c1, c2);
        new StartEndDateValidation(p1, p2);
        StartEndDateWithinMonth(c1, p2, i18n.tr("Current Move In Date Should Be Within 30 Days Of Previous Move Out Date"));
        StartEndDateWithinMonth(p2, c1, i18n.tr("Current Move In Date Should Be Within 30 Days Of Previous Move Out Date"));

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

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveInDate())));

        // ------------------------------------------------------------------------------------------------

        //TODO notify landlord if the previous move in date is still too close to current (person changes addresses too often).
        //Possibly should be dealt with on a case by case basis

        // ------------------------------------------------------------------------------------------------

        get(proto().secureIdentifier()).addValueValidator(new CanadianSinValidator());

        if (!SecurityController.checkBehavior(VistaTenantBehavior.Guarantor)) {
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

    private void StartEndDateWithinMonth(final CComponent<LogicalDate, ?> value1, final CComponent<LogicalDate, ?> value2, final String message) {
        value1.addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                if (getValue() == null || getValue().isEmpty()) {
                    return null;
                }

                Date date = value2.getValue();
                long limit1 = date.getTime() + 2678400000L; //limits date1 to be within a month of date2
                long limit2 = date.getTime() - 2678400000L;
                return (date == null || (value.getTime() > limit2 && value.getTime() < limit1)) ? null : new ValidationFailure(message);
            }

        });
    }
}
