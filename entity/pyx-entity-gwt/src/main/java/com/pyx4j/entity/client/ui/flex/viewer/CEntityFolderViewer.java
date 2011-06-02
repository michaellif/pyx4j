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
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex.viewer;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.client.ui.flex.CEntityComponent;
import com.pyx4j.entity.client.ui.flex.CEntityContainer;
import com.pyx4j.entity.client.ui.flex.NativeEntityPanel;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;

/**
 * This component represents list of IEntities
 */
public abstract class CEntityFolderViewer<E extends IEntity> extends CEntityContainer<IList<E>, NativeEntityPanel<IList<E>>> {

    private static final Logger log = LoggerFactory.getLogger(CEntityFolderViewer.class);

    private IFolderViewerDecorator<E> folderDecorator;

    private final FlowPanel container;

    protected int currentRowDebugId = 0;

    private final LinkedHashMap<E, CEntityFolderItemViewer<E>> itemsMap;

    private final E entityPrototype;

    public CEntityFolderViewer(Class<E> rowClass) {
        container = new FlowPanel();
        itemsMap = new LinkedHashMap<E, CEntityFolderItemViewer<E>>();
        if (rowClass != null) {
            entityPrototype = EntityFactory.getEntityPrototype(rowClass);
        } else {
            entityPrototype = null;
        }
    }

    /**
     * This mainly use for columns creation when TableFolderDecorator is used
     */
    public E proto() {
        return entityPrototype;
    }

    @Override
    public void onBound(CEntityComponent<?, ?> parent) {
        super.onBound(parent);
        setFolderDecorator(createContent());
        addValidations();
    }

    @Override
    public IFolderViewerDecorator<E> createContent() {
        return createFolderDecorator();
    }

    protected abstract CEntityFolderItemViewer<E> createItem();

    protected abstract IFolderViewerDecorator<E> createFolderDecorator();

    public void setFolderDecorator(IFolderViewerDecorator<E> folderDecorator) {
        this.folderDecorator = folderDecorator;

        asWidget().setWidget(folderDecorator);

        folderDecorator.setFolder(this);

        //TODO use components inheritance
        if (this.getDebugId() != null) {
            folderDecorator.asWidget().ensureDebugId(this.getDebugId().debugId() + IFolderEditorDecorator.DEBUGID_SUFIX);
        }
    }

    public IFolderViewerDecorator<E> getFolderDecorator() {
        return folderDecorator;
    }

    @Override
    public void populate(IList<E> value) {
        super.populate(value);
        for (E item : value) {
            CEntityFolderItemViewer<E> comp = createItem();
            comp.onBound(this);
            comp.populate(item);
            adoptFolderItem(comp);
        }
    }

    private void adoptFolderItem(final CEntityFolderItemViewer<E> component) {

        final IFolderItemViewerDecorator folderItemDecorator = component.createFolderItemDecorator();

        component.setFolderItemDecorator(folderItemDecorator);
        component.addAccessAdapter(this);

        if (container.getWidgetIndex(component) == -1) {
            container.add(component);
        }
        itemsMap.put(component.getValue(), component);

        IDebugId rowDebugId = new CompositeDebugId(this.getDebugId(), "row", currentRowDebugId);
        currentRowDebugId++;

        component.setDebugId(rowDebugId);
        folderItemDecorator.asWidget().ensureDebugId(rowDebugId.debugId());

    }

    @Override
    public void setDebugId(IDebugId debugId) {
        super.setDebugId(debugId);
        if ((debugId != null) && (folderDecorator != null)) {
            folderDecorator.asWidget().ensureDebugId(this.getDebugId().debugId() + IFolderEditorDecorator.DEBUGID_SUFIX);
        }
    }

    @Override
    public Collection<? extends CEditableComponent<?, ?>> getComponents() {
        if (itemsMap != null) {
            return itemsMap.values();
        } else {
            return null;
        }
    }

    @Override
    public ValidationResults getValidationResults() {
        return getAllValidationResults();
    }

    protected CEntityFolderItemViewer<E> getFolderRow(E value) {
        return itemsMap.get(value);
    }

    @Override
    protected NativeEntityPanel<IList<E>> createWidget() {
        return new NativeEntityPanel<IList<E>>();
    }

    public FlowPanel getContainer() {
        return container;
    }

    @Override
    public void applyVisibilityRules() {
        super.applyVisibilityRules();
        if (getComponents() != null) {
            for (CComponent<?> component : getComponents()) {
                component.applyVisibilityRules();
            }
        }
    }

}
