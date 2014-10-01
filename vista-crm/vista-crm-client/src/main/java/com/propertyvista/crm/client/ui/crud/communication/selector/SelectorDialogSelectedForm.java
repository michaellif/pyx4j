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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

public class SelectorDialogSelectedForm extends CForm<CommunicationEndpointCollection> {

    public SelectorDialogSelectedForm() {
        super(CommunicationEndpointCollection.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().to(), new CommunicationEndpointCollectionFolder());
        return formPanel;
    }

}
