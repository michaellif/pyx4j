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
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.folder.TableFolderDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.dialogs.SelectDialog;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.dto.BuildingDTO;

class UtilityFolder extends VistaTableFolder<ProductItemType> {

    private static final I18n i18n = I18n.get(UtilityFolder.class);

    public static final ArrayList<EntityFolderColumnDescriptor> COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();
    static {
        ProductItemType proto = EntityFactory.getEntityPrototype(ProductItemType.class);
        COLUMNS.add(new EntityFolderColumnDescriptor(proto.name(), "30em"));
    }

    private final CEntityEditor<BuildingDTO> building;

    public UtilityFolder(CEntityEditor<BuildingDTO> building) {
        super(ProductItemType.class, building.isEditable());
        this.building = building;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ProductItemType) {
            return new UtilityEditor();
        } else {
            return super.create(member);
        }
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return COLUMNS;
    }

    @Override
    protected void addItem() {
        new SelectDialog<ProductItemType>(i18n.tr("Select Utilities"), true, getNotSelectedUtilities(building)) {
            @Override
            public boolean onClickOk() {
                for (ProductItemType item : getSelectedItems()) {
                    addItem(item);
                }
                return true;
            }

            @Override
            public String defineWidth() {
                return "300px";
            }

            @Override
            public String defineHeight() {
                return "100px";
            }
        }.show();
    }

    private static List<ProductItemType> getNotSelectedUtilities(CEntityEditor<BuildingDTO> building) {
        List<ProductItemType> alreadySelected = new ArrayList<ProductItemType>();
        for (ProductItemType item : building.getValue().productCatalog().includedUtilities()) {
            alreadySelected.add(item);
        }
        for (ProductItemType item : building.getValue().productCatalog().externalUtilities()) {
            alreadySelected.add(item);
        }
        List<ProductItemType> canBeSelected = new ArrayList<ProductItemType>();
        for (ProductItemType item : building.getValue().availableUtilities()) {
            if (!alreadySelected.contains(item)) {
                canBeSelected.add(item);
            }
        }
        return canBeSelected;
    }

    @Override
    protected IFolderDecorator<ProductItemType> createDecorator() {
        TableFolderDecorator<ProductItemType> decor = (TableFolderDecorator<ProductItemType>) super.createDecorator();
        decor.setShowHeader(false);
        return decor;
    }

    class UtilityEditor extends CEntityFolderRowEditor<ProductItemType> {
        public UtilityEditor() {
            super(ProductItemType.class, UtilityFolder.COLUMNS);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().name()) {
                return inject(column.getObject(), new CLabel());
            }
            return super.createCell(column);
        }

    }
}