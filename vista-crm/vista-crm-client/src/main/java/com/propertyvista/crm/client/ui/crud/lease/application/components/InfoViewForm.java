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
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.editors.PriorAddressEditor;
import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateValidator;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.misc.BusinessRules;

public class InfoViewForm extends CForm<TenantInfoDTO> {

    private static final I18n i18n = I18n.get(InfoViewForm.class);

    private final FormPanel previousAddress;

    private IdUploaderFolder fileUpload;

    public InfoViewForm() {
        super(TenantInfoDTO.class, new VistaEditorsComponentFactory());

        previousAddress = new FormPanel(this) {
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
    protected IsWidget createContent() {
        InfoViewFormPanel formPanel = new InfoViewFormPanel(this);

        formPanel.append(Location.Dual, proto().person().name(), new NameEditor(i18n.tr("Person")));

        formPanel.append(Location.Left, proto().person().sex()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().person().birthDate()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().person().email()).decorate().componentWidth(300);

        formPanel.append(Location.Right, proto().person().homePhone()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().person().mobilePhone()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().person().workPhone()).decorate().componentWidth(180);

        formPanel.h1(i18n.tr("Identification Documents"));
        formPanel.append(Location.Dual, proto().version().documents(), fileUpload = new IdUploaderFolder());

        formPanel.h1(proto().version().currentAddress().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().version().currentAddress(), new PriorAddressEditor());

        previousAddress.h1(proto().version().previousAddress().getMeta().getCaption());
        previousAddress.append(Location.Dual, proto().version().previousAddress(), new PriorAddressEditor());
        formPanel.append(Location.Dual, previousAddress);

        formPanel.h1(proto().version().legalQuestions().getMeta().getCaption());
        formPanel.appendLegalQuestion(proto().version().legalQuestions().suedForRent());
        formPanel.appendLegalQuestion(proto().version().legalQuestions().suedForDamages());
        formPanel.appendLegalQuestion(proto().version().legalQuestions().everEvicted());
        formPanel.appendLegalQuestion(proto().version().legalQuestions().defaultedOnLease());
        formPanel.appendLegalQuestion(proto().version().legalQuestions().convictedOfFelony());
        formPanel.appendLegalQuestion(proto().version().legalQuestions().legalTroubles());
        formPanel.appendLegalQuestion(proto().version().legalQuestions().filedBankruptcy());

        if (!SecurityController.check(PortalResidentBehavior.Guarantor)) {
            formPanel.h1(proto().emergencyContacts().getMeta().getCaption());
            formPanel.append(Location.Dual, proto().emergencyContacts(), new EmergencyContactFolder(isEditable()));
        }

        return formPanel;
    }

    @Override
    public void addValidations() {
        CForm<PriorAddress> currentAddressForm = ((CForm<PriorAddress>) get(proto().version().currentAddress()));
        CForm<PriorAddress> previousAddressForm = ((CForm<PriorAddress>) get(proto().version().previousAddress()));

        CComponent<?, LogicalDate, ?> c1 = currentAddressForm.get(currentAddressForm.proto().moveInDate());
        CComponent<?, LogicalDate, ?> c2 = currentAddressForm.get(currentAddressForm.proto().moveOutDate());
        CComponent<?, LogicalDate, ?> p1 = previousAddressForm.get(previousAddressForm.proto().moveInDate());
        CComponent<?, LogicalDate, ?> p2 = previousAddressForm.get(previousAddressForm.proto().moveOutDate());

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                enablePreviousAddress();
            }
        });

        p1.addComponentValidator(new PastDateValidator());
        c1.addComponentValidator(new PastDateIncludeTodayValidator());
        c2.addComponentValidator(new FutureDateIncludeTodayValidator());

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

        if (!SecurityController.check(PortalResidentBehavior.Guarantor)) {
            get(proto().emergencyContacts()).addComponentValidator(new AbstractComponentValidator<List<EmergencyContact>>() {
                @Override
                public BasicValidationError isValid() {
                    if (getComponent().getValue() == null || getValue() == null) {
                        return null;
                    }

                    if (getComponent().getValue().isEmpty()) {
                        return new BasicValidationError(getComponent(), i18n.tr("Empty Emergency Contacts list"));
                    }

                    return !EntityGraph.hasBusinessDuplicates(getValue().emergencyContacts()) ? null : new BasicValidationError(getComponent(), i18n
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
            fileUpload.setPolicyEntity(getValue());
        }

        enablePreviousAddress();
    }

    private void StartEndDateWithinMonth(final CComponent<?, LogicalDate, ?> value1, final CComponent<?, LogicalDate, ?> value2, final String message) {
        value1.addComponentValidator(new AbstractComponentValidator<LogicalDate>() {

            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() == null || getValue() == null || getValue().isEmpty() || value2.getValue() == null) {
                    return null;
                }

                Date date = value2.getValue();
                long limit1 = date.getTime() + 2678400000L; //limits date1 to be within a month of date2
                long limit2 = date.getTime() - 2678400000L;
                return (date == null || (getComponent().getValue().getTime() > limit2 && getComponent().getValue().getTime() < limit1)) ? null
                        : new BasicValidationError(getComponent(), message);
            }
        });
    }

    class InfoViewFormPanel extends FormPanel {

        public InfoViewFormPanel(CForm<?> parent) {
            super(parent);
        }

        void appendLegalQuestion(IObject<?> member) {
            append(Location.Dual, member).decorate().labelWidth(400).labelPosition(LabelPosition.top).useLabelSemicolon(false);
        }
    }
}
