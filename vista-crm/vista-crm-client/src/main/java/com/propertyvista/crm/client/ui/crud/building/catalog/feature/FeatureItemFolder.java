/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog.feature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.IShowable;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.components.boxes.LockerAreaSelectionDialog;
import com.propertyvista.crm.client.ui.components.boxes.ParkingSelectionDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.shared.config.VistaFeatures;

class FeatureItemFolder extends VistaBoxFolder<ProductItem> {

    private final CrmEntityForm<Feature> parent;

    public FeatureItemFolder(CrmEntityForm<Feature> parent) {
        super(ProductItem.class, parent.isEditable());
        this.parent = parent;

        if (VistaFeatures.instance().yardiIntegration()) {
            setAddable(false);
            setRemovable(false);
            setOrderable(false);
        }
    }

    @Override
    public VistaBoxFolderItemDecorator<ProductItem> createItemDecorator() {
        VistaBoxFolderItemDecorator<ProductItem> decor = super.createItemDecorator();
        decor.setExpended(false);
        decor.setCaptionFormatter(new IFormatter<ProductItem, SafeHtml>() {
            @Override
            public SafeHtml format(ProductItem value) {
                return SafeHtmlUtils.fromString(value.name().getStringView() + ", Price: $" + value.price().getStringView());
            }
        });
        return decor;
    }

    @Override
    protected CForm<ProductItem> createItemForm(IObject<?> member) {
        return new FeatureItemEditor();
    }

    @Override
    protected void addItem() {
        IShowable buildingElementSelectionBox = null;
        switch (parent.getValue().code().type().getValue()) {
        case Parking:
            Set<Parking> alreadySelectedParking = new HashSet<Parking>(getValue().size());
            for (ProductItem item : getValue()) {
                if (!item.element().isNull()) {
                    alreadySelectedParking.add((Parking) item.element().cast());
                }
            }
            buildingElementSelectionBox = new ParkingSelectionDialog(true, alreadySelectedParking) {
                @Override
                protected void setFilters(List<Criterion> filters) {
                    super.setFilters(filters);
                    addFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(Parking.class).building().productCatalog(), parent.getValue().catalog()));
                }

                @Override
                public boolean onClickOk() {
                    for (BuildingElement element : getSelectedItems()) {
                        ProductItem item = EntityFactory.create(ProductItem.class);
                        item.element().set(element);
                        addItem(item);
                    }
                    return true;
                }
            };
            break;

        case Locker:
            Set<LockerArea> alreadySelectedLockerArea = new HashSet<LockerArea>(getValue().size());
            for (ProductItem item : getValue()) {
                if (!item.element().isNull()) {
                    alreadySelectedLockerArea.add((LockerArea) item.element().cast());
                }
            }
            buildingElementSelectionBox = new LockerAreaSelectionDialog(true, alreadySelectedLockerArea) {
                @Override
                protected void setFilters(List<Criterion> filters) {
                    super.setFilters(filters);
                    addFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(LockerArea.class).building().productCatalog(), parent.getValue().catalog()));
                }

                @Override
                public boolean onClickOk() {
                    for (BuildingElement element : getSelectedItems()) {
                        ProductItem item = EntityFactory.create(ProductItem.class);
                        item.element().set(element);
                        addItem(item);
                    }
                    return true;
                }
            };
            break;

        default:
            break;
        }

        if (buildingElementSelectionBox != null) {
            buildingElementSelectionBox.show();
        } else {
            super.addItem();
        }
    }

    private class FeatureItemEditor extends CForm<ProductItem> {
        private final CEntityHyperlink<BuildingElement> buildingElement = new CEntityHyperlink<BuildingElement>(new Command() {
            @Override
            public void execute() {
                goToBuildingElement();
            }
        });

        public FeatureItemEditor() {
            super(ProductItem.class);
        }

        private void goToBuildingElement() {
            Class<? extends IEntity> buildingElementClass = null;
            switch (parent.getValue().code().type().getValue()) {
            case Parking:
                buildingElementClass = Parking.class;
                break;
            case Locker:
                buildingElementClass = LockerArea.class;
                break;
            default:
                break;
            }

            if (buildingElementClass != null) {
                AppSite.getPlaceController()
                        .goTo(AppPlaceEntityMapper.resolvePlace(buildingElementClass).formViewerPlace(getValue().element().getPrimaryKey()));
            }
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().name()).decorate();
            formPanel.append(Location.Left, proto().element(), buildingElement).decorate();
            formPanel.append(Location.Left, proto().description()).decorate();

            formPanel.append(Location.Right, proto().price()).decorate().componentWidth(100);
            formPanel.append(Location.Right, proto().depositLMR()).decorate().componentWidth(100);
            formPanel.append(Location.Right, proto().depositMoveIn()).decorate().componentWidth(100);
            formPanel.append(Location.Right, proto().depositSecurity()).decorate().componentWidth(100);

            return formPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            buildingElement.setVisible(!getValue().element().isNull());

            if (isViewable()) {
                get(proto().depositLMR()).setVisible(parent.getValue().version().depositLMR().enabled().getValue());
                get(proto().depositMoveIn()).setVisible(parent.getValue().version().depositMoveIn().enabled().getValue());
                get(proto().depositSecurity()).setVisible(parent.getValue().version().depositSecurity().enabled().getValue());
            }
        }
    }
}