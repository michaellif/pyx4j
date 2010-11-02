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
 * Created on May 15, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.widgets.client.photoalbum;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.util.BrowserType;

public class TabBanner extends AbsolutePanel {

    private static final Logger log = LoggerFactory.getLogger(TabBanner.class);

    private static final int ANIMATION_ITTERATIONS = BrowserType.isIE() ? 10 : 50;

    private final List<BannerItem> items;

    private final int width;

    private final int height;

    private final String buttonStyle;

    private int currentIndex = -1;

    private ControlPanel controlPanel;

    private Timer slideChangeTimer;

    private boolean animationIsRunning = false;

    private final int initPosition;

    private final boolean runOnInit;

    public TabBanner(int width, int height, String buttonStyle) {
        this(width, height, buttonStyle, 0, true);
    }

    public TabBanner(int width, int height, String buttonStyle, int initPosition, boolean runOnInit) {
        this.width = width;
        this.height = height;
        this.buttonStyle = buttonStyle;
        this.initPosition = initPosition;
        this.runOnInit = runOnInit;
        items = new ArrayList<BannerItem>();
        setSize(width + "px", height + "px");
    }

    public void addItem(BannerItem item) {
        item.getWidget().setSize(width + "px", height + "px");
        items.add(item);
        add(item.getWidget(), 0, 0);
        item.getWidget().setVisible(false);
    }

    public void start() {
        if (slideChangeTimer != null) {
            stop();
        }
        slideChangeTimer = new Timer() {
            @Override
            public void run() {
                show((currentIndex + 1) % items.size());
            }
        };
        slideChangeTimer.run();
        slideChangeTimer.scheduleRepeating(10000);
    }

    public void stop() {
        if (slideChangeTimer != null) {
            slideChangeTimer.cancel();
            slideChangeTimer = null;
        }
    }

    protected void init() {
        //keep control panel on top and counter updated
        if (controlPanel != null) {
            remove(controlPanel);
        }
        controlPanel = new ControlPanel();
        add(controlPanel, 0, 0);
        setWidgetPosition(controlPanel, 0, height - 40);
        show(initPosition);
    }

    protected void hide() {
        stop();
        final Widget currentItem = currentIndex < 0 ? null : items.get(currentIndex).getWidget();
        if (currentItem != null) {
            currentItem.setVisible(false);
        }
        currentIndex = -1;
    }

    public void show(final int index) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if ((currentIndex == index) || animationIsRunning) {
                    return;
                }
                final Widget fadeOut = currentIndex < 0 ? null : items.get(currentIndex).getWidget();
                final Widget fadeIn = items.get(index).getWidget();
                setOpacity(fadeIn, 0);
                fadeIn.setVisible(true);
                currentIndex = index;
                controlPanel.setSelectedItem(index);
                animationIsRunning = true;
                controlPanel.setEnabled(false);
                Timer animationTimer = new Timer() {
                    int iterationCounter = 0;

                    @Override
                    public void run() {

                        setOpacity(fadeIn, ((double) iterationCounter) / ANIMATION_ITTERATIONS);
                        if (fadeOut != null) {
                            setOpacity(fadeOut, (1 - ((double) iterationCounter) / ANIMATION_ITTERATIONS));
                        }
                        iterationCounter++;
                        if (iterationCounter == ANIMATION_ITTERATIONS) {
                            if (fadeOut != null) {
                                fadeOut.setVisible(false);
                            }
                            this.cancel();
                            animationIsRunning = false;
                            controlPanel.setEnabled(true);
                        }
                    }
                };
                animationTimer.scheduleRepeating(500 / ANIMATION_ITTERATIONS);
            }
        });
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        try {
            init();
            if (runOnInit) {
                start();
            }
        } catch (Throwable t) {
            log.error("Failed to init slideshow", t);
        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        try {
            hide();
        } catch (Throwable t) {
            log.error("Failed to hide slideshow", t);
        }

    }

    class ControlPanel extends HorizontalPanel {

        private final ArrayList<Button> itemActionList = new ArrayList<Button>();

        ControlPanel() {

            setWidth("100%");
            for (int i = 0; i < items.size(); i++) {
                Button itemAction = new Button(items.get(i).getTabCaption(), buttonStyle);
                itemActionList.add(itemAction);
                add(itemAction);
                setCellHorizontalAlignment(itemAction, ALIGN_CENTER);
                setCellWidth(itemAction, (double) 100 / items.size() + "%");
                final int finalI = i;
                itemAction.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        stop();
                        show(finalI);
                    }
                });
            }

        }

        public void setEnabled(boolean flag) {
            for (int i = 0; i < itemActionList.size(); i++) {
                itemActionList.get(i).setEnabled(flag);
            }
        }

        public void setSelectedItem(int currentIndex) {
            for (int i = 0; i < itemActionList.size(); i++) {
                if (i == currentIndex) {
                    itemActionList.get(i).addStyleDependentName("selected");
                } else {
                    itemActionList.get(i).removeStyleDependentName("selected");
                }
            }
        }

    }

    private void setOpacity(Widget widget, double opacity) {
        if (BrowserType.isIE7()) {
            widget.getElement().getStyle().setProperty("filter", "alpha(opacity=" + (opacity * 100) + ")");
        } else {
            widget.getElement().getStyle().setOpacity(opacity);
        }
    }

    public static class BannerItem {

        private final Widget widget;

        private final String tabCaption;

        public BannerItem(Widget widget, String tabCaption) {
            super();
            this.widget = widget;
            this.tabCaption = tabCaption;
        }

        public Widget getWidget() {
            return widget;
        }

        public String getTabCaption() {
            return tabCaption;
        }

    }

}
