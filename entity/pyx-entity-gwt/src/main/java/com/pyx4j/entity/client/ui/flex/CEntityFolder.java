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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.gwt.commons.UnrecoverableClientError;

/**
 * This component represents list of IEntities
 */
public abstract class CEntityFolder<E extends IEntity> extends CEditableComponent<IList<E>, NativeEntityFolder<IList<E>>> {

    private FolderDecorator folderDecorator;

    private FlowPanel content;

    public CEntityFolder() {
    }

    public void createContent() {
        setFolderDecorator(createFolderDecorator());
    }

    protected abstract CEntityFolderItem<E> createItem();

    protected abstract FolderDecorator createFolderDecorator();

    public void setFolderDecorator(FolderDecorator folderDecorator) {
        this.folderDecorator = folderDecorator;
        asWidget().setWidget(folderDecorator);

        content = new FlowPanel();
        folderDecorator.setWidget(content);

        folderDecorator.addRowAddClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addRow();
            }
        });
    }

    protected void addRow() {
        final CEntityFolderItem<E> comp = createItem();

        @SuppressWarnings("unchecked")
        E newEntity = (E) EntityFactory.create(comp.proto().getValueClass());

        createNewEntity(newEntity, new AsyncCallback<E>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(E result) {
                getValue().add(result);
                comp.createContent();
                comp.populate(result);
                adoptFolderItem(comp);
            }

        });

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
            comp.createContent();
            comp.populate(item);
            adoptFolderItem(comp);
        }
    }

    private void adoptFolderItem(final CEntityFolderItem<E> comp) {
        final FolderItemDecorator folderItemDecorator = comp.createFolderItemDecorator();
        folderItemDecorator.setWidget(comp);
        content.add(folderItemDecorator);
        folderItemDecorator.addRowRemoveClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                removeRow(comp, folderItemDecorator);
            }
        });
    }

    //TODO add remove handlers
    protected void removeRow(CEntityFolderItem<E> comp, FolderItemDecorator folderItemDecorator) {
        content.remove(folderItemDecorator);
    }

    @Override
    protected NativeEntityFolder<IList<E>> initWidget() {
        return new NativeEntityFolder<IList<E>>();
    }

    public void setWidget(Widget widget) {
        folderDecorator.setWidget(widget);
    }

}
