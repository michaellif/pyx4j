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
 * Created on Feb 12, 2011
 * @author Misha
 */
package com.pyx4j.forms.client.ui.folder;

import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecorator;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecoratorContent;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.decorators.IFieldDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme;

public class CFolderRowEditor<E extends IEntity> extends CForm<E> {

    protected final List<FolderColumnDescriptor> columns;

    public CFolderRowEditor(Class<E> clazz, List<FolderColumnDescriptor> columns) {
        this(clazz, columns, null);
    }

    public CFolderRowEditor(Class<E> clazz, List<FolderColumnDescriptor> columns, IEditableComponentFactory factory) {
        super(clazz, factory);
        this.columns = columns;
    }

    @Override
    protected IsWidget createContent() {
        HorizontalPanel main = new HorizontalPanel();
        for (FolderColumnDescriptor column : columns) {
            CField<?, ?> component = createCell(column);
            if (column.isReadOnly()) {
                component.setViewable(true);
            }
            component.asWidget().setWidth("100%");
            component.setDecorator(new CellDecorator(column.getWidth()));
            main.add(component.getDecorator());
        }
        return main;
    }

    protected CField<?, ?> createCell(FolderColumnDescriptor column) {
        return inject(column.getObject());
    }

    class CellDecorator extends SimplePanel implements IFieldDecorator {

        private CField<?, ?> component;

        protected CellDecorator(String width) {

            getElement().getStyle().setPaddingLeft(3, Unit.PX);
            getElement().getStyle().setPaddingRight(3, Unit.PX);
            setWidth(width);
            setWidget(component);

            setStyleName(WidgetDecorator.name());

        }

        @Override
        public void setContent(IsWidget content) {
            setWidget(content);
        }

        @Override
        public void init(final CField<?, ?> component) {
            this.component = component;
            final Widget nativeComponent = component.asWidget();
            nativeComponent.addStyleName(WidgetDecoratorContent.name());

            component.addPropertyChangeHandler(new PropertyChangeHandler() {
                @Override
                public void onPropertyChange(PropertyChangeEvent event) {
                    if (event.isEventOfType(PropertyName.valid, PropertyName.visited, PropertyName.repopulated, PropertyName.enabled, PropertyName.editable)) {
                        if (!component.isValid()) {
                            component.asWidget().addStyleDependentName(WidgetDecoratorTheme.StyleDependent.invalid.name());
                        } else {
                            component.asWidget().removeStyleDependentName(WidgetDecoratorTheme.StyleDependent.invalid.name());
                        }
                    }
                }
            });

        }

        public CField<?, ?> getComnponent() {
            return component;
        }

        @Override
        public void onSetDebugId(IDebugId parentDebugId) {
            // TODO Auto-generated method stub

        }

    }
}
