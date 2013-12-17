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
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import java.util.Date;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.editors.PriorAddressEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateValidator;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.misc.BusinessRules;

public class InfoViewForm extends CEntityForm<TenantInfoDTO> {

    private static final I18n i18n = I18n.get(InfoViewForm.class);

    private final TwoColumnFlexFormPanel previousAddress;

    private IdUploaderFolder fileUpload;

    public InfoViewForm() {
        super(TenantInfoDTO.class, new VistaEditorsComponentFactory());

        previousAddress = new TwoColumnFlexFormPanel() {
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
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setWidget(++row, 0, 2, inject(proto().person().name(), new NameEditor(i18n.tr("Person"))));

        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().sex()), 7).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().birthDate()), 10).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().email()), 25).build());

        row -= 3;
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().person().homePhone()), 15).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().person().mobilePhone()), 15).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().person().workPhone()), 15).build());

        main.setH1(++row, 0, 2, i18n.tr("Identification Documents"));
        main.setWidget(++row, 0, 2, inject(proto().version().documents(), fileUpload = new IdUploaderFolder()));

        main.setH1(++row, 0, 2, proto().version().currentAddress().getMeta().getCaption());
        main.setWidget(++row, 0, 2, inject(proto().version().currentAddress(), new PriorAddressEditor()));

        previousAddress.setH1(0, 0, 2, proto().version().previousAddress().getMeta().getCaption());
        previousAddress.setWidget(1, 0, 2, inject(proto().version().previousAddress(), new PriorAddressEditor()));
        main.setWidget(++row, 0, 2, previousAddress);

        TwoColumnFlexFormPanel questionary = new TwoColumnFlexFormPanel();
        questionary.setH1(++row, 0, 2, proto().version().legalQuestions().getMeta().getCaption());
        questionary.setWidget(++row, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().suedForRent())));
        questionary.setHR(++row, 0, 2);
        questionary.setWidget(++row, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().suedForDamages())));
        questionary.setHR(++row, 0, 2);
        questionary.setWidget(++row, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().everEvicted())));
        questionary.setHR(++row, 0, 2);
        questionary.setWidget(++row, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().defaultedOnLease())));
        questionary.setHR(++row, 0, 2);
        questionary.setWidget(++row, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().convictedOfFelony())));
        questionary.setHR(++row, 0, 2);
        questionary.setWidget(++row, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().legalTroubles())));
        questionary.setHR(++row, 0, 2);
        questionary.setWidget(++row, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().filedBankruptcy())));
        main.setWidget(++row, 0, 2, questionary);

        if (!SecurityController.checkBehavior(PortalResidentBehavior.Guarantor)) {
            main.setH1(++row, 0, 2, proto().emergencyContacts().getMeta().getCaption());
            main.setWidget(++row, 0, 2, inject(proto().emergencyContacts(), new EmergencyContactFolder(isEditable())));
        }

        return main;
    }

    @Override
    public void addValidations() {
        CEntityForm<PriorAddress> currentAddressForm = ((CEntityForm<PriorAddress>) get(proto().version().currentAddress()));
        CEntityForm<PriorAddress> previousAddressForm = ((CEntityForm<PriorAddress>) get(proto().version().previousAddress()));

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

        p1.addValueValidator(new PastDateValidator());
        c1.addValueValidator(new PastDateIncludeTodayValidator());
        c2.addValueValidator(new FutureDateIncludeTodayValidator());

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

        if (!SecurityController.checkBehavior(PortalResidentBehavior.Guarantor)) {
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
                if (value == null || getValue() == null || getValue().isEmpty() || value2.getValue() == null) {
                    return null;
                }

                Date date = value2.getValue();
                long limit1 = date.getTime() + 2678400000L; //limits date1 to be within a month of date2
                long limit2 = date.getTime() - 2678400000L;
                return (date == null || (value.getTime() > limit2 && value.getTime() < limit1)) ? null : new ValidationError(component, message);
            }
        });
    }

    private WidgetDecorator decorateLegalQuestion(CComponent<?> comp) {
        return new FormDecoratorBuilder(comp, 60, 10, 20).labelAlignment(Alignment.left).useLabelSemicolon(false).build();
    }
}
