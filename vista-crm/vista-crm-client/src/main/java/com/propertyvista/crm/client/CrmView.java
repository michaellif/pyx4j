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
package com.propertyvista.crm.client;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.pyx4j.site.client.ui.AppSiteView;
import com.pyx4j.widgets.client.style.Theme;

@Singleton
public class CrmView extends AppSiteView {

    @Inject
    public CrmView(

    //            LogoActivityMapper logoActivityMapper,
    //
    //    ActionsActivityMapper actionsActivityMapper,
    //
    //    MainNavigActivityMapper mainNavigActivityMapper,
    //
    //    SecondNavigActivityMapper secondNavigActivityMapper,
    //
    //    CaptionActivityMapper captionActivityMapper,
    //
    //    MessageActivityMapper messageActivityMapper,
    //
    //    ContentActivityMapper contentActivityMapper,
    //
    //    Left1ActivityMapper left1ActivityMapper,
    //
    //    Left2ActivityMapper left2ActivityMapper,
    //
    //    Right1ActivityMapper right1ActivityMapper,
    //
    //    Right2ActivityMapper right2ActivityMapper,
    //
    //    BottomActivityMapper bottomActivityMapper,

            EventBus eventBus,

            Theme theme) {

        super(null, null, null, null, null, null, null, null, null, null, null, null, eventBus, theme);
        //        super(logoActivityMapper, actionsActivityMapper, mainNavigActivityMapper, captionActivityMapper, secondNavigActivityMapper, messageActivityMapper,
        //                contentActivityMapper, left1ActivityMapper, left2ActivityMapper, right1ActivityMapper, right2ActivityMapper, bottomActivityMapper, eventBus,
        //                theme);

    }

}
