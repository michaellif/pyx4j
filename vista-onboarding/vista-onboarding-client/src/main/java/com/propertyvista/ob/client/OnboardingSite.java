/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.ob.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.ClientEntityFactory;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.SingletonViewFactory;
import com.pyx4j.site.client.activity.AppActivityManager;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.events.NotificationEvent;
import com.propertyvista.common.client.events.NotificationHandler;
import com.propertyvista.common.client.handlers.VistaUnrecoverableErrorHandler;
import com.propertyvista.common.client.site.VistaSite;
import com.propertyvista.ob.client.mvp.OnboardingActivityMapper;
import com.propertyvista.ob.client.themes.OnboardingPalette;
import com.propertyvista.ob.client.themes.OnboardingStyles;
import com.propertyvista.ob.client.themes.OnboardingTheme;
import com.propertyvista.ob.rpc.OnboardingSiteMap;
import com.propertyvista.ob.rpc.services.OnboardingAuthenticationService;

public class OnboardingSite extends VistaSite {

    public static final String ONBOARDING_INSERTION_ID = "vista.ob";

    static {
        ClientEntityFactory.ensureIEntityImplementations();
    }

    public OnboardingSite() {
        super("vista-onboarding", OnboardingSiteMap.class, new SingletonViewFactory(), new OnboardingAppPlaceDispatcher());
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();
        UncaughtHandler.setUnrecoverableErrorHandler(new VistaUnrecoverableErrorHandler());
        // subscribe to UserMessageEvent fired from VistaUnrecoverableErrorHandler
        getEventBus().addHandler(NotificationEvent.getType(), new NotificationHandler() {
            @Override
            public void onUserMessage(NotificationEvent event) {
                setNotification(event.getNotification());
                getPlaceController().goToUserMessagePlace();
            }
        });

        getHistoryHandler().register(getPlaceController(), getEventBus(), AppPlace.NOWHERE);
        StyleManager.installTheme(new OnboardingTheme(), new OnboardingPalette());

        createAndBindUI();

        obtainAuthenticationData();
    }

    @Override
    public void showMessageDialog(String message, String title) {
        MessageDialog.info(title, message);
    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData((GWT.<OnboardingAuthenticationService> create(OnboardingAuthenticationService.class)),
                new DefaultAsyncCallback<Boolean>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        hideLoadingIndicator();
                        super.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        hideLoadingIndicator();
                        AppSite.getHistoryHandler().handleCurrentHistory();
                    }

                });
    }

    private void createAndBindUI() {
        RootPanel rootPanel = getRootPanel();
        SimplePanel onboardingPanel = new SimplePanel();
        onboardingPanel.setStyleName(OnboardingStyles.VistaObMainPanel.name());

        rootPanel.add(onboardingPanel);

        if (ApplicationMode.isDevelopment()) {
            addDevLogout();
        }

        AppActivityManager activityManager = new AppActivityManager(new OnboardingActivityMapper(), AppSite.getEventBus());
        activityManager.setDisplay(onboardingPanel);
    }

    private void addDevLogout() {
        if (ApplicationMode.isDevelopment()) {
            RootPanel r = getRootPanel();
            Anchor logoutAnchor = new Anchor();
            logoutAnchor.getElement().getStyle().setFloat(Float.RIGHT);
            logoutAnchor.setText("Developer Logout");
            logoutAnchor.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    ClientContext.logout(GWT.<OnboardingAuthenticationService> create(OnboardingAuthenticationService.class),
                            new DefaultAsyncCallback<AuthenticationResponse>() {
                                @Override
                                public void onSuccess(AuthenticationResponse result) {
                                    // the place redirection should be handled automatically by security controller
                                }
                            });
                }

            });
            SimplePanel panel = new SimplePanel();
            panel.getElement().getStyle().setMarginTop(50, Unit.PX);
            panel.add(logoutAnchor);
            r.add(panel);
        }
    }

    private RootPanel getRootPanel() {
        RootPanel root = RootPanel.get(ONBOARDING_INSERTION_ID);
        if (root == null) {
            root = RootPanel.get();
        }
        return root;
    }

}
