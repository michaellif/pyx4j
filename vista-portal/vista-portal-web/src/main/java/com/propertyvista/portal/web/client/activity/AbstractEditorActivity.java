/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity;

import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.ui.IEditorView;
import com.propertyvista.portal.web.client.ui.IEditorView.IEditorPresenter;

public abstract class AbstractEditorActivity<E extends IEntity> extends SecurityAwareActivity implements IEditorPresenter<E> {

    private final IEditorView<E> view;

    public AbstractEditorActivity(Class<? extends IEditorView<E>> viewType) {
        view = PortalWebSite.getViewFactory().instantiate(viewType);
        view.setPresenter(this);
    }

    public IEditorView<E> getView() {
        return view;
    }

    @Override
    public void edit() {
        getView().setEditable(true);
    }

    @Override
    public void save() {
        getView().setEditable(false);
    }

    @Override
    public void cancel() {
        getView().setEditable(false);
    }
}
