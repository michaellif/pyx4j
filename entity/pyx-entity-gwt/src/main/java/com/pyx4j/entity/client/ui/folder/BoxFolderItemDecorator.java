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
 * Created on Feb 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.folder;

import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderBoxItem;
import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderBoxItemDecorator;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.i18n.shared.I18n;

public class BoxFolderItemDecorator<E extends IEntity> extends BaseFolderItemDecorator<E> {

    private static final I18n i18n = I18n.get(BoxFolderItemDecorator.class);

    public static enum DebugIds implements IDebugId {
        BoxFolderItemDecorator, ToolBar;

        @Override
        public String debugId() {
            return name();
        }
    }

    private boolean expended = true;

    private boolean collapsible = true;

    private BoxFolderItemToolbar toolbar;

    private SimplePanel contentHolder;

    private ItemActionsBar actionsPanel;

    private IDebugId parentDebugId;

    public BoxFolderItemDecorator(EntityFolderImages images) {
        this(images, "Remove");
    }

    //TODO propagate removeLabel
    public BoxFolderItemDecorator(EntityFolderImages images, String removeLabel) {
        super(images);

        setStyleName(EntityFolderBoxItemDecorator.name());

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.setWidth("100%");
        setWidget(mainPanel);

        toolbar = new BoxFolderItemToolbar(this);
        mainPanel.add(toolbar);

        contentHolder = new SimplePanel();
        contentHolder.setStyleName(EntityFolderBoxItem.name());

        mainPanel.add(contentHolder);

    }

    @Override
    public void setComponent(final CEntityFolderItem<E> folderItem) {
        super.setComponent(folderItem);
        contentHolder.setWidget(folderItem.createContent());
        toolbar.setTitleIcon(folderItem.getIcon());
        folderItem.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyName.repopulated) {
                    toolbar.update(expended);
                } else if (event.getPropertyName() == PropertyName.valid) {
                    if (folderItem.isValid()) {
                        toolbar.setWarningMessage(null);
                    } else {
                        toolbar.setWarningMessage(i18n.tr("Some fields are not valid or mandatory but not set."));
                    }
                }
            }
        });
        folderItem.addValueChangeHandler(new ValueChangeHandler<E>() {
            @Override
            public void onValueChange(ValueChangeEvent<E> event) {
                toolbar.update(expended);
            }
        });
    }

    public void setExpended(boolean expended) {
        this.expended = expended;
        contentHolder.setVisible(expended);
        toolbar.update(expended);
    }

    public void setCollapsible(boolean collapsible) {
        this.collapsible = collapsible;
        toolbar.setCollapseButtonVisible(collapsible);
        if (collapsible == false) {
            setExpended(true);
        }
    }

    public boolean isCollapsible() {
        return collapsible;
    }

    public boolean isExpended() {
        return expended;
    }

    @Override
    public void setActionsState(boolean removable, boolean up, boolean down) {
        actionsPanel.setActionsState(removable, up, down);
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        this.parentDebugId = parentDebugId;
        toolbar.ensureDebugId(new CompositeDebugId(parentDebugId, new CompositeDebugId(DebugIds.BoxFolderItemDecorator, DebugIds.ToolBar)).debugId());
    }

    @Override
    public void setItemActionsBar(final ItemActionsBar actionsPanel) {
        this.actionsPanel = actionsPanel;

        actionsPanel.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        if (BrowserType.isIE7()) {
            actionsPanel.getElement().getStyle().setMarginRight(40, Unit.PX);
        }
        actionsPanel.ensureDebugId(new CompositeDebugId(parentDebugId, new CompositeDebugId(IFolderDecorator.DecoratorsIds.BoxFolderItemToolbar,
                IFolderDecorator.DecoratorsIds.ActionPanel)).debugId());

        addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                actionsPanel.setHover(true);
            }
        }, MouseOverEvent.getType());

        addDomHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                actionsPanel.setHover(false);
            }
        }, MouseOutEvent.getType());

        toolbar.setActionsBar(actionsPanel);
    }
}
