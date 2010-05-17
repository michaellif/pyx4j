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
package com.pyx4j.widgets.client.banner;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.Button;

public class Banner extends AbsolutePanel {

    private final List<Widget> items;

    private final int width;

    private final int height;

    private final String buttonStyle;

    private int currentIndex = -1;

    private ControlPanel controlPanel;

    private Timer slideChangeTimer;

    private boolean animationIsRunning = false;

    private Button startStopAction;

    public Banner(int width, int height, String buttonStyle) {
        this.width = width;
        this.height = height;
        this.buttonStyle = buttonStyle;
        items = new ArrayList<Widget>();
        setSize(width + "px", height + "px");
    }

    public void addItem(Widget widget) {
        widget.setSize(width + "px", height + "px");
        items.add(widget);
        add(widget, 0, 0);
        widget.setVisible(false);
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
        slideChangeTimer.scheduleRepeating(6000);
        startStopAction.setCaption("&#x25A0;");
    }

    public void stop() {
        if (slideChangeTimer != null) {
            slideChangeTimer.cancel();
            slideChangeTimer = null;
            startStopAction.setCaption("&#x25B6;");
        }
    }

    protected void init() {
        //keep control panel on top and counter updated
        if (controlPanel != null) {
            remove(controlPanel);
        }
        controlPanel = new ControlPanel();
        add(controlPanel, 0, 0);
        show(0);
    }

    public void show(int index) {
        if (animationIsRunning) {
            return;
        }
        animationIsRunning = true;
        if (currentIndex == index) {
            return;
        }
        final Widget fadeOut = currentIndex < 0 ? null : items.get(currentIndex);
        final Widget fadeIn = items.get(index);
        fadeIn.getElement().getStyle().setOpacity(0);
        fadeIn.setVisible(true);
        currentIndex = index;
        controlPanel.setSelectedItem(currentIndex);
        Timer timer = new Timer() {
            int iterationCounter = 0;

            @Override
            public void run() {

                fadeIn.getElement().getStyle().setOpacity(((double) iterationCounter) / 100);
                if (fadeOut != null) {
                    fadeOut.getElement().getStyle().setOpacity(1 - ((double) iterationCounter) / 100);
                }
                iterationCounter++;
                if (iterationCounter == 100) {
                    if (fadeOut != null) {
                        fadeOut.setVisible(false);
                    }
                    this.cancel();
                    animationIsRunning = false;
                }
            }
        };
        timer.scheduleRepeating(10);
    }

    @Override
    protected void onLoad() {
        init();
        super.onLoad();
        int x = width - controlPanel.getOffsetWidth() - 100;
        setWidgetPosition(controlPanel, x, height - 30);
        start();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        show(0);
        stop();
    }

    class ControlPanel extends HorizontalPanel {

        private final ArrayList<Button> itemActionList = new ArrayList<Button>();

        ControlPanel() {
            Button leftAction = new Button("&#171;", buttonStyle);
            leftAction.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    stop();
                    if (currentIndex == 0) {
                        show(items.size() - 1);
                    } else {
                        show((currentIndex - 1) % items.size());
                    }
                }
            });
            add(leftAction);

            for (int i = 0; i < items.size(); i++) {
                Button itemAction = new Button((i + 1) + "", buttonStyle);
                itemActionList.add(itemAction);
                add(itemAction);
                final int finalI = i;
                itemAction.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        stop();
                        show(finalI);
                    }
                });
            }

            startStopAction = new Button("&#x25A0;", buttonStyle);//&#x25A0;//&#x25B6;
            startStopAction.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (slideChangeTimer != null) {
                        stop();
                    } else {
                        start();
                    }
                }
            });
            add(startStopAction);
            Button rightAction = new Button("&#187;", buttonStyle);
            rightAction.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    stop();
                    show((currentIndex + 1) % items.size());
                }
            });
            add(rightAction);

            getElement().getStyle().setOpacity(0.7);

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

}
