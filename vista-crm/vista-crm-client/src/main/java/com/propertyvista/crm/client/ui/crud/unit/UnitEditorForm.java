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

import java.io.Serializable;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.editors.MarketingEditor;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.AptUnitDTO;

public class UnitEditorForm extends CrmEntityForm<AptUnitDTO> {

    private static final I18n i18n = I18n.get(UnitEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    private SimplePanel buildingPlace;

    public UnitEditorForm() {
        this(false);
    }

    public UnitEditorForm(boolean viewMode) {
        super(AptUnitDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));

        tabPanel.add(isEditable() ? new HTML() : new ScrollPanel(((UnitViewerView) getParentView()).getUnitItemsListerView().asWidget()), i18n.tr("Details"));
        tabPanel.setLastTabDisabled(isEditable());
        tabPanel.add(isEditable() ? new HTML() : new ScrollPanel(((UnitViewerView) getParentView()).getOccupanciesListerView().asWidget()),
                i18n.tr("Occupancy"));
        tabPanel.setLastTabDisabled(isEditable());

// TODO Hided till further investigation:
//        tabPanel.add(createMarketingTab(), i18n.tr("Marketing"));
        tabPanel.add(new CrmScrollPanel(new Label("Notes and attachments goes here... ")), i18n.tr("Notes & Attachments"));

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        if (isEditable()) {
            // setup Building Editor:
            CComponent<?, ?> building = null;
            if (getValue().belongsTo().getPrimaryKey() == null) {
                CEntityComboBox<Building> combo = new CEntityComboBox<Building>(Building.class);
                combo.addValueChangeHandler(new ValueChangeHandler<Building>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Building> event) {
                        setupFloorplanCombo(event.getValue());
                    }
                });
                building = combo;
                // Hack to clear CEntityComboBox
                setupFloorplanCombo(null);
            } else {
                setupFloorplanCombo(getValue().belongsTo());
                if (!getValue().belongsTo().isEmpty()) {
                    CEntityLabel<Building> label = new CEntityLabel<Building>();
                    label.populate(getValue().belongsTo());
                    building = label;
                }
            }

            buildingPlace.clear();
            if (building != null) {
                unbind(proto().belongsTo());
                buildingPlace.setWidget(new DecoratorBuilder(inject(proto().belongsTo(), building), 20).build());
            }
        }

        get(proto()._availableForRent()).setVisible(!getValue()._availableForRent().isNull());
        get(proto().financial()._unitRent()).setVisible(!getValue().financial()._unitRent().isNull());
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

        int row = -1;
        FormFlexPanel left = new FormFlexPanel();
        if (isEditable()) {
            left.setWidget(++row, 0, buildingPlace = new SimplePanel());
        } else {
            left.setWidget(++row, 0,
                    new DecoratorBuilder(inject(proto().belongsTo(), new CEntityCrudHyperlink<Building>(MainActivityMapper.getCrudAppPlace(Building.class))),
                            20).build());
        }
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().floorplan()), 20).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().economicStatus()), 20).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().economicStatusDescription()), 20).build());

        left.setBR(++row, 0, 1);
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto()._availableForRent()), 9).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().financial()._unitRent()), 7).build());
//        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().financial()._marketRent()), 10).build());

        left.setH3(++row, 0, 1, proto().maketPrices().getMeta().getCaption());
        left.setWidget(++row, 0, inject(proto().maketPrices(), new UnitServicePriceFolder()));

        row = -1;
        FormFlexPanel right = new FormFlexPanel();
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().floor()), 5).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().number()), 5).build());

        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info()._bedrooms()), 5).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info()._bathrooms()), 5).build());

        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().area()), 8).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().areaUnits()), 8).build());

        // tweak UI:
        get(proto()._availableForRent()).setViewable(true);
        get(proto().financial()._unitRent()).setViewable(true);
//        get(proto().financial()._marketRent()).setViewable(true);
        get(proto().info()._bedrooms()).setViewable(true);
        get(proto().info()._bathrooms()).setViewable(true);

        // restrict floorplan combo here to any free :
        CComponent<Floorplan, ?> comp = get(proto().floorplan());
        if (isEditable() && comp instanceof CEntityComboBox<?>) {
            @SuppressWarnings("unchecked")
            CEntityComboBox<Floorplan> combo = (CEntityComboBox<Floorplan>) comp;
            combo.resetCriteria();
            combo.addCriterion(PropertyCriterion.eq(combo.proto().building(), (Serializable) null));
            combo.addValueChangeHandler(new ValueChangeHandler<Floorplan>() {
                @Override
                public void onValueChange(ValueChangeEvent<Floorplan> event) {
                    if (event.getValue() != null) {
                        get(proto().info()._bedrooms()).setValue(event.getValue().bedrooms().getValue());
                        get(proto().info()._bathrooms()).setValue(event.getValue().bathrooms().getValue());
                    } else {
                        get(proto().info()._bedrooms()).setValue(null);
                        get(proto().info()._bathrooms()).setValue(null);
                    }
                }
            });
        }

        // form main panel from those two:
        FormFlexPanel main = new FormFlexPanel();
        main.setWidget(0, 0, left);
        main.setWidget(0, 1, right);
        main.getColumnFormatter().setWidth(0, "40%");
        main.getColumnFormatter().setWidth(1, "60%");
        main.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
        return new CrmScrollPanel(main);
    }

    private Widget createMarketingTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().marketing(), new MarketingEditor()));

        return new CrmScrollPanel(main);
    }

    private void setupFloorplanCombo(Building building) {
        // restrict floorplan combo here to current building:
        CComponent<Floorplan, ?> comp = get(proto().floorplan());
        if (isEditable() && comp instanceof CEntityComboBox<?>) {
            @SuppressWarnings("unchecked")
            CEntityComboBox<Floorplan> combo = (CEntityComboBox<Floorplan>) comp;
            combo.resetCriteria(); // reload options with new criteria...
            combo.addCriterion(PropertyCriterion.eq(combo.proto().building(), building));
            combo.retriveOptions(null);
        }
    }
}
