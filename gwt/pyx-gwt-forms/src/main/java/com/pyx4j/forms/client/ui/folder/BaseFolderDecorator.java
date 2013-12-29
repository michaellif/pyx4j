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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.folder;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CComponentTheme;
import com.pyx4j.forms.client.ui.FormNavigationDebugId;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.gwt.commons.HandlerRegistrationGC;
import com.pyx4j.widgets.client.Button;

public abstract class BaseFolderDecorator<E extends IEntity> extends FlowPanel implements IFolderDecorator<E> {

    private final SimplePanel container;

    private Button addButton = null;

    private boolean addable;

    private final HTML validationMessageHolder;

    public BaseFolderDecorator(EntityFolderImages images, String title, boolean addable) {
        this.addable = addable;
        addButton = new Button(images.addButton().regular(), title);
        addButton.setStyleName(DefaultEntityFolderTheme.StyleName.EntityFolderAddButton.name());

        validationMessageHolder = new HTML();
        validationMessageHolder.setStyleName(CComponentTheme.StyleName.ValidationLabel.name());

        container = new SimplePanel();
        container.setStyleName(DefaultEntityFolderTheme.StyleName.EntityFolderContent.name());

    }

    protected SimplePanel getContainer() {
        return container;
    }

    protected Button getAddButton() {
        return addButton;
    }

    protected HTML getValidationMessageHolder() {
        return validationMessageHolder;
    }

    protected boolean isAddable() {
        return addable;
    }

    public void setAddable(boolean addable) {
        this.addable = addable;
    }

    @Override
    public HandlerRegistration addItemAddClickHandler(ClickHandler handler) {
        if (isAddable()) {
            HandlerRegistrationGC h = new HandlerRegistrationGC();
            h.add(addButton.addClickHandler(handler));
            return h;
        }
        return null;
    }

    @Override
    public void setComponent(final CEntityFolder<E> folder) {
        container.setWidget(folder.createContent());

        folder.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyName.debugId) {
                    onSetDebugId(folder.getDebugId());
                }
                if (event.isEventOfType(PropertyName.valid, PropertyName.showErrorsUnconditional, PropertyName.repopulated)) {
                    if (folder.isUnconditionalValidationErrorRendering()) {
                        ValidationResults validationResults = folder.getValidationResults();
                        ValidationResults results = validationResults.getValidationResultsByOriginator(folder);
                        validationMessageHolder.setHTML(results.getValidationMessage(true, true, false));
                    } else {
                        validationMessageHolder.setHTML("");
                    }
                }
            }
        });

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
