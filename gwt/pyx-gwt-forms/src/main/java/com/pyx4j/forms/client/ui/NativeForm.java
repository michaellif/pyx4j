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
package com.pyx4j.forms.client.ui;

import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.Printable;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CForm.InfoImageAlignment;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.decorators.SpaceHolder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Tooltip;
import com.pyx4j.widgets.client.util.BrowserType;

public class NativeForm extends FlowPanel implements INativeComponent {

    private static final I18n i18n = I18n.get(NativeForm.class);

    enum ToolbarMode {
        First, Last, Only, Inner
    }

    public static final int LEFT_LABEL_WIDTH = 120;

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

        if (form.getParentContainer() != null) {
            toolbar = new Toolbar(form.getParentContainer() instanceof CFormFolder<?>);
            toolbarHolder.setWidget(toolbar);
            getElement().getStyle().setBorderWidth(1, Unit.PX);
            getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
            getElement().getStyle().setBorderColor("#5B7575");
        }

        setWidth(form.getWidth());
        setHeight(form.getHeight());

        sinkEvents(Event.ONMOUSEOVER);
        sinkEvents(Event.ONMOUSEOUT);

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
        //Prevent collapsing invisible column
        if (components.length > 0) {
            FlexCellFormatter cellFormatter = grid.getFlexCellFormatter();
            for (int j = 0; j < components[0].length; j++) {
                grid.setWidget(components.length, j, new HTML("&nbsp;"));
                cellFormatter.setWidth(components.length, j, Math.round((double) 100 / columnCount) + "%");
            }
        }

    }

    private void addComponent(CComponent<?> component, int row, int column) {

        if (component instanceof SelfManagedComponent) {
            grid.setWidget(row, column, component.asWidget());
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
                } else if (components[i][j] instanceof CEditableComponent<?, ?>) {
                    table.setWidget(labelRow, labelColumn, new Label(components[i][j].getTitle() + ": "));
                    Object value = ((CEditableComponent<?, ?>) components[i][j]).getValue();
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

                cellFormatter.setWordWrap(labelRow, labelColumn, false);

            }
        }

        return table.toString();
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public CForm getCComponent() {
        return form;
    }

    @Override
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
                if (component != null && component.asWidget() != null) {
                    Element element = ((Widget) component.asWidget()).getElement();
                    if (DOM.isOrHasChild(element, hItem)) {
                        return component;
                    }
                }
            }
        }
        return null;
    }

    class Toolbar extends HorizontalPanel {

        ActionsPanel actionsPanel;

        Image collapseImage;

        HTML caption;

        Image imageWarn;

        Tooltip tooltipWarn;

        Toolbar(boolean showActions) {
            setWidth("100%");

            SimplePanel collapseImageHolder = new SimplePanel();
            collapseImageHolder.getElement().getStyle().setPadding(2, Unit.PX);

            collapseImage = new Image();
            //Fix the ensureDebugId initialisation
            collapseImage.setResource(ImageFactory.getImages().groupBoxClose());
            collapseImage.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    form.setExpended(!form.isExpended());
                }
            });

            IDebugId parentDebugId;
            if (getCComponent().getParentContainer() instanceof CFormFolder<?>) {
                parentDebugId = ((CFormFolder<?>) getCComponent().getParentContainer()).getCurrentRowDebugId();
            } else {
                parentDebugId = getCComponent().getParentContainer().getDebugId();
            }
            collapseImage.ensureDebugId(new CompositeDebugId(parentDebugId, FormNavigationDebugId.Form_Collapse).debugId());
            collapseImageHolder.setWidget(collapseImage);

            add(collapseImageHolder);

            HorizontalPanel captionHolder = new HorizontalPanel();
            captionHolder.getElement().getStyle().setMarginLeft(5, Unit.PX);
            captionHolder.getElement().getStyle().setMarginRight(5, Unit.PX);

            caption = new HTML();
            caption.setWidth("100%");
            caption.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    form.setExpended(!form.isExpended());
                }
            });

            if (form.getParentContainer() != null && form.getParentContainer().getTitleImage() != null) {
                Image image = new Image(form.getParentContainer().getTitleImage());
                image.getElement().getStyle().setMarginTop(2, Unit.PX);
                image.getElement().getStyle().setPaddingRight(2, Unit.PX);
                captionHolder.add(image);
            }
            captionHolder.add(caption);

            add(captionHolder);
            setCellWidth(captionHolder, "100%");

            imageWarn = new Image(ImageFactory.getImages().formTooltipWarn());
            imageWarn.setVisible(false);
            imageWarn.getElement().getStyle().setMargin(2, Unit.PX);
            imageWarn.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
            add(imageWarn);

            if (showActions) {
                actionsPanel = new ActionsPanel();
                add(actionsPanel);
                actionsPanel.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
                if (BrowserType.isIE7()) {
                    actionsPanel.getElement().getStyle().setMarginRight(40, Unit.PX);
                }
            }

            installMouseOverStyles();

        }

        class ActionsPanel extends HorizontalPanel {

            Image removeCommand;

            Image upCommand;

            Image downCommand;

            ActionsPanel() {

                IDebugId rowDebugId = ((CFormFolder) getCComponent().getParentContainer()).getCurrentRowDebugId();
                upCommand = new Image();
                upCommand.setResource(ImageFactory.getImages().moveUp());
                upCommand.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
                upCommand.getElement().getStyle().setMargin(2, Unit.PX);
                upCommand.ensureDebugId(new CompositeDebugId(rowDebugId, FormNavigationDebugId.Form_MoveUp).debugId());
                upCommand.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        ((CFormFolder) getCComponent().getParentContainer()).moveItem(getCComponent(), true);
                        mouseOver = false;
                        installMouseOverStyles();
                    }
                });
                add(upCommand);
                upCommand.setTitle(i18n.tr("Move up"));

                downCommand = new Image();
                downCommand.setResource(ImageFactory.getImages().moveDown());
                downCommand.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
                downCommand.getElement().getStyle().setMargin(2, Unit.PX);
                downCommand.ensureDebugId(new CompositeDebugId(rowDebugId, FormNavigationDebugId.Form_MoveDown).debugId());
                downCommand.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        ((CFormFolder) getCComponent().getParentContainer()).moveItem(getCComponent(), false);
                        mouseOver = false;
                        installMouseOverStyles();
                    }
                });
                add(downCommand);
                downCommand.setTitle(i18n.tr("Move down"));

                removeCommand = new Image();
                removeCommand.setResource(ImageFactory.getImages().deleteItem());
                removeCommand.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
                removeCommand.getElement().getStyle().setMargin(2, Unit.PX);
                removeCommand.ensureDebugId(new CompositeDebugId(rowDebugId, FormNavigationDebugId.Form_Remove).debugId());
                removeCommand.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        ((CFormFolder) getCComponent().getParentContainer()).removeItem(getCComponent());
                    }
                });
                add(removeCommand);
                removeCommand.setTitle(i18n.tr("Delete Item"));
            }
        }
    }

    class WidgetContainer extends DockPanel {

        private final CComponent<?> component;

        private final Widget nativeComponent;

        private final Label label;

        private final SpaceHolder imageMandatoryHolder;

        private Image imageInfoWarn;

        private final SpaceHolder imageInfoWarnHolder;

        private Image imageMandatory;

        private Tooltip tooltip;

        WidgetContainer(final CComponent<?> component) {

            getElement().getStyle().setPadding(2, Unit.PX);

            this.component = component;
            nativeComponent = component.asWidget();

            label = new Label(component.getTitle() == null ? "" : component.getTitle());

            if (allignment.equals(LabelAlignment.LEFT)) {
                label.getElement().getStyle().setProperty("textAlign", "right");
            }

            Cursor.setDefault(label.getElement());

            if (nativeComponent == null) {
                throw new RuntimeException("initNativeComponent() method call on [" + component.getClass() + "] returns null.");
            }
            if (nativeComponent instanceof NativeCheckBox) {
                ((NativeCheckBox) nativeComponent).setText(null);
            }

            if (nativeComponent instanceof Focusable) {
                label.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        ((Focusable) nativeComponent).setFocus(true);
                    }
                });
            }

            imageInfoWarnHolder = new SpaceHolder("18px");
            imageInfoWarnHolder.getElement().getStyle().setPaddingTop(2, Unit.PX);
            imageInfoWarnHolder.getElement().getStyle().setPaddingLeft(10, Unit.PX);

            imageMandatoryHolder = new SpaceHolder("7px");

            renderToolTip();
            renderMandatoryStar();

            label.setVisible(component.isVisible());
            setVisible(component.isVisible());

            component.addPropertyChangeHandler(new PropertyChangeHandler() {
                @Override
                public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                    if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.VISIBILITY_PROPERTY) {
                        label.setVisible(component.isVisible());
                        setVisible(component.isVisible());
                    } else if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.TITLE_PROPERTY) {
                        label.setText(component.getTitle() + ":");
                    } else if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.MANDATORY_PROPERTY) {
                        renderMandatoryStar();
                    } else if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.TOOLTIP_PROPERTY
                            || propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.VALIDITY) {
                        renderToolTip();
                    }

                }
            });

            HorizontalPanel labelHolder = new HorizontalPanel();
            labelHolder.getElement().getStyle().setPaddingRight(10, Unit.PX);

            if (allignment.equals(LabelAlignment.LEFT)) {
                labelHolder.add(label);
                labelHolder.add(imageMandatoryHolder);
                add(labelHolder, WEST);
                setCellVerticalAlignment(labelHolder, ALIGN_MIDDLE);
            } else {
                labelHolder.add(imageMandatoryHolder);
                labelHolder.add(label);
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

            setWidth("100%");
            label.getElement().getStyle().setWidth(labelWidth, Unit.PX);
            label.getElement().getStyle().setFontSize(0.8, Unit.EM);
            label.getElement().getStyle().setColor("#888888");
            label.getElement().getStyle().setFontWeight(FontWeight.BOLD);

        }

        private void renderToolTip() {
            if (!InfoImageAlignment.HIDDEN.equals(infoImageAlignment)) {
                String message = null;
                ImageResource imageResource = null;
                String tooltipText = component.getToolTip();
                if (tooltipText == null) {
                    tooltipText = "";
                } else {
                    tooltipText = tooltipText.trim();
                }
                if (component instanceof CEditableComponent<?, ?> && ((CEditableComponent<?, ?>) component).isMandatoryConditionMet()
                        && !((CEditableComponent<?, ?>) component).isValid()) {
                    message = "<div style='color:red'>" + ((CEditableComponent<?, ?>) component).getValidationMessage() + "</div>"
                            + (tooltipText.equals("") ? "" : ("<br/><div>" + component.getToolTip() + "</div>"));
                    imageResource = ImageFactory.getImages().formTooltipWarn();
                } else if (!tooltipText.trim().equals("")) {
                    message = component.getToolTip();
                    imageResource = ImageFactory.getImages().formTooltipInfo();
                }

                if (imageResource == null) {
                    imageInfoWarnHolder.clear();
                } else {
                    if (imageInfoWarn == null) {
                        imageInfoWarn = new Image();
                        tooltip = Tooltip.tooltip(imageInfoWarn, "");
                    }
                    imageInfoWarn.setResource(imageResource);

                    imageInfoWarnHolder.setWidget(imageInfoWarn);
                    tooltip.setTooltipText(message);
                }

            }
        }

        private void renderMandatoryStar() {
            if (component instanceof CEditableComponent<?, ?>) {
                if (((CEditableComponent<?, ?>) component).isMandatory()) {
                    if (imageMandatory == null) {
                        imageMandatory = new Image();
                        imageMandatory.setResource(ImageFactory.getImages().mandatory());
                        imageMandatory.setTitle("This field is mandatory");
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

    public void setToolbarMode(ToolbarMode mode, boolean removable) {
        if (toolbar == null || toolbar.actionsPanel == null) {
            return;
        }
        toolbar.actionsPanel.removeCommand.setVisible(removable);
        switch (mode) {
        case First:
            toolbar.actionsPanel.upCommand.setVisible(false);
            toolbar.actionsPanel.downCommand.setVisible(true);
            break;
        case Last:
            toolbar.actionsPanel.upCommand.setVisible(true);
            toolbar.actionsPanel.downCommand.setVisible(false);
            break;
        case Only:
            toolbar.actionsPanel.upCommand.setVisible(false);
            toolbar.actionsPanel.downCommand.setVisible(false);
            break;
        case Inner:
            toolbar.actionsPanel.upCommand.setVisible(true);
            toolbar.actionsPanel.downCommand.setVisible(true);
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
            toolbar.imageWarn.setVisible(!form.isValid() && !expanded);
            if (!form.isValid() && !expanded) {
                if (toolbar.tooltipWarn == null) {
                    toolbar.tooltipWarn = Tooltip.tooltip(toolbar.imageWarn, "");
                }
                toolbar.tooltipWarn.setTooltipText(form.getValidationResults().getMessagesText(true));
            }
            toolbar.caption.setVisible(!expanded);
            toolbar.collapseImage.setResource(expanded ? ImageFactory.getImages().groupBoxOpen() : ImageFactory.getImages().groupBoxClose());
            installMouseOverStyles();
        }
    }

    private void installMouseOverStyles() {
        if (mouseOver) {
            if (toolbar != null) {
                getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
                if (toolbar.actionsPanel != null) {
                    toolbar.actionsPanel.removeCommand.getElement().getStyle().setOpacity(1);
                    toolbar.actionsPanel.upCommand.getElement().getStyle().setOpacity(1);
                    toolbar.actionsPanel.downCommand.getElement().getStyle().setOpacity(1);
                }
                toolbar.collapseImage.getElement().getStyle().setOpacity(1);
            }
        } else {
            if (toolbar != null) {
                getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
                if (toolbar.actionsPanel != null) {
                    toolbar.actionsPanel.removeCommand.getElement().getStyle().setOpacity(0.3);
                    toolbar.actionsPanel.upCommand.getElement().getStyle().setOpacity(0.3);
                    toolbar.actionsPanel.downCommand.getElement().getStyle().setOpacity(0.3);
                }
                toolbar.collapseImage.getElement().getStyle().setOpacity(0.3);
            }
        }

    }

    @Override
    public void installStyles(String stylePrefix) {
        // TODO Auto-generated method stub

    }
}