package com.pyx4j.site.demo.client.ui;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.pyx4j.site.client.ui.AppSiteView;
import com.pyx4j.site.demo.client.mvp.ActionsActivityMapper;
import com.pyx4j.site.demo.client.mvp.BottomActivityMapper;
import com.pyx4j.site.demo.client.mvp.Center1ActivityMapper;
import com.pyx4j.site.demo.client.mvp.Center2ActivityMapper;
import com.pyx4j.site.demo.client.mvp.Center3ActivityMapper;
import com.pyx4j.site.demo.client.mvp.Left1ActivityMapper;
import com.pyx4j.site.demo.client.mvp.Left2ActivityMapper;
import com.pyx4j.site.demo.client.mvp.LogoActivityMapper;
import com.pyx4j.site.demo.client.mvp.MainNavigActivityMapper;
import com.pyx4j.site.demo.client.mvp.Right1ActivityMapper;
import com.pyx4j.site.demo.client.mvp.Right2ActivityMapper;
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
