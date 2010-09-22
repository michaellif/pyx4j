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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.style.CSSClass;
import com.pyx4j.widgets.client.util.BrowserType;

public class Slideshow extends AbsolutePanel {

    private static final Logger log = LoggerFactory.getLogger(Slideshow.class);

    private static final int ANIMATION_ITTERATIONS = 20;

    private final List<Widget> items;

    private final int width;

    private final int height;

    private final String buttonStyle;

    private int currentIndex = -1;

    private ControlPanel controlPanel;

    private Timer slideChangeTimer;

    private boolean animationIsRunning = false;

    private final int initPosition;

    private final boolean runOnInit;

    public Slideshow(int width, int height, String buttonStyle) {
        this(width, height, buttonStyle, 0, true);
    }

    public Slideshow(int width, int height, String buttonStyle, int initPosition, boolean runOnInit) {
        this.width = width;
        this.height = height;
        this.buttonStyle = buttonStyle;
        this.initPosition = initPosition;
        this.runOnInit = runOnInit;
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
        controlPanel.start();
    }

    public void stop() {
        if (slideChangeTimer != null) {
            slideChangeTimer.cancel();
            slideChangeTimer = null;
        }
        controlPanel.stop();
    }

    protected void init() {
        //keep control panel on top and counter updated
        if (controlPanel != null) {
            remove(controlPanel);
        }
        controlPanel = new ControlPanel();
        add(controlPanel, 0, 0);
        int x = width - controlPanel.getOffsetWidth() - 100;
        setWidgetPosition(controlPanel, x, height - 30);
        show(initPosition);
        if (runOnInit) {
            controlPanel.getStartStopAction().setText("&#x25A0;");
        } else {
            controlPanel.getStartStopAction().setText("&#x25B6;");
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
        DeferredCommand.addCommand(new Command() {
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

        ControlPanel() {
            leftAction = new Action("&#171;");
            leftAction.addStyleDependentName("left");
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
                Action itemAction = new Action((i + 1) + "");
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

            startStopAction = new Action("&#x25A0;");
            startStopAction.addStyleDependentName("startStop");
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
            rightAction = new Action("&#187;");
            rightAction.addStyleDependentName("right");
            rightAction.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    stop();
                    show((currentIndex + 1) % items.size());
                }
            });
            add(rightAction);

            setOpacity(this, 0.7);

        }

        public void stop() {
            startStopAction.addStyleDependentName("start");
        }

        public void start() {
            startStopAction.addStyleDependentName("stop");
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
