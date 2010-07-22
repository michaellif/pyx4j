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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.DomainManager;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.commons.Print;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RecoverableAsyncCallback;
import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.site.client.NavigationUri;
import com.pyx4j.site.shared.meta.NavigNode;
import com.pyx4j.widgets.client.event.shared.PageLeavingEvent;
import com.pyx4j.widgets.client.event.shared.PageLeavingHandler;

public abstract class EntityEditorWidget<E extends IEntity> extends DockPanel implements InlineWidget, PageLeavingHandler {

    private static I18n i18n = I18nFactory.getI18n(EntityEditorWidget.class);

    private static Logger log = LoggerFactory.getLogger(EntityEditorWidget.class);

    private final EntityEditorPanel<E> editorPanel;

    private final Class<E> clazz;

    private final String entityName;

    private Map<String, String> args;

    private final VerticalPanel centerPanel;

    private final Class<? extends NavigNode> editorPage;

    private final MessagePanel messagePanel;

    private int backFromNewItem = 0;

    public static enum EditorAction {

        BACK,

        PRINT
    }

    public EntityEditorWidget(Class<E> clazz, final Class<? extends NavigNode> editorPage, final EntityEditorPanel<E> editorPanel) {
        this.clazz = clazz;
        this.editorPage = editorPage;
        this.editorPanel = editorPanel;
        editorPanel.setParentWidget(this);
        String[] path = clazz.getName().split("\\.");
        entityName = path[path.length - 1];
        VerticalPanel leftPanel = new VerticalPanel();
        leftPanel.setWidth("220px");
        leftPanel.getElement().getStyle().setMarginRight(5, Unit.PX);

        centerPanel = new VerticalPanel();

        messagePanel = new MessagePanel();

        add(messagePanel, DockPanel.NORTH);
        add(leftPanel, DockPanel.WEST);
        add(centerPanel, DockPanel.CENTER);

        leftPanel.add(createActionsPanel());

        centerPanel.add(editorPanel);

    }

    protected ActionsPanel createActionsPanel() {
        return createActionsPanel(EditorAction.BACK, EditorAction.PRINT);
    }

    protected ActionsPanel createActionsPanel(EditorAction... actions) {
        ActionsPanel actionsPanel = new ActionsPanel();
        for (EditorAction action : actions) {
            switch (action) {
            case BACK:
                actionsPanel.addItem(i18n.tr("Back"), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        boolean doubleBack = (backFromNewItem == 2);
                        AbstractSiteDispatcher.back();
                        //Handle entity new Case
                        if (doubleBack) {
                            DeferredCommand.addCommand(new Command() {
                                @Override
                                public void execute() {
                                    AbstractSiteDispatcher.back();
                                }
                            });
                        }

                    }
                });
                break;
            case PRINT:
                actionsPanel.addItem(i18n.tr("Print"), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Print.it(getEditorPanel().toStringForPrint());

                    }
                });
                break;
            }
        }
        return actionsPanel;
    }

    public VerticalPanel getCenterPanel() {
        return centerPanel;
    }

    @Override
    public void populate(Map<String, String> args) {
        this.args = args;
        if (args == null) {
            throw new RuntimeException("Missing args in URL");
        }

        String entityIdStr = args.get("entity_id");

        if ("new".equals(entityIdStr)) {
            backFromNewItem = 1;
            populateForm(null);
        } else {
            backFromNewItem = 0;
            final long entityId = Long.parseLong(entityIdStr);

            AsyncCallback<IEntity> callback = new RecoverableAsyncCallback<IEntity>() {

                @Override
                @SuppressWarnings("unchecked")
                public void onSuccess(IEntity result) {
                    if (result != null) {
                        DomainManager.entityOpened(result);
                        populateForm((E) result);
                    } else {
                        log.warn(entityName + " not found");
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }
            };

            RPCManager.execute(getRetrieveService(), EntityCriteriaByPK.create(clazz, entityId), callback);
        }
    }

    protected Class<? extends EntityServices.Retrieve> getRetrieveService() {
        return EntityServices.Retrieve.class;
    }

    protected void populateForm(E entity) {
        if (entity == null) {
            getEditorPanel().populateForm(createNewEntity());
        } else {
            getEditorPanel().populateForm(entity);
            updateHistoryToken(entity);
        }
    }

    protected E createNewEntity() {
        return EntityFactory.create(clazz);
    }

    @Override
    public void onPageLeaving(PageLeavingEvent event) {
        editorPanel.onPageLeaving(event);
    }

    protected void updateHistoryToken(E entity) {
        String entityIdStr = args.get("entity_id");
        if (entity != null && entity.getPrimaryKey() != null && "new".equals(entityIdStr)) {
            if (backFromNewItem == 1) {
                // New Entity saved.
                backFromNewItem = 2;
            }
            int saveFlag = backFromNewItem;
            AbstractSiteDispatcher.show(new NavigationUri(editorPage, "entity_id", entity.getPrimaryKey().toString()));
            args.put("entity_id", entity.getPrimaryKey().toString());
            backFromNewItem = saveFlag;
        }
    }

    @Override
    protected void onUnload() {
        DeferredCommand.addCommand(new Command() {
            @Override
            public void execute() {
                try {
                    getEditorPanel().clearForm();
                    setMessage(null);
                } catch (Exception e) {
                    log.error("Clean form failed", e);
                }
            }
        });
        backFromNewItem = 0;
        super.onUnload();
    }

    public EntityEditorPanel<E> getEditorPanel() {
        return editorPanel;
    }

    public Map<String, String> getRequestArgs() {
        return args;
    }

    public void setMessage(String message) {
        messagePanel.setMessage(message);
    }

}
