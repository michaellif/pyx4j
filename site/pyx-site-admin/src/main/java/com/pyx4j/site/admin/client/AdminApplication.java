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
 * Created on Apr 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.admin.client;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.ria.client.FolderSectionPanel;
import com.pyx4j.ria.client.HeaderPanel;
import com.pyx4j.ria.client.IApplication;
import com.pyx4j.ria.client.IView;
import com.pyx4j.ria.client.Perspective;
import com.pyx4j.ria.client.StatusBar;
import com.pyx4j.ria.client.ThreeFoldersMainPanel;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.rpc.SiteServices;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.site.shared.domain.Portlet;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class AdminApplication implements IApplication {

    private static Logger log = LoggerFactory.getLogger(AdminApplication.class);

    private Perspective perspective;

    private StatusBar statusBar;

    private Command saveCommand;

    private ThreeFoldersMainPanel mainPanel;

    private final SiteData siteData;

    private SiteMapView siteMapView;

    private PortletsView portletsView;

    public AdminApplication() {
        siteData = new SiteData();
    }

    private void openEditor(IView editor) {
        mainPanel.getTopFolder().addView(editor, true);
        mainPanel.getTopFolder().showView(editor);
    }

    public void editSite(Site site) {
        openEditor(new SiteEditor(site));
    }

    public void editPage(Page page) {
        openEditor(new PageEditor(page));
    }

    public void editPortlet(Portlet portlet) {
        openEditor(new PortletEditor(portlet));
    }

    public void onLoad() {

        final AsyncCallback<Vector<Site>> rpcCallback = new AsyncCallback<Vector<Site>>() {

            public void onFailure(Throwable t) {
                log.error(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            public void onSuccess(Vector<Site> result) {
                siteData.setSites(result);
                siteMapView.update();
            }
        };
        RPCManager.execute(EntityServices.Query.class, EntityCriteria.create(Site.class), (AsyncCallback) rpcCallback);

        final AsyncCallback rpcCallbackPortlet = new AsyncCallback<Vector<Portlet>>() {

            public void onFailure(Throwable t) {
                log.error(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            public void onSuccess(Vector<Portlet> result) {
                siteData.setPortlets(result);
                portletsView.update();
            }
        };
        RPCManager.execute(EntityServices.Query.class, EntityCriteria.create(Portlet.class), rpcCallbackPortlet);

        final AsyncCallback rpcSaveCallback = new AsyncCallback() {

            public void onFailure(Throwable t) {
                MessageDialog.error("Save failed", t);
            }

            public void onSuccess(Object result) {
                MessageDialog.info("Save", "Object " + ((IEntity<?>) result).getEntityMeta().getCaption() + " saved");
            }
        };

        saveCommand = new Command() {
            @Override
            public void execute() {
                FolderSectionPanel panel = mainPanel.getTopFolder();
                IView currentEditor = panel.getCurrentView();
                if (currentEditor instanceof SiteEditor) {
                    SiteEditor siteEditor = (SiteEditor) currentEditor;
                    RPCManager.execute(SiteServices.Save.class, siteEditor.getSite(), rpcSaveCallback);
                } else if (currentEditor instanceof PageEditor) {
                    PageEditor pageEditor = (PageEditor) currentEditor;
                    Page page = pageEditor.getUpdatedPage();
                    RPCManager.execute(SiteServices.Save.class, page, rpcSaveCallback);
                } else if (currentEditor instanceof PortletEditor) {
                    PortletEditor portletEditor = (PortletEditor) currentEditor;
                    Portlet portlet = portletEditor.getUpdatedPortlet();
                    RPCManager.execute(SiteServices.Save.class, portlet, rpcSaveCallback);
                }
            }
        };

        perspective = new Perspective();

        statusBar = new StatusBar();

        perspective.setHeaderPanel(new HeaderPanel("Site Admin"));
        perspective.setMenuBar(new MainMenu(this));

        perspective.setActionsToolbar(new Toolbar(this));

        HorizontalPanel links = new HorizontalPanel();
        Anchor logoutHyperlink = new Anchor("Sign out", "javascript:void(0)");
        DOM.setStyleAttribute(logoutHyperlink.getElement(), "marginRight", "10px");
        DOM.setStyleAttribute(logoutHyperlink.getElement(), "whiteSpace", "nowrap");
        logoutHyperlink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ClientContext.logout(new AsyncCallback<AuthenticationResponse>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        log.error("Logout failure", caught);
                    }

                    @Override
                    public void onSuccess(AuthenticationResponse result) {
                        Window.Location.replace("/");
                    }
                });
            }
        });
        links.add(logoutHyperlink);
        perspective.setLinksToolbar(links);

        mainPanel = new ThreeFoldersMainPanel();

        siteMapView = new SiteMapView(siteData);

        mainPanel.getLeftFolder().addView(siteMapView, false);

        portletsView = new PortletsView(siteData);

        mainPanel.getLeftFolder().addView(portletsView, false);

        //TODO
        //mainPanel.getBottomFolder().addView(new LogView("Log", mainPanel.getBottomFolder()));

        perspective.setMainPanel(mainPanel);

        perspective.setStatusPanel(statusBar);

        perspective.attachToParent(RootPanel.get());
    }

    public void onDiscard() {
    }

    public Command getSaveCommand() {
        return saveCommand;
    }

}
