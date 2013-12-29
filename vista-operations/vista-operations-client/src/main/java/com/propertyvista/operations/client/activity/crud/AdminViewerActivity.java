/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 14, 2012
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.AbstractViewerActivity;
import com.pyx4j.site.client.ui.prime.form.IViewer;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.event.CrudNavigateEvent;

public class AdminViewerActivity<E extends IEntity> extends AbstractViewerActivity<E> {

    private final CrudAppPlace place;

    public AdminViewerActivity(CrudAppPlace place, IViewer<E> view, AbstractCrudService<E> service) {
        super(place, view, service);

        assert (place instanceof CrudAppPlace);
        this.place = place;
    }

    @Override
    protected void onPopulateSuccess(E result) {
        super.onPopulateSuccess(result);
        AppSite.instance();
        AppSite.getEventBus().fireEvent(new CrudNavigateEvent(place, result));
    }
}
