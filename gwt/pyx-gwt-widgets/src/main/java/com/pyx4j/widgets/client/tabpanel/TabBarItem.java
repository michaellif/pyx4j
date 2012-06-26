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

import com.pyx4j.widgets.client.ImageFactory;

/**
 * @author michaellif
 * 
 */
public class TabBarItem extends HorizontalPanel {

    private final Label label;

    private boolean dirtyMarker = false;

    private final HTML leftSubpanel;

    private final HorizontalPanel rightSubpanel;

    private Image icon;

    private final Tab tab;

    private boolean selected;

    public TabBarItem(final Tab tab, ImageResource tabImage, boolean closable) {
        super();

        this.tab = tab;

        sinkEvents(Event.ONCLICK);

        setStyleName(DefaultTabTheme.StyleName.TabBarItem.name());

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

        leftSubpanel.setStyleName(DefaultTabTheme.StyleName.TabBarItemLeft.name());
        rightSubpanel.setStyleName(DefaultTabTheme.StyleName.TabBarItemRight.name());
        if (icon != null) {
            icon.setStyleName(DefaultTabTheme.StyleName.TabBarItemImage.name());
        }
        label.setStyleName(DefaultTabTheme.StyleName.TabBarItemLabel.name());

        addDomHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (tab.isTabEnabled()) {
                    tab.setTabSelected();
                }
            }
        }, ClickEvent.getType());

        addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                if (!selected && tab.isTabEnabled()) {
                    addStyleDependentName(DefaultTabTheme.StyleDependent.hover.name());
                }
            }
        }, MouseOverEvent.getType());

        addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                removeStyleDependentName(DefaultTabTheme.StyleDependent.hover.name());
            }
        }, MouseOutEvent.getType());

    }

    public void onTabTitleChange(String labelString) {
        label.setText(labelString);
    }

    void onSelected(boolean selected) {
        this.selected = selected;
        String dependentSuffix = DefaultTabTheme.StyleDependent.selected.name();
        if (selected) {
            addStyleDependentName(dependentSuffix);
            getWidget(0).addStyleDependentName(dependentSuffix);
            getWidget(1).addStyleDependentName(dependentSuffix);
            removeStyleDependentName(DefaultTabTheme.StyleDependent.hover.name());
        } else {
            removeStyleDependentName(dependentSuffix);
            getWidget(0).removeStyleDependentName(dependentSuffix);
            getWidget(1).removeStyleDependentName(dependentSuffix);
        }
    }

    void onEnabled(boolean enabled) {
        String dependentSuffix = DefaultTabTheme.StyleDependent.disabled.name();
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

    public void onVisible(boolean visible) {
        String dependentSuffix = DefaultTabTheme.StyleDependent.hidden.name();
        if (!visible) {
            addStyleDependentName(dependentSuffix);
            getWidget(0).addStyleDependentName(dependentSuffix);
            getWidget(1).addStyleDependentName(dependentSuffix);
        } else {
            removeStyleDependentName(dependentSuffix);
            getWidget(0).removeStyleDependentName(dependentSuffix);
            getWidget(1).removeStyleDependentName(dependentSuffix);
        }
    }

    public void onDirty(boolean isDirty) {
        if (!dirtyMarker && isDirty) {
            label.setText("*" + label.getText());
            dirtyMarker = true;
        } else if (dirtyMarker && !isDirty) {
            label.setText(label.getText().substring(1, label.getText().length()));
            dirtyMarker = false;
        }

    }

    public Tab getTab() {
        return tab;
    }

}