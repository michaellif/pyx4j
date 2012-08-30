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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.selections.SelectFloorplanListService;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class UnitForm extends CrmEntityForm<AptUnitDTO> {

    private static final I18n i18n = I18n.get(UnitForm.class);

    public UnitForm() {
        this(false);
    }

    public UnitForm(boolean viewMode) {
        super(AptUnitDTO.class, viewMode);
    }

    @Override
    public void createTabs() {

        Tab tab = addTab(createGeneralTab(i18n.tr("General")));
        selectTab(tab);

        tab = addTab(isEditable() ? new HTML() : ((UnitViewerView) getParentView()).getUnitItemsListerView().asWidget(), i18n.tr("Details"));
        setTabEnabled(tab, !isEditable());

        if (VistaFeatures.instance().occupancyModel()) {
            tab = addTab(isEditable() ? new HTML() : ((UnitViewerView) getParentView()).getOccupanciesListerView().asWidget(), i18n.tr("Occupancy"));
            setTabEnabled(tab, !isEditable());
        }

// TODO Hided till further investigation:
//        addTab(createMarketingTab(), i18n.tr("Marketing"));

        if (!VistaTODO.removedForProduction) {
            tab = addTab(createNotesAndAttachmentsTab(i18n.tr("Notes & Attachments")));
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().lease()).setVisible(!getValue().lease().isNull());
        if (VistaFeatures.instance().occupancyModel()) {
            get(proto()._availableForRent()).setVisible(!getValue()._availableForRent().isNull());
            get(proto().financial()._unitRent()).setVisible(!getValue().financial()._unitRent().isNull());
        }
    }

    private FormFlexPanel createGeneralTab(String title) {

        int row = -1;
        FormFlexPanel left = new FormFlexPanel();

        left.setWidget(
                ++row,
                0,
                new DecoratorBuilder(inject(proto().building(), isEditable() ? new CEntityLabel<Building>() : new CEntityCrudHyperlink<Building>(
                        AppPlaceEntityMapper.resolvePlace(Building.class))), 20).build());

        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().floorplan(), new FloorplanSelectorHyperlink()), 20).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().economicStatus()), 20).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().info().economicStatusDescription()), 20).build());

        left.setBR(++row, 0, 1);
        left.setWidget(
                ++row,
                0,
                new DecoratorBuilder(inject(proto().lease(),
                        isEditable() ? new CEntityLabel<Lease>() : new CEntityCrudHyperlink<Lease>(AppPlaceEntityMapper.resolvePlace(Lease.class))), 20)
                        .build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto()._availableForRent()), 9).build());
        left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().financial()._unitRent()), 7).build());

        if (VistaFeatures.instance().productCatalog()) {
            left.setH3(++row, 0, 1, proto().marketPrices().getMeta().getCaption());
            left.setWidget(++row, 0, inject(proto().marketPrices(), new UnitServicePriceFolder()));
        } else {
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().financial()._marketRent()), 10).build());
        }

        row = -1;
        FormFlexPanel right = new FormFlexPanel();
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().floor()), 5).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().number()), 5).build());

        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info()._bedrooms()), 5).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info()._bathrooms()), 5).build());

        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().area()), 8).build());
        right.setWidget(++row, 1, new DecoratorBuilder(inject(proto().info().areaUnits()), 8).build());

        // tweak UI:
        if (VistaFeatures.instance().occupancyModel()) {
            get(proto()._availableForRent()).setViewable(true);
        }
        if (VistaFeatures.instance().productCatalog()) {
            get(proto().financial()._unitRent()).setViewable(true);
        }
//        get(proto().financial()._marketRent()).setViewable(true);
        get(proto().info()._bedrooms()).setViewable(true);
        get(proto().info()._bathrooms()).setViewable(true);

        // form main panel from those two:
        FormFlexPanel main = new FormFlexPanel(title);

        main.setWidget(0, 0, left);
        main.setWidget(0, 1, right);
        main.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        main.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

        main.getColumnFormatter().setWidth(0, "40%");
        main.getColumnFormatter().setWidth(1, "60%");

        return main;
    }

    private FormFlexPanel createNotesAndAttachmentsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = -1;
//        main.setWidget(++row, 0, new NotesAndAttachmentsForm());
        return main;
    }

    private static class BuildingBoundFloorplanSelectorDialog extends EntitySelectorTableDialog<Floorplan> {

        private static final List<ColumnDescriptor> COLUMNS;

        static {
            Floorplan proto = EntityFactory.getEntityPrototype(Floorplan.class);
            COLUMNS = Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto.name()).build(),
                    new MemberColumnDescriptor.Builder(proto.marketingName(), false).build(),
                    new MemberColumnDescriptor.Builder(proto.floorCount()).build(),
                    new MemberColumnDescriptor.Builder(proto.bedrooms()).build(),
                    new MemberColumnDescriptor.Builder(proto.bathrooms()).build(),
                    new MemberColumnDescriptor.Builder(proto.halfBath()).build(),
                    new MemberColumnDescriptor.Builder(proto.dens()).build(),
                    new MemberColumnDescriptor.Builder(proto.description(), false).build()
            );//@formatter:on
        }

        private final AsyncCallback<Floorplan> onSelectedCallback;

        public BuildingBoundFloorplanSelectorDialog(Key ownerBuildingPk, AsyncCallback<Floorplan> onSelectedCallback) {
            super(Floorplan.class, false, Collections.<Floorplan> emptyList(), i18n.tr("Select Floorplan"));
            setParentFiltering(ownerBuildingPk);
            this.onSelectedCallback = onSelectedCallback;
        }

        @Override
        public boolean onClickOk() {
            onSelectedCallback.onSuccess(getSelectedItems().get(0));
            return true;
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return COLUMNS;
        }

        @Override
        protected AbstractListService<Floorplan> getSelectService() {
            return GWT.<SelectFloorplanListService> create(SelectFloorplanListService.class);
        }
    }

    private class FloorplanSelectorHyperlink extends CEntitySelectorHyperlink<Floorplan> {

        public FloorplanSelectorHyperlink() {
            addValueChangeHandler(new ValueChangeHandler<Floorplan>() {
                @Override
                public void onValueChange(ValueChangeEvent<Floorplan> event) {
                    if (event.getValue() != null) {
                        get(UnitForm.this.proto().info()._bedrooms()).setValue(event.getValue().bedrooms().getValue());
                        get(UnitForm.this.proto().info()._bathrooms()).setValue(event.getValue().bathrooms().getValue());

                        if (get(UnitForm.this.proto().info().area()).getValue() == null) {
                            get(UnitForm.this.proto().info().area()).setValue(event.getValue().area().getValue());
                            get(UnitForm.this.proto().info().areaUnits()).setValue(event.getValue().areaUnits().getValue());
                        }
                    } else {
                        get(UnitForm.this.proto().info()._bedrooms()).setValue(null);
                        get(UnitForm.this.proto().info()._bathrooms()).setValue(null);
                    }
                }
            });
        }

        @Override
        protected AppPlace getTargetPlace() {
            return AppPlaceEntityMapper.resolvePlace(Floorplan.class, getValue().getPrimaryKey());
        }

        @Override
        protected AbstractEntitySelectorDialog<Floorplan> getSelectorDialog() {
            return new BuildingBoundFloorplanSelectorDialog(UnitForm.this.getValue().building().getPrimaryKey(), new DefaultAsyncCallback<Floorplan>() {
                @Override
                public void onSuccess(Floorplan result) {
                    get(UnitForm.this.proto().floorplan()).setValue(result);
                }
            });
        }
    }
}
