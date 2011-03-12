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
package com.propertyvista.portal.tester.ui;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.propertyvista.portal.tester.mvp.ActionsActivityMapper;
import com.propertyvista.portal.tester.mvp.BottomActivityMapper;
import com.propertyvista.portal.tester.mvp.CaptionActivityMapper;
import com.propertyvista.portal.tester.mvp.MessageActivityMapper;
import com.propertyvista.portal.tester.mvp.ContentActivityMapper;
import com.propertyvista.portal.tester.mvp.Left1ActivityMapper;
import com.propertyvista.portal.tester.mvp.Left2ActivityMapper;
import com.propertyvista.portal.tester.mvp.LogoActivityMapper;
import com.propertyvista.portal.tester.mvp.MainNavigActivityMapper;
import com.propertyvista.portal.tester.mvp.Right1ActivityMapper;
import com.propertyvista.portal.tester.mvp.Right2ActivityMapper;
import com.propertyvista.portal.tester.mvp.SecondNavigActivityMapper;

import com.pyx4j.site.client.ui.AppSiteView;
import com.pyx4j.widgets.client.style.Theme;

@Singleton
public class SiteView extends AppSiteView {

    @Inject
    public SiteView(LogoActivityMapper logoActivityMapper,

    ActionsActivityMapper actionsActivityMapper,

    MainNavigActivityMapper mainNavigActivityMapper,

    CaptionActivityMapper captionctivityMapper,

    SecondNavigActivityMapper secondNavigActivityMapper,

    MessageActivityMapper messageActivityMapper,

    ContentActivityMapper content3ActivityMapper,

    Left1ActivityMapper left1ActivityMapper,

    Left2ActivityMapper left2ActivityMapper,

    Right1ActivityMapper right1ActivityMapper,

    Right2ActivityMapper right2ActivityMapper,

    BottomActivityMapper bottomActivityMapper,

    EventBus eventBus,

    Theme theme) {

        super(logoActivityMapper, actionsActivityMapper, mainNavigActivityMapper, captionctivityMapper, secondNavigActivityMapper, messageActivityMapper,
                content3ActivityMapper, left1ActivityMapper, left2ActivityMapper, right1ActivityMapper, right2ActivityMapper, bottomActivityMapper, eventBus,
                theme);
    }

}
