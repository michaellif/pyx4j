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
package com.pyx4j.forms.client.ui.folder;

import static com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderBoxItem;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.DecoratorDebugIds;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.decorators.EntityContainerDecoratorToolbar;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.widgets.client.CollapsablePanel;
import com.pyx4j.widgets.client.event.shared.ToggleEvent;
import com.pyx4j.widgets.client.event.shared.ToggleHandler;

public class BoxFolderItemDecorator<E extends IEntity> extends BaseFolderItemDecorator<E> {

    public static enum DebugIds implements IDebugId {
        BoxFolderItemDecorator, ToolBar;

        @Override
        public String debugId() {
            return name();
        }
    }

    private CollapsablePanel collapsablePanel;

    private EntityContainerDecoratorToolbar toolbar;

    private SimplePanel contentHolder;

    private IDebugId parentDebugId;

    public BoxFolderItemDecorator(EntityFolderImages images) {
        this(images, "Remove");
    }

    //TODO propagate removeLabel
    public BoxFolderItemDecorator(EntityFolderImages images, String removeLabel) {
        super(images);

        setStyleName(DefaultWidgetDecoratorTheme.StyleName.EntityContainerDecorator.name());

        collapsablePanel = new CollapsablePanel(images);
        setWidget(collapsablePanel);

        collapsablePanel.addToggleHandler(new ToggleHandler() {

            @Override
            public void onToggle(ToggleEvent event) {
                contentHolder.setVisible(event.isToggleOn());
                toolbar.update(event.isToggleOn());
            }
        });

        FlowPanel mainPanel = new FlowPanel();
        mainPanel.setWidth("100%");
        collapsablePanel.setWidget(mainPanel);

        ensureDebugId(new CompositeDebugId(DecoratorDebugIds.BoxFolderItemToolbar, EntityContainerDecoratorToolbar.DebugIds.Decorator).debugId());

        toolbar = new EntityContainerDecoratorToolbar(this.getImages());
        toolbar.addCaptionHolderClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setExpended(!isExpended());
            }
        });
        toolbar.update(isExpended());

        mainPanel.add(toolbar);

        contentHolder = new SimplePanel();
        contentHolder.setStyleName(EntityFolderBoxItem.name());

        mainPanel.add(contentHolder);

    }

    @Override
    public void setComponent(final CEntityFolderItem<E> folderItem) {
        super.setComponent(folderItem);
        contentHolder.setWidget(getContent());
        toolbar.setEntityContainer(folderItem);

        folderItem.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.repopulated)) {
                    toolbar.update(collapsablePanel.isExpended());
                }
                if (event.isEventOfType(PropertyName.valid, PropertyName.repopulated, PropertyName.visited)) {
                    String message = null;
                    message = folderItem.getValidationResults().getValidationShortMessage();
                    toolbar.setWarningMessage(message.isEmpty() ? null : message);
                }
            }
        });
        folderItem.addValueChangeHandler(new ValueChangeHandler<E>() {
            @Override
            public void onValueChange(ValueChangeEvent<E> event) {
                toolbar.update(collapsablePanel.isExpended());
            }
        });
    }

    public void setExpended(boolean expended) {
        collapsablePanel.setExpended(expended);
    }

    public void setCollapsible(boolean collapsible) {
        collapsablePanel.setCollapsible(collapsible);
    }

    public boolean isCollapsible() {
        return collapsablePanel.isCollapsible();
    }

    public boolean isExpended() {
        return collapsablePanel.isExpended();
    }

    @Override
    public void setActionsState(boolean removable, boolean up, boolean down) {
        getFolderItem().getItemActionsBar().setDefaultActionsState(removable, up, down);
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        this.parentDebugId = parentDebugId;
        toolbar.ensureDebugId(new CompositeDebugId(parentDebugId, new CompositeDebugId(DebugIds.BoxFolderItemDecorator, DebugIds.ToolBar)).debugId());
    }

    @Override
    public void adoptItemActionsBar() {
        final ItemActionsBar actionsBar = getFolderItem().getItemActionsBar();
        actionsBar.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        if (BrowserType.isIE7()) {
            actionsBar.getElement().getStyle().setMarginRight(40, Unit.PX);
        }
        actionsBar.ensureDebugId(new CompositeDebugId(parentDebugId,
                new CompositeDebugId(DecoratorDebugIds.BoxFolderItemToolbar, DecoratorDebugIds.ActionPanel)).debugId());

        addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                actionsBar.setHover(true);
            }
        }, MouseOverEvent.getType());

        addDomHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                actionsBar.setHover(false);
            }
        }, MouseOutEvent.getType());

        toolbar.setActionsBar(actionsBar);
    }
}
