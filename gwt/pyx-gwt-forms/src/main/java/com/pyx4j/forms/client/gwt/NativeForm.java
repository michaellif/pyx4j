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
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Printable;
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
import com.pyx4j.widgets.client.util.BrowserType;

public class NativeForm extends FlowPanel implements INativeComponent {

    enum ToolbarMode {
        First, Last, Only, Inner
    }

    public static final int LEFT_LABEL_WIDTH = 130;

    public static final int TOP_LABEL_WIDTH = 200;

    private final static DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("MMM d, yyyy");

    private static final Logger log = LoggerFactory.getLogger(NativeForm.class);

    private final FlexTable grid;

    private final CForm form;

    private CComponent<?>[][] components;

    private int columnCount = 1;

    private final int[][][] spans;

    private final LabelAlignment allignment;

    private final InfoImageAlignment infoImageAlignment;

    private int labelWidth;

    private final SimplePanel toolbarHolder;

    private Toolbar toolbar;

    private boolean mouseOver = false;

    private boolean componentsInitialized = false;

    public NativeForm(final CForm form, CComponent<?>[][] comp, LabelAlignment allignment, InfoImageAlignment infoImageAlignment) {
        super();

        toolbarHolder = new SimplePanel();
        toolbarHolder.setWidth("100%");
        add(toolbarHolder);

        grid = new FlexTable();
        grid.setWidth("100%");
        add(grid);

        grid.setBorderWidth(0);
        grid.setCellSpacing(0);
        this.form = form;
        components = comp;
        columnCount = components[0].length;
        this.allignment = allignment;
        this.infoImageAlignment = infoImageAlignment;

        switch (allignment) {
        case LEFT:
            if (columnCount == 1) {
                labelWidth = 2 * LEFT_LABEL_WIDTH;
            } else {
                labelWidth = LEFT_LABEL_WIDTH;
            }
            break;
        case TOP:
            labelWidth = TOP_LABEL_WIDTH;
            break;
        }

        spans = new int[components.length][components[0].length][2];
        preprocess();
        if (form.isExpended()) {
            addAllComponents();
            componentsInitialized = true;
        }

        if (form.getFolder() != null) {
            toolbar = new Toolbar();
            toolbarHolder.setWidget(toolbar);
        }

        setWidth(form.getWidth());
        setHeight(form.getHeight());

        sinkEvents(Event.ONMOUSEOVER);
        sinkEvents(Event.ONMOUSEOUT);

        if (form.getFolder() != null) {
            getElement().getStyle().setBorderWidth(1, Unit.PX);
            getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
            getElement().getStyle().setBorderColor("#518BDC");
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
            grid.setWidget(row, column, (Widget) component.initNativeComponent());
        } else {
            grid.setWidget(row, column, new WidgetContainer(component));
        }

        FlexCellFormatter cellFormatter = grid.getFlexCellFormatter();
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
                    Object value = ((CEditableComponent<?>) components[i][j]).getValue();
                    if (value instanceof Printable) {
                        table.setWidget(widgetRow, widgetColumn, new Label(((Printable) value).getStringView()));
                    } else if (value instanceof Date) {
                        table.setWidget(widgetRow, widgetColumn, new Label(dateTimeFormat.format((Date) value)));
                    } else {
                        table.setWidget(widgetRow, widgetColumn, new Label(CommonsStringUtils.nvl(value)));
                    }
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

    @Override
    public void onBrowserEvent(Event event) {

        //Show toString of CComponent in tooltip while mouse-over component and Shift pressed

        if (!GWT.isScript()) {
            CComponent<?> component = findItem(DOM.eventGetTarget(event));
            if (event.getShiftKey() && component != null) {
                log.debug(component.toString());
            }
        }

        super.onBrowserEvent(event);
        switch (event.getTypeInt()) {
        case Event.ONMOUSEOUT:
            mouseOver = false;
            installMouseOverStyles();
            break;
        case Event.ONMOUSEOVER:
            mouseOver = true;
            installMouseOverStyles();
            break;
        }

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

    class Toolbar extends HorizontalPanel {

        Image removeCommand;

        Image upCommand;

        Image downCommand;

        Image collapseImage;

        HTML caption;

        Toolbar() {
            setWidth("100%");

            SimplePanel collapseImageHolder = new SimplePanel();
            collapseImageHolder.getElement().getStyle().setPadding(2, Unit.PX);

            collapseImage = new Image();
            collapseImage.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    form.setExpended(!form.isExpended());
                }
            });
            collapseImageHolder.setWidget(collapseImage);

            add(collapseImageHolder);

            SimplePanel captionHolder = new SimplePanel();
            captionHolder.getElement().getStyle().setMarginLeft(5, Unit.PX);
            captionHolder.getElement().getStyle().setMarginRight(5, Unit.PX);

            caption = new HTML();
            caption.setWidth("100%");
            caption.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    form.setExpended(!form.isExpended());
                }
            });

            captionHolder.setWidget(caption);

            add(captionHolder);
            setCellWidth(captionHolder, "100%");

            HorizontalPanel actionsPanel = new HorizontalPanel();

            upCommand = new Image();
            upCommand.setResource(ImageFactory.getImages().moveUp());
            upCommand.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
            upCommand.getElement().getStyle().setMargin(2, Unit.PX);
            upCommand.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    getCComponent().getFolder().moveItem(getCComponent(), true);
                    mouseOver = false;
                    installMouseOverStyles();
                }
            });
            actionsPanel.add(upCommand);

            downCommand = new Image();
            downCommand.setResource(ImageFactory.getImages().moveDown());
            downCommand.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
            downCommand.getElement().getStyle().setMargin(2, Unit.PX);
            downCommand.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    getCComponent().getFolder().moveItem(getCComponent(), false);
                    mouseOver = false;
                    installMouseOverStyles();
                }
            });
            actionsPanel.add(downCommand);

            removeCommand = new Image();
            removeCommand.setResource(ImageFactory.getImages().deleteItem());
            removeCommand.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
            removeCommand.getElement().getStyle().setMargin(2, Unit.PX);
            removeCommand.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    getCComponent().getFolder().removeItem(getCComponent());
                }
            });
            actionsPanel.add(removeCommand);

            add(actionsPanel);
            actionsPanel.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
            if (BrowserType.isIE7()) {
                actionsPanel.getElement().getStyle().setMarginRight(40, Unit.PX);
            }

            installMouseOverStyles();

        }

    }

    class WidgetContainer extends DockPanel {

        private final CComponent<?> component;

        private final Widget nativeComponent;

        private final Label label;

        private final ImageHolder imageMandatoryHolder;

        private Image imageInfoWarn;

        private final ImageHolder imageInfoWarnHolder;

        private Image imageMandatory;

        private Tooltip tooltip;

        WidgetContainer(final CComponent<?> component) {

            getElement().getStyle().setPadding(2, Unit.PX);

            this.component = component;
            nativeComponent = (Widget) component.initNativeComponent();

            label = new Label(component.getTitle() == null ? "" : component.getTitle());

            if (allignment.equals(LabelAlignment.LEFT)) {
                label.getElement().getStyle().setProperty("textAlign", "right");
            }

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

            imageInfoWarnHolder = new ImageHolder("18px");

            imageMandatoryHolder = new ImageHolder("7px");

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

            HorizontalPanel labelHolder = new HorizontalPanel();
            labelHolder.getElement().getStyle().setPaddingRight(10, Unit.PX);

            labelHolder.add(label);
            labelHolder.add(imageMandatoryHolder);

            if (allignment.equals(LabelAlignment.LEFT)) {
                add(labelHolder, WEST);
            } else {
                add(labelHolder, NORTH);
                nativeComponent.getElement().getStyle().setMarginLeft(7, Unit.PX);
            }

            HorizontalPanel nativeComponentHolder = new HorizontalPanel();
            nativeComponentHolder.setWidth("100%");
            nativeComponentHolder.add(nativeComponent);
            nativeComponentHolder.setCellWidth(nativeComponent, "100%");
            nativeComponentHolder.add(imageInfoWarnHolder);

            add(nativeComponentHolder, CENTER);
            setCellWidth(nativeComponentHolder, "100%");

            label.getElement().getStyle().setWidth(labelWidth, Unit.PX);

        }

        private void renderToolTip() {
            if (!InfoImageAlignment.HIDDEN.equals(infoImageAlignment)) {
                if (component.getToolTip() == null || component.getToolTip().trim().length() == 0) {
                    imageInfoWarnHolder.clear();
                } else {
                    if (imageInfoWarn == null) {
                        imageInfoWarn = new Image();
                        imageInfoWarn.getElement().getStyle().setMarginLeft(7, Unit.PX);
                        tooltip = Tooltip.tooltip(imageInfoWarn, "");
                    }
                    if (component instanceof CEditableComponent<?> && ((CEditableComponent<?>) component).isMandatoryConditionMet()
                            && !((CEditableComponent<?>) component).isValid()) {
                        imageInfoWarn.setResource(ImageFactory.getImages().formTooltipWarn());
                    } else {
                        imageInfoWarn.setResource(ImageFactory.getImages().formTooltipInfo());
                    }
                    imageInfoWarnHolder.setWidget(imageInfoWarn);
                    tooltip.setTooltipText(component.getToolTip());
                }
            }
        }

        private void renderMandatoryStar() {
            if (component instanceof CEditableComponent<?>) {
                if (!((CEditableComponent<?>) component).isMandatoryConditionMet()) {
                    if (imageMandatory == null) {
                        imageMandatory = new Image();
                        Tooltip.tooltip(imageMandatory, "This field is mandatory");
                        imageMandatory.setResource(ImageFactory.getImages().mandatory());
                    }
                    imageMandatoryHolder.setWidget(imageMandatory);
                } else {
                    imageMandatoryHolder.clear();
                }
            } else {
                imageMandatoryHolder.clear();
            }
        }

    }

    class ImageHolder extends SimplePanel {

        private final String width;

        private HTML spaceHolder;

        ImageHolder(String width) {
            this.width = width;
            clear();
        }

        @Override
        public void setWidget(Widget w) {
            super.setWidget(w);
            w.setWidth(width);
        }

        @Override
        public void clear() {
            super.clear();
            if (spaceHolder == null) {
                spaceHolder = new HTML("&nbsp;");

            }
            setWidget(spaceHolder);
        }
    }

    public void setToolbarMode(ToolbarMode mode) {
        if (toolbar == null) {
            return;
        }
        switch (mode) {
        case First:
            toolbar.upCommand.setVisible(false);
            toolbar.downCommand.setVisible(true);
            break;
        case Last:
            toolbar.upCommand.setVisible(true);
            toolbar.downCommand.setVisible(false);
            break;
        case Only:
            toolbar.upCommand.setVisible(false);
            toolbar.downCommand.setVisible(false);
            break;
        case Inner:
            toolbar.upCommand.setVisible(true);
            toolbar.downCommand.setVisible(true);
            break;

        default:
            break;
        }
    }

    public void setExpanded(boolean expanded) {
        if (expanded && !componentsInitialized) {
            addAllComponents();
            componentsInitialized = true;
        }

        grid.setVisible(expanded);
        if (toolbar != null) {
            toolbar.caption.setHTML(form.getTitle());
            toolbar.caption.setVisible(!expanded);
            toolbar.collapseImage.setResource(expanded ? ImageFactory.getImages().groupBoxOpen() : ImageFactory.getImages().groupBoxClose());
            installMouseOverStyles();
        }
    }

    private void installMouseOverStyles() {
        if (mouseOver) {
            if (toolbar != null) {
                getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
                toolbar.removeCommand.getElement().getStyle().setOpacity(1);
                toolbar.upCommand.getElement().getStyle().setOpacity(1);
                toolbar.downCommand.getElement().getStyle().setOpacity(1);
                toolbar.collapseImage.getElement().getStyle().setOpacity(1);
            }
        } else {
            if (toolbar != null) {
                getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
                toolbar.removeCommand.getElement().getStyle().setOpacity(0.3);
                toolbar.upCommand.getElement().getStyle().setOpacity(0.3);
                toolbar.downCommand.getElement().getStyle().setOpacity(0.3);
                toolbar.collapseImage.getElement().getStyle().setOpacity(0.3);
            }
        }

    }

}