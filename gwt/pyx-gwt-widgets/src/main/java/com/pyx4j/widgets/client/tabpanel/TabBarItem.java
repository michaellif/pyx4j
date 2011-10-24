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
 * @version $Id$
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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.css.Selector;
import com.pyx4j.widgets.client.ImageFactory;

/**
 * @author michaellif
 * 
 */
public class TabBarItem extends HorizontalPanel {

    private final Label label;

    private boolean modifyedMarker = false;

    private final HTML leftSubpanel;

    private final HorizontalPanel rightSubpanel;

    private Image icon;

    private final Tab tab;

    private boolean selected;

    private boolean enabled = true;

    public TabBarItem(final Tab tab, ImageResource tabImage, boolean closable) {
        super();

        this.tab = tab;

        sinkEvents(Event.ONCLICK);

        getElement().getStyle().setProperty("cssFloat", "left");

        leftSubpanel = new HTML("&nbsp;");
        leftSubpanel.setWidth("8px");
        add(leftSubpanel);

        rightSubpanel = new HorizontalPanel();
        rightSubpanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);

        if (tabImage != null) {
            icon = new Image(tabImage);
            rightSubpanel.add(icon);
            rightSubpanel.setCellVerticalAlignment(icon, HasVerticalAlignment.ALIGN_MIDDLE);
        }

        label = new Label(tab.getTabTitle());
        label.setWordWrap(false);
        rightSubpanel.add(label);

        if (closable) {

            final Image closeImage = new Image(ImageFactory.getImages().closeTab());

            closeImage.getElement().getStyle().setCursor(Cursor.POINTER);
            closeImage.getElement().getStyle().setMarginRight(3, Unit.PX);
            closeImage.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    tab.close();
                }
            });
            closeImage.addMouseOverHandler(new MouseOverHandler() {

                @Override
                public void onMouseOver(MouseOverEvent event) {
                    closeImage.setResource(ImageFactory.getImages().closeTabFocused());
                }
            });
            closeImage.addMouseOutHandler(new MouseOutHandler() {

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    closeImage.setResource(ImageFactory.getImages().closeTab());
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

        addDomHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                tab.setSelected();
            }
        }, ClickEvent.getType());

        addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                if (!selected && enabled) {
                    String dependentSuffix = Selector.getDependentName(TabPanel.StyleDependent.hover);
                    addStyleDependentName(dependentSuffix);
                }
            }
        }, MouseOverEvent.getType());

        addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                String dependentSuffix = Selector.getDependentName(TabPanel.StyleDependent.hover);
                removeStyleDependentName(dependentSuffix);
            }
        }, MouseOutEvent.getType());

    }

    public void setStylePrefix(String styleName) {
        setStyleName(Selector.getStyleName(styleName, TabPanel.StyleName.TabBarItem));
        leftSubpanel.setStyleName(Selector.getStyleName(styleName, TabPanel.StyleName.TabBarItemLeft));
        rightSubpanel.setStyleName(Selector.getStyleName(styleName, TabPanel.StyleName.TabBarItemRight));
        if (icon != null) {
            icon.setStyleName(Selector.getStyleName(styleName, TabPanel.StyleName.TabBarItemImage));
        }
        label.setStyleName(Selector.getStyleName(styleName, TabPanel.StyleName.TabBarItemLabel));
    }

    public void onTabTitleChange(String labelString) {
        label.setText(labelString);
    }

    void onSelected(boolean selected) {
        this.selected = selected;
        String dependentSuffix = Selector.getDependentName(TabPanel.StyleDependent.selected);
        if (selected) {
            addStyleDependentName(dependentSuffix);
            getWidget(0).addStyleDependentName(dependentSuffix);
            getWidget(1).addStyleDependentName(dependentSuffix);
            removeStyleDependentName(Selector.getDependentName(TabPanel.StyleDependent.hover));
        } else {
            removeStyleDependentName(dependentSuffix);
            getWidget(0).removeStyleDependentName(dependentSuffix);
            getWidget(1).removeStyleDependentName(dependentSuffix);
        }
    }

    void onEnabled(boolean enabled) {
        this.enabled = enabled;
        String dependentSuffix = Selector.getDependentName(TabPanel.StyleDependent.disabled);
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

    public void onModifyed(boolean isModifyed) {
        if (!modifyedMarker && isModifyed) {
            label.setText("*" + label.getText());
            modifyedMarker = true;
        } else if (modifyedMarker && !isModifyed) {
            label.setText(label.getText().substring(1, label.getText().length()));
            modifyedMarker = false;
        }

    }

    public Tab getTab() {
        return tab;
    }

}