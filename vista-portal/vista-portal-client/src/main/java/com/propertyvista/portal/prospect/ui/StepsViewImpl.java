/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-30
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.widgets.client.DropDownPanel;

public class StepsViewImpl extends FlowPanel implements StepsView {

    private static final I18n i18n = I18n.get(StepsViewImpl.class);

    private StepsPresenter presenter;

    public StepsViewImpl() {

        for (int i = 1; i < 5; i++) {
            add(new StepButton(i + "", "Step " + i, StepButton.Size.large, StepButton.Status.complete, new Command() {

                @Override
                public void execute() {
                    // TODO Auto-generated method stub

                }
            }));
        }
        add(new StepButton("5", "Step 5", StepButton.Size.large, StepButton.Status.invalid, null));
        add(new StepButton("6", "Step 6", StepButton.Size.large, StepButton.Status.selected, null));
        for (int i = 7; i < 12; i++) {
            add(new StepButton(i + "", "Step " + i, StepButton.Size.large, StepButton.Status.notVisited, null));
        }

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    @Override
    public void setPresenter(final StepsPresenter presenter) {
        this.presenter = presenter;
    }

    private void doLayout(LayoutType layoutType) {

    }

    private static class StepButton extends HTML {

        static enum Size {
            large, medium, small
        }

        static enum Status {
            notVisited, selected, complete, invalid
        }

        private Command command;

        private Status status;

        private final CaptionPanel captionPanel;

        private HandlerRegistration clickHandlerRegistration;

        StepButton(String label, String caption, Size size, final Status status, Command navigationCommand) {
            super(label);

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            getElement().getStyle().setFontStyle(FontStyle.ITALIC);
            getElement().getStyle().setColor("white");
            getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            getElement().getStyle().setTextAlign(TextAlign.CENTER);

            setSize(size);
            setStatus(status);
            setNavigation(navigationCommand);

            captionPanel = new CaptionPanel(caption);

            addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    showCaption(true);
                }
            });

            addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    showCaption(false);
                }
            });

        }

        void showCaption(boolean flag) {
            if (flag) {
                captionPanel.showRelativeTo(this);
            } else {
                captionPanel.hide(true);
            }
        }

        void setNavigation(final Command command) {
            this.command = command;
            if (command == null) {
                if (clickHandlerRegistration != null) {
                    clickHandlerRegistration.removeHandler();
                }
                clickHandlerRegistration = null;
                getElement().getStyle().setCursor(Cursor.DEFAULT);
            } else {
                clickHandlerRegistration = addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        command.execute();
                    }
                });
                getElement().getStyle().setCursor(Cursor.POINTER);
            }
        }

        void setSize(Size size) {
            switch (size) {
            case large:
                setHeight("50px");
                getElement().getStyle().setProperty("minWidth", "50px");
                getElement().getStyle().setLineHeight(50, Unit.PX);
                getElement().getStyle().setFontSize(1.5, Unit.EM);
                getElement().getStyle().setProperty("margin", "10px 1%");
                getElement().getStyle().setProperty("borderRadius", "25px");
                break;
            case medium:
                setHeight("36px");
                getElement().getStyle().setProperty("minWidth", "36px");
                getElement().getStyle().setLineHeight(36, Unit.PX);
                getElement().getStyle().setFontSize(1, Unit.EM);
                getElement().getStyle().setProperty("margin", "6px 1%");
                getElement().getStyle().setProperty("borderRadius", "18px");
                break;
            case small:
                setHeight("24px");
                getElement().getStyle().setProperty("minWidth", "24px");
                getElement().getStyle().setLineHeight(24, Unit.PX);
                getElement().getStyle().setFontSize(0.7, Unit.EM);
                getElement().getStyle().setProperty("margin", "3px 0.6%");
                getElement().getStyle().setProperty("borderRadius", "12px");
                break;

            }
        }

        void setStatus(Status status) {
            this.status = status;
            switch (status) {
            case notVisited:
                getElement().getStyle().setBackgroundColor("#999");
                break;
            case selected:
                getElement().getStyle().setBackgroundColor("#179bdd");
                break;
            case complete:
                getElement().getStyle().setBackgroundColor("#93c948");
                break;
            case invalid:
                getElement().getStyle().setBackgroundColor("#ef372f");
                break;
            }
        }

        class CaptionPanel extends DropDownPanel {
            public CaptionPanel(String caption) {
                setWidget(new HTML(caption));
                getElement().getStyle().setProperty("background", "#aaa");
                getElement().getStyle().setProperty("color", "#fff");
                getElement().getStyle().setProperty("margin", "5px");
                getElement().getStyle().setProperty("padding", "2px 10px");
                getElement().getStyle().setProperty("borderRadius", "4px");
            }
        }
    }

}
