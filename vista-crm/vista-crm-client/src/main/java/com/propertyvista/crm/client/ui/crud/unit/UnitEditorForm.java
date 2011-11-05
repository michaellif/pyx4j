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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.editors.CMarketing;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.dto.AptUnitDTO;

public class UnitEditorForm extends CrmEntityForm<AptUnitDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public UnitEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public UnitEditorForm(IEditableComponentFactory factory) {
        super(AptUnitDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));

        tabPanel.addDisable(isEditable() ? new HTML() : new ScrollPanel(((UnitViewerView) getParentView()).getUnitItemsListerView().asWidget()),
                i18n.tr("Details"));
        tabPanel.addDisable(isEditable() ? new HTML() : new ScrollPanel(((UnitViewerView) getParentView()).getOccupanciesListerView().asWidget()),
                i18n.tr("Occupancies"));

        tabPanel.add(createFinancialsTab(), i18n.tr("Financial"));
        tabPanel.add(createMarketingTab(), i18n.tr("Marketing"));
        tabPanel.add(new CrmScrollPanel(new Label("Notes and attachments goes here... ")), i18n.tr("Notes & Attachments"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void populate(AptUnitDTO value) {
        CComponent<Floorplan, ?> comp = get(proto().floorplan());
        if (isEditable() && comp instanceof CEntityComboBox<?>) {
            @SuppressWarnings("unchecked")
            CEntityComboBox<Floorplan> combo = (CEntityComboBox<Floorplan>) comp;
            combo.resetCriteria(); // reload options with new criteria...
            combo.addCriterion(PropertyCriterion.eq(combo.proto().building(), value.belongsTo().detach()));
        }

        super.populate(value);
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    private Widget createGeneralTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().economicStatus()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().economicStatusDescription()), 20).build());
        main.getFlexCellFormatter().setRowSpan(row, 0, 3);
        row += 2;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().floorplan()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().availableForRent()), 8.2).build());

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().floor()), 5).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().number()), 5).build());

        // shift one column left because economicStatusDescription field RowSpan:
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info()._bedrooms(), new CNumberLabel()), 5).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info()._bathrooms(), new CNumberLabel()), 5).build());

        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().area()), 8).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().areaUnits()), 8).build());

        // restrict floorplan combo here to current building:
        CComponent<Floorplan, ?> comp = get(proto().floorplan());
        if (isEditable() && comp instanceof CEntityComboBox<?>) {
            @SuppressWarnings("unchecked")
            CEntityComboBox<Floorplan> combo = (CEntityComboBox<Floorplan>) comp;
            combo.addCriterion(PropertyCriterion.eq(combo.proto().building(), null));
            combo.addValueChangeHandler(new ValueChangeHandler<Floorplan>() {
                @Override
                public void onValueChange(ValueChangeEvent<Floorplan> event) {
                    get(proto().info()._bedrooms()).setValue(event.getValue().bedrooms().getValue());
                    get(proto().info()._bathrooms()).setValue(event.getValue().bathrooms().getValue());
                }
            });
        }

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(2, "50%");

        return new CrmScrollPanel(main);
    }

    private Widget createFinancialsTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().financial().unitRent(), new CNumberLabel()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().financial().marketRent(), new CNumberLabel()), 10).build());

        return new CrmScrollPanel(main);
    }

    private Widget createMarketingTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().marketing(), new CMarketing()));

        return new CrmScrollPanel(main);
    }
}