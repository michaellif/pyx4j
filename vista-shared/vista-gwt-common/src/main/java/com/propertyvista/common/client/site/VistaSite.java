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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.client.ClientDeploymentConfig;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.client.ApplicationCommon;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.i18n.shared.I18n;
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

    private static final Logger log = LoggerFactory.getLogger(VistaSite.class);

    private static final I18n i18n = I18n.get(VistaSite.class);

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

    protected abstract boolean isBrowserCompatible();

    protected boolean isBrowserIEDocumentModeCompatible() {
        return VistaBrowserRequirments.isBrowserIEDocumentModeCompatible();
    }

    protected boolean verifyBrowserCompatibility() {
        if (!isBrowserCompatible()) {
            hideLoadingIndicator();
            log.warn("Unsupported Browser UserAgent [{}]", BrowserType.getUserAgent());
            Window.alert(i18n.tr("Unsupported Browser")
                    + "\n"
                    + i18n.tr("Your current Browser Version will restrict the functionality of this application.\n"
                            + "Please use an updated version of Internet Explorer.\n"
                            + "This application will also work with current versions of Mozilla Firefox, Google Chrome or Apple Safari"));
            return false;
        } else if (BrowserType.isIE() && BrowserType.isIENative() && !isBrowserIEDocumentModeCompatible()) {
            hideLoadingIndicator();
            Window.alert(i18n.tr("Unsupported Browser Compatibility Mode")
                    + "\n"
                    + i18n.tr("Your current Browser Compatibility Mode settings will restrict the functionality of this application.\n"
                            + "Please change setting 'Document Mode' to IE9 standards.\n"
                            + "This application will also work with current versions of Mozilla Firefox, Google Chrome or Apple Safari"));
            return false;
        }
        return true;
    }

}
