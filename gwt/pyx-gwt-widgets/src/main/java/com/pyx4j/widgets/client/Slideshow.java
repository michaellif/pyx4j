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
package com.pyx4j.widgets.client;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.CSSClass;
import com.pyx4j.gwt.commons.BrowserType;

public class Slideshow extends LayoutPanel {

    private static final Logger log = LoggerFactory.getLogger(Slideshow.class);

    private static final int ANIMATION_ITTERATIONS = 20;

    private final List<Widget> items;

    private int currentIndex = -1;

    private final LayoutPanel slidesPanel;

    private final ControlPanel controlPanel;

    private Timer slideChangeTimer;

    private boolean animationIsRunning = false;

    private final int initPosition;

    private final boolean runOnInit;

    private int slideChangeSpeed = 6000;

    public Slideshow() {
        this(0, true);
    }

    public Slideshow(int initPosition, boolean runOnInit) {
        this.initPosition = initPosition;
        this.runOnInit = runOnInit;
        items = new ArrayList<Widget>();

        slidesPanel = new LayoutPanel();
        slidesPanel.setWidth("100%");
        slidesPanel.setHeight("100%");
        add(slidesPanel);

        controlPanel = new ControlPanel();
        controlPanel.getElement().getStyle().setPadding(5, Unit.PX);
        add(controlPanel);
        setWidgetHorizontalPosition(controlPanel, Alignment.END);
        setWidgetVerticalPosition(controlPanel, Alignment.END);

    }

    public void addItem(Widget widget) {
        widget.setPixelSize(getOffsetWidth(), getOffsetHeight());
        items.add(widget);
        slidesPanel.add(widget);
        widget.setVisible(false);
        controlPanel.reset();
    }

    public void removeAllItems() {
        for (Widget item : items) {
            super.remove(item);
        }
        items.clear();
        controlPanel.reset();
    }

    public Iterable<Widget> items() {
        return items;
    }

    public void setSlideChangeSpeed(int slideChangeSpeed) {
        this.slideChangeSpeed = slideChangeSpeed;
    }

    public void start() {
        if (slideChangeTimer != null) {
            stop();
        }
        slideChangeTimer = new Timer() {
            @Override
            public void run() {
                if (items.size() > 0) {
                    show((currentIndex + 1) % items.size());
                }
            }
        };
        slideChangeTimer.run();
        slideChangeTimer.scheduleRepeating(slideChangeSpeed);
        controlPanel.play(true);
    }

    public void stop() {
        if (slideChangeTimer != null) {
            slideChangeTimer.cancel();
            slideChangeTimer = null;
        }
        controlPanel.play(false);
    }

    public void init() {
        if (initPosition == -1) {
            show(initPosition);
            if (runOnInit) {
                controlPanel.play(true);
            } else {
                controlPanel.play(false);
            }
        }
    }

    protected void hide() {
        stop();
        final Widget currentItem = currentIndex < 0 ? null : items.get(currentIndex);
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
                final Widget fadeOut = currentIndex < 0 ? null : items.get(currentIndex);
                final Widget fadeIn = items.get(index);
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

        private final ArrayList<Action> itemActionList = new ArrayList<Action>();

        private final Action leftAction;

        private final Action startStopAction;

        private final Action rightAction;

        private final HorizontalPanel itemActionsHolder;

        ControlPanel() {
            leftAction = new Action();
            leftAction.addStyleDependentName("left");
            leftAction.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    stop();
                    if (currentIndex == 0) {
                        show(items.size() - 1);
                    } else {
                        if (items.size() > 0) {
                            show((currentIndex - 1) % items.size());
                        }
                    }
                }
            });
            add(leftAction);

            itemActionsHolder = new HorizontalPanel();
            add(itemActionsHolder);

            startStopAction = new Action();
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
            rightAction = new Action();
            rightAction.addStyleDependentName("right");
            rightAction.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    stop();
                    if (items.size() > 0) {
                        show((currentIndex + 1) % items.size());
                    }
                }
            });
            add(rightAction);

            setOpacity(this, 0.7);

        }

        void reset() {
            itemActionList.clear();
            itemActionsHolder.clear();
            for (int i = 0; i < items.size(); i++) {
                Action itemAction = new Action();
                itemActionList.add(itemAction);
                itemActionsHolder.add(itemAction);
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

        public void play(boolean flag) {
            if (flag) {
                startStopAction.removeStyleDependentName("paused");
                startStopAction.addStyleDependentName("playing");
            } else {
                startStopAction.removeStyleDependentName("playing");
                startStopAction.addStyleDependentName("paused");
            }
        }

        public void setEnabled(boolean flag) {
            for (int i = 0; i < itemActionList.size(); i++) {
                itemActionList.get(i).setEnabled(flag);
            }

            leftAction.setEnabled(flag);

            startStopAction.setEnabled(flag);

            rightAction.setEnabled(flag);

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

        public Action getStartStopAction() {
            return startStopAction;
        }

        class Action extends HTML {

            private boolean enabled;

            public Action(String text) {
                super(text);
                setStyleName(CSSClass.pyx4j_SlideshowAction.name());
                setEnabled(true);
            }

            public Action() {
                super("&nbsp;");
                setStyleName(CSSClass.pyx4j_SlideshowAction.name());
                setEnabled(true);
            }

            public void setEnabled(boolean flag) {
                this.enabled = flag;
                if (flag) {
                    addStyleDependentName("enabled");
                } else {
                    removeStyleDependentName("enabled");
                }
            }

            public boolean isEnabled() {
                return enabled;
            }

            @Override
            public HandlerRegistration addClickHandler(final ClickHandler handler) {
                return super.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        if (enabled) {
                            handler.onClick(event);
                        }
                    }
                });
            }

        }

    }

    private void setOpacity(Widget widget, double opacity) {
        if (BrowserType.isIE()) {
            widget.getElement().getStyle().setProperty("filter", "alpha(opacity=" + (opacity * 100) + ")");
        } else {
            widget.getElement().getStyle().setOpacity(opacity);
        }
    }

}