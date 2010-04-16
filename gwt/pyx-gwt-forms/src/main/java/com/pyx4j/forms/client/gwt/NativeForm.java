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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.gwt;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.INativeComponent;
import com.pyx4j.forms.client.ui.CForm.InfoImageAlignment;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.widgets.client.Tooltip;

public class NativeForm extends FlexTable implements INativeComponent {

    private static final Logger log = LoggerFactory.getLogger(NativeForm.class);

    private final CForm form;

    private CComponent<?>[][] components;

    private final int[][][] spans;

    private final Label[][] labels;

    private int columnCount = 1;

    private final LabelAlignment allignment;

    private final InfoImageAlignment infoImageAlignment;

    public NativeForm(final CForm form, CComponent<?>[][] comp, LabelAlignment allignment, InfoImageAlignment infoImageAlignment) {
        super();
        setBorderWidth(0);
        setCellSpacing(0);
        this.form = form;
        components = comp;
        this.allignment = allignment;
        this.infoImageAlignment = infoImageAlignment;

        spans = new int[components.length][components[0].length][2];
        labels = new Label[components.length][components[0].length];
        columnCount = components[0].length;
        preprocess();
        addAllComponents();

        setWidth(form.getWidth());
        setHeight(form.getHeight());

        //TODO
        //        if (ClientState.isDevMode()) {
        //            sinkEvents(Event.ONMOUSEOVER);
        //        }
    }

    private void addAllComponents() {
        for (int i = 0; i < components.length; i++) {
            for (int j = 0; j < components[i].length; j++) {
                CComponent<?> component = components[i][j];
                if (component != null) {
                    addComponent(component, i, j);
                }
            }
        }

    }

