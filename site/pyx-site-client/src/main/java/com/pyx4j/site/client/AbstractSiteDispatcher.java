/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 20, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.gwt.commons.GoogleAnalytics;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.shared.AuthenticationRequiredException;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.site.shared.domain.ResourceUri;
import com.pyx4j.site.shared.util.PageTypeUriEnum;
import com.pyx4j.widgets.client.GlassPanel;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.DialogOptions;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.YesNoOption;
import com.pyx4j.widgets.client.event.shared.PageLeavingEvent;

public abstract class AbstractSiteDispatcher {

    private static final Logger log = LoggerFactory.getLogger(AbstractSiteDispatcher.class);

    private final HashMap<String, SitePanel> sitePanels = new HashMap<String, SitePanel>();

    private SitePanel currentSitePanel;

    private String welcomeUri;

    private NavigationUri pathShown = new NavigationUri("");

    private final ValueChangeHandler<String> historyChangeHandler;

    public AbstractSiteDispatcher() {
        historyChangeHandler = new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(final ValueChangeEvent<String> event) {
                final NavigationUri navigationUri = new NavigationUri(event.getValue());
                if (currentSitePanel != null) {
                    log.debug("page change [{}] -> [{}]", pathShown.getPath(), event.getValue());
                    PageLeavingEvent ple;
                    if (navigationUri.getPageUri().equals(pathShown.getPageUri())) {
                        ple = new PageLeavingEvent(PageLeavingEvent.ChageType.ARGUMETS_CHANGING);
                    } else {
                        ple = new PageLeavingEvent(PageLeavingEvent.ChageType.PAGE_CHANGING);
                    }
                    currentSitePanel.onPageLeaving(ple);
                    if (ple.hasMessage()) {

                        //TODO allow to answer Save when available and navigate anyway. 
                        DialogOptions options = new OkCancelOption() {
                            @Override
                            public boolean onClickOk() {
                                doShow(navigationUri);
                                return true;
                            }

                            @Override
                            public boolean onClickCancel() {
                                History.newItem(pathShown.getPath(), false);
                                return true;
                            }
                        };

                        Dialog d = new Dialog("Confirm", "Are you sure you want to navigate away from this page?\n" + ple.getMessage()
                                + "\nPress OK to continue, or Cancel to stay on the current page.", Dialog.Type.Confirm, options);

                        d.show();
                        return;
                    }
                }
                doShow(navigationUri);
            }
        };
        History.addValueChangeHandler(historyChangeHandler);

        ClientSecurityController.instance().addValueChangeHandler(new ValueChangeHandler<Set<Behavior>>() {
            @Override
            public void onValueChange(ValueChangeEvent<Set<Behavior>> event) {
                onAuthenticationChange();
            }
        });

        RootPanel.get().add(GlassPanel.instance());

