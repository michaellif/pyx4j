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
 * Created on Apr 22, 2009
 * @author michaellif
 * @version $Id: TabBarItem.java 7480 2010-11-13 03:06:33Z michaellif $
 */
package com.pyx4j.widgets.client.tabpanel;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.ImageFactory.WidgetsImageBundle;
import com.pyx4j.widgets.client.style.Selector;

/**
 * @author michaellif
 * 
 */
public class TabBarItemNew extends HorizontalPanel {

    private final Label label;

    private boolean isModifyed = false;

    private final Label modifyedLabel;

    private boolean isEnabled = true;

    private boolean isSelected = false;

    private Image closeImage;

    private final WidgetsImageBundle images = ImageFactory.getImages();

    public TabBarItemNew(final TabBarNew parent, String labelString, ImageResource imageResource, boolean closable, String styleName) {
        super();

        sinkEvents(Event.ONCLICK);
        addClickHandler(parent);

        getElement().getStyle().setProperty("cssFloat", "left");
        HTML leftSubpanel = new HTML("&nbsp;");
        leftSubpanel.setWidth("8px");
        add(leftSubpanel);
        setStyleName(Selector.getStyleName(styleName, TabPanel.StyleSuffix.BarItem));
        leftSubpanel.setStyleName(Selector.getStyleName(styleName, TabPanel.StyleSuffix.BarItemLeft));

        final HorizontalPanel rightSubpanel = new HorizontalPanel();
        rightSubpanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
        rightSubpanel.setStyleName(Selector.getStyleName(styleName, TabPanel.StyleSuffix.BarItemRight));

        if (imageResource != null) {
            Image image = new Image(imageResource);
            image.setStyleName(Selector.getStyleName(styleName, TabPanel.StyleSuffix.BarItemImage));
            rightSubpanel.add(image);
            rightSubpanel.setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_MIDDLE);
        }

        modifyedLabel = new Label("*");
        modifyedLabel.setVisible(isModifyed);
        rightSubpanel.add(modifyedLabel);

        label = new Label(labelString);
        label.setStyleName(Selector.getStyleName(styleName, TabPanel.StyleSuffix.BarItemLabel));
        label.setWordWrap(false);
        rightSubpanel.add(label);

        if (closable) {

            closeImage = new Image(images.closeTab());

            closeImage.getElement().getStyle().setCursor(Cursor.POINTER);
            closeImage.getElement().getStyle().setMarginRight(3, Unit.PX);
            closeImage.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    for (int i = 0; i < parent.getTabBarPanel().getWidgetCount(); ++i) {
                        if (parent.getTabBarPanel().getWidget(i) == closeImage.getParent().getParent()) {
                            parent.getTabPanelModel().remove(i, false);
                        }
                    }
                }
            });
            closeImage.addMouseOverHandler(new MouseOverHandler() {

                @Override
                public void onMouseOver(MouseOverEvent event) {
                    closeImage.setResource(images.closeTabFocused());
                }
            });
            closeImage.addMouseOutHandler(new MouseOutHandler() {

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    closeImage.setResource(images.closeTab());
                }
            });

            closeImage.setTitle("Close");

            rightSubpanel.add(closeImage);
            rightSubpanel.setCellHorizontalAlignment(closeImage, HasHorizontalAlignment.ALIGN_RIGHT);
            rightSubpanel.setCellVerticalAlignment(closeImage, HasVerticalAlignment.ALIGN_MIDDLE);
        }

        rightSubpanel.setCellWidth(label, "100%");
        rightSubpanel.setCellHeight(label, "100%");

        add(rightSubpanel);
        setCellWidth(leftSubpanel, "8px");
        setCellHeight(rightSubpanel, "100%");
    }

    /**
     * @param label
     */
    public void setLabel(String labelString) {
        label.setText(labelString);
    }

    boolean isSelected() {
        return isSelected;
    }

    void setSelected(boolean selected) {
        this.isSelected = selected;
        String dependentSuffix = Selector.getDependentSuffix(TabPanel.StyleDependent.selected);
        if (selected) {
            addStyleDependentName(dependentSuffix);
            getWidget(0).addStyleDependentName(dependentSuffix);
            getWidget(1).addStyleDependentName(dependentSuffix);
        } else {
            removeStyleDependentName(dependentSuffix);
            getWidget(0).removeStyleDependentName(dependentSuffix);
            getWidget(1).removeStyleDependentName(dependentSuffix);
        }
    }

    boolean isEnabled() {
        return isEnabled;
    }

    void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        String dependentSuffix = Selector.getDependentSuffix(TabPanel.StyleDependent.disabled);
        if (!enabled) {
            addStyleDependentName(dependentSuffix);
            getWidget(0).addStyleDependentName(dependentSuffix);
            getWidget(1).addStyleDependentName(dependentSuffix);
        } else {
            removeStyleDependentName(dependentSuffix);
            getWidget(0).removeStyleDependentName(dependentSuffix);
            getWidget(1).removeStyleDependentName(dependentSuffix);
        }
    }

    //    @Override
    //    public void onBrowserEvent(Event event) {
    //
    //        if (closeImage != null && DOM.eventGetTarget(event).equals(closeImage.getElement())) {
    //            return;
    //        }
    //        switch (DOM.eventGetType(event)) {
    //        case Event.ONCLICK:
    //            //            if (clickListeners != null) {
    //            //                clickListeners.fireClick(this);
    //            //            }
    //            break;
    //        }
    //    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public boolean isModifyed() {
        return isModifyed;
    }

    public void setModifyed(boolean isModifyed) {
        this.isModifyed = isModifyed;
        modifyedLabel.setVisible(isModifyed);
    }

}