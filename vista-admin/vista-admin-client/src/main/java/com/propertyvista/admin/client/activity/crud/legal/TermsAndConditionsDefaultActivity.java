/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 22, 2012
 * @author dev_vista
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.crud.legal;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.domain.legal.TermsAndConditions;
import com.propertyvista.admin.rpc.services.TermsAndConditionsCrudService;

public class TermsAndConditionsDefaultActivity extends AbstractActivity {
    private final CrudAppPlace place;

    public TermsAndConditionsDefaultActivity(Place place) {
        assert (place instanceof CrudAppPlace);
        this.place = (CrudAppPlace) place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        TermsAndConditionsCrudService srv = GWT.create(TermsAndConditionsCrudService.class);
        srv.retrieveDocument(new DefaultAsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                CrudAppPlace dst = AppSite.getHistoryMapper().createPlace(place.getClass());
                if (result == null) {
                    TermsAndConditions doc = EntityFactory.create(TermsAndConditions.class);
                    dst.formNewItemPlace(doc);
                } else {
                    dst.formViewerPlace(result);
                }
                AppSite.getPlaceController().goTo(dst);
            }
        });
    }
}
