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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.backoffice.ui.IPane;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.CEntitySelectorHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.IShowable;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.components.editors.InternationalAddressEditor;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.selections.SelectFloorplanListService;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class UnitForm extends CrmEntityForm<AptUnitDTO> {

    private static final I18n i18n = I18n.get(UnitForm.class);

    private Widget buildingLegalAddressLabel;

    private Widget unitLegalAddressLabel;

    private final FormPanel catalogMarketPricesPanel;

    public UnitForm(IForm<AptUnitDTO> view) {
        super(AptUnitDTO.class, view);

        catalogMarketPricesPanel = new FormPanel(this);

        selectTab(addTab(createGeneralTab(), i18n.tr("General")));
        addTab(isEditable() ? new HTML() : ((UnitViewerView) getParentView()).getUnitItemsListerView(), i18n.tr("Details")).setTabEnabled(!isEditable());
        if (!VistaFeatures.instance().yardiIntegration()) {
            addTab(isEditable() ? new HTML() : ((UnitViewerView) getParentView()).getOccupanciesListerView(), i18n.tr("Occupancy"))
                    .setTabEnabled(!isEditable());
        }
        addTab(createLegalAddresslTab(), i18n.tr("Legal Address"));
        // TODO Hided till further investigation:
        // addTab(createMarketingTab(), i18n.tr("Marketing"));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().lease()).setVisible(!getValue().lease().isNull());
        get(proto().availability().availableForRent()).setVisible(!getValue().availability().availableForRent().isNull());
        get(proto().financial()._unitRent()).setVisible(!getValue().financial()._unitRent().isNull());
        get(proto().reservedUntil()).setVisible(!getValue().reservedUntil().isNull());

        get(proto().financial()._marketRent()).setVisible(false);
        catalogMarketPricesPanel.setVisible(!getValue().building().defaultProductCatalog().getValue(false));

        updateSelectedLegalAddress();
    }

    private void updateSelectedLegalAddress() {
        boolean customLegalAddress = getValue().info().legalAddressOverride().getValue(false);
        // unit custom address visibility
        get(proto().info().legalAddress()).setVisible(customLegalAddress);
        unitLegalAddressLabel.setVisible(customLegalAddress);
        // building legal address visibility is opposite to unit custom address above
        get(proto().buildingLegalAddress()).setVisible(!customLegalAddress);
        buildingLegalAddressLabel.setVisible(!customLegalAddress);
    }

    private IsWidget createGeneralTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().building(),
                isEditable() ? new CEntityLabel<Building>() : new CEntityCrudHyperlink<Building>(AppPlaceEntityMapper.resolvePlace(Building.class))).decorate();
        formPanel.append(Location.Right, proto().info().floor()).decorate().componentWidth(100);

        formPanel.append(Location.Left, proto().floorplan(), new FloorplanSelectorHyperlink()).decorate();
        formPanel.append(Location.Right, proto().info().number()).decorate().componentWidth(100);

        formPanel.append(Location.Left, proto().info().economicStatus()).decorate();
        formPanel.append(Location.Right, proto().info()._bedrooms()).decorate().componentWidth(100);

        formPanel.append(Location.Left, proto().info().economicStatusDescription()).decorate();
        formPanel.append(Location.Right, proto().info()._bathrooms()).decorate().componentWidth(100);

        formPanel.append(Location.Left, proto().lease(),
                isEditable() ? new CEntityLabel<Lease>() : new CEntityCrudHyperlink<Lease>(AppPlaceEntityMapper.resolvePlace(Lease.class))).decorate();
        formPanel.append(Location.Right, proto().info().area()).decorate().componentWidth(100);

        formPanel.append(Location.Left, proto().availability().availableForRent()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().info().areaUnits()).decorate().componentWidth(100);

        formPanel.append(Location.Left, proto().reservedUntil()).decorate().componentWidth(100);

        formPanel.append(Location.Left, proto().financial()._unitRent()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().financial()._marketRent()).decorate().componentWidth(100);

        catalogMarketPricesPanel.h1(proto().marketPrices().getMeta().getCaption());
        catalogMarketPricesPanel.append(Location.Left, proto().marketPrices(), new UnitServicePriceFolder());
        formPanel.append(Location.Dual, catalogMarketPricesPanel);

        return formPanel;
    }

    private IsWidget createLegalAddresslTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().info().legalAddressOverride()).decorate().labelWidth("240px");
        unitLegalAddressLabel = formPanel.h1(proto().info().legalAddress().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().info().legalAddress(), new InternationalAddressEditor());

        buildingLegalAddressLabel = formPanel.h1(proto().buildingLegalAddress().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().buildingLegalAddress(), new InternationalAddressEditor());
        get(proto().buildingLegalAddress()).setEditable(false);

        get(proto().info().legalAddressOverride()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                updateSelectedLegalAddress();

                // Populate default values base on building address
                if (getValue().info().legalAddressOverride().getValue(false) && get(proto().info().legalAddress()).getValue().isEmpty()) {
                    get(proto().info().legalAddress()).setValue(getValue().buildingLegalAddress());
                }
            }
        });

        return formPanel;
    }

    private static class BuildingBoundFloorplanSelectorDialog extends EntitySelectorTableVisorController<Floorplan> {

        private final AsyncCallback<Floorplan> onSelectedCallback;

        public BuildingBoundFloorplanSelectorDialog(IPane parentView, Key ownerBuildingPk, AsyncCallback<Floorplan> onSelectedCallback) {
            super(parentView, Floorplan.class, false, Collections.<Floorplan> emptySet(), i18n.tr("Select Floorplan"));
            setParentFiltering(ownerBuildingPk);
            this.onSelectedCallback = onSelectedCallback;
        }

        @Override
        public void onClickOk() {
            onSelectedCallback.onSuccess(getSelectedItem());
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList( // @formatter:off
                    new MemberColumnDescriptor.Builder(proto().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().marketingName(),false).build(),
                    new MemberColumnDescriptor.Builder(proto().floorCount()).build(),
                    new MemberColumnDescriptor.Builder(proto().bedrooms()).build(),
                    new MemberColumnDescriptor.Builder(proto().bathrooms()).build(),
                    new MemberColumnDescriptor.Builder(proto().halfBath()).build(),
                    new MemberColumnDescriptor.Builder(proto().dens()).build(),
                    new MemberColumnDescriptor.Builder(proto().description(),false).build()
                );// @formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().marketingName(), false), new Sort(proto().bedrooms(), false));
        }

        @Override
        protected AbstractListCrudService<Floorplan> getSelectService() {
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
        protected IShowable getSelectorDialog() {
            return new BuildingBoundFloorplanSelectorDialog(UnitForm.this.getParentView(), UnitForm.this.getValue().building().getPrimaryKey(),
                    new DefaultAsyncCallback<Floorplan>() {
                        @Override
                        public void onSuccess(Floorplan result) {
                            get(UnitForm.this.proto().floorplan()).setValue(result);
                        }
                    });
        }
    }
}
