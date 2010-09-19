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
 * Created on Feb 16, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.essentials.client.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RecoverableAsyncCallback;
import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.site.client.NavigationUri;
import com.pyx4j.site.shared.meta.NavigNode;
import com.pyx4j.site.shared.meta.NavigUtils;

public class EntityListWidget<E extends IEntity> extends DockPanel implements InlineWidget {

    private static Logger log = LoggerFactory.getLogger(EntityListWidget.class);

    private final EntityListPanel<E> searchResultsPanel;

    private final EntitySearchCriteria<E> criteria;

    private final Class<? extends NavigNode> serachPage;

    private final Class<? extends NavigNode> editorPage;

    private final HTML descriptionPanel;

    protected final E metaEntity;

    public EntityListWidget(Class<E> clazz, final Class<? extends NavigNode> serachPage, final Class<? extends NavigNode> editorPage,
            EntitySearchCriteria<E> criteria, final EntityListPanel<E> searchResultsPanel) {

        setWidth("100%");

        this.serachPage = serachPage;
        this.editorPage = editorPage;
        this.criteria = criteria;
        this.metaEntity = EntityFactory.create(clazz);

        this.searchResultsPanel = searchResultsPanel;
        searchResultsPanel.setEditorPageType(editorPage);

        searchResultsPanel.setPrevActionHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                show(searchResultsPanel.getPageNumber() - 1);
            }
        });

        searchResultsPanel.setNextActionHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                show(searchResultsPanel.getPageNumber() + 1);
            }
        });

        SimplePanel actionsHolderPanel = new SimplePanel();
        actionsHolderPanel.setWidth("220px");
        actionsHolderPanel.getElement().getStyle().setMarginRight(5, Unit.PX);

        descriptionPanel = new HTML();
        add(descriptionPanel, DockPanel.NORTH);

        add(actionsHolderPanel, DockPanel.NORTH);

        Widget actionsPanel = createActionsPanel();
        if (actionsPanel != null) {
            actionsHolderPanel.add(actionsPanel);
        }

        add(searchResultsPanel, DockPanel.CENTER);
    }

    public void setDescription(String description) {
        descriptionPanel.setHTML(description);
    }

    protected ActionsPanel createActionsPanel() {
        ActionsBarPanel actionsPanel = new ActionsBarPanel();
        actionsPanel.addItem("New " + metaEntity.getEntityMeta().getCaption(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AbstractSiteDispatcher.show(new NavigationUri(getEditorPage(), NavigUtils.ENTITY_ID, "new"));
            }
        });
        return actionsPanel;
    }

    protected Class<? extends NavigNode> getEditorPage() {
        return editorPage;
    }

    protected void show(int pageNumber) {
        NavigationUri uri = new NavigationUri(serachPage);
        uri.addArg("pageNumber", String.valueOf(pageNumber));
        AbstractSiteDispatcher.show(uri);
    }

    @Override
    public void populate(Map<String, String> args) {
        int pageNumber = 0;
        if (args != null) {
            String pageNumberStr = args.get("pageNumber");
            if (pageNumberStr != null) {
                try {
                    pageNumber = Integer.parseInt(pageNumberStr);
                } catch (Exception e) {
                    log.warn("Failed to convert pageNumber to int", e);
                }
            }
        }
        populate(pageNumber);
    }

    protected void populate(int pageNumber) {
        final long start = System.currentTimeMillis();
        log.debug("Show page " + pageNumber);
        criteria.setPageSize(searchResultsPanel.getPageSize());
        criteria.setPageNumber(pageNumber);

        AsyncCallback<EntitySearchResult<? extends IEntity>> callback = new RecoverableAsyncCallback<EntitySearchResult<? extends IEntity>>() {

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(EntitySearchResult<? extends IEntity> result) {
                log.debug("Loaded " + result.getData().size() + " " + metaEntity.getEntityMeta().getCaption() + "('s) in {} msec ", System.currentTimeMillis()
                        - start);
                List<E> entities = new ArrayList<E>();
                for (IEntity entity : result.getData()) {
                    entities.add((E) entity);
                }
                long startPopulate = System.currentTimeMillis();
                populateData(entities, criteria.getPageNumber(), result.hasMoreData());
                log.debug("Populated table in {} msec ", System.currentTimeMillis() - startPopulate);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        };

        log.debug("criteria:" + criteria.toString());
        RPCManager.execute(getSearchService(), criteria, callback);
    }

    protected Class<? extends EntityServices.Search> getSearchService() {
        return EntityServices.Search.class;
    }

    protected void populateData(List<E> entities, int pageNumber, boolean hasMoreData) {
        searchResultsPanel.populateData(entities, pageNumber, hasMoreData);
    }

    public EntityListPanel<E> getSearchResultsPanel() {
        return searchResultsPanel;
    }

}
