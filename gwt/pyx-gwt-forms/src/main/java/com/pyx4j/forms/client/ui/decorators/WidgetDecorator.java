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
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorComponent;
import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorComponentHolder;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CComponentTheme;
import com.pyx4j.forms.client.ui.Cursor;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.LabelPosition;

public class WidgetDecorator extends FlowPanel implements IDecorator<CComponent<?>> {

    public enum DebugIds implements IDebugId {
        Label, InfoImageHolder, InfoImage, MandatoryImage, ValidationLabel;

        @Override
        public String debugId() {
            return name();
        }
    }

    private CComponent<?> component;

    private final Label label;

    private final SpaceHolder mandatoryImageHolder;

    private final SimplePanel infoImageHolder;

    private final SimplePanel assistantWidgetHolder;

    private Image mandatoryImage;

    private final Label validationLabel;

    private final Label noteLabel;

    private final SimplePanel labelHolder;

    private final FlowPanel contentPanel;

    private final FlexTable containerPanel;

    private final SimplePanel componentHolder;

    private final Builder builder;

    public WidgetDecorator(CComponent<?> component) {
        this(new Builder(component));
    }

    protected WidgetDecorator(final Builder builder) {
        this.builder = builder;

        setStyleName(WidgetDecorator.name());
        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        builder.component.setDecorator(this);
        final Widget nativeComponent = component.asWidget();
        nativeComponent.addStyleName(WidgetDecoratorComponent.name());
        nativeComponent.getElement().getStyle().setProperty("textAlign", builder.componentAlignment.name());
        nativeComponent.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        label = new Label();
        label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        label.setStyleName(WidgetDecoratorLabel.name());

        Cursor.setDefault(label.getElement());

        if (nativeComponent instanceof Focusable) {
            label.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    ((Focusable) nativeComponent).setFocus(true);
                }
            });
        }

        label.ensureDebugId(CompositeDebugId.debugId(component.getDebugId(), DebugIds.Label));

        infoImageHolder = new SimplePanel();
        infoImageHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        infoImageHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        mandatoryImageHolder = new SpaceHolder();
        mandatoryImageHolder.setStyleName(WidgetDecoratorMandatoryImage.name());

        if (builder.mandatoryMarker) {
            renderMandatoryStar();
        }

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

        componentHolder = new SimplePanel();
        componentHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        componentHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        componentHolder.setStyleName(WidgetDecoratorComponentHolder.name());
        componentHolder.getElement().getStyle().setProperty("textAlign", builder.componentAlignment.name());
        componentHolder.getElement().getStyle().setProperty("maxWidth", builder.componentWidth);
        componentHolder.add(nativeComponent);

        validationLabel = new Label();
        validationLabel.setVisible(false);
        validationLabel.setWidth(builder.contentWidth);
        validationLabel.setStyleName(CComponentTheme.StyleName.ValidationLabel.name());

        noteLabel = new Label();
        noteLabel.setVisible(false);
        noteLabel.setWidth(builder.contentWidth);
        noteLabel.setStyleName(CComponentTheme.StyleName.NoteLabel.name());

        assistantWidgetHolder = new SimplePanel();
        assistantWidgetHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        assistantWidgetHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        assistantWidgetHolder.setWidget(builder.assistantWidget);

        contentPanel = new FlowPanel();

        contentPanel.setStyleName(WidgetDecoratorContentPanel.name());
        contentPanel.getElement().getStyle().setProperty("textAlign", builder.componentAlignment.name());
        contentPanel.setWidth(builder.contentWidth);
        contentPanel.add(componentHolder);
        contentPanel.add(assistantWidgetHolder);
        contentPanel.add(infoImageHolder);

        containerPanel = new FlexTable();
        containerPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        containerPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        containerPanel.setWidget(0, 1, contentPanel);
        containerPanel.setWidget(1, 1, validationLabel);
        containerPanel.setWidget(2, 1, noteLabel);

        add(labelHolder);
        add(containerPanel);

        setLabelPosition(builder.labelPosition);

        updateLabelAlignment();
        updateNote();
        updateCaption();
        updateViewable();
        updateTooltip();
        updateVisibility();

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
                } else if (event.isEventOfType(PropertyName.valid, PropertyName.visited, PropertyName.showErrorsUnconditional, PropertyName.repopulated,
                        PropertyName.enabled, PropertyName.editable)) {
                    renderValidationMessage();
                    if (builder.mandatoryMarker) {
                        renderMandatoryStar();
                    }
                }
            }
        });

        HorizontalAlignmentConstant componentAlignment = builder.componentAlignment == Alignment.right ? HasHorizontalAlignment.ALIGN_RIGHT
                : builder.componentAlignment == Alignment.left ? HasHorizontalAlignment.ALIGN_LEFT : HasHorizontalAlignment.ALIGN_CENTER;

        containerPanel.getCellFormatter().setHorizontalAlignment(0, 0, componentAlignment);
        containerPanel.getCellFormatter().setHorizontalAlignment(1, 0, componentAlignment);
        containerPanel.getCellFormatter().setHorizontalAlignment(2, 0, componentAlignment);

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

    public CComponent<?> getComnponent() {
        return component;
    }

    public void setLabelPosition(LabelPosition layout) {
        builder.labelPosition = layout;
        switch (layout) {
        case left:
            labelHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            labelHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            containerPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            containerPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
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

    public LabelPosition getLabelPosition() {
        return builder.labelPosition;
    }

    protected void renderMandatoryStar() {
        if (mandatoryImageHolder != null) {
            if (!((CComponent<?>) component).isMandatoryConditionMet()) {
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
        if ((this.component.isUnconditionalValidationErrorRendering() || component.isVisited()) && !component.isValid()) {
            validationLabel.setText(component.getValidationResults().getValidationMessage(false, false, false));
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

    public void updateLabelAlignment() {
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
            componentHolder.setWidth("auto");
        } else {
            componentHolder.setWidth(builder.componentWidth);
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

    public static class Builder {

        public enum Alignment {
            left, right, center
        }

        public enum LabelPosition {
            left, top, hidden
        }

        private final CComponent<?> component;

        private String labelWidth;

        private String componentWidth;

        private String contentWidth;

        private String customLabel;

        private Widget assistantWidget;

        private boolean useLabelSemicolon = true;

        private boolean mandatoryMarker = true;

        private Alignment labelAlignment = Alignment.right;

        private Alignment componentAlignment = Alignment.left;

        private LabelPosition labelPosition = LabelPosition.left;

        public Builder(final CComponent<?> component) {
            this.component = component;
            labelWidth = "15em";
            componentWidth = "25em";
            contentWidth = "auto";
        }

        public WidgetDecorator build() {
            return new WidgetDecorator(this);
        }

        public CComponent<?> getComponent() {
            return component;
        }

        @Deprecated
        public Builder labelWidth(double labelWidth) {
            this.labelWidth = labelWidth + "em";
            return this;
        }

        @Deprecated
        public Builder componentWidth(double componentWidth) {
            this.componentWidth = componentWidth + "em";
            return this;
        }

        public Builder labelWidth(String labelWidth) {
            this.labelWidth = labelWidth;
            return this;
        }

        public Builder labelAlignment(Alignment labelAlignment) {
            this.labelAlignment = labelAlignment;
            return this;
        }

        public Builder labelPosition(LabelPosition position) {
            this.labelPosition = position;
            return this;
        }

        public Builder customLabel(String customLabel) {
            this.customLabel = customLabel;
            return this;
        }

        public Builder contentWidth(String contentWidth) {
            this.contentWidth = contentWidth;
            return this;
        }

        public Builder componentWidth(String componentWidth) {
            this.componentWidth = componentWidth;
            return this;
        }

        public Builder componentAlignment(Alignment componentAlignment) {
            this.componentAlignment = componentAlignment;
            return this;
        }

        public Builder mandatoryMarker(boolean visible) {
            this.mandatoryMarker = visible;
            return this;
        }

        public Builder useLabelSemicolon(boolean useLabelSemicolon) {
            this.useLabelSemicolon = useLabelSemicolon;
            return this;
        }

        public Builder assistantWidget(Widget assistantWidget) {
            this.assistantWidget = assistantWidget;
            return this;
        }

    }

    @Deprecated
    public static WidgetDecorator build(CComponent<?> component, double componentWidth) {
        return new WidgetDecorator.Builder(component).componentWidth(componentWidth).build();
    }

    @Deprecated
    public static WidgetDecorator build(CComponent<?> component, double labelWidth, double componentWidth) {
        return new WidgetDecorator.Builder(component).labelWidth(labelWidth).componentWidth(componentWidth).build();
    }

    @Override
    public void setComponent(CComponent<?> component) {
        this.component = component;

    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        // TODO Auto-generated method stub

    }

}