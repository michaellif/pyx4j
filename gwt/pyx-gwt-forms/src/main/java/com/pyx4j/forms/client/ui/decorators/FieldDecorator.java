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
 * Created on Feb 10, 2011
 * @author Misha
 */
package com.pyx4j.forms.client.ui.decorators;

import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecorator;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecoratorContainerPanel;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecoratorContent;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecoratorContentHolder;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecoratorContentPanel;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecoratorInfoImage;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecoratorLabel;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecoratorLabelHolder;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecoratorMandatoryImage;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme.StyleName.WidgetDecoratorMessagePanel;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.INativeField;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.gwt.commons.css.CssVariable;
import com.pyx4j.gwt.commons.layout.ILayoutable;
import com.pyx4j.gwt.commons.layout.LayoutType;

public class FieldDecorator extends FlowPanel implements IFieldDecorator, ILayoutable {

    public static final String CSS_VAR_FIELD_DECORATOR_LABEL_POSITION_LAYOUT_TYPE = "FieldDecoratorLabelPositionLayoutType";

    public enum DebugIds implements IDebugId {
        Label, InfoImageHolder, InfoImage, MandatoryImage, ValidationLabel;

        @Override
        public String debugId() {
            return name();
        }
    }

    private CField<?, ?> component;

    private Label label;

    private SimplePanel mandatoryImageHolder;

    private SimplePanel infoImageHolder;

    private SimplePanel assistantWidgetHolder;

    private Image mandatoryImage;

    private MessagePannel messagePannel;

    private SimplePanel labelHolder;

    private FlowPanel contentPanel;

    private FlowPanel containerPanel;

    private final SimplePanel contentHolder;

    private final Builder<?> builder;

    private boolean narrowLayout;

    public FieldDecorator() {
        this(new Builder<>());
    }

    protected FieldDecorator(final Builder<?> builder) {
        this.builder = builder;
        setStyleName(WidgetDecorator.name());
        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        contentHolder = new SimplePanel();
    }

