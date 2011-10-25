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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;

class FeatureItemFolder extends VistaTableFolder<ServiceItem> {

    private final CEntityEditor<Feature> parent;

    public FeatureItemFolder(CEntityEditor<Feature> parent) {
        super(ServiceItem.class, parent.isEditable());
        this.parent = parent;
    }

    @Override
    protected List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().type(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().price(), "8em"));
        columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
//            columns.add(new EntityFolderColumnDescriptor(proto().element(), "15em"));
        return columns;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ServiceItem) {
            return new ServiceItemEditor();
        }
        return super.create(member);
    }

    class ServiceItemEditor extends CEntityFolderRowEditor<ServiceItem> {

        public ServiceItemEditor() {
            super(ServiceItem.class, columns());
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?> comp = super.createCell(column);
            if (column.getObject() == proto().type()) {
                if (comp instanceof CEntityComboBox<?>) {
                    @SuppressWarnings("unchecked")
                    CEntityComboBox<ServiceItemType> combo = (CEntityComboBox<ServiceItemType>) comp;
                    combo.setOptionsFilter(new OptionsFilter<ServiceItemType>() {
                        @Override
                        public boolean acceptOption(ServiceItemType entity) {
                            Feature value = parent.getValue();
                            if (value != null && !value.isNull()) {
                                return entity.featureType().equals(value.type());
                            }
                            return false;
                        }
                    });
                    combo.addValueChangeHandler(new ValueChangeHandler<ServiceItemType>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<ServiceItemType> event) {
                            for (ServiceItemType item : parent.getValue().catalog().includedUtilities()) {
                                if (item.equals(event.getValue())) {
                                    MessageDialog.warn(VistaTableFolder.i18n.tr("Note"),
                                            VistaTableFolder.i18n.tr("The selected Utility Type is included in price!"));
                                }
                            }

                        }
                    });
                }
            }
            return comp;
        }
    }
}