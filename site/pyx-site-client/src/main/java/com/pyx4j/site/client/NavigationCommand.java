/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Feb 8, 2016
 * @author vlads
 */
package com.pyx4j.site.client;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.HumanInputCommand;
import com.pyx4j.widgets.client.HumanInputInfo;

public class NavigationCommand implements HumanInputCommand {

    private final AppPlace place;

    public NavigationCommand(AppPlace place) {
        this.place = place;
    }

    @Override
    public final void execute() {
        execute(HumanInputInfo.robot);
    }

    @Override
    public void execute(HumanInputInfo humanInputInfo) {
        AppSite.getPlaceController().open(place, humanInputInfo.isControlKeyDown());
    }

}
