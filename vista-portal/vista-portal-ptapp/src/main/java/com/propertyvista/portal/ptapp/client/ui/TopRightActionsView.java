/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui;

import java.util.List;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.portal.ptapp.client.ui.TopRightActionsViewImpl.Theme;
import com.propertyvista.shared.CompiledLocale;

public interface TopRightActionsView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {

        public PlaceController getPlaceController();

        public void setTheme(Theme theme);

        public void logout();

        public void login();

        void setLocale(CompiledLocale locale);

    }

    public void onLogedOut();

    public void onLogedIn(String userName);

    void setAvailableLocales(List<CompiledLocale> locales);

}