    @Override
    public void setContent(IsWidget content) {
        contentHolder.setWidget(content);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void init(CField<?, ?> component) {
        this.component = component;

        label = new Label();
        label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        label.setStyleName(WidgetDecoratorLabel.name());

        infoImageHolder = new SimplePanel();
        infoImageHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        infoImageHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);

        mandatoryImageHolder = new SimplePanel();
        mandatoryImageHolder.setStyleName(WidgetDecoratorMandatoryImage.name());

        FlowPanel labelContent = new FlowPanel();
        labelContent.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        labelContent.getElement().getStyle().setPosition(Position.RELATIVE);
        labelContent.add(mandatoryImageHolder);
        labelContent.add(label);

        labelHolder = new SimplePanel();
        labelHolder.setStyleName(WidgetDecoratorLabelHolder.name());
        labelHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        labelHolder.setWidth(builder.labelWidth);
        labelHolder.setWidget(labelContent);

        contentHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        contentHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        contentHolder.setStyleName(WidgetDecoratorContentHolder.name());
        contentHolder.getElement().getStyle().setProperty("textAlign", builder.componentAlignment.name());
        contentHolder.setWidth(builder.componentWidth);

        messagePannel = new MessagePannel(MessagePannel.Location.Bottom);
        messagePannel.setStyleName(WidgetDecoratorMessagePanel.name());
        messagePannel.init(component);

        assistantWidgetHolder = new SimplePanel();
        assistantWidgetHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        assistantWidgetHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        assistantWidgetHolder.setWidget(builder.assistantWidget);

        contentPanel = new FlowPanel();

        contentPanel.setStyleName(WidgetDecoratorContentPanel.name());
        contentPanel.getElement().getStyle().setProperty("textAlign", builder.componentAlignment.name());
        contentPanel.add(contentHolder);
        contentPanel.add(assistantWidgetHolder);
        contentPanel.add(infoImageHolder);

        containerPanel = new FlowPanel();
        containerPanel.setStyleName(WidgetDecoratorContainerPanel.name());
        containerPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        containerPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        containerPanel.add(contentPanel);
        containerPanel.add(messagePannel);

        add(labelHolder);
        add(containerPanel);

        label.ensureDebugId(CompositeDebugId.debugId(component.getDebugId(), DebugIds.Label));

        final INativeField<?> nativeComponent = component.getNativeComponent();

        Widget content = nativeComponent.asWidget();

        content.addStyleName(WidgetDecoratorContent.name());
        content.getElement().getStyle().setProperty("textAlign", builder.componentAlignment.name());
        content.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        if (nativeComponent instanceof Focusable) {
            label.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    ((Focusable) nativeComponent).setFocus(true);
                }
            });
        }

        component.addValueChangeHandler(new ValueChangeHandler() {

            @Override
            public void onValueChange(ValueChangeEvent event) {
                renderMandatoryStar();
            }
        });

        component.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyChangeEvent.PropertyName.viewable) {
                    updateViewable();
                } else if (event.getPropertyName() == PropertyChangeEvent.PropertyName.visible) {
                    updateVisibility();
                } else if (event.getPropertyName() == PropertyChangeEvent.PropertyName.title) {
                    renderLabel();
                } else if (event.getPropertyName() == PropertyChangeEvent.PropertyName.tooltip) {
                    renderTooltip();
                } else if (event.getPropertyName() == PropertyChangeEvent.PropertyName.note) {
                    messagePannel.renderNote();
                } else if (event.isEventOfType(PropertyName.valid, PropertyName.editingInProgress, PropertyName.editingCompleted)) {
                    messagePannel.renderValidationMessage();
                }
                if (event.isEventOfType(PropertyName.valid, PropertyName.visited, PropertyName.repopulated, PropertyName.enabled, PropertyName.editable,
                        PropertyName.visible, PropertyName.mandatory)) {
                    renderMandatoryStar();
                }
            }
        });

        renderMandatoryStar();
        updateLabelPosition();
        updateLabelAlignment();
        renderLabel();
        updateViewable();
        renderTooltip();
        updateVisibility();

        messagePannel.renderNote();
        messagePannel.renderValidationMessage();

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        }
    }

    public Label getLabel() {
        return label;
    }

    public CField<?, ?> getComponent() {
        return component;
    }

    public LabelPosition getLabelPosition() {
        return builder.labelPosition;
    }

    protected void renderMandatoryStar() {
        if (builder.mandatoryMarker && mandatoryImageHolder != null) {
            if (component.isVisible() && component.isEditable() && component.isEnabled() && component.isPopulated() && !component.isViewable()
                    && component.isMandatory() && component.isValueEmpty()) {
                if (mandatoryImage == null) {
                    mandatoryImage = new Image();
                    mandatoryImage.setResource(ImageFactory.getImages().mandatory());
                    mandatoryImage.setTitle("This field is mandatory");

                    if (component.getDebugId() != null) {
                        mandatoryImage.ensureDebugId(new CompositeDebugId(component.getDebugId(), DebugIds.MandatoryImage).debugId());
                    }
                }
                mandatoryImageHolder.setWidget(mandatoryImage);
            } else {
                mandatoryImageHolder.clear();
            }
        }
    }

    protected void updateLabelPosition() {
        if (component == null) {//Not initiated yet
            return;
        }
        if (builder.labelPosition == LabelPosition.hidden) {
            labelHolder.getElement().getStyle().setDisplay(Display.NONE);
        } else if (builder.labelPosition == LabelPosition.left && !narrowLayout) {
            labelHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            labelHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            containerPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            containerPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            removeStyleDependentName(WidgetDecoratorTheme.StyleDependent.verticalAlign.name());
        } else {
            labelHolder.getElement().getStyle().setDisplay(Display.BLOCK);
            containerPanel.getElement().getStyle().setDisplay(Display.BLOCK);
            addStyleDependentName(WidgetDecoratorTheme.StyleDependent.verticalAlign.name());
        }

    }

    protected void updateLabelAlignment() {
        if (component == null) {//Not initiated yet
            return;
        }
        switch (builder.labelAlignment) {
        case left:
            labelHolder.removeStyleDependentName(WidgetDecoratorTheme.StyleDependent.labelAlignCenter.name());
            labelHolder.removeStyleDependentName(WidgetDecoratorTheme.StyleDependent.labelAlignRight.name());
            break;
        case center:
            labelHolder.addStyleDependentName(WidgetDecoratorTheme.StyleDependent.labelAlignCenter.name());
            labelHolder.removeStyleDependentName(WidgetDecoratorTheme.StyleDependent.labelAlignRight.name());
            break;
        case right:
            labelHolder.removeStyleDependentName(WidgetDecoratorTheme.StyleDependent.labelAlignCenter.name());
            labelHolder.addStyleDependentName(WidgetDecoratorTheme.StyleDependent.labelAlignRight.name());
            break;
        }

    }

    private void updateVisibility() {
        if (component == null) {//Not initiated yet
            return;
        }
        setVisible(component.isVisible());
    }

    private void renderTooltip() {
        if (component == null) {//Not initiated yet
            return;
        }
        if (component.getTooltip() != null && component.getTooltip().trim().length() > 0) {
            Image infoImage = new Image(ImageFactory.getImages().formTooltipInfo());
            infoImage.setTitle(component.getTooltip());

            infoImageHolder.ensureDebugId(CompositeDebugId.debugId(component.getDebugId(), DebugIds.InfoImageHolder));
            infoImage.ensureDebugId(CompositeDebugId.debugId(component.getDebugId(), new CompositeDebugId(DebugIds.InfoImageHolder, DebugIds.InfoImage)));
            infoImageHolder.setWidget(infoImage);
            infoImageHolder.getWidget().setStyleName(WidgetDecoratorInfoImage.name());
        } else {
            infoImageHolder.clear();
        }
    }

    protected void updateViewable() {
        if (component == null) {//Not initiated yet
            return;
        }
        if (component.isViewable()) {
            addStyleDependentName(WidgetDecoratorTheme.StyleDependent.viewable.name());
            mandatoryImageHolder.setVisible(false);
        } else {
            removeStyleDependentName(WidgetDecoratorTheme.StyleDependent.viewable.name());
            mandatoryImageHolder.setVisible(true);
        }
    }

    protected void renderLabel() {
        if (component == null) {//Not initiated yet
            return;
        }
        String caption = builder.customLabel;
        if (caption == null) {
            caption = component.getTitle();
        }
        if (caption != null && caption.length() > 0) {
            caption += builder.useLabelSemicolon ? ":" : "";
        }
        label.setText(caption);
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void doLayout(LayoutType type) {
        String var = CssVariable.getVariable(getElement(), CSS_VAR_FIELD_DECORATOR_LABEL_POSITION_LAYOUT_TYPE);
        if (var == null) {
            setNarrowLayout(false);
        } else {
            LayoutType collapseType = LayoutType.valueOf(var);
            if (collapseType != null) {
                setNarrowLayout(collapseType.compareTo(type) > 0);
            }
        }
    }

    public void setNarrowLayout(boolean narrowLayout) {
        this.narrowLayout = narrowLayout;
        updateLabelPosition();
    }

    protected Builder<?> getBuilder() {
        return builder;
    }

    public static class Builder<E extends Builder<E>> {

        private static String LABEL_WIDTH = "150px";

        public static void setDefaultLabelWidth(String labelWidth) {
            LABEL_WIDTH = labelWidth;
        }

        public enum Alignment {
            left, right, center
        }

        public enum LabelPosition {
            left, top, hidden
        }

        private String labelWidth;

        private String componentWidth;

        private String customLabel;

        private IsWidget assistantWidget;

        private boolean useLabelSemicolon = true;

        private boolean mandatoryMarker = true;

        private Alignment labelAlignment = Alignment.right;

        private Alignment componentAlignment = Alignment.left;

        private LabelPosition labelPosition = LabelPosition.left;

        public Builder() {
            labelWidth = LABEL_WIDTH;
            componentWidth = "100%";
        }

        public FieldDecorator build() {
            return new FieldDecorator(this);
        }

        @SuppressWarnings("unchecked")
        @Deprecated
        public E labelWidth(double labelWidth) {
            this.labelWidth = labelWidth + "em";
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        @Deprecated
        public E componentWidth(double componentWidth) {
            this.componentWidth = componentWidth + "em";
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E labelWidth(String labelWidth) {
            this.labelWidth = labelWidth;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E labelAlignment(Alignment labelAlignment) {
            this.labelAlignment = labelAlignment;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E labelPosition(LabelPosition position) {
            this.labelPosition = position;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E customLabel(String customLabel) {
            this.customLabel = customLabel;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E componentWidth(String componentWidth) {
            this.componentWidth = componentWidth;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E componentAlignment(Alignment componentAlignment) {
            this.componentAlignment = componentAlignment;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E mandatoryMarker(boolean visible) {
            this.mandatoryMarker = visible;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E useLabelSemicolon(boolean useLabelSemicolon) {
            this.useLabelSemicolon = useLabelSemicolon;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E assistantWidget(IsWidget assistantWidget) {
            this.assistantWidget = assistantWidget;
            return (E) this;
        }

    }

}