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
package com.propertyvista.portal.web.client.ui.profile;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.portal.domain.dto.ResidentDTO;
import com.propertyvista.portal.web.client.themes.EntityViewTheme;
import com.propertyvista.portal.web.client.ui.CPortalEntityViewer;
import com.propertyvista.portal.web.client.ui.profile.ProfileView.ProfilePresenter;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;
import com.propertyvista.shared.config.VistaFeatures;

public class ProfileForm extends CPortalEntityViewer<ResidentDTO> {

    private static final I18n i18n = I18n.get(ProfileForm.class);

    private ProfilePresenter presenter;

    private final ProfileViewImpl view;

    public ProfileForm(ProfileViewImpl view) {
        super(ResidentDTO.class, new VistaEditorsComponentFactory(), null, "Tenant Profile", ThemeColor.contrast2);
        this.view = view;
        asWidget().setStyleName(EntityViewTheme.StyleName.EntityView.name());
    }

    public void setPresenter(ProfilePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        mainPanel.setH1(++row, 0, 1, i18n.tr("Basic Information"));
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name(), new CEntityLabel<Name>()), "200px").customLabel(i18n.tr("Full Name"))
                .build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().sex()), "100px").build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().birthDate()), "150px").build());

        mainPanel.setH1(++row, 0, 1, i18n.tr("Contact Information"));
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().homePhone()), "200px").build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().mobilePhone()), "200px").build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().workPhone()), "200px").build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().email()), "230px").build());

        mainPanel.setH1(++row, 0, 1, proto().emergencyContacts().getMeta().getCaption());
        mainPanel.setWidget(++row, 0, inject(proto().emergencyContacts(), new EmergencyContactFolder(view)));

        return mainPanel;
    }

    @Override
    public void addValidations() {
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
