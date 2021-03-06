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
import com.pyx4j.gwt.commons.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.pyx4j.gwt.commons.ui.HorizontalPanel;
import com.pyx4j.gwt.commons.ui.Image;
import com.pyx4j.gwt.commons.ui.Label;
import com.pyx4j.gwt.commons.ui.SimplePanel;

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

    private final SimplePanel warningImageHolder;

    private final Tab tab;

    private boolean masked = false;

    public TabBarItem(final Tab tab, ImageResource tabImage, boolean closable) {
        super();

        this.tab = tab;

        sinkEvents(Event.ONCLICK);

        setStyleName(TabTheme.StyleName.TabBarItem.name());

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

        warningImageHolder = new SimplePanel();

        rightSubpanel.add(warningImageHolder);
        rightSubpanel.setCellVerticalAlignment(warningImageHolder, HasVerticalAlignment.ALIGN_MIDDLE);

        if (closable) {

            final Image closeImage = new Image(ImageFactory.getImages().closeTab());

            closeImage.getStyle().setCursor(Cursor.POINTER);
            closeImage.getStyle().setMarginRight(3, Unit.PX);
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

        leftSubpanel.setStyleName(TabTheme.StyleName.TabBarItemLeft.name());
        rightSubpanel.setStyleName(TabTheme.StyleName.TabBarItemRight.name());
        if (icon != null) {
            icon.setStyleName(TabTheme.StyleName.TabBarItemImage.name());
        }
        label.setStyleName(TabTheme.StyleName.TabBarItemLabel.name());

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
                if (!tab.isTabSelected() && tab.isTabEnabled()) {
                    addStyleDependentName(TabTheme.StyleDependent.hover.name());
                }
            }
        }, MouseOverEvent.getType());

        addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                removeStyleDependentName(TabTheme.StyleDependent.hover.name());
            }
        }, MouseOutEvent.getType());

    }

    public void onTabTitleChange(String labelString) {
        label.setText(labelString);
    }

    void onSelected(boolean selected) {
        String dependentSuffix = TabTheme.StyleDependent.selected.name();
        if (selected) {
            addStyleDependentName(dependentSuffix);
            getWidget(0).addStyleDependentName(dependentSuffix);
            getWidget(1).addStyleDependentName(dependentSuffix);
            removeStyleDependentName(TabTheme.StyleDependent.hover.name());
        } else {
            removeStyleDependentName(dependentSuffix);
            getWidget(0).removeStyleDependentName(dependentSuffix);
            getWidget(1).removeStyleDependentName(dependentSuffix);
        }
    }

    void onEnabled(boolean enabled) {
        String dependentSuffix = TabTheme.StyleDependent.disabled.name();
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
        String dependentSuffix = TabTheme.StyleDependent.hidden.name();
        if (!visible) {
            addStyleDependentName(dependentSuffix);
        } else {
            removeStyleDependentName(dependentSuffix);
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

    void onWarning(String message) {
        warningImageHolder.clear();
        if (message != null) {
            Image tabWarningImage = new Image(ImageFactory.getImages().tabWarning());
            tabWarningImage.setTitle(message);
            warningImageHolder.add(tabWarningImage);
        }
    }

    boolean isTabExposed() {
        return (getTab().getTabPanel().getTabBar().getAbsoluteTop() - getAbsoluteTop() > -10);
    }

    void setTabMasked(boolean masked) {
        this.masked = masked;
        String dependentSuffix = TabTheme.StyleDependent.masked.name();
        if (masked) {
            addStyleDependentName(dependentSuffix);
        } else {
            removeStyleDependentName(dependentSuffix);
        }
    }

    boolean isTabMasked() {
        return masked;
    }

}