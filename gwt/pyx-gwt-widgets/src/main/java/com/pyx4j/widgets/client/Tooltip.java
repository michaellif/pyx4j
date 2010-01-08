/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on May 10, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.style.CSSClass;

public class Tooltip implements MouseOverHandler, MouseOutHandler, MouseMoveHandler {

    private static final int DELAY_TO_SHOW = 1000;

    private static final int DELAY_TO_HIDE = 7000;

    private static final int OFFSET_X = 0;

    private static final int OFFSET_Y = 22;

    private static TooltipPanel tooltipPanel;

    private String text;

    private final HasAllMouseHandlers target;

    public static Tooltip tooltip(HasAllMouseHandlers sender, String text) {
        return new Tooltip(sender, text);
    }

    private static class TooltipPanel extends PopupPanel {

        private final HTML content;

        private Timer delayShowTimer;

        private Timer delayHideTimer;

        private int popupLeft;

        private int popupTop;

        private long hideTimeStamp;

        public TooltipPanel() {
            super(true);
            add(content = new HTML());
            setStyleName(CSSClass.pyx4j_Tooltip.name());
        }

        private void setPointerLocation(int left, int top) {
            this.popupLeft = left;
            this.popupTop = top;
        }

        private void scheduleShow(final HasAllMouseHandlers target, final String text) {
            if (delayShowTimer != null) {
                delayShowTimer.cancel();
            }
            delayShowTimer = new Timer() {
                @Override
                public void run() {
                    delayShowTimer = null;
                    if (target instanceof Widget && ((Widget) target).isAttached() && ((Widget) target).isVisible()) {
                        int left = popupLeft + OFFSET_X;
                        int top = popupTop + OFFSET_Y;

                        // Fix position for the border of the window
                        int width = 10 + 6 * text.length();
                        int height = 30;
                        if (top >= Window.getClientHeight() - height) {
                            top = Window.getClientHeight() - height - 5;
                        }
                        if (left >= Window.getClientWidth() - width) {
                            left = Window.getClientWidth() - width - 5;
                        }

                        setPopupPosition(left, top);
                        content.setHTML(text);
                        TooltipPanel.this.show();
                        scheduleHide();
                    }
                }
            };
            delayShowTimer.schedule((System.currentTimeMillis() - hideTimeStamp > 200) ? DELAY_TO_SHOW : 200);
        }

        private void scheduleHide() {
            if (delayHideTimer != null) {
                delayHideTimer.cancel();
            }
            delayHideTimer = new Timer() {
                @Override
                public void run() {
                    TooltipPanel.this.hide();
                }
            };
            delayHideTimer.schedule(DELAY_TO_HIDE);
        }

        @Override
        public void hide() {
            if (delayShowTimer != null) {
                delayShowTimer.cancel();
                delayShowTimer = null;
            }
            if (delayHideTimer != null) {
                delayHideTimer.cancel();
                delayHideTimer = null;
            }
            hideTimeStamp = System.currentTimeMillis();
            TooltipPanel.super.hide();
        }

    }

    protected Tooltip(HasAllMouseHandlers target, String text) {
        this.text = text;
        this.target = target;
        target.addMouseOverHandler(this);
        target.addMouseOutHandler(this);
        target.addMouseMoveHandler(this);
    }

    public void setTooltipText(String text) {
        this.text = text;
        //TODO update text if it is shown
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        if (tooltipPanel == null) {
            tooltipPanel = new TooltipPanel();
        }
        tooltipPanel.setPointerLocation(event.getClientX(), event.getClientY());
        tooltipPanel.scheduleShow(target, this.text);
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        if (tooltipPanel != null) {
            tooltipPanel.setPointerLocation(event.getClientX(), event.getClientY());
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        if (tooltipPanel != null) {
            tooltipPanel.hide();
        }
    }

}