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
 */
package com.propertyvista.crm.client.ui.crud.complex;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.ComplexDTO;

public class ComplexForm extends CrmEntityForm<ComplexDTO> {

    private static final I18n i18n = I18n.get(ComplexForm.class);

    public ComplexForm(IPrimeFormView<ComplexDTO, ?> view) {
        super(ComplexDTO.class, view);

        selectTab(addTab(createGeneralPanel(), i18n.tr("General")));
        addTab(createBuildingsPanel(), i18n.tr("Buildings"));
    }

    private FormPanel createGeneralPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Right, proto().website()).decorate();

        get(proto().website()).addComponentValidator(new AbstractComponentValidator<String>() {

            @Override
            public BasicValidationError isValid() {
                if (getCComponent().getValue() != null) {
                    if (ValidationUtils.isSimpleUrl(getCComponent().getValue())) {
                        return null;
                    } else {
                        return new BasicValidationError(getCComponent(), i18n.tr("Please use proper URL format, e.g. www.propertyvista.com"));
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

        return formPanel;
    }

    private FormPanel createBuildingsPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().buildings(), new ComplexBuildingFolder(this));

        return formPanel;
    }
}