        Window.addWindowClosingHandler(new ClosingHandler() {
            @Override
            public void onWindowClosing(ClosingEvent event) {
                if (currentSitePanel != null) {
                    log.debug("page leaving on window closing");
                    PageLeavingEvent ple = new PageLeavingEvent(PageLeavingEvent.ChageType.WINDOW_CLOSING);
                    currentSitePanel.onPageLeaving(ple);
                    if (ple.hasMessage()) {
                        event.setMessage(ple.getMessage());
                    }
                }
            }
        });

    }

    /**
     * Show first page. Call it from onModuleLoad()
     */
    public static void show() {
        History.fireCurrentHistoryState();
    }

    public static void show(String path) {
        History.newItem(path);
    }

    public static void show(PageTypeUriEnum page) {
        show(page.getUri().uri().getValue());
    }

    public static void show(NavigationUri uri) {
        show(uri.getPath());
    }

    public static void back() {
        History.back();
    }

    public static void forward() {
        History.forward();
    }

    public void addHistoryToken(String uri, Map<String, String> history) {
        StringBuilder newToken = new StringBuilder();
        newToken.append(uri);
        newToken.append(ResourceUri.ARGS_GROUP_SEPARATOR);

        boolean first = true;
        for (Map.Entry<String, String> me : history.entrySet()) {
            if (first) {
                first = false;
            } else {
                newToken.append(ResourceUri.ARGS_SEPARATOR);
            }
            newToken.append(me.getKey());
            newToken.append(ResourceUri.NAME_VALUE_SEPARATOR);
            newToken.append(URL.encode(me.getValue()));
        }

        History.newItem(newToken.toString(), false);
    }

    //TODO handle wrong tokens !!!
    private void doShow(final NavigationUri navigationUri) {
        log.debug("Page URI  {}", navigationUri.getPageUri());
        log.debug("Page Args {}", navigationUri.getArgs());

        if (!CommonsStringUtils.isStringSet(navigationUri.getPageUri())) {
            if (welcomeUri == null) {
                throw new RuntimeException("welcomeUri is not set");
            }
            navigationUri.setPath(welcomeUri);
        }

        AsyncCallback<SitePanel> callback = new AsyncCallback<SitePanel>() {
            @Override
            public void onFailure(Throwable caught) {
                hideLoadingIndicator();
                log.warn("obtainSite error", caught);
                //TODO handle SecurityViolationException to show login form
                handleObtainSiteFailure(caught, navigationUri.getSiteName());
            }

            @Override
            public void onSuccess(SitePanel sitePanel) {
                try {
                    if (sitePanel != null) {
                        initSitePanel(navigationUri.getSiteName(), sitePanel);

                        if (!sitePanel.equals(currentSitePanel)) {
                            if (currentSitePanel != null) {
                                RootPanel.get().remove(currentSitePanel);
                            }
                            currentSitePanel = sitePanel;
                            RootPanel.get().add(currentSitePanel);
                        }
                        GoogleAnalytics.track("#" + navigationUri.getPageUri());
                        sitePanel.show(navigationUri.getPageUri(), navigationUri.getArgs());

                        pathShown = navigationUri;
                    } else {
                        throw new Error("sitePanel is not found");
                    }
                } finally {
                    hideLoadingIndicator();
                }
            }
        };

        //TODO check site permission

        if (isPredefinedSite(navigationUri.getSiteName())) {
            obtainPredefinedSite(navigationUri.getSiteName(), callback);
        } else {
            obtainSite(navigationUri.getSiteName(), callback);
        }
    }

    protected boolean isPredefinedSite(String siteName) {
        return false;
    }

    protected void obtainPredefinedSite(String siteName, AsyncCallback<SitePanel> callback) {
    }

    //TODO define better even handling in application
    protected boolean handleObtainSiteFailure(Throwable caught, String siteName) {
        if (caught instanceof AuthenticationRequiredException) {
            if (handleAuthenticationRequiredException((AuthenticationRequiredException) caught, siteName)) {
                return true;
            }
        }
        MessageDialog.error("Application error", caught.getMessage() + "\nContact administrator.");
        return true;
    }

    /**
     * Implementation will decide to use GoogleAccounts or not.
     */
    protected boolean handleAuthenticationRequiredException(AuthenticationRequiredException caught, String siteName) {
        if (caught.isDeveloperAccessRequired()) {
            showGoogleAccountsLoginRedirect(caught.getMessage());
            return true;
        } else {
            return false;
        }
    }

    protected void showGoogleAccountsLoginRedirect(String title) {
        Dialog d = new Dialog(title, "Redirect to Google login page?", Dialog.Type.Confirm, new YesNoOption() {
            @Override
            public boolean onClickYes() {
                ClientContext.googleAccountsLogin();
                return true;
            }

            @Override
            public boolean onClickNo() {
                History.back();
                return true;
            }
        });
        d.show();
    }

    protected abstract void obtainSite(String siteName, AsyncCallback<SitePanel> callback);

    public abstract String getAppId();

    protected void onAuthenticationChange() {
        if (ClientContext.isAuthenticated()) {
            onAfterLogIn();
        } else {
            onAfterLogOut();
        }
        doShow(pathShown);
    }

    protected void onAfterLogOut() {
        log.debug("onAfterLogOut");
        for (SitePanel panel : sitePanels.values()) {
            panel.onAfterLogOut();
        }

    }

    protected void onAfterLogIn() {
        log.debug("onAfterLogIn");
        for (SitePanel panel : sitePanels.values()) {
            panel.onAfterLogIn();
        }

    }

    public String getWelcomeUri() {
        return welcomeUri;
    }

    public void setWelcomeUri(String welcomeUri) {
        this.welcomeUri = welcomeUri;
    }

    public SitePanel getCurrentSitePanel() {
        return currentSitePanel;
    }

    public void setCurrentSitePanel(SitePanel currentSitePanel) {
        this.currentSitePanel = currentSitePanel;
    }

    /**
     * TODO mode to HistoryUtils.
     */
    private Map<String, String> parsArgs(String substring) {
        Map<String, String> args = null;
        String[] nameValues = substring.split(ResourceUri.ARGS_SEPARATOR);
        if (nameValues.length > 0) {
            args = new HashMap<String, String>();
            for (int i = 0; i < nameValues.length; i++) {
                String[] nameAndValue = nameValues[i].split(ResourceUri.NAME_VALUE_SEPARATOR);
                if (nameAndValue.length == 2) {
                    args.put(nameAndValue[0], URL.decode(nameAndValue[1]));
                } else {
                    log.warn("Can't pars argument {}", nameValues[i]);
                }
            }
        }
        return args;
    }

    /**
     * Hide loading image if you had shown one.
     */
    public static void hideLoadingIndicator() {
        // Remove the loading icon
        RootPanel loading = RootPanel.get("loading");
        if (loading != null) {
            com.google.gwt.user.client.Element elem = loading.getElement();
            UIObject.setVisible(elem, false);
            DOM.setInnerHTML(elem, "");
            loading.removeFromParent();
            elem.getParentElement().removeChild(elem);
        }
    }

    private void initSitePanel(String name, SitePanel sitePanel) {
        sitePanels.put(name, sitePanel);
        if (ClientContext.isAuthenticated()) {
            sitePanel.onAfterLogIn();
        } else {
            sitePanel.onAfterLogOut();
        }
    }

    public HashMap<String, SitePanel> getSitePanels() {
        return sitePanels;
    }

}
