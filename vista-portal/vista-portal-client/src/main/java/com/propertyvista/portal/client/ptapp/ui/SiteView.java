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
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.mvp.ActionsActivityMapper;
import com.propertyvista.portal.client.ptapp.mvp.BottomActivityMapper;
import com.propertyvista.portal.client.ptapp.mvp.Center1ActivityMapper;
import com.propertyvista.portal.client.ptapp.mvp.Center2ActivityMapper;
import com.propertyvista.portal.client.ptapp.mvp.Center3ActivityMapper;
import com.propertyvista.portal.client.ptapp.mvp.Left1ActivityMapper;
import com.propertyvista.portal.client.ptapp.mvp.Left2ActivityMapper;
import com.propertyvista.portal.client.ptapp.mvp.LogoActivityMapper;
import com.propertyvista.portal.client.ptapp.mvp.MainNavigActivityMapper;
import com.propertyvista.portal.client.ptapp.mvp.Right1ActivityMapper;
import com.propertyvista.portal.client.ptapp.mvp.Right2ActivityMapper;

import com.pyx4j.site.client.ui.AppSiteView;
import com.pyx4j.widgets.client.style.Theme;

@Singleton
public class SiteView extends AppSiteView {

    @Inject
    public SiteView(LogoActivityMapper logoActivityMapper,

    ActionsActivityMapper actionsActivityMapper,

    MainNavigActivityMapper mainNavigActivityMapper,

    Center1ActivityMapper center1ActivityMapper,

    Center2ActivityMapper center2ActivityMapper,

    Center3ActivityMapper center3ActivityMapper,

    Left1ActivityMapper left1ActivityMapper,

    Left2ActivityMapper left2ActivityMapper,

    Right1ActivityMapper right1ActivityMapper,

    Right2ActivityMapper right2ActivityMapper,

    BottomActivityMapper bottomActivityMapper,

    EventBus eventBus,

    Theme theme) {

        super(logoActivityMapper, actionsActivityMapper, mainNavigActivityMapper, center1ActivityMapper, center2ActivityMapper, center3ActivityMapper,
                left1ActivityMapper, left2ActivityMapper, right1ActivityMapper, right2ActivityMapper, bottomActivityMapper, eventBus, theme);

    }

}
