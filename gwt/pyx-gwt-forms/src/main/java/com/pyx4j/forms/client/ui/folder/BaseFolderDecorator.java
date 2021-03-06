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
 * Created on Mar 5, 2011
 * @author Misha
 */
package com.pyx4j.forms.client.ui.folder;

import com.google.gwt.user.client.Command;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.pyx4j.gwt.commons.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.FormNavigationDebugId;
import com.pyx4j.forms.client.ui.decorators.MessagePannel;
import com.pyx4j.widgets.client.Button;

public abstract class BaseFolderDecorator<E extends IEntity> extends FlowPanel implements IFolderDecorator<E> {

    private final SimplePanel contentPanel;

    private Button addButton = null;

    private boolean addable;

    private final MessagePannel messagePannel;

    public BaseFolderDecorator(FolderImages images, String title, boolean addable) {
        this.addable = addable;
        addButton = new Button(images.addIcon(), title);
        addButton.addStyleName(FolderTheme.StyleName.CFolderAddButton.name());

        contentPanel = new SimplePanel();
        contentPanel.setStyleName(FolderTheme.StyleName.CFolderContent.name());

        messagePannel = new MessagePannel(MessagePannel.Location.Top);
        messagePannel.setStyleName(FolderTheme.StyleName.CFolderMessagePanel.name());

    }

    protected Panel getMessagePannel() {
        return messagePannel;
    }

    protected Panel getContentPanel() {
        return contentPanel;
    }

    protected Button getAddButton() {
        return addButton;
    }

    protected boolean isAddable() {
        return addable;
    }

    public void setAddable(boolean addable) {
        this.addable = addable;
    }

    @Override
    public void setItemAddCommand(Command command) {
        if (isAddable()) {
            addButton.setCommand(command);
        }
    }

    @Override
    public void setContent(IsWidget content) {
        contentPanel.setWidget(content);
    }

    @Override
    public void init(final CFolder<E> folder) {

        messagePannel.init(folder);

        folder.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid)) {
                    messagePannel.renderValidationMessage();
                }
                if (event.isEventOfType(PropertyName.note)) {
                    messagePannel.renderNote();
                }
                if (event.getPropertyName() == PropertyName.debugId) {
                    onSetDebugId(folder.getDebugId());
                }
            }
        });

        messagePannel.renderValidationMessage();
        messagePannel.renderNote();
        onSetDebugId(folder.getDebugId());
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        if (isAddable()) {
            String baseID = parentDebugId.debugId();

            if (baseID.endsWith(IFolderDecorator.DEBUGID_SUFIX)) {
                baseID = baseID.substring(0, baseID.length() - IFolderDecorator.DEBUGID_SUFIX.length());
            }
            addButton.ensureDebugId(baseID + "-" + FormNavigationDebugId.Form_Add.debugId());
        }
    }

    @Override
    public void setAddButtonVisible(boolean visible) {
        addButton.setVisible(visible);
    }

}
