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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.folder;

import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecorator;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorComponent;

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
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.decorators.IDecorator;

public class CEntityFolderRowEditor<E extends IEntity> extends CEntityForm<E> {

    protected final List<EntityFolderColumnDescriptor> columns;

    public CEntityFolderRowEditor(Class<E> clazz, List<EntityFolderColumnDescriptor> columns) {
        this(clazz, columns, null);
    }

    public CEntityFolderRowEditor(Class<E> clazz, List<EntityFolderColumnDescriptor> columns, IEditableComponentFactory factory) {
        super(clazz, factory);
        this.columns = columns;
    }

    @Override
    public IsWidget createContent() {
        HorizontalPanel main = new HorizontalPanel();
        for (EntityFolderColumnDescriptor column : columns) {
            CComponent<?> component = createCell(column);
            if (column.isReadOnly()) {
                component.setViewable(true);
            }
            component.asWidget().setWidth("100%");
            component.setDecorator(new CellDecorator(column.getWidth()));
            main.add(component.getDecorator());
        }
        return main;
    }

    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
        CComponent<?> comp = inject(column.getObject());

        // Special TableFolder customization
        if (comp instanceof CCheckBox) {
            ((CCheckBox) comp).setAlignmet(CCheckBox.Alignment.center);
        }

        return comp;
    }

    class CellDecorator extends SimplePanel implements IDecorator<CComponent<?>> {

        private CComponent<?> component;

        protected CellDecorator(String width) {

            getElement().getStyle().setPaddingLeft(3, Unit.PX);
            getElement().getStyle().setPaddingRight(3, Unit.PX);
            setWidth(width);
            setWidget(component);

            setStyleName(WidgetDecorator.name());

        }

        @Override
        public void setComponent(final CComponent<?> component) {
            this.component = component;
            setWidget(component);
            final Widget nativeComponent = component.asWidget();
            nativeComponent.addStyleName(WidgetDecoratorComponent.name());

            component.addPropertyChangeHandler(new PropertyChangeHandler() {
                @Override
                public void onPropertyChange(PropertyChangeEvent event) {
                    if (event.isEventOfType(PropertyName.valid, PropertyName.visited, PropertyName.showErrorsUnconditional, PropertyName.repopulated,
                            PropertyName.enabled, PropertyName.editable)) {
                        if ((component.isUnconditionalValidationErrorRendering() || component.isVisited()) && !component.isValid()) {
                            component.asWidget().addStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.invalid.name());
                        } else {
                            component.asWidget().removeStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.invalid.name());
                        }
                    }
                }
            });

        }

        public CComponent<?> getComnponent() {
            return component;
        }

        @Override
        public void onSetDebugId(IDebugId parentDebugId) {
            // TODO Auto-generated method stub

        }

    }
}
