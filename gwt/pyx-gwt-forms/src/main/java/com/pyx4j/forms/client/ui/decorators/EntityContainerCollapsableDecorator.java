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
 */
package com.pyx4j.forms.client.ui.decorators;

import static com.pyx4j.forms.client.ui.folder.FolderTheme.StyleName.CFolderBoxItem;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.pyx4j.gwt.commons.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.widgets.client.CollapsablePanel;
import com.pyx4j.widgets.client.event.shared.ToggleEvent;
import com.pyx4j.widgets.client.event.shared.ToggleHandler;
import com.pyx4j.widgets.client.images.WidgetsImages;

public class EntityContainerCollapsableDecorator<E extends IEntity> extends CollapsablePanel implements IFormDecorator<E> {

    private final EntityContainerDecoratorToolbar<E> toolbar;

    private final SimplePanel contentHolder;

    public EntityContainerCollapsableDecorator(WidgetsImages images) {
        super(images);

        setStyleName(WidgetDecoratorTheme.StyleName.EntityContainerDecorator.name());

        addToggleHandler(new ToggleHandler() {

            @Override
            public void onToggle(ToggleEvent event) {
                contentHolder.setVisible(event.isToggleOn());
                toolbar.update(event.isToggleOn());
            }
        });

        FlowPanel mainPanel = new FlowPanel();
        mainPanel.setWidth("100%");
        setWidget(mainPanel);

        toolbar = new EntityContainerDecoratorToolbar<>(images);
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
    public void init(final CForm<E> entityContainer) {
        toolbar.setEntityForm(entityContainer);

        entityContainer.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.repopulated)) {
                    toolbar.update(isExpended());
                }
                if (event.isEventOfType(PropertyName.valid, PropertyName.repopulated, PropertyName.visited)) {
                    String message = null;
                    message = entityContainer.getValidationResults().getValidationShortMessage();
                    toolbar.setWarningMessage(message.isEmpty() ? null : message);
                }
            }
        });
        entityContainer.addValueChangeHandler(new ValueChangeHandler<E>() {
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

    @Override
    public void setContent(IsWidget content) {
        contentHolder.setWidget(content);
    }

    public void setCaptionFormatter(IFormatter<E, SafeHtml> formatter) {
        toolbar.setCaptionFormatter(formatter);
    }
}
