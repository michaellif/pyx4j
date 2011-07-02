/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.dto.AptUnitDTO;

public class UnitEditorForm extends CrmEntityForm<AptUnitDTO> {

    private CEntityComboBox<Floorplan> floorplanCompbo;

    public UnitEditorForm(IView<AptUnitDTO> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public UnitEditorForm(IEditableComponentFactory factory, IView<AptUnitDTO> parentView) {
        super(AptUnitDTO.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {
        TabLayoutPanel tabPanel = new TabLayoutPanel(2.7, Unit.EM);

        tabPanel.add(new ScrollPanel(createGeneralTab()), i18n.tr("General"));
        tabPanel.add(((UnitView) getParentView()).getDetailsListerView().asWidget(), i18n.tr("Details"));
        tabPanel.add(new ScrollPanel(createUtilitiesTab()), i18n.tr("Utilities"));
        tabPanel.add(((UnitView) getParentView()).getOccupanciesListerView().asWidget(), i18n.tr("Occupancies"));
        tabPanel.add(new ScrollPanel(createFinancialsTab()), i18n.tr("Financial"));
        tabPanel.add(new ScrollPanel(createMarketingTab()), i18n.tr("Marketing"));
        tabPanel.add(new ScrollPanel(new Label("Notes and attachments goes here... ")), i18n.tr("Notes & Attachments"));

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void populate(AptUnitDTO value) {

        if (floorplanCompbo != null) { // restrict floorplan combo here to current building:
            floorplanCompbo.resetCriteria();
            PropertyCriterion criterion = PropertyCriterion.eq(EntityFactory.getEntityPrototype(Floorplan.class).building(), value.belongsTo());
// TODO refine search mechanics  - currently it doesn't work!..             
//            floorplanCompbo.addCriterion(criterion);
        }

        super.populate(value);
    }

    @SuppressWarnings("unchecked")
    private Widget createMarketingTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();
        main.add(split);

        SubtypeInjectors.injectMarketing(main, split, proto().marketing(), this);

        main.add(inject(proto().marketing().floorplan()), 15);
        CEditableComponent<Floorplan, ?> comp = get(proto().marketing().floorplan());
        if (comp instanceof CEntityComboBox<?>) {
            floorplanCompbo = (CEntityComboBox<Floorplan>) comp;
        }

        return main;
    }

    private Widget createFinancialsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();

        split.getLeftPanel().add(inject(proto().financial().unitRent()), 15);
        split.getRightPanel().add(inject(proto().financial().marketRent()), 15);
        main.add(split);

        return main;
    }

    private Widget createUtilitiesTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        main.add(inject(proto().info().utilities(), createUtilitiesListEditor()));
        return main;
    }

    private Widget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();

        split.getLeftPanel().add(inject(proto().info().name()), 15);
        split.getLeftPanel().add(inject(proto().marketing().name()), 15);

        split.getLeftPanel().add(inject(proto().info().type()), 15);

        split.getLeftPanel().add(inject(proto().info().economicStatus()), 15);
        split.getLeftPanel().add(inject(proto().info().economicStatusDescription()), 15);

        split.getLeftPanel().add(inject(proto().info().floor()), 15);
        split.getRightPanel().add(inject(proto().info().number()), 15);

        split.getRightPanel().add(inject(proto().info().area()), 15);
        split.getRightPanel().add(inject(proto().info().areaUnits()), 15);

        split.getRightPanel().add(inject(proto().info().bedrooms()), 15);
        split.getRightPanel().add(inject(proto().info().bathrooms()), 15);
        main.add(split);

        return main;
    }

    private CEntityFolderEditor<Utility> createUtilitiesListEditor() {
        return new CrmEntityFolder<Utility>(Utility.class, "Utility", isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "35em"));
                return columns;
            }
        };
    }

}
