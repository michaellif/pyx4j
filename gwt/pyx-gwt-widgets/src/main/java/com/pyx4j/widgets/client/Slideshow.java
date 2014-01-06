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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import com.pyx4j.gwt.commons.BrowserType;

public class Slideshow extends LayoutPanel {

    private static final Logger log = LoggerFactory.getLogger(Slideshow.class);

    private static final int ANIMATION_ITTERATIONS = 20;

    private int currentIndex = -1;

    private Widget fadeOut;

    private final LayoutPanel slides;

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

        slides = new LayoutPanel();
        slides.setWidth("100%");
        slides.setHeight("100%");
        add(slides);

        controlPanel = new ControlPanel();
        controlPanel.getElement().getStyle().setPadding(5, Unit.PX);
        add(controlPanel);
        setWidgetHorizontalPosition(controlPanel, Alignment.END);
        setWidgetVerticalPosition(controlPanel, Alignment.END);

        setStyleName(DefaultWidgetsTheme.StyleName.Slideshow.name());

    }

    public void addItem(Widget widget) {
        widget.setVisible(false);
        slides.add(widget);
        controlPanel.reset();
    }

    public void removeItem(Widget widget) {
        slides.remove(widget);
        controlPanel.reset();
    }

    public void removeAllItems() {
        slides.clear();
        controlPanel.reset();
    }

    public int getItemCount() {
        return slides.getWidgetCount();
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
                if (getItemCount() > 0) {
                    show((currentIndex + 1) % getItemCount(), true);
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
            show(initPosition, false);
            if (runOnInit) {
                controlPanel.play(true);
            } else {
                controlPanel.play(false);
            }
        }
    }

    protected void hide() {
        stop();
        final Widget currentItem = currentIndex < 0 ? null : slides.getWidget(currentIndex);
        if (currentItem != null) {
            currentItem.setVisible(false);
        }
        currentIndex = -1;
    }

    public void show(final int index, boolean animated) {
        if (getItemCount() == 0) {
            return;
        }

        if (animated && animationIsRunning) {
            return;
        }

        int idx = index % getItemCount();
        currentIndex = idx;
        controlPanel.setSelectedItem(idx);
        final Widget fadeIn = slides.getWidget(idx);
        fadeIn.setVisible(true);
        if (animated) {
            animationIsRunning = true;
            controlPanel.setEnabled(false);
            setOpacity(fadeIn, 0);
            new Timer() {
                int iterationCounter = 0;

                @Override
                public void run() {
                    setOpacity(fadeIn, ((double) iterationCounter) / ANIMATION_ITTERATIONS);
                    if (fadeOut != null) {
                        setOpacity(fadeOut, (1 - ((double) iterationCounter) / ANIMATION_ITTERATIONS));
                    }
                    iterationCounter++;
                    if (iterationCounter == ANIMATION_ITTERATIONS) {
                        setOpacity(fadeIn, 1);
                        if (fadeOut != null) {
                            fadeOut.setVisible(false);
                        }
                        this.cancel();
                        animationIsRunning = false;
                        controlPanel.setEnabled(true);
                        fadeOut = fadeIn;
                    }
                }
            }.scheduleRepeating(500 / ANIMATION_ITTERATIONS);
        } else {
            if (fadeOut != null) {
                fadeOut.setVisible(false);
            }
            fadeOut = fadeIn;
        }
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
            leftAction.setTitle("Back");
            leftAction.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.left.name());
            leftAction.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    stop();
                    if (currentIndex == 0) {
                        show(getItemCount() - 1, true);
                    } else {
                        if (getItemCount() > 0) {
                            show((currentIndex - 1) % getItemCount(), true);
                        }
                    }
                }
            });
            add(leftAction);

            itemActionsHolder = new HorizontalPanel();
            add(itemActionsHolder);

            startStopAction = new Action();
            startStopAction.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.paused.name());

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
            rightAction.setTitle("Next");
            rightAction.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.right.name());
            rightAction.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    stop();
                    if (getItemCount() > 0) {
                        show((currentIndex + 1) % getItemCount(), true);
                    }
                }
            });
            add(rightAction);

            setOpacity(this, 0.7);

        }

        void reset() {
            itemActionList.clear();
            itemActionsHolder.clear();
            setVisible(getItemCount() > 1);
            for (int i = 0; i < getItemCount(); i++) {
                Action itemAction = new Action();
                itemActionList.add(itemAction);
                itemActionsHolder.add(itemAction);
                final int finalI = i;
                itemAction.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        stop();
                        show(finalI, true);
                    }
                });
            }
        }

        public void play(boolean flag) {
            if (flag) {
                startStopAction.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.paused.name());
                startStopAction.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.playing.name());
                startStopAction.setTitle("Stop");
            } else {
                startStopAction.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.playing.name());
                startStopAction.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.paused.name());
                startStopAction.setTitle("Play");
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
                    itemActionList.get(i).addStyleDependentName(DefaultWidgetsTheme.StyleDependent.selected.name());
                } else {
                    itemActionList.get(i).removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.selected.name());
                }
            }
        }

        public Action getStartStopAction() {
            return startStopAction;
        }

        class Action extends HTML {

            private boolean enabled;

            public Action() {
                this("&nbsp;");
            }

            public Action(String text) {
                super(text);
                setStyleName(DefaultWidgetsTheme.StyleName.SlideshowAction.name());
                setEnabled(true);
            }

            public void setEnabled(boolean flag) {
                this.enabled = flag;
                if (flag) {
                    removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.disabled.name());
                } else {
                    addStyleDependentName(DefaultWidgetsTheme.StyleDependent.disabled.name());
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
