/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.gwt.commons.css.CssVariable;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceDispatcher;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.RootPane;
import com.pyx4j.site.client.SingletonViewFactory;
import com.pyx4j.site.client.events.NotificationEvent;
import com.pyx4j.site.client.events.NotificationHandler;
import com.pyx4j.site.client.ui.layout.frontoffice.FrontOfficeLayoutPanel;

import com.propertyvista.common.client.ClientLocaleUtils;
import com.propertyvista.common.client.config.VistaFeaturesCustomizationClient;
import com.propertyvista.common.client.handlers.VistaUnrecoverableErrorHandler;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.site.VistaBrowserRequirments;
import com.propertyvista.common.client.site.VistaSite;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.SiteDefinitionsDTO;
import com.propertyvista.portal.rpc.portal.shared.services.PortalPolicyRetrieveService;
import com.propertyvista.portal.rpc.portal.shared.services.SiteThemeServices;
import com.propertyvista.portal.rpc.shared.services.PolicyRetrieveService;
import com.propertyvista.portal.shared.themes.PortalPalette;
import com.propertyvista.portal.shared.themes.PortalTheme;

public abstract class PortalSite extends VistaSite {

    private static SiteThemeServices siteThemeServices = GWT.create(SiteThemeServices.class);

    private final RootPane<FrontOfficeLayoutPanel> rootPane;

    private final PortalTheme portalTheme;

    private int windowHeight;

    private boolean canHideAddrBar = false;

    public PortalSite(String appId, Class<? extends PortalSiteMap> siteMapClass, RootPane<FrontOfficeLayoutPanel> rootPane, AppPlaceDispatcher placeDispatcher,
            PortalTheme portalTheme) {
        super(appId, siteMapClass, new SingletonViewFactory(), placeDispatcher);
        this.rootPane = rootPane;
        this.portalTheme = portalTheme;
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();
        AppSite.getEventBus().addHandler(NotificationEvent.getType(), new PortalUserMessageHandler());

        UncaughtHandler.setUnrecoverableErrorHandler(new VistaUnrecoverableErrorHandler());

        getHistoryHandler().register(getPlaceController(), getEventBus());

        RootLayoutPanel.get().add(rootPane);

        hideLoadingIndicator();

        if (verifyBrowserCompatibility()) {
            initialize();
        }
    }

    @Override
    protected boolean isBrowserCompatible() {
        return VistaBrowserRequirments.isBrowserCompatiblePortal();
    }

    private void initialize() {
        initSiteTheme();

        PortalSessionInactiveHandler.register();

        ClientPolicyManager.initialize(GWT.<PolicyRetrieveService> create(PortalPolicyRetrieveService.class));

        initAddressBarControl();
    }

    public final native boolean isTouchScreenDevice() /*-{
		// from the "word on the street"...
		return (!!$doc["createTouch"]) || (!!$doc["ontouchstart"]);
    }-*/;

    /** Enable Address Bar hiding on scroll - subject to availability of the feature on mobile platform. */
    // TODO - NOTE. Currently no consistent behavior between various platforms and browsers in providing
    // window scroll or resize events has been found. So the implementation is rather ugly...
    private void initAddressBarControl() {
        if (isTouchScreenDevice()) {
            windowHeight = Window.getClientHeight();
            // this dummy element will serve as window repaint trigger when it's content is updated, see below...
            // TODO - This is a hack - browser compatibility is not guaranteed.
            final HTML repaintTrigger = new HTML();
            repaintTrigger.getElement().getStyle().setProperty("position", "fixed");
            repaintTrigger.getElement().getStyle().setProperty("top", "-100px");
            Document.get().getDocumentElement().appendChild(repaintTrigger.getElement());

            // extend body height for about the size of address bar
            Document.get().getBody().getStyle().setProperty("height", (windowHeight + 48) + "px");
            RootLayoutPanel.get().getElement().getStyle().setProperty("bottom", "-48px");
            // check if address bar will hide
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    Window.scrollTo(0, 48);
                    // we have to keep this timer going to refresh page via repaintTrigger.setHTML(), see below...
                    new Timer() {
                        @Override
                        public void run() {
                            int curHeight = Window.getClientHeight();
                            // increased window height indicates that the address bar has moved up and we can safely attach
                            // content back to the window bottom... NOTE. This is Android behavior - not supported by iOS...
                            if (!canHideAddrBar && curHeight > windowHeight) {
                                RootLayoutPanel.get().getElement().getStyle().setProperty("bottom", "0");
                                canHideAddrBar = true;
                            }
                            // this seems to trigger content repaint that will keep it in sync with the window height.
                            // NOTE. for this to work the content must come from a variable...
                            repaintTrigger.setHTML("" + curHeight);
                        }
                    }.scheduleRepeating(500);
                }
            }); // scroll down to hide address bar
        }
    }

    private void initSiteTheme() {
        siteThemeServices.retrieveSiteDescriptor(new DefaultAsyncCallback<SiteDefinitionsDTO>() {
            @Override
            public void onSuccess(SiteDefinitionsDTO descriptor) {
                hideLoadingIndicator();
                StyleManager.installTheme(portalTheme, new PortalPalette());
                VistaFeaturesCustomizationClient.setVistaFeatures(descriptor.features());
                VistaFeaturesCustomizationClient.setGoogleAnalyticDisableForEmployee(descriptor.isGoogleAnalyticDisableForEmployee().getValue());
                VistaFeaturesCustomizationClient.enviromentTitleVisible = descriptor.enviromentTitleVisible().getValue(Boolean.TRUE);
                obtainAuthenticationData();
            }

            @Override
            public void onFailure(Throwable caught) {
                hideLoadingIndicator();
                super.onFailure(caught);
            }
        }, ClientLocaleUtils.getCurrentLocale());

    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData(null, new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                getHistoryHandler().handleCurrentHistory();
            }

            @Override
            public void onFailure(Throwable caught) {
                getHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
            }
        }, true, null);
    }

    public static PortalSite instance() {
        return (PortalSite) AppSite.instance();
    }

    public static void scrollToTop() {
        instance().rootPane.asWidget().scrollToTop();
    }

    //  portalRootPane

    private class PortalUserMessageHandler implements NotificationHandler {

        @Override
        public void onNotification(NotificationEvent event) {
            PortalSite.getPlaceController().showNotification(event.getNotification());
        }

    }

}
