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

import static com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderRowItemDecorator;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CComponentTheme;
import com.pyx4j.forms.client.ui.decorators.DecoratorDebugIds;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.forms.client.validators.ValidationResults;

public class TableFolderItemDecorator<E extends IEntity> extends BaseFolderItemDecorator<E> {

    public static enum DebugIds implements IDebugId {
        TableFolderItemDecorator, ToolBar;

        @Override
        public String debugId() {
            return name();
        }
    }

    private final HTML validationMessageHolder;

    private SimplePanel actionsPanelHolder;

    private SimplePanel contentHolder;

    private IDebugId parentDebugId;

    public TableFolderItemDecorator(EntityFolderImages images) {
        this(images, null);
    }

    //TODO propagate removeLabel to tooltip
    public TableFolderItemDecorator(EntityFolderImages images, String removeLabel) {
        super(images);

        setStyleName(EntityFolderRowItemDecorator.name());
        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        DockPanel mainPanel = new DockPanel();
        setWidget(mainPanel);

        validationMessageHolder = new HTML();
        validationMessageHolder.setStyleName(CComponentTheme.StyleName.ValidationLabel.name());
        mainPanel.add(validationMessageHolder, DockPanel.SOUTH);

        actionsPanelHolder = new SimplePanel();
        mainPanel.add(actionsPanelHolder, DockPanel.EAST);

        contentHolder = new SimplePanel();
        mainPanel.add(contentHolder, DockPanel.CENTER);

    }

    @Override
    public void setComponent(final CEntityFolderItem<E> folderItem) {
        super.setComponent(folderItem);
        contentHolder.setWidget(getContent());
        folderItem.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid, PropertyName.visited, PropertyName.showErrorsUnconditional, PropertyName.repopulated)) {
                    if (folderItem.isUnconditionalValidationErrorRendering()) {
                        validationMessageHolder.setHTML(folderItem.getValidationResults().getValidationMessage(true, true, false));
                    } else {
                        ArrayList<ValidationError> errors = folderItem.getValidationResults().getValidationErrors();
                        ValidationResults results = new ValidationResults();
                        for (ValidationError validationError : errors) {
                            CComponent<?> component = validationError.getOriginator();
                            if ((component.isUnconditionalValidationErrorRendering() || component.isVisited()) && !component.isValid()) {
                                results.appendValidationError(validationError);
                            }
                        }
                        validationMessageHolder.setHTML(results.getValidationMessage(true, true, false));
                    }
                }
            }

        });
    }

    @Override
    public void setActionsState(boolean removable, boolean up, boolean down) {
        ItemActionsBar actionsPanel = getFolderItem().getItemActionsBar();
        if (actionsPanel != null) {
            actionsPanel.setDefaultActionsState(removable, up, down);
        }
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        this.parentDebugId = parentDebugId;
        validationMessageHolder.ensureDebugId(new CompositeDebugId(parentDebugId, new CompositeDebugId(DecoratorDebugIds.TableFolderItemDecorator,
                DecoratorDebugIds.Label)).debugId());
    }

    @Override
    public void adoptItemActionsBar() {
        ItemActionsBar actionsBar = getFolderItem().getItemActionsBar();
        actionsPanelHolder.setWidget(actionsBar);
        actionsBar.ensureDebugId(new CompositeDebugId(parentDebugId, new CompositeDebugId(DecoratorDebugIds.TableFolderItemDecorator,
                DecoratorDebugIds.ActionPanel)).debugId());
        addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                getFolderItem().getItemActionsBar().setHover(true);
            }
        }, MouseOverEvent.getType());

        addDomHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                getFolderItem().getItemActionsBar().setHover(false);
            }
        }, MouseOutEvent.getType());
    }

}
