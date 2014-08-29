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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;

class FeatureItemFolder extends VistaBoxFolder<ProductItem> {

    private final CForm<Feature> parent;

    public FeatureItemFolder(CForm<Feature> parent) {
        super(ProductItem.class, parent.isEditable());
        this.parent = parent;
    }

    @Override
    public IFolderItemDecorator<ProductItem> createItemDecorator() {
        BoxFolderItemDecorator<ProductItem> decor = (BoxFolderItemDecorator<ProductItem>) super.createItemDecorator();
        decor.setExpended(false);
        decor.setCaptionFormatter(new IFormatter<ProductItem, String>() {
            @Override
            public String format(ProductItem value) {
                return value.name().getStringView() + ", Price: $" + value.price().getStringView();
            }
        });
        return decor;
    }

    @Override
    protected CForm<ProductItem> createItemForm(IObject<?> member) {
        return new FeatureItemEditor();
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