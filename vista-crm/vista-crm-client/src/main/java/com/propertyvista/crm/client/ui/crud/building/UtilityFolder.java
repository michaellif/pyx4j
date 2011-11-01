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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.dto.BuildingDTO;

class UtilityFolder extends VistaTableFolder<ServiceItemType> {

    public static final ArrayList<EntityFolderColumnDescriptor> COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();
    static {
        ServiceItemType proto = EntityFactory.getEntityPrototype(ServiceItemType.class);
        COLUMNS.add(new EntityFolderColumnDescriptor(proto.name(), "30em"));
    }

    private final CEntityEditor<BuildingDTO> building;

    public UtilityFolder(CEntityEditor<BuildingDTO> building) {
        super(ServiceItemType.class, building.isEditable());
        this.building = building;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ServiceItemType) {
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
        new ShowPopUpBox<SelectUtilityBox>(new SelectUtilityBox(building)) {
            @Override
            protected void onClose(SelectUtilityBox box) {
                if (box.getSelectedItems() != null) {
                    for (ServiceItemType item : box.getSelectedItems()) {
                        addItem(item);
                    }
                }
            }
        };
    }

    @Override
    protected IFolderDecorator<ServiceItemType> createDecorator() {
        TableFolderDecorator<ServiceItemType> decor = (TableFolderDecorator<ServiceItemType>) super.createDecorator();
        decor.setShowHeader(false);
        return decor;
    }

    class UtilityEditor extends CEntityFolderRowEditor<ServiceItemType> {
        public UtilityEditor() {
            super(ServiceItemType.class, UtilityFolder.COLUMNS);
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().name()) {
                return inject(column.getObject(), new CLabel());
            }
            return super.createCell(column);
        }

    }

    private class SelectUtilityBox extends OkCancelBox {

        private ListBox list;

        private List<ServiceItemType> selectedItems;

        private final CEntityEditor<BuildingDTO> building;

        public SelectUtilityBox(CEntityEditor<BuildingDTO> building) {
            super(i18n.tr("Select Utilities"));
            this.building = building;
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(false);

            if (!building.getValue().availableUtilities().isEmpty()) {
                list = new ListBox(true);
                list.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        okButton.setEnabled(list.getSelectedIndex() >= 0);
                    }
                });

                List<ServiceItemType> alreadySelected = new ArrayList<ServiceItemType>();
                for (ServiceItemType item : building.getValue().serviceCatalog().includedUtilities()) {
                    alreadySelected.add(item);
                }
                for (ServiceItemType item : building.getValue().serviceCatalog().externalUtilities()) {
                    alreadySelected.add(item);
                }

                for (ServiceItemType item : building.getValue().availableUtilities()) {
                    if (!alreadySelected.contains(item)) {
                        list.addItem(item.getStringView());
                        list.setValue(list.getItemCount() - 1, item.id().toString());
                    }
                }
                list.setVisibleItemCount(8);
                list.setWidth("100%");
                return list.asWidget();
            } else {
                return new HTML(i18n.tr("There are no items available!"));
            }
        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        @Override
        protected boolean onOk() {
            selectedItems = new ArrayList<ServiceItemType>(4);
            for (int i = 0; i < list.getItemCount(); ++i) {
                if (list.isItemSelected(i)) {
                    for (ServiceItemType item : building.getValue().availableUtilities()) {
                        if (list.getValue(i).contentEquals(item.id().toString())) {
                            selectedItems.add(item);
                        }
                    }
                }
            }
            return super.onOk();
        }

        @Override
        protected void onCancel() {
            selectedItems = null;
        }

        protected List<ServiceItemType> getSelectedItems() {
            return selectedItems;
        }
    }
}