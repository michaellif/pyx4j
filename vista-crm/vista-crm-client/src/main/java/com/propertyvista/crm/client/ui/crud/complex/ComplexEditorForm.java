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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.ComplexDTO;

public class ComplexEditorForm extends CrmEntityForm<ComplexDTO> {
    private static I18n i18n = I18n.get(ComplexEditorForm.class);

    private static final String TAB_CAPTION_DASHBOARD = "Dashboard";

    private static final String TAB_CAPTION_GENERAL = "General";

    private static final String TAB_CAPTION_BUILDINGS = "Buildings";

    private final VistaTabLayoutPanel tabPanel;

    private CComboBox<Building> primaryBuildingSelector;

    public ComplexEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public ComplexEditorForm(IEditableComponentFactory factory) {
        super(ComplexDTO.class, factory);
        tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);
    }

    @Override
    public IsWidget createContent() {
        tabPanel.addDisable(isEditable() ? new HTML() : getParentComplexViewerView().getDashboardView(), i18n.tr(TAB_CAPTION_DASHBOARD));
        tabPanel.add(createGeneralPanel(), i18n.tr(TAB_CAPTION_GENERAL));
        tabPanel.addDisable(isEditable() ? new HTML() : getParentComplexViewerView().getBuildingListerView(), i18n.tr(TAB_CAPTION_BUILDINGS));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void populate(ComplexDTO value) {
        CEditableComponent<Building, ?> primaryBuildingWidget = get(proto().primaryBuilding());
        if (isEditable() && primaryBuildingWidget instanceof CEntityComboBox<?>) {
            CEntityComboBox<Building> primaryBuildingCombo = (CEntityComboBox<Building>) primaryBuildingWidget;
            primaryBuildingCombo.resetCriteria();
            primaryBuildingCombo.addCriterion(PropertyCriterion.eq(primaryBuildingCombo.proto().complex(), value));
        }
        super.populate(value);
    }

    private Widget createGeneralPanel() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = 0;

        panel.setWidget(row++, 0, (new DecoratorBuilder(inject(proto().name()))).build());
        panel.setWidget(row++, 0, (new DecoratorBuilder(inject(proto().website()))).build());
        panel.setWidget(row++, 0, new DecoratorBuilder(inject(proto().primaryBuilding())).build());

        CEditableComponent<Building, ?> primaryBuildingWidget = get(proto().primaryBuilding());

        // restrict primary building selector to this complex
        if (isEditable() && primaryBuildingWidget instanceof CEntityComboBox<?>) {
            @SuppressWarnings("unchecked")
            CEntityComboBox<Building> primaryBuildingCombo = (CEntityComboBox<Building>) primaryBuildingWidget;
            primaryBuildingCombo.addCriterion(PropertyCriterion.eq(primaryBuildingCombo.proto().complex(), null));
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
