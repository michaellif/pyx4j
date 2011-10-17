/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.ShortCutsView;
import com.propertyvista.crm.client.ui.ShortCutsView.ShortCutsPresenter;
import com.propertyvista.crm.client.ui.viewfactories.CrmVeiwFactory;

public class ShortCutsActivity extends AbstractActivity implements ShortCutsPresenter {

    private static I18n i18n = I18n.get(ShortCutsActivity.class);

    private final ShortCutsView view;

    public ShortCutsActivity(Place place) {
        view = (ShortCutsView) CrmVeiwFactory.instance(ShortCutsView.class);
        assert (view != null);
        view.setPresenter(this);
        withPlace(place);
    }

    public ShortCutsActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

    }

    @Override
    public void navigTo(Place place) {
        AppSite.getPlaceController().goTo(place);

    }

    @Override
    public String getNavigLabel(AppPlace place) {
        return AppSite.getHistoryMapper().getPlaceInfo(place).getNavigLabel();
    }

    @Override
    public List<NavigFolder> getNavigFolders() {

        ArrayList<NavigFolder> list = new ArrayList<NavigFolder>();
        //ShortCuts
        NavigFolder folder = new NavigFolder(i18n.tr("ShortCuts"));
        list.add(folder);

        return list;

    }

    @Override
    public Place getWhere() {
        return AppSite.getPlaceController().getWhere();
    }

}
