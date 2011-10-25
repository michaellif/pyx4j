/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.dictionary;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ServiceDictionaryViewForm extends CrmEntityForm<ServiceItemType> {

    public ServiceDictionaryViewForm() {
        super(ServiceItemType.class, new CrmViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(true);

        main.add(new CrmSectionSeparator(i18n.tr("Service Types") + ":"));
        main.add(((ServiceDictionaryView) getParentView()).getServiceListerView().asWidget());
        main.add(new CrmSectionSeparator(i18n.tr("Feature Types") + ":"));
        main.add(((ServiceDictionaryView) getParentView()).getFeatureListerView().asWidget());

        return new ScrollPanel(main);
    }
}