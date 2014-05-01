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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.decorators;

import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecorator;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorContent;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorContentHolder;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorContentPanel;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorInfoImage;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabelHolder;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorMandatoryImage;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
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
import com.pyx4j.forms.client.ui.CComponentTheme;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.Cursor;
import com.pyx4j.forms.client.ui.INativeField;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;

public class FieldDecorator extends FlowPanel implements IFieldDecorator {

    public enum DebugIds implements IDebugId {
        Label, InfoImageHolder, InfoImage, MandatoryImage, ValidationLabel;

        @Override
        public String debugId() {
            return name();
        }
    }

    private CField<?, ?> component;

    private Label label;

    private SpaceHolder mandatoryImageHolder;

    private SimplePanel infoImageHolder;

    private SimplePanel assistantWidgetHolder;

    private Image mandatoryImage;

    private HTML validationLabel;

    private Label noteLabel;

    private SimplePanel labelHolder;

    private FlowPanel contentPanel;

    private FlowPanel containerPanel;

    private final SimplePanel contentHolder;

    private final Builder<?> builder;

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
        Cursor.setDefault(label.getElement());

        infoImageHolder = new SimplePanel();
        infoImageHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        infoImageHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        mandatoryImageHolder = new SpaceHolder();
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

        validationLabel = new HTML();
        validationLabel.setVisible(false);
        validationLabel.setStyleName(CComponentTheme.StyleName.ValidationLabel.name());

        noteLabel = new Label();
        noteLabel.setVisible(false);
        noteLabel.setStyleName(CComponentTheme.StyleName.NoteLabel.name());

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
        containerPanel.setWidth("100%");
        containerPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        containerPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        containerPanel.add(contentPanel);
        containerPanel.add(validationLabel);
        containerPanel.add(noteLabel);

        add(labelHolder);
        add(containerPanel);

        //TODO implement component alignment
        HorizontalAlignmentConstant componentAlignment = builder.componentAlignment == Alignment.right ? HasHorizontalAlignment.ALIGN_RIGHT
                : builder.componentAlignment == Alignment.left ? HasHorizontalAlignment.ALIGN_LEFT : HasHorizontalAlignment.ALIGN_CENTER;

        label.ensureDebugId(CompositeDebugId.debugId(component.getDebugId(), DebugIds.Label));

        final INativeField<?> nativeComponent = component.getNativeComponent();

