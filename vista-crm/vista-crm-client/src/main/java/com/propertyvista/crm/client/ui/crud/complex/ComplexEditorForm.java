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

import java.io.Serializable;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.ComplexDTO;

public class ComplexEditorForm extends CrmEntityForm<ComplexDTO> {

    private static final I18n i18n = I18n.get(ComplexEditorForm.class);

    private final VistaTabLayoutPanel tabPanel;

    public ComplexEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public ComplexEditorForm(IEditableComponentFactory factory) {
        super(ComplexDTO.class, factory);
        tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);
    }

    @Override
    public IsWidget createContent() {
        tabPanel.addDisable(isEditable() ? new HTML() : getParentComplexViewerView().getDashboardView(), i18n.tr("Dashboard"));
        tabPanel.add(createGeneralPanel(), i18n.tr("General"));
        tabPanel.addDisable(isEditable() ? new HTML() : getParentComplexViewerView().getBuildingListerView(), i18n.tr("Buildings"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPopulate() {
        super.onPopulate();
        CComponent<Building, ?> primaryBuildingWidget = get(proto().primaryBuilding());
        if (isEditable() && primaryBuildingWidget instanceof CEntityComboBox<?>) {
            CEntityComboBox<Building> primaryBuildingCombo = (CEntityComboBox<Building>) primaryBuildingWidget;
            primaryBuildingCombo.resetCriteria();
            if ((getValue() != null) && (getValue().getPrimaryKey() != null)) {
                primaryBuildingCombo.addCriterion(PropertyCriterion.eq(primaryBuildingCombo.proto().complex(), getValue()));
                primaryBuildingCombo.setEnabled(true);
                primaryBuildingCombo.setVisible(true);
            } else {
                primaryBuildingCombo.setEnabled(false);
                primaryBuildingCombo.setVisible(false);
            }
        }
    }

    private Widget createGeneralPanel() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = 0;

        panel.setWidget(row++, 0, (new DecoratorBuilder(inject(proto().name()))).build());
        panel.setWidget(row++, 0, (new DecoratorBuilder(inject(proto().website()))).build());
        panel.setWidget(row++, 0, new DecoratorBuilder(inject(proto().primaryBuilding())).build());

        CComponent<Building, ?> primaryBuildingWidget = get(proto().primaryBuilding());

        // restrict primary building selector to this complex
        if (isEditable() && primaryBuildingWidget instanceof CEntityComboBox<?>) {
            @SuppressWarnings("unchecked")
            CEntityComboBox<Building> primaryBuildingCombo = (CEntityComboBox<Building>) primaryBuildingWidget;
            primaryBuildingCombo.addCriterion(PropertyCriterion.eq(primaryBuildingCombo.proto().complex(), (Serializable) null));
        }

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
}
