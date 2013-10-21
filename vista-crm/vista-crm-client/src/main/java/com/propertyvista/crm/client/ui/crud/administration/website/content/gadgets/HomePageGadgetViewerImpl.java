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
package com.propertyvista.crm.client.ui.crud.administration.website.content.gadgets;

import com.pyx4j.commons.Key;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.site.gadgets.HomePageGadget;

public class HomePageGadgetViewerImpl extends CrmViewerViewImplBase<HomePageGadget> implements HomePageGadgetViewer {

    public HomePageGadgetViewerImpl() {
        setForm(new HomePageGadgetForm(this));
    }

    @Override
    public void viewGadget(Key id) {
        ((HomePageGadgetViewer.Presenter) getPresenter()).viewGadget(id);
    }

    @Override
    public void newGadget(Key parentId) {
        ((HomePageGadgetViewer.Presenter) getPresenter()).editNew(parentId);
    }
}