    private void addComponent(final CComponent<?> component, int row, int column) {
        final Label label = new Label(component.getTitle() == null ? "" : component.getTitle() + ":");
        Cursor.setDefault(label.getElement());
        labels[row][column] = label;

        int labelRow = 0;
        int labelColumn = 0;
        int widgetRow = 0;
        int widgetColumn = 0;
        if (allignment.equals(LabelAlignment.LEFT)) {
            labelRow = row;
            labelColumn = 2 * column;
            widgetRow = row;
            widgetColumn = 2 * column + 1;
        } else {
            labelRow = 2 * row;
            labelColumn = column;
            widgetRow = 2 * row + 1;
            widgetColumn = column;
        }

        setWidget(labelRow, labelColumn, label);
        final Widget nativeComponent = (Widget) component.initNativeComponent();
        if (nativeComponent == null) {
            throw new RuntimeException("initNativeComponent() method call on " + component.getName() + "[" + component.getClass() + "] returns null.");
        }
        if (nativeComponent instanceof NativeCheckBox) {
            ((NativeCheckBox) nativeComponent).setText(null);
        }

        // TODO move ensureDebugId GWT call to proper place e.g. NativeComponent creation
        if (component.getComponentDebugID() != null) {
            nativeComponent.ensureDebugId(component.getComponentDebugID());
        }

        if (nativeComponent instanceof Focusable) {
            label.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    ((Focusable) nativeComponent).setFocus(true);
                }
            });
        }

        final HorizontalPanel widgetContainer = new HorizontalPanel();

        final Image imageInfoWarn = new Image();
        log.trace("tooltip.bundle.applyTo");
        imageInfoWarn.setResource(ImageFactory.getImages().formTooltipEmpty());
        imageInfoWarn.getElement().getStyle().setMarginRight(10, Unit.PX);
        imageInfoWarn.getElement().getStyle().setMarginLeft(2, Unit.PX);

        log.trace("cr.tooltip");
        final Tooltip tooltip = Tooltip.tooltip(imageInfoWarn, "");

        renderToolTip(tooltip, component, imageInfoWarn);
        label.setVisible(component.isVisible());
        widgetContainer.setVisible(component.isVisible());

        component.addPropertyChangeHandler(new PropertyChangeHandler() {
            public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                CComponent<?> source = (CComponent<?>) propertyChangeEvent.getSource();
                if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.VISIBILITY_PROPERTY) {
                    label.setVisible(source.isVisible());
                    widgetContainer.setVisible(source.isVisible());
                } else if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.TITLE_PROPERTY) {
                    label.setText(component.getTitle() + ":");
                }
                renderToolTip(tooltip, source, imageInfoWarn);
            }
        });

        if (InfoImageAlignment.BEFORE.equals(infoImageAlignment)) {
            widgetContainer.add(imageInfoWarn);
            widgetContainer.add(nativeComponent);
            //            DOM.setStyleAttribute(nativeComponent.getElement(), "marginRight", "20px");
            //            widgetContainer.setCellWidth(imageInfoWarn, "20px");
        } else if (InfoImageAlignment.AFTER.equals(infoImageAlignment)) {
            widgetContainer.add(nativeComponent);
            widgetContainer.add(imageInfoWarn);
            //            widgetContainer.setCellWidth(imageInfoWarn, "20px");
        } else {
            widgetContainer.add(nativeComponent);
        }

        widgetContainer.setCellVerticalAlignment(imageInfoWarn, HasVerticalAlignment.ALIGN_MIDDLE);
        widgetContainer.setCellHorizontalAlignment(imageInfoWarn, HasHorizontalAlignment.ALIGN_LEFT);
        widgetContainer.getElement().getStyle().setPadding(2, Unit.PX);

        setWidget(widgetRow, widgetColumn, widgetContainer);

        FlexCellFormatter cellFormatter = getFlexCellFormatter();

        int rowSpan = spans[row][column][0];
        int columnSpan = spans[row][column][1];

        if (rowSpan > 1) {
            if (allignment.equals(LabelAlignment.LEFT)) {
                cellFormatter.setRowSpan(labelRow, labelColumn, rowSpan);
                cellFormatter.setRowSpan(widgetRow, widgetColumn, rowSpan);
            } else {
                cellFormatter.setRowSpan(widgetRow, widgetColumn, 2 * rowSpan - 1);
            }
        }

        if (columnSpan > 1) {
            if (allignment.equals(LabelAlignment.LEFT)) {
                cellFormatter.setColSpan(widgetRow, widgetColumn, 2 * columnSpan - 1);
            } else {
                cellFormatter.setColSpan(labelRow, labelRow, columnSpan);
                cellFormatter.setColSpan(widgetRow, widgetColumn, columnSpan);
            }
        }

        if (allignment.equals(LabelAlignment.LEFT)) {
            cellFormatter.setVerticalAlignment(labelRow, labelColumn, HasVerticalAlignment.ALIGN_MIDDLE);
            cellFormatter.setVerticalAlignment(widgetRow, widgetColumn, HasVerticalAlignment.ALIGN_MIDDLE);
        } else {
            cellFormatter.setVerticalAlignment(labelRow, labelColumn, HasVerticalAlignment.ALIGN_TOP);
            cellFormatter.setVerticalAlignment(widgetRow, widgetColumn, HasVerticalAlignment.ALIGN_TOP);
        }

        if (allignment.equals(LabelAlignment.LEFT)) {
            cellFormatter.setWidth(labelRow, labelColumn, Math.round((double) 2 / 5 * 100 / columnCount) + "%");
            cellFormatter.setWidth(widgetRow, widgetColumn, Math.round((double) 3 / 5 * 100 / columnCount + (double) (columnSpan - 1) * 100 / columnCount)
                    + "%");
        }

        cellFormatter.setWordWrap(labelRow, labelColumn, false);

    }

    private void preprocess() {
        ArrayList<CComponent<?>> handledComponents = new ArrayList<CComponent<?>>();
        CComponent<?>[][] componentMatrix = new CComponent[components.length][components[0].length];
        for (int i = 0; i < components.length; i++) {
            int jj = 0;
            for (int j = 0; j < components[i].length; j++) {
                CComponent<?> component = components[i][j];
                if (handledComponents.contains(component)) {
                    continue;
                } else if (component == null) {
                    spans[i][jj][0] = 1;
                    spans[i][jj][1] = 1;
                } else {
                    handledComponents.add(component);
                    spans[i][jj][0] = calcRowSpan(i, j);
                    spans[i][jj][1] = calcColumnSpan(i, j);
                }
                componentMatrix[i][jj] = component;
                jj++;
            }
        }
        components = componentMatrix;
    }

    private int calcRowSpan(int i, int j) {
        int span = 1;
        if (i == components.length - 1) {
            return span;
        }
        for (int k = i + 1; k < components.length; k++) {
            if (components[k][j] == components[i][j]) {
                span++;
            } else {
                break;
            }
        }
        return span;
    }

    private int calcColumnSpan(int i, int j) {
        int span = 1;
        if (j == components[0].length - 1) {
            return span;
        }
        for (int k = j + 1; k < components[0].length; k++) {
            if (components[i][k] == components[i][j]) {
                span++;
            } else {
                break;
            }
        }
        return span;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (CComponent<?>[] component : components) {
            for (int j = 0; j < component.length; j++) {
                buffer.append(component[j]).append("       ");
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

    private void renderToolTip(Tooltip tooltip, CComponent<?> source, Image image) {
        log.trace("renderToolTip");
        if (!InfoImageAlignment.HIDDEN.equals(infoImageAlignment)) {
            tooltip.setTooltipText(source.getToolTip());
            if (source.getToolTip() == null || source.getToolTip().trim().length() == 0) {
                Cursor.setDefault(image.getElement());
                image.setResource(ImageFactory.getImages().formTooltipEmpty());
            } else {
                Cursor.setHand(image.getElement());
                if (source instanceof CEditableComponent<?> && !((CEditableComponent<?>) source).isValid()) {
                    image.setResource(ImageFactory.getImages().formTooltipWarn());
                } else {
                    image.setResource(ImageFactory.getImages().formTooltipInfo());
                }
            }
        }
    }

    public void setEnabled(boolean enabled) {
    }

    public CForm getCComponent() {
        return form;
    }

    public boolean isEnabled() {
        return true;
    }

    /**
     * Show toString of CComponent in tooltip while mouse-over component and Shift pressed
     */
    @Override
    public void onBrowserEvent(Event event) {
        //TODO
        //        if (ClientState.isDevMode()) {
        //            CComponent<?> component = findItem(DOM.eventGetTarget(event));
        //            if (event.getShiftKey() && component != null) {
        //                log.debug(component.toString());
        //            }
        //        }
        super.onBrowserEvent(event);
    }

    private CComponent<?> findItem(Element hItem) {
        for (int i = 0; i < components.length; i++) {
            for (int j = 0; j < components[i].length; j++) {
                CComponent<?> component = components[i][j];
                if (component != null && component.getNativeComponent() != null) {
                    Element element = ((Widget) component.getNativeComponent()).getElement();
                    if (DOM.isOrHasChild(element, hItem)) {
                        return component;
                    }
                }
            }
        }
        return null;
    }

}