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

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Layout;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.portal.domain.dto.ResidentDTO;
import com.propertyvista.portal.web.client.themes.EntityViewTheme;
import com.propertyvista.portal.web.client.ui.EntityViewImpl;
import com.propertyvista.portal.web.client.ui.profile.ProfileView.ProfilePresenter;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;
import com.propertyvista.shared.config.VistaFeatures;

public class ProfileForm extends CEntityForm<ResidentDTO> {

    private static final I18n i18n = I18n.get(ProfileForm.class);

    private ProfilePresenter presenter;

    boolean expanded = true;

    public ProfileForm() {
        super(ResidentDTO.class, new VistaEditorsComponentFactory());
        asWidget().setStyleName(EntityViewTheme.StyleName.EntityView.name());

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout();
            }

        });
    }

    public void setPresenter(ProfilePresenter presenter) {
        this.presenter = presenter;
    }

    private void doLayout() {
        switch (LayoutType.getLayoutType(Window.getClientWidth())) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            if (expanded) {
                EntityViewImpl.updateDecoratorsLayout(this, Layout.vertical);
                expanded = false;
            }
            break;
        case tabletLandscape:
        case monitor:
        case huge:
            if (!expanded) {
                EntityViewImpl.updateDecoratorsLayout(this, Layout.horisontal);
                expanded = true;
            }
            break;
        }

    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel mainPanel = new FormFlexPanel();
        int row = -1;

        mainPanel.setH1(++row, 0, 1, i18n.tr("Basic Information"));
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name(), new CEntityLabel<Name>()), "250px").customLabel("").build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().sex()), "50px").build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().birthDate()), "150px").build());

        mainPanel.setH1(++row, 0, 1, i18n.tr("Contact Information"));
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().homePhone()), "250px").build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().mobilePhone()), "250px").build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().workPhone()), "250px").build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().email()), "250px").build());

        mainPanel.setH1(++row, 0, 1, proto().emergencyContacts().getMeta().getCaption());
        mainPanel.setWidget(++row, 0, inject(proto().emergencyContacts(), new EmergencyContactFolder()));

        SimplePanel contentPanel = new SimplePanel(mainPanel);
        contentPanel.setStyleName(EntityViewTheme.StyleName.EntityViewContent.name());

        SimplePanel containerPanel = new SimplePanel(contentPanel);
        containerPanel.setStyleName(EntityViewTheme.StyleName.EntityViewContainer.name());

        doLayout();

        return containerPanel;
    }

    @Override
    public void addValidations() {
        get(proto().emergencyContacts()).addValueValidator(new EditableValueValidator<List<EmergencyContact>>() {
            @Override
            public ValidationError isValid(CComponent<List<EmergencyContact>, ?> component, List<EmergencyContact> value) {
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

    class NameEditor extends CEntityForm<Name> {

        private final CComponent<Name, ?> viewComp;

        private final String customViewLabel;

        public NameEditor() {
            this(null);
        }

        public NameEditor(String customViewLabel) {
            this(customViewLabel, null);
        }

        public NameEditor(String customViewLabel, Class<? extends IEntity> linkType) {
            super(Name.class);
            this.customViewLabel = customViewLabel;

            viewComp = new CEntityLabel<Name>();
            viewComp.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            if (isEditable()) {
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().namePrefix()), "50px").build());
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().firstName()), "150px").build());
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().middleName()), "50px").build());
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().lastName()), "250px").build());
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().maidenName()), "250px").build());
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().nameSuffix()), "50px").build());
            } else {
                main.setWidget(++row, 0, new FormDecoratorBuilder(viewComp, "250px").customLabel(customViewLabel).build());
                viewComp.setViewable(true);
            }

            return main;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            if (!isEditable()) {
                viewComp.setValue(getValue());
            }
        }
    }
}
