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

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.ComplexDTO;

public class ComplexEditorForm extends CrmEntityForm<ComplexDTO> {

    private static final I18n i18n = I18n.get(ComplexEditorForm.class);

    private final VistaTabLayoutPanel tabPanel;

    public ComplexEditorForm() {
        this(false);
    }

    public ComplexEditorForm(boolean viewMode) {
        super(ComplexDTO.class, viewMode);
        tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);
    }

    @Override
    public IsWidget createContent() {
        tabPanel.add(isEditable() ? new HTML() : getParentComplexViewerView().getDashboardView(), i18n.tr("Dashboard"));
        tabPanel.setLastTabDisabled(isEditable());
        tabPanel.add(createGeneralPanel(), i18n.tr("General"));
        tabPanel.add(createBuildingsPanel(), i18n.tr("Buildings"));

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    private Widget createGeneralPanel() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = 0;

        panel.setWidget(row++, 0, (new DecoratorBuilder(inject(proto().name()))).build());
        if (isEditable()) {
            panel.setWidget(row++, 0, (new DecoratorBuilder(inject(proto().website()))).build());
            get(proto().website()).addValueValidator(new EditableValueValidator<String>() {

                @Override
                public ValidationFailure isValid(CComponent<String, ?> component, String url) {
                    if (url != null) {
                        if (ValidationUtils.isSimpleUrl(url)) {
                            return null;
                        } else {
                            return new ValidationFailure(i18n.tr("Please use proper URL format, e.g. www.propertyvista.com"));
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

        return new CrmScrollPanel(panel);
    }

    private Widget createBuildingsPanel() {
        FormFlexPanel panel = new FormFlexPanel();

        panel.setWidget(0, 0, inject(proto().buildings(), new ComplexBuildingFolder(isEditable())));

        return new CrmScrollPanel(panel);
    }

    private ComplexViewerView getParentComplexViewerView() {
        return (ComplexViewerView) getParentView();
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().buildings()).addValueValidator(new EditableValueValidator<List<Building>>() {
            @Override
            public ValidationFailure isValid(CComponent<List<Building>, ?> component, List<Building> value) {
                if (value != null) {
                    boolean primaryFound = false;
                    for (Building item : value) {
                        if (item.complexPrimary().isBooleanTrue()) {
                            primaryFound = true;
                            break;
                        }
                    }
                    if (!primaryFound) {
                        return new ValidationFailure(i18n.tr("Primary building should be selected"));
                    }
                }
                return null;
            }
        });
    }
}