        Widget content = nativeComponent.getContent().asWidget();

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
                    updateCaption();
                } else if (event.getPropertyName() == PropertyChangeEvent.PropertyName.tooltip) {
                    updateTooltip();
                } else if (event.getPropertyName() == PropertyChangeEvent.PropertyName.note) {
                    updateNote();
                } else if (event.isEventOfType(PropertyName.valid, PropertyName.editingInProgress)) {
                    renderValidationMessage();
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
        updateNote();
        updateCaption();
        updateViewable();
        updateTooltip();
        updateVisibility();

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
            if (component.isVisible() && component.isEditable() && component.isEnabled() && !component.isViewable() && component.isMandatory()
                    && component.isValueEmpty()) {
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

    protected void renderValidationMessage() {
        if (!component.isValid() && !component.isEditingInProgress()) {
            validationLabel.setHTML(component.getValidationResults().getValidationMessage(true));
            component.asWidget().addStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.invalid.name());
            validationLabel.setVisible(true);
        } else {
            validationLabel.setText(null);
            component.asWidget().removeStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.invalid.name());
            validationLabel.setVisible(false);
        }

        if (component.getDebugId() != null) {
            validationLabel.ensureDebugId(new CompositeDebugId(component.getDebugId(), DebugIds.ValidationLabel).debugId());
        }

    }

    protected void updateLabelPosition() {
        switch (builder.labelPosition) {
        case left:
            labelHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            labelHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            containerPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            containerPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            containerPanel.getElement().getStyle().setProperty("marginLeft", "-" + builder.labelWidth);
            contentPanel.getElement().getStyle().setProperty("paddingLeft", builder.labelWidth);
            removeStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.verticalAlign.name());
            break;
        case top:
            labelHolder.getElement().getStyle().setDisplay(Display.BLOCK);
            containerPanel.getElement().getStyle().setDisplay(Display.BLOCK);
            addStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.verticalAlign.name());
            break;
        case hidden:
            labelHolder.getElement().getStyle().setDisplay(Display.NONE);
            break;

        }
    }

    protected void updateLabelAlignment() {
        switch (builder.labelAlignment) {
        case left:
            labelHolder.removeStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.labelAlignCenter.name());
            labelHolder.removeStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.labelAlignRight.name());
            break;
        case center:
            labelHolder.addStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.labelAlignCenter.name());
            labelHolder.removeStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.labelAlignRight.name());
            break;
        case right:
            labelHolder.removeStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.labelAlignCenter.name());
            labelHolder.addStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.labelAlignRight.name());
            break;
        }

    }

    private void updateVisibility() {
        setVisible(component.isVisible());
    }

    private void updateTooltip() {
        if (component.getTooltip() != null && component.getTooltip().trim().length() > 0) {
            Image infoImage = new Image(ImageFactory.getImages().formTooltipInfo());
            infoImage.setTitle(component.getTooltip());

            infoImageHolder.ensureDebugId(CompositeDebugId.debugId(component.getDebugId(), DebugIds.InfoImageHolder));
            infoImage.ensureDebugId(CompositeDebugId.debugId(component.getDebugId(), new CompositeDebugId(DebugIds.InfoImageHolder, DebugIds.InfoImage)));
            infoImageHolder.setWidget(new SpaceHolder(infoImage));
            infoImageHolder.getWidget().setStyleName(WidgetDecoratorInfoImage.name());
        } else {
            infoImageHolder.clear();
        }
    }

    private void updateNote() {
        if (component.getNote() != null && component.getNote().trim().length() > 0) {
            noteLabel.setText(component.getNote());
            noteLabel.setVisible(true);
            noteLabel.addStyleDependentName(component.getNoteStyle().getStyle().toString());
        } else {
            noteLabel.setText(null);
            noteLabel.setVisible(false);
            for (CComponentTheme.StyleDependent style : CComponentTheme.StyleDependent.values()) {
                noteLabel.removeStyleDependentName(style.toString());
            }
        }
    }

    protected void updateViewable() {
        if (component.isViewable()) {
            addStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.viewable.name());
            mandatoryImageHolder.setVisible(false);
        } else {
            removeStyleDependentName(DefaultWidgetDecoratorTheme.StyleDependent.viewable.name());
            mandatoryImageHolder.setVisible(true);
        }
    }

    protected void updateCaption() {
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

    protected Builder<?> getBuilder() {
        return builder;
    }

    public static class Builder<E extends Builder<E>> {

        public enum Alignment {
            left, right, center
        }

        public enum LabelPosition {
            left, top, hidden
        }

        private String labelWidth;

        private String componentWidth;

        private String customLabel;

        private Widget assistantWidget;

        private boolean useLabelSemicolon = true;

        private boolean mandatoryMarker = true;

        private Alignment labelAlignment = Alignment.right;

        private Alignment componentAlignment = Alignment.left;

        private LabelPosition labelPosition = LabelPosition.left;

        public Builder() {
            labelWidth = "170px";
            componentWidth = "100%";
        }

        public FieldDecorator build() {
            return new FieldDecorator(this);
        }

        @Deprecated
        public E labelWidth(double labelWidth) {
            this.labelWidth = labelWidth + "em";
            return (E) this;
        }

        @Deprecated
        public E componentWidth(double componentWidth) {
            this.componentWidth = componentWidth + "em";
            return (E) this;
        }

        public E labelWidth(String labelWidth) {
            this.labelWidth = labelWidth;
            return (E) this;
        }

        public E labelAlignment(Alignment labelAlignment) {
            this.labelAlignment = labelAlignment;
            return (E) this;
        }

        public E labelPosition(LabelPosition position) {
            this.labelPosition = position;
            return (E) this;
        }

        public E customLabel(String customLabel) {
            this.customLabel = customLabel;
            return (E) this;
        }

        public E componentWidth(String componentWidth) {
            this.componentWidth = componentWidth;
            return (E) this;
        }

        public E componentAlignment(Alignment componentAlignment) {
            this.componentAlignment = componentAlignment;
            return (E) this;
        }

        public E mandatoryMarker(boolean visible) {
            this.mandatoryMarker = visible;
            return (E) this;
        }

        public E useLabelSemicolon(boolean useLabelSemicolon) {
            this.useLabelSemicolon = useLabelSemicolon;
            return (E) this;
        }

        public E assistantWidget(Widget assistantWidget) {
            this.assistantWidget = assistantWidget;
            return (E) this;
        }

    }

}