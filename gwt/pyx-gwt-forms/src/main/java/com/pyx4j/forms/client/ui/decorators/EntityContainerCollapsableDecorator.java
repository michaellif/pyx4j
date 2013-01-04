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
 * Created on May 26, 2010
 * @author stanp
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.decorators;

import static com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderBoxItem;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityContainer;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.widgets.client.CollapsablePanel;
import com.pyx4j.widgets.client.event.shared.ToggleEvent;
import com.pyx4j.widgets.client.event.shared.ToggleHandler;
import com.pyx4j.widgets.client.images.WidgetsImages;

public class EntityContainerCollapsableDecorator<E extends IEntity> extends CollapsablePanel implements IDecorator<CEntityContainer<E>> {

    private final EntityContainerDecoratorToolbar toolbar;

    private final SimplePanel contentHolder;

    public EntityContainerCollapsableDecorator(WidgetsImages images) {
        super(images);
        addToggleHandler(new ToggleHandler() {

            @Override
            public void onToggle(ToggleEvent event) {
                contentHolder.setVisible(event.isToggleOn());
                toolbar.update(event.isToggleOn());
            }
        });

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.setWidth("100%");
        setWidget(mainPanel);

        toolbar = new EntityContainerDecoratorToolbar(images);
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
    public void setComponent(final CEntityContainer<E> viewer) {
        setWidget(viewer.createContent());
        toolbar.setEntityContainer(viewer);

        viewer.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.repopulated)) {
                    toolbar.update(isExpended());
                }
                if (event.isEventOfType(PropertyName.valid, PropertyName.repopulated, PropertyName.showErrorsUnconditional)) {
                    String message = null;
                    if (viewer.isUnconditionalValidationErrorRendering()) {
                        message = viewer.getValidationResults().getValidationShortMessage();
                    } else {
                        ArrayList<ValidationError> errors = viewer.getValidationResults().getValidationErrors();
                        ValidationResults results = new ValidationResults();
                        for (ValidationError validationError : errors) {
                            CComponent<?, ?> originator = validationError.getOriginator();
                            if ((originator.isUnconditionalValidationErrorRendering() || originator.isVisited()) && !originator.isValid()) {
                                results.appendValidationError(validationError);
                            }
                        }
                        message = results.getValidationShortMessage();
                    }
                    toolbar.setWarningMessage(message.isEmpty() ? null : message);
                }
            }
        });
        viewer.addValueChangeHandler(new ValueChangeHandler<E>() {
            @Override
            public void onValueChange(ValueChangeEvent<E> event) {
                toolbar.update(isExpended());
            }
        });
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        // TODO Auto-generated method stub
    }

}
