/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 26, 2014
 * @author arminea
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolder;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.dto.CommunicationEndpointDTO;

public class CommunicationEndpointCollectionFolder extends CFolder<CommunicationEndpointDTO> {

    public CommunicationEndpointCollectionFolder() {
        super(CommunicationEndpointDTO.class);
        setAddable(false);
        setOrderable(false);
        setRemovable(true);
    }

    @Override
    protected IFolderItemDecorator<CommunicationEndpointDTO> createItemDecorator() {
        BoxFolderItemDecorator<CommunicationEndpointDTO> itemDecorator = new BoxFolderItemDecorator<CommunicationEndpointDTO>(VistaImages.INSTANCE);
        itemDecorator.setExpended(false);
        return itemDecorator;
    }

    @Override
    protected CForm<CommunicationEndpointDTO> createItemForm(IObject<?> member) {
        return new CommunicationEndpointDTOPresenter();
    }

    @Override
    protected IFolderDecorator<CommunicationEndpointDTO> createFolderDecorator() {
        return new BoxFolderDecorator<CommunicationEndpointDTO>(VistaImages.INSTANCE, "", true);
    }

    private class CommunicationEndpointDTOPresenter extends CForm<CommunicationEndpointDTO> {
        public CommunicationEndpointDTOPresenter() {
            super(CommunicationEndpointDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Dual, proto().name()).decorate();
            formPanel.append(Location.Left, proto().type()).decorate();
            return formPanel;
        }
    }
}
