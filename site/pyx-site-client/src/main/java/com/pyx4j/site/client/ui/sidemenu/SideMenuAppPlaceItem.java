/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2012-12-19
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.client.ui.sidemenu;

import com.google.gwt.user.client.Command;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.images.ButtonImages;

public class SideMenuAppPlaceItem extends SideMenuItem {

    private final AppPlace appPlace;

    public SideMenuAppPlaceItem(final AppPlace appPlace, ButtonImages images) {
        super(new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(appPlace);
            }
        }, null, AppSite.getHistoryMapper().getPlaceInfo(appPlace).getNavigLabel(), images);
        this.appPlace = appPlace;
    }

    public AppPlace getPlace() {
        return appPlace;
    }

    @Override
    public void select(AppPlace appPlace) {
        super.select(appPlace);
        if (appPlace == null) {
            setSelected(false);
        } else if (appPlace.equals(this.appPlace)) {
            setSelected(true);
        }
    }
}