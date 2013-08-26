/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 13, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.common.client.site;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.client.ClientDeploymentConfig;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.client.ApplicationCommon;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.log4gwt.rpcappender.RPCAppender;
import com.pyx4j.log4gwt.shared.Level;
import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.site.client.AppPlaceDispatcher;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.SingletonViewFactory;
import com.pyx4j.site.client.ViewFactory;
import com.pyx4j.site.shared.meta.SiteMap;
import com.pyx4j.widgets.client.GlassPanel;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.portal.rpc.DeploymentConsts;

public abstract class VistaSite extends AppSite {

    private final ViewFactory viewFactory;

    private Notification notification;

    public VistaSite(String appId, Class<? extends SiteMap> siteMapClass, ViewFactory viewFactory) {
        this(appId, siteMapClass, viewFactory, null);
    }

    public VistaSite(String appId, Class<? extends SiteMap> siteMapClass, ViewFactory viewFactory, AppPlaceDispatcher dispatcher) {
        super(appId, siteMapClass, dispatcher);
        this.viewFactory = viewFactory;
    }

    @Override
    public void onSiteLoad() {
        ImageFactory.setImageBundle((VistaImages) GWT.create(VistaImages.class));
        ApplicationCommon.initRpcGlassPanel();
        if (ApplicationMode.isDevelopment() && Window.Location.getParameter("trace") != null) {
            RPCAppender rpcAppender = new RPCAppender(Level.TRACE);
            rpcAppender.autoFlush(2 * Consts.SEC2MILLISECONDS);
            ClientLogger.addAppender(rpcAppender);
            ClientLogger.setTraceOn(true);
        } else {
            ClientLogger.addAppender(new RPCAppender(Level.WARN));
        }
        RootPanel.get().add(GlassPanel.instance());

        ClientDeploymentConfig.setDownloadServletMapping(DeploymentConsts.downloadServletMapping);
        ClientDeploymentConfig.setUploadServletMapping(DeploymentConsts.uploadServletMapping);

        getEventBus().addHandler(BehaviorChangeEvent.getType(), new BehaviorChangeHandler() {
            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                if (viewFactory instanceof SingletonViewFactory) {
                    ((SingletonViewFactory) viewFactory).invalidate();
                }
            }
        });

    }

    public static VistaSite instance() {
        return (VistaSite) AppSite.instance();
    }

    public static ViewFactory getViewFactory() {
        return instance().viewFactory;
    }

    protected void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }

    public abstract void showMessageDialog(String message, String title);

    public native static boolean isIEVersion9Native()
    /*-{ return $wnd.ieVersion9 === true; }-*/;

    public native static boolean isIEVersion8Native()
    /*-{ return $wnd.ieVersion8 === true; }-*/;

    public static boolean isIEDocumentModeComatible(int expectedMode) {
        try {
            int mode = getIEDocumentModeNative();
            return mode >= expectedMode;
        } catch (Throwable e) {
            return false;
        }
    }

    public native static int getIEDocumentModeNative()
    /*-{  return $doc.documentMode; }-*/;
}
