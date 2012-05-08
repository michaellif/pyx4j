/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-26
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.settings.content;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.settings.content.gadgets.HomePageGadgetEditor;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.HomePageGadgetCrudService;
import com.propertyvista.domain.site.gadgets.HomePageGadget;

public class HomePageGadgetEditorActivity extends EditorActivityBase<HomePageGadget> implements HomePageGadgetEditor.Presenter {

    @SuppressWarnings("unchecked")
    public HomePageGadgetEditorActivity(CrudAppPlace place) {
        super(place, SettingsViewFactory.instance(HomePageGadgetEditor.class), (AbstractCrudService<HomePageGadget>) GWT
                .create(HomePageGadgetCrudService.class), HomePageGadget.class);
    }
}
