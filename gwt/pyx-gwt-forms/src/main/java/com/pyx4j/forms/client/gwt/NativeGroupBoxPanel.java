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

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CGroupBoxPanel;
import com.pyx4j.forms.client.ui.CGroupBoxPanel.Layout;
import com.pyx4j.forms.client.ui.CLayoutConstraints;
import com.pyx4j.forms.client.ui.INativeComponent;
import com.pyx4j.forms.client.ui.INativeSimplePanel;
import com.pyx4j.widgets.client.FieldSetPanel;
import com.pyx4j.widgets.client.Tooltip;
import com.pyx4j.widgets.client.style.CSSClass;

public class NativeGroupBoxPanel extends FieldSetPanel implements INativeSimplePanel {

    private final CGroupBoxPanel panel;

    private final NativeLegendPanel legend;

    private final InlineHTML caption;

    private Image collapseImage;

    private PlainCheckBox collapseCheckBox;

    private Tooltip collapseButtonTooltip;

    private Panel container;

    private static class PlainCheckBox extends ButtonBase {

        PlainCheckBox() {
            super(DOM.createInputCheck());
        }

        void setValue(boolean checked) {
            InputElement.as(this.getElement()).setChecked(checked);
        }

    }

    public NativeGroupBoxPanel(final CGroupBoxPanel panel, final Layout layout) {
        super();
        this.panel = panel;
        setStyleName(CSSClass.pyx4j_GroupBox.name());

        legend = new NativeLegendPanel();
        super.add(legend);

        caption = new InlineHTML();
        caption.setStyleName(CSSClass.pyx4j_GroupBox_Caption.name());

        switch (layout) {
        case PLAIN:
            Cursor.setDefault(legend.getElement());
            Cursor.setDefault(caption.getElement());
            legend.add(caption);
            addStyleDependentName("expanded");
            break;
        case COLLAPSIBLE:
            collapseImage = new Image();
            ClickHandler expandClickHandler = new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setExpanded(!panel.isExpended());
                }
            };
            caption.addClickHandler(expandClickHandler);
            collapseImage.addClickHandler(expandClickHandler);

            collapseButtonTooltip = Tooltip.tooltip(collapseImage, tooltipText());
            Cursor.setHand(caption);
            Element collapseButtonElement = collapseImage.getElement();
            Cursor.setHand(collapseButtonElement);
            collapseButtonElement.getStyle().setProperty("verticalAlign", "bottom");

            legend.add(collapseImage);
            legend.add(caption);

            setExpanded(panel.isExpended());

            break;
        case CHECKBOX_TOGGLE:
            collapseCheckBox = new PlainCheckBox();
            ClickHandler expandClickHandlerCheckBox = new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setExpanded(!panel.isExpended());
                }
            };
            caption.addClickHandler(expandClickHandlerCheckBox);
            collapseCheckBox.addClickHandler(expandClickHandlerCheckBox);

            collapseButtonTooltip = Tooltip.tooltip(collapseCheckBox, tooltipText());
            Cursor.setHand(caption);
            Cursor.setHand(collapseCheckBox);

            legend.add(collapseCheckBox);
            legend.add(caption);
            setExpanded(panel.isExpended());
            break;
        default:
            throw new IllegalArgumentException();
        }

        this.setWidth(panel.getWidth());
        if (panel.getHeight() != null) {
            this.setHeight(panel.getHeight());
        }

        setCaption(panel.getTitle());

        panel.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (PropertyChangeEvent.PropertyName.TITLE_PROPERTY == propertyChangeEvent.getPropertyName()) {
                    setCaption(panel.getTitle());
                }
            }
        });

    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        caption.ensureDebugId(baseID);
        if (collapseImage != null) {
            collapseImage.ensureDebugId(baseID + "-image");
        }
        if (collapseCheckBox != null) {
            collapseCheckBox.ensureDebugId(baseID + "-input");
        }
    }

    protected void setCaption(String text) {
        caption.setText(text);
    }

    protected String getCaption() {
        return caption.getText();
    }

    @Override
    public void add(INativeComponent nativeWidget, CLayoutConstraints layoutConstraints) {
        Widget w = (Widget) nativeWidget;
        if (container == null) {
            container = new SimplePanel();
            container.setWidth("100%");
            container.setHeight("100%");
            container.setVisible(panel.isExpended());
            super.add(container);
        }
        container.add(w);
        w.setWidth("100%");
    }

    @Override
    public CGroupBoxPanel getCComponent() {
        return panel;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private String tooltipText() {
        return panel.isExpended() ? "Collapse" : "Expand";
    }

    public void setExpanded(boolean expended) {
        if (container != null) {
            container.setVisible(expended);
        }

        if (expended) {
            addStyleDependentName("expanded");
            removeStyleDependentName("collapsed");
            // Fix trigger element moving because border is bigger now
            legend.getElement().getStyle().setProperty("paddingLeft", "0px");
        } else {
            addStyleDependentName("collapsed");
            removeStyleDependentName("expanded");
            // Fix trigger element moving because border is smaller
            legend.getElement().getStyle().setProperty("paddingLeft", "1px");
        }
        panel.onExpended(expended);
        if (collapseImage != null) {
            if (expended) {
                collapseImage.setResource(ImageFactory.getImages().groupBoxOpen());
            } else {
                collapseImage.setResource(ImageFactory.getImages().groupBoxClose());
            }
        } else if (collapseCheckBox != null) {
            collapseCheckBox.setValue(expended);
        }
        collapseButtonTooltip.setTooltipText(tooltipText());
    }

}
