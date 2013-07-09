/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.personalinfo;

import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.folders.EmergencyContactFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.portal.domain.dto.ResidentDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class PersonalInfoForm extends CEntityDecoratableForm<ResidentDTO> {

    private static final I18n i18n = I18n.get(PersonalInfoForm.class);

    private PersonalInfoView.Presenter presenter;

    public PersonalInfoForm() {
        super(ResidentDTO.class, new VistaEditorsComponentFactory());
    }

    public void setPresenter(PersonalInfoView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel container = new FormFlexPanel();
        int row = -1;

        container.setH1(++row, 0, 1, i18n.tr("Contact Details"));
        container.setWidget(++row, 0, inject(proto().name(), new NameEditor(i18n.tr("Resident"))));
        container.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().sex()), 7).build());
        container.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().birthDate()), 9).build());

        container.setBR(++row, 0, 1);

        container.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().homePhone()), 15).build());
        container.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().mobilePhone()), 15).build());
        container.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().workPhone()), 15).build());
        container.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().email()), 20).build());

        container.setH1(++row, 0, 1, proto().emergencyContacts().getMeta().getCaption());
        container.setWidget(++row, 0, inject(proto().emergencyContacts(), new EmergencyContactFolder(isEditable(), true)));
        container.getCellFormatter().getElement(row, 0).getStyle().setPadding(10, Unit.PX);

        if (isViewable()) {
            container.setH1(++row, 0, 1, i18n.tr("Miscellaneous"));

            container.setBR(++row, 0, 1);

            Anchor resetPassword = new Anchor(i18n.tr("Reset Password"), new Command() {
                @Override
                public void execute() {
                    presenter.resetPassword();
                }
            });
            resetPassword.asWidget().getElement().getStyle().setMarginLeft(1, Unit.EM);
            resetPassword.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);
            resetPassword.asWidget().getElement().getStyle().setColor("#F3931F");

            container.setWidget(++row, 0, resetPassword);
        }

        if (VistaFeatures.instance().yardiIntegration()) {
            get(proto().name()).setViewable(true);
        }

        return container;
    }

    @Override
    public void addValidations() {
        new PastDateValidation(get(proto().birthDate()));

        get(proto().emergencyContacts()).addValueValidator(new EditableValueValidator<List<EmergencyContact>>() {
            @Override
            public ValidationError isValid(CComponent<List<EmergencyContact>> component, List<EmergencyContact> value) {
                if (value == null || getValue() == null) {
                    return null;
                }

                if (!VistaFeatures.instance().yardiIntegration()) {
                    if (value.isEmpty()) {
                        return new ValidationError(component, i18n.tr("Empty Emergency Contacts list"));
                    }
                }

                return !EntityGraph.hasBusinessDuplicates(getValue().emergencyContacts()) ? null : new ValidationError(component, i18n
                        .tr("Duplicate Emergency Contacts specified"));
            }
        });
    }
}
