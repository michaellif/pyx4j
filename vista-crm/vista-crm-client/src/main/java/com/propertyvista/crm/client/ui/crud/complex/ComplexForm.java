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
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.complex;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.ComplexDTO;

public class ComplexForm extends CrmEntityForm<ComplexDTO> {

    private static final I18n i18n = I18n.get(ComplexForm.class);

    public ComplexForm(IForm<ComplexDTO> view) {
        super(ComplexDTO.class, view);

        Tab tab = addTab(createGeneralPanel(i18n.tr("General")));
        selectTab(tab);

        addTab(createBuildingsPanel(i18n.tr("Buildings")));

    }

    private TwoColumnFlexFormPanel createGeneralPanel(String title) {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(title);

        panel.setWidget(0, 0, (new FormDecoratorBuilder(inject(proto().name()))).build());
        panel.setWidget(0, 1, (new FormDecoratorBuilder(inject(proto().website()))).build());
        get(proto().website()).addValueValidator(new EditableValueValidator<String>() {

            @Override
            public ValidationError isValid(CComponent<String> component, String url) {
                if (url != null) {
                    if (ValidationUtils.isSimpleUrl(url)) {
                        return null;
                    } else {
                        return new ValidationError(component, i18n.tr("Please use proper URL format, e.g. www.propertyvista.com"));
                    }
                }
                return null;
            }
        });
        ((CField) get(proto().website())).setNavigationCommand(new Command() {
            @Override
            public void execute() {
                String url = getValue().website().getValue();
                if (!ValidationUtils.urlHasProtocol(url)) {
                    url = "http://" + url;
                }
                if (!ValidationUtils.isCorrectUrl(url)) {
                    throw new Error(i18n.tr("The URL is not in proper format"));
                }

                Window.open(url, proto().website().getMeta().getCaption(), "status=1,toolbar=1,location=1,resizable=1,scrollbars=1");
            }
        });

        return panel;
    }

    private TwoColumnFlexFormPanel createBuildingsPanel(String title) {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(title);

        panel.setWidget(0, 0, 2, inject(proto().buildings(), new ComplexBuildingFolder(isEditable())));

        return panel;
    }
}
