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
package com.propertyvista.crm.client.ui.crud.settings.content.layoutmodule;

import com.pyx4j.commons.Key;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.site.HomePageGadget;

public class LayoutModuleViewerImpl extends CrmViewerViewImplBase<HomePageGadget> implements LayoutModuleViewer {

    public LayoutModuleViewerImpl() {
        super(CrmSiteMap.Settings.HomePageModules.class, new LayoutModuleEditorForm(true));
    }

    @Override
    public void viewModule(Key id) {
        ((LayoutModuleViewer.Presenter) getPresenter()).viewModule(id);
    }

    @Override
    public void newModule(Key parentId) {
        ((LayoutModuleViewer.Presenter) getPresenter()).editNew(parentId);
    }
}