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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.validators.FutureDateValidation;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.dto.AptUnitDTO;

public class UnitEditorForm extends CrmEntityForm<AptUnitDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public UnitEditorForm(IFormView<AptUnitDTO> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public UnitEditorForm(IEditableComponentFactory factory, IFormView<AptUnitDTO> parentView) {
        super(AptUnitDTO.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));

        tabPanel.addDisable(new ScrollPanel(((UnitView) getParentView()).getUnitItemsListerView().asWidget()), i18n.tr("Details"));
        tabPanel.addDisable(new ScrollPanel(((UnitView) getParentView()).getOccupanciesListerView().asWidget()), i18n.tr("Occupancies"));

        tabPanel.add(createFinancialsTab(), i18n.tr("Financial"));
        tabPanel.add(createMarketingTab(), i18n.tr("Marketing"));
        tabPanel.add(new CrmScrollPanel(new Label("Notes and attachments goes here... ")), i18n.tr("Notes & Attachments"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    @Override
    public void addValidations() {
        new FutureDateValidation(get(proto().availableForRent()));
    }

    private Widget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable());
        main.add(split);

        split.getLeftPanel().add(inject(proto().info().name()), 15);
        split.getLeftPanel().add(inject(proto().info().type()), 15);
        split.getLeftPanel().add(inject(proto().info().economicStatus()), 15);
        split.getLeftPanel().add(inject(proto().info().economicStatusDescription()), 15);
        split.getLeftPanel().add(inject(proto().floorplan()), 15);
        split.getLeftPanel().add(inject(proto().availableForRent()), 8.2);

        split.getRightPanel().add(inject(proto().info().floor()), 5);
        split.getRightPanel().add(inject(proto().info().number()), 5);
        split.getRightPanel().add(inject(proto().info().bedrooms()), 5);
        split.getRightPanel().add(inject(proto().info().bathrooms()), 5);
        split.getRightPanel().add(inject(proto().info().area()), 8);
        split.getRightPanel().add(inject(proto().info().areaUnits()), 8);

        // restrict floorplan combo here to current building:
        CEditableComponent<Floorplan, ?> comp = get(proto().floorplan());
        if (isEditable() && comp instanceof CEntityComboBox<?>) {
            @SuppressWarnings("unchecked")
            CEntityComboBox<Floorplan> floorplanCompbo = (CEntityComboBox<Floorplan>) comp;
            floorplanCompbo.setOptionsFilter(new OptionsFilter<Floorplan>() {
                @Override
                public boolean acceptOption(Floorplan entity) {
                    if ((getValue() != null) && !getValue().isNull()) {
                        return entity.building().equals(getValue().belongsTo());
                    }
                    return false;
                }
            });
        }

        return new CrmScrollPanel(main);
    }

    private Widget createFinancialsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().financial().unitRent()), 10);
        main.add(inject(proto().financial().marketRent()), 10);

        return new CrmScrollPanel(main);
    }

    private Widget createMarketingTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        SubtypeInjectors.injectMarketing(main, proto().marketing(), this);

        return new CrmScrollPanel(main);
    }
}