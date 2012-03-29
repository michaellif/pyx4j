/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 23, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.folder.TableFolderDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.AptUnitServicePriceDTO;

public class UnitServicePriceFolder extends VistaTableFolder<AptUnitServicePriceDTO> {

    public UnitServicePriceFolder() {
        super(AptUnitServicePriceDTO.class, false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().type(), "18em"));
        columns.add(new EntityFolderColumnDescriptor(proto().price(), "7em"));
        return columns;
    }

    @Override
    protected IFolderDecorator<AptUnitServicePriceDTO> createDecorator() {
        IFolderDecorator<AptUnitServicePriceDTO> decor = super.createDecorator();
        if (decor instanceof TableFolderDecorator) {
            ((TableFolderDecorator<AptUnitServicePriceDTO>) decor).setShowHeader(false);
        }
        return decor;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof AptUnitServicePriceDTO) {
            return new ServicePriceEditor();
        }
        return super.create(member);
    }

    private class ServicePriceEditor extends CEntityFolderRowEditor<AptUnitServicePriceDTO> {

        public ServicePriceEditor() {
            super(AptUnitServicePriceDTO.class, columns());
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?, ?> comp;
            if (column.getObject() == proto().type()) {
                if (isEditable()) {
                    comp = inject(proto().type(), new CLabel());
                } else {
                    comp = inject(proto().type(), new CHyperlink(new Command() {
                        @Override
                        public void execute() {
                            CrudAppPlace place = new CrmSiteMap.Properties.Service();
                            place.formViewerPlace(getValue().getPrimaryKey());
                            AppSite.getPlaceController().goTo(place);
                        }
                    }));
                }
            } else {
                comp = super.createCell(column);
            }
            comp.inheritViewable(false);
            comp.setViewable(true);
            return comp;
        }
    }

}
