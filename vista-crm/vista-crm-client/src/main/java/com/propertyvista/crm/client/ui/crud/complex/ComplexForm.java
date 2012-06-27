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
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.ComplexDTO;

public class ComplexForm extends CrmEntityForm<ComplexDTO> {

    private static final I18n i18n = I18n.get(ComplexForm.class);

    public ComplexForm() {
        this(false);
    }

    public ComplexForm(boolean viewMode) {
        super(ComplexDTO.class, viewMode);
    }

    @Override
    public void createTabs() {
        Tab tab = addTab(isEditable() ? new HTML() : getParentComplexViewerView().getDashboardView().asWidget(), i18n.tr("Dashboard"));
        setTabEnabled(tab, !isEditable());
        selectTab(tab);

        addTab(createGeneralPanel(i18n.tr("General")));
        addTab(createBuildingsPanel(i18n.tr("Buildings")));

    }

    private FormFlexPanel createGeneralPanel(String title) {
        FormFlexPanel panel = new FormFlexPanel(title);
        int row = 0;

        panel.setWidget(row++, 0, (new DecoratorBuilder(inject(proto().name()))).build());
        if (isEditable()) {
            panel.setWidget(row++, 0, (new DecoratorBuilder(inject(proto().website()))).build());
            get(proto().website()).addValueValidator(new EditableValueValidator<String>() {

                @Override
                public ValidationError isValid(CComponent<String, ?> component, String url) {
                    if (url != null) {
                        if (ValidationUtils.isSimpleUrl(url)) {
                            return null;
                        } else {
                            return new ValidationError(i18n.tr("Please use proper URL format, e.g. www.propertyvista.com"));
                        }
                    }
                    return null;
                }
            });
        } else {
            panel.setWidget(row++, 0, new DecoratorBuilder(inject(proto().website(), new CHyperlink(new Command() {
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
            })), 50).build());
        }

        return panel;
    }

    private FormFlexPanel createBuildingsPanel(String title) {
        FormFlexPanel panel = new FormFlexPanel(title);

        panel.setWidget(0, 0, inject(proto().buildings(), new ComplexBuildingFolder(isEditable())));

        return panel;
    }

    private ComplexViewerView getParentComplexViewerView() {
        return (ComplexViewerView) getParentView();
    }

}
