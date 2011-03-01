/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Feb 11, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.entity.client.ui.flex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

/**
 * This component represents list of IEntities
 */
public abstract class CEntityFolder<E extends IEntity> extends CEditableComponent<IList<E>, NativeEntityFolder<IList<E>>> implements IFlexContentComponent {

    private static final Logger log = LoggerFactory.getLogger(CEntityFolder.class);

    private FolderDecorator<E> folderDecorator;

    private final FlowPanel content;

    protected int currentRowDebugId = 0;

    public CEntityFolder() {
        content = new FlowPanel();
    }

    @Override
    public FolderDecorator<E> createContent() {
        return createFolderDecorator();
    }

    protected abstract CEntityFolderItem<E> createItem();

    protected abstract FolderDecorator<E> createFolderDecorator();

    public void setFolderDecorator(FolderDecorator<E> folderDecorator) {
        this.folderDecorator = folderDecorator;

        addValueChangeHandler(folderDecorator);

        asWidget().setWidget(folderDecorator);

        folderDecorator.setFolder(this);

        folderDecorator.addItemAddClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addItem();
            }
        });
        //TODO use components inheritance
        if (this.getDebugId() != null) {
            folderDecorator.asWidget().ensureDebugId(this.getDebugId().getDebugIdString() + "_fd_");
        }
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        super.setDebugId(debugId);
        if ((debugId != null) && (folderDecorator != null)) {
            folderDecorator.asWidget().ensureDebugId(this.getDebugId().getDebugIdString() + "_fd_");
        }
    }

    protected void addItem() {
        if (getValue() == null) {
            log.warn("Request to add item has been issued before the form populated with value");
            return;
        }

        final CEntityFolderItem<E> comp = createItem();
        comp.setRowDebugId(++currentRowDebugId);

        @SuppressWarnings("unchecked")
        E newEntity = (E) EntityFactory.create(comp.proto().getValueClass());

        createNewEntity(newEntity, new DefaultAsyncCallback<E>() {

            @Override
            public void onSuccess(E result) {
                comp.setFirst(content.getWidgetCount() == 0);
                getValue().add(result);
                comp.getContent().setWidget(comp.createContent());
                comp.populate(result);
                adoptFolderItem(comp);
            }

        });

    }

    //TODO add remove handlers
    protected void removeItem(CEntityFolderItem<E> comp, FolderItemDecorator folderItemDecorator) {
        getValue().remove(comp.getValue());
        abandonFolderItem(comp);
    }

    /**
     * Implementation to override new Entity creation. No need to call
     * super.createNewEntity().
     * 
     * @param newEntity
     * @param callback
     */
    protected void createNewEntity(E newEntity, AsyncCallback<E> callback) {
        callback.onSuccess(newEntity);
    }

    @Override
    public void populate(IList<E> value) {
        super.populate(value);

        //TODO reuse existing  CEntityEditableComponent.  See  CEntityFormFolder

        content.clear();
        for (E item : value) {
            CEntityFolderItem<E> comp = createItem();
            comp.setRowDebugId(++currentRowDebugId);
            comp.setFirst(content.getWidgetCount() == 0);
            comp.getContent().setWidget(comp.createContent());
            comp.populate(item);
            adoptFolderItem(comp);
        }
    }

    private void abandonFolderItem(final CEntityFolderItem<E> comp) {
        content.remove(comp);
        ValueChangeEvent.fire(this, getValue());
    }

    private void adoptFolderItem(final CEntityFolderItem<E> comp) {

        final FolderItemDecorator folderItemDecorator = comp.createFolderItemDecorator();

        comp.setFolderItemDecorator(folderItemDecorator);

        content.add(comp);
        ValueChangeEvent.fire(this, getValue());

        folderItemDecorator.addItemRemoveClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                removeItem(comp, folderItemDecorator);
            }
        });

    }

    @Override
    protected NativeEntityFolder<IList<E>> createWidget() {
        return new NativeEntityFolder<IList<E>>();
    }

    public ValidationResults getValidationResults() {
        // TODO Implement propagation of validation
        return null;
    }

    @Override
    public void setVisited(boolean visited) {
        super.setVisited(visited);
    }

    public FlowPanel getContent() {
        return content;
    }

}
