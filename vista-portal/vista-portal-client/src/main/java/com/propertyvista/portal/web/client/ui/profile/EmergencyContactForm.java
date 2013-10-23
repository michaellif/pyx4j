/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.profile;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.portal.web.client.ui.AbstractPortalPanel;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class EmergencyContactForm extends CEntityForm<EmergencyContact> {

    private static final I18n i18n = I18n.get(EmergencyContactForm.class);

    private final ProfilePageViewImpl view;

    public EmergencyContactForm(ProfilePageViewImpl view) {
        super(EmergencyContact.class);
        this.view = view;
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

        int row = -1;
        mainPanel
                .setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name(), new CEntityLabel<Name>()), 200).customLabel(i18n.tr("Full Name")).build());

        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name().firstName()), 200).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name().lastName()), 200).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name().namePrefix()), 70).build());

        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().email()), 230).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().homePhone()), 200).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().mobilePhone()), 200).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().workPhone()), 200).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().address().street1()), 200).customLabel("Address").build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().address().city()), 200).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().address().province()), 200).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().address().country()), 200).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().address().postalCode()), 200).build());

        calculateFieldsStatus();

        addPropertyChangeHandler(new PropertyChangeHandler() {

            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.viewable)) {
                    calculateFieldsStatus();
                }
            }
        });

        AbstractPortalPanel.updateDecoratorsLayout(mainPanel, view.getWidgetLayout());

        return mainPanel;
    }

    private void calculateFieldsStatus() {
        if (isViewable()) {
            get(proto().name()).setVisible(true);
            get(proto().name().firstName()).setVisible(false);
            get(proto().name().lastName()).setVisible(false);
            get(proto().name().namePrefix()).setVisible(false);
        } else {
            get(proto().name()).setVisible(false);
            get(proto().name().firstName()).setVisible(true);
            get(proto().name().lastName()).setVisible(true);
            get(proto().name().namePrefix()).setVisible(true);
        }

    }

}