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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CFormFolder;
import com.pyx4j.forms.client.ui.INativeComponent;
import com.pyx4j.forms.client.ui.CForm.InfoImageAlignment;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.widgets.client.Tooltip;

public class NativeForm extends FlexTable implements INativeComponent {

    private final int LEFT_LABEL_WIDTH = 100;

    private final int TOP_LABEL_WIDTH = 250;

    private static final Logger log = LoggerFactory.getLogger(NativeForm.class);

    private final CForm form;

    private CComponent<?>[][] components;

    private int columnCount = 1;

    private final int[][][] spans;

    private final LabelAlignment allignment;

    private final InfoImageAlignment infoImageAlignment;

    public NativeForm(final CForm form, CComponent<?>[][] comp, LabelAlignment allignment, InfoImageAlignment infoImageAlignment) {
        super();
        setBorderWidth(0);
        setCellSpacing(0);
        this.form = form;
        components = comp;
        columnCount = components[0].length;
        this.allignment = allignment;
        this.infoImageAlignment = infoImageAlignment;

        spans = new int[components.length][components[0].length][2];
        preprocess();
        addAllComponents();

        setWidth(form.getWidth());
        setHeight(form.getHeight());

        if (!GWT.isScript()) {
            sinkEvents(Event.ONMOUSEOVER);
        }
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

    private void addComponent(CComponent<?> component, int row, int column) {

        if (component instanceof CFormFolder) {
            setWidget(row, column, new FolderContainer((CFormFolder) component));
        } else {
            setWidget(row, column, new WidgetContainer(component));
        }

        FlexCellFormatter cellFormatter = getFlexCellFormatter();
        cellFormatter.setRowSpan(row, column, spans[row][column][0]);
        cellFormatter.setColSpan(row, column, spans[row][column][1]);

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
        StringBuilder builder = new StringBuilder();
        for (CComponent<?>[] componentRow : components) {
            for (CComponent<?> component : componentRow) {
                builder.append(component).append("       ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public String toStringForPrint() {
        FlexTable table = new FlexTable();
        for (int i = 0; i < components.length; i++) {
            for (int j = 0; j < components[i].length; j++) {
                int labelRow = 0;
                int labelColumn = 0;
                int widgetRow = 0;
                int widgetColumn = 0;
                if (allignment.equals(LabelAlignment.LEFT)) {
                    labelRow = i;
                    labelColumn = 2 * j;
                    widgetRow = i;
                    widgetColumn = 2 * j + 1;
                } else {
                    labelRow = 2 * i;
                    labelColumn = j;
                    widgetRow = 2 * i + 1;
                    widgetColumn = j;
                }
                if (components[i][j] == null) {
                } else if (components[i][j] instanceof CEditableComponent<?>) {
                    table.setWidget(labelRow, labelColumn, new Label(components[i][j].getTitle() + ": "));
                    table.setWidget(widgetRow, widgetColumn, new Label(CommonsStringUtils.nvl(((CEditableComponent) components[i][j]).getValue())));
                } else {

                }
                FlexCellFormatter cellFormatter = table.getFlexCellFormatter();

                int rowSpan = spans[i][j][0];
                int columnSpan = spans[i][j][1];

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
                    cellFormatter.setWidth(widgetRow, widgetColumn, Math.round((double) 3 / 5 * 100 / columnCount + (double) (columnSpan - 1) * 100
                            / columnCount)
                            + "%");
                }

                cellFormatter.setWordWrap(labelRow, labelColumn, false);

            }
        }

        return table.toString();
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
        if (!GWT.isScript()) {
            CComponent<?> component = findItem(DOM.eventGetTarget(event));
            if (event.getShiftKey() && component != null) {
                log.debug(component.toString());
            }
        }
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

    class WidgetContainer extends ComplexPanel {

        private final CComponent<?> component;

        private final Widget nativeComponent;

        private final Label label;

        private final Image imageInfoWarn;

        private final Image imageMandatory;

        private final Tooltip tooltip;

        WidgetContainer(final CComponent<?> component) {
            setElement(DOM.createDiv());

            this.component = component;
            nativeComponent = (Widget) component.initNativeComponent();
            label = new Label(component.getTitle() == null ? "" : component.getTitle() + ":");
            label.getElement().getStyle().setPosition(Position.ABSOLUTE);
            Cursor.setDefault(label.getElement());

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

            imageInfoWarn = new Image();
            imageInfoWarn.setResource(ImageFactory.getImages().formTooltipInfo());
            imageInfoWarn.getElement().getStyle().setPosition(Position.ABSOLUTE);

            imageMandatory = new Image();
            imageMandatory.setResource(ImageFactory.getImages().mandatory());
            imageMandatory.getElement().getStyle().setPosition(Position.ABSOLUTE);

            tooltip = Tooltip.tooltip(imageInfoWarn, "");

            Tooltip.tooltip(imageMandatory, "This field is mandatory");

            renderToolTip();
            renderMandatoryStar();

            label.setVisible(component.isVisible());
            setVisible(component.isVisible());

            component.addPropertyChangeHandler(new PropertyChangeHandler() {
                public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                    if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.VISIBILITY_PROPERTY) {
                        label.setVisible(component.isVisible());
                        setVisible(component.isVisible());
                    } else if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.TITLE_PROPERTY) {
                        label.setText(component.getTitle() + ":");
                    }
                    renderToolTip();
                    renderMandatoryStar();
                }
            });

            add(nativeComponent, getElement());
            add(imageInfoWarn, getElement());
            add(imageMandatory, getElement());
            add(label, getElement());

            if (allignment.equals(LabelAlignment.LEFT)) {
                label.getElement().getStyle().setWidth(LEFT_LABEL_WIDTH, Unit.PX);
                label.getElement().getStyle().setOverflow(Overflow.HIDDEN);
                label.setWordWrap(true);
                getElement().getStyle().setPaddingTop(5, Unit.PX);
                getElement().getStyle().setPaddingLeft(LEFT_LABEL_WIDTH + 20, Unit.PX);
                getElement().getStyle().setPaddingBottom(20, Unit.PX);
                imageInfoWarn.getElement().getStyle().setProperty("top", "6px");
            } else {
                label.getElement().getStyle().setWidth(TOP_LABEL_WIDTH, Unit.PX);
                getElement().getStyle().setPaddingTop(25, Unit.PX);
                getElement().getStyle().setPaddingLeft(5, Unit.PX);
                getElement().getStyle().setPaddingBottom(5, Unit.PX);
                imageInfoWarn.getElement().getStyle().setProperty("top", "26px");
            }
            label.getElement().getStyle().setProperty("top", "5px");
            label.getElement().getStyle().setProperty("left", "15px");

            imageMandatory.getElement().getStyle().setProperty("top", "5px");
            imageMandatory.getElement().getStyle().setProperty("left", "5px");

            getElement().getStyle().setPaddingRight(30, Unit.PX);
            getElement().getStyle().setPosition(Position.RELATIVE);
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            if (allignment.equals(LabelAlignment.LEFT)) {
                imageInfoWarn.getElement().getStyle().setProperty("left", (nativeComponent.getOffsetWidth() + LEFT_LABEL_WIDTH + 25) + "px");
            } else {
                imageInfoWarn.getElement().getStyle().setProperty("left", (nativeComponent.getOffsetWidth() + 10) + "px");
            }
        }

        private void renderToolTip() {
            if (!InfoImageAlignment.HIDDEN.equals(infoImageAlignment)) {
                tooltip.setTooltipText(component.getToolTip());
                if (component.getToolTip() == null || component.getToolTip().trim().length() == 0) {
                    imageInfoWarn.setVisible(false);
                } else {
                    if (component instanceof CEditableComponent<?> && ((CEditableComponent<?>) component).isMandatoryConditionMet()
                            && !((CEditableComponent<?>) component).isValid()) {
                        imageInfoWarn.setResource(ImageFactory.getImages().formTooltipWarn());
                    } else {
                        imageInfoWarn.setResource(ImageFactory.getImages().formTooltipInfo());
                    }
                    imageInfoWarn.setVisible(true);
                }
            }
        }

        private void renderMandatoryStar() {
            if (component instanceof CEditableComponent<?>) {
                imageMandatory.setVisible(!((CEditableComponent<?>) component).isMandatoryConditionMet());
            } else {
                imageMandatory.setVisible(false);
            }
        }

    }

    class FolderContainer extends ComplexPanel {

        private final CFormFolder<?, ?> folder;

        private final Anchor addCommand;

        private final Label label;

        private final VerticalPanel container;

        FolderContainer(final CFormFolder<?, ?> folder) {
            setElement(DOM.createDiv());

            this.folder = folder;

            container = new VerticalPanel();
            container.setWidth("100%");
            container.add(new Label("CONTAINER"));

            label = new Label(folder.getTitle() == null ? "" : folder.getTitle());
            label.getElement().getStyle().setPosition(Position.ABSOLUTE);
            label.getElement().getStyle().setFontWeight(FontWeight.BOLD);

            addCommand = new Anchor("add");
            addCommand.getElement().getStyle().setPosition(Position.ABSOLUTE);
            addCommand.getElement().getStyle().setFontStyle(FontStyle.OBLIQUE);
            addCommand.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // TODO Auto-generated method stub

                }
            });

            label.setVisible(folder.isVisible());
            setVisible(folder.isVisible());

            folder.addPropertyChangeHandler(new PropertyChangeHandler() {
                public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                    if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.VISIBILITY_PROPERTY) {
                        label.setVisible(folder.isVisible());
                        setVisible(folder.isVisible());
                    } else if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.TITLE_PROPERTY) {
                        label.setText(folder.getTitle() + ":");
                    }
                }
            });

            add(container, getElement());

            add(label, getElement());

            add(addCommand, getElement());

            label.setWordWrap(false);
            label.getElement().getStyle().setProperty("top", "5px");
            label.getElement().getStyle().setProperty("left", "15px");

            addCommand.getElement().getStyle().setProperty("left", (LEFT_LABEL_WIDTH + 25) + "px");
            addCommand.getElement().getStyle().setProperty("top", "5px");

            getElement().getStyle().setPaddingTop(25, Unit.PX);
            getElement().getStyle().setPaddingBottom(20, Unit.PX);
            getElement().getStyle().setPosition(Position.RELATIVE);
        }

        @Override
        protected void onLoad() {
            super.onLoad();
        }

    }
}