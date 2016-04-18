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
 */
package com.pyx4j.forms.client.ui.folder;

import static com.pyx4j.forms.client.ui.folder.FolderTheme.StyleName.CFolderBoxItem;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.decorators.DecoratorDebugIds;
import com.pyx4j.forms.client.ui.decorators.EntityContainerDecoratorToolbar;
import com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.SimplePanel;
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

    private EntityContainerDecoratorToolbar<E> toolbar;

    private SimplePanel contentHolder;

    private IDebugId parentDebugId;

    public BoxFolderItemDecorator(FolderImages images) {
        this(images, "Remove");
    }

    //TODO propagate removeLabel
    public BoxFolderItemDecorator(FolderImages images, String removeLabel) {
        super(images);

        setStyleName(WidgetDecoratorTheme.StyleName.EntityContainerDecorator.name());

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

        toolbar = new EntityContainerDecoratorToolbar<>(this.getImages());
        toolbar.addCaptionHolderClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setExpended(!isExpended());
            }
        });
        toolbar.update(isExpended());

        mainPanel.add(toolbar);

        contentHolder = new SimplePanel();
        contentHolder.setStyleName(CFolderBoxItem.name());

        mainPanel.add(contentHolder);

    }

    @Override
    public void setContent(IsWidget content) {
        contentHolder.setWidget(content);
    }

    @Override
    public void init(final CFolderItem<E> folderItem) {
        super.init(folderItem);

        toolbar.setEntityForm(folderItem.getEntityForm());
        toolbar.update(collapsablePanel.isExpended());
        toolbar.setWarningMessage(folderItem.getValidationResults().getValidationShortMessage());

        folderItem.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.repopulated)) {
                    toolbar.update(collapsablePanel.isExpended());
                }
                if (event.isEventOfType(PropertyName.valid, PropertyName.repopulated, PropertyName.visited)) {
                    toolbar.setWarningMessage(folderItem.getValidationResults().getValidationShortMessage());
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
        actionsBar.ensureDebugId(
                new CompositeDebugId(parentDebugId, new CompositeDebugId(DecoratorDebugIds.BoxFolderItemToolbar, DecoratorDebugIds.ActionPanel)).debugId());

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

    public void setCaptionFormatter(IFormatter<E, SafeHtml> formatter) {
        toolbar.setCaptionFormatter(formatter);
    }
}
