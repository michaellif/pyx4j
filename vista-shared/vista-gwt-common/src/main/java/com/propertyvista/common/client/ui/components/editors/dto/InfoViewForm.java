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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.editors.PriorAddressEditor;
import com.propertyvista.common.client.ui.components.folders.EmergencyContactFolder;
import com.propertyvista.common.client.ui.components.folders.IdUploaderFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.FutureDateValidation;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.misc.BusinessRules;

public class InfoViewForm extends CEntityDecoratableForm<TenantInfoDTO> {

    private static final I18n i18n = I18n.get(InfoViewForm.class);

    private final FormFlexPanel previousAddress;

    private IdUploaderFolder fileUpload;

    public InfoViewForm() {
        super(TenantInfoDTO.class, new VistaEditorsComponentFactory());

        previousAddress = new FormFlexPanel() {
            @Override
            public void setVisible(boolean visible) {
                get(proto().version().previousAddress()).setVisible(visible);
                super.setVisible(visible);
            }
        };
    }

    public InfoViewForm(boolean viewMode) {
        this();

        if (viewMode) {
            setEditable(false);
            setViewable(true);
        }
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Contact Details"));
        main.setWidget(++row, 0, inject(proto().person().name(), new NameEditor(i18n.tr("Person"))));
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().sex()), 7).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().birthDate(), new CDateLabel()), 9).build());

        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().homePhone()), 15).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().mobilePhone()), 15).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().workPhone()), 15).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().email()), 25).build());

        main.setH1(++row, 0, 1, i18n.tr("Identification Documents"));

        main.setWidget(++row, 0, inject(proto().documents(), fileUpload = new IdUploaderFolder()));
        fileUpload.asWidget().getElement().getStyle().setMarginTop(1, Unit.EM);
        fileUpload.asWidget().getElement().getStyle().setMarginBottom(1, Unit.EM);
        fileUpload.asWidget().setWidth("40em");

        main.setH1(++row, 0, 1, proto().version().currentAddress().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().version().currentAddress(), new PriorAddressEditor()));

        previousAddress.setH1(0, 0, 1, proto().version().previousAddress().getMeta().getCaption());
        previousAddress.setWidget(1, 0, inject(proto().version().previousAddress(), new PriorAddressEditor()));
        main.setWidget(++row, 0, previousAddress);

        main.setH1(++row, 0, 1, proto().version().legalQuestions().getMeta().getCaption());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().legalQuestions().suedForRent()), 54, true).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().legalQuestions().suedForDamages()), 54, true).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().legalQuestions().everEvicted()), 54, true).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().version().legalQuestions().defaultedOnLease()), 54, true).labelAlignment(Alignment.left)
                        .useLabelSemicolon(false).build());
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().version().legalQuestions().convictedOfFelony()), 54, true).labelAlignment(Alignment.left)
                        .useLabelSemicolon(false).build());
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().legalQuestions().legalTroubles()), 54, true).labelAlignment(Alignment.left)
                .useLabelSemicolon(false).build());
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().legalQuestions().filedBankruptcy()), 54, true)
                .labelAlignment(Alignment.left).useLabelSemicolon(false).build());

        if (!SecurityController.checkBehavior(VistaCustomerBehavior.Guarantor)) {
            main.setH1(++row, 0, 1, proto().emergencyContacts().getMeta().getCaption());
            main.setWidget(++row, 0, inject(proto().emergencyContacts(), new EmergencyContactFolder(isEditable())));
        }

        addValidations();

        return main;
    }

    @Override
    public void addValidations() {
        @SuppressWarnings("unchecked")
        CEntityForm<PriorAddress> currentAddressForm = ((CEntityForm<PriorAddress>) get(proto().version().currentAddress()));
        @SuppressWarnings("unchecked")
        final CEntityForm<PriorAddress> previousAddressForm = ((CEntityForm<PriorAddress>) get(proto().version().previousAddress()));
        CComponent<LogicalDate> c1 = currentAddressForm.get(currentAddressForm.proto().moveInDate());
        CComponent<LogicalDate> c2 = currentAddressForm.get(currentAddressForm.proto().moveOutDate());
        CComponent<LogicalDate> p1 = previousAddressForm.get(previousAddressForm.proto().moveInDate());
        CComponent<LogicalDate> p2 = previousAddressForm.get(previousAddressForm.proto().moveOutDate());

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

        if (!SecurityController.checkBehavior(VistaCustomerBehavior.Guarantor)) {
            get(proto().emergencyContacts()).addValueValidator(new EditableValueValidator<List<EmergencyContact>>() {
                @Override
                public ValidationError isValid(CComponent<List<EmergencyContact>> component, List<EmergencyContact> value) {
                    if (value == null || getValue() == null) {
                        return null;
                    }

                    if (value.isEmpty()) {
                        return new ValidationError(component, i18n.tr("Empty Emergency Contacts list"));
                    }

                    return !EntityGraph.hasBusinessDuplicates(getValue().emergencyContacts()) ? null : new ValidationError(component, i18n
                            .tr("Duplicate Emergency Contacts specified"));
                }
            });
        }
    }

    private void enablePreviousAddress() {
        previousAddress.setVisible(BusinessRules.infoPageNeedPreviousAddress(getValue().version().currentAddress().moveInDate().getValue()));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isEditable()) {
            fileUpload.setParentEntity(getValue());
        }

        enablePreviousAddress();
    }

    private void StartEndDateWithinMonth(final CComponent<LogicalDate> value1, final CComponent<LogicalDate> value2, final String message) {
        value1.addValueValidator(new EditableValueValidator<LogicalDate>() {

            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (getValue() == null || getValue().isEmpty() || value2.getValue() == null) {
                    return null;
                }

                Date date = value2.getValue();
                long limit1 = date.getTime() + 2678400000L; //limits date1 to be within a month of date2
                long limit2 = date.getTime() - 2678400000L;
                return (date == null || (value.getTime() > limit2 && value.getTime() < limit1)) ? null : new ValidationError(component, message);
            }
        });
    }

}
