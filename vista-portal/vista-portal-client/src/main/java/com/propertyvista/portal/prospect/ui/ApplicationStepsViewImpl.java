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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
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

import com.propertyvista.domain.tenant.prospect.ApplicationStepDescriptor;
import com.propertyvista.portal.shared.themes.StepsTheme;

public class ApplicationStepsViewImpl {

//    extends FlowPanel implements ApplicationStepsView {
//
//
//    private static final I18n i18n = I18n.get(ApplicationStepsViewImpl.class);
//
//    private ApplicationStepsPresenter presenter;
//
//    private final List<StepButton> stepButtons;
//
//    public ApplicationStepsViewImpl() {
//
//        setStyleName(StepsTheme.StyleName.WizardStepPanel.name());
//
//        stepButtons = new ArrayList<StepButton>();
//
//        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));
//
//        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {
//
//            @Override
//            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
//                doLayout(event.getLayoutType());
//            }
//
//        });
//    }
//
//    @Override
//    public void setStepButtons(ApplicationStepDescriptorsDTO stepDescriptors) {
//        removeAllStepButtons();
//
//        for (int i = 0; i < stepDescriptors.steps().size(); i++) {
//            ApplicationStepDescriptor stepDescriptor = stepDescriptors.steps().get(i);
//            addStepButton((i + 1) + "", stepDescriptor.stepId().getValue().getCaption(), stepDescriptor.status().getValue(), null);
//        }
//    }
//
//    private void addStepButton(String label, String caption, final ApplicationStepDescriptor.Status status, Command navigationCommand) {
//        StepButton stepButton = new StepButton(label, caption, status, navigationCommand);
//        add(stepButton);
//        stepButtons.add(stepButton);
//    }
//
//    private void removeAllStepButtons() {
//        clear();
//        stepButtons.clear();
//    }
//
//    @Override
//    public void setPresenter(final ApplicationStepsPresenter presenter) {
//        this.presenter = presenter;
//    }
//
//    private void doLayout(LayoutType layoutType) {
//        for (StepButton stepButton : stepButtons) {
//            stepButton.doLayout(layoutType);
//        }
//    }
//
//    private static class StepButton extends HTML {
//
//        static enum Size {
//            large, medium, small
//        }
//
//        private Command command;
//
//        private ApplicationStepDescriptor.Status status;
//
//        private final CaptionPanel captionPanel;
//
//        private HandlerRegistration clickHandlerRegistration;
//
//        StepButton(String label, String caption, final ApplicationStepDescriptor.Status status, Command navigationCommand) {
//            super(label);
//
//            setStyleName(StepsTheme.StyleName.WizardStepHandler.name());
//            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
//            getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
//            getElement().getStyle().setTextAlign(TextAlign.CENTER);
//
//            setStatus(status);
//            setNavigation(navigationCommand);
//
//            captionPanel = new CaptionPanel(caption);
//
//            addMouseOverHandler(new MouseOverHandler() {
//                @Override
//                public void onMouseOver(MouseOverEvent event) {
//                    showCaption(true);
//                }
//            });
//
//            addMouseOutHandler(new MouseOutHandler() {
//                @Override
//                public void onMouseOut(MouseOutEvent event) {
//                    showCaption(false);
//                }
//            });
//
//            doLayout(LayoutType.getLayoutType(Window.getClientWidth()));
//
//        }
//
//        void showCaption(boolean flag) {
//            if (flag) {
//                captionPanel.showRelativeTo(this);
//            } else {
//                captionPanel.hide(true);
//            }
//        }
//
//        void setNavigation(final Command command) {
//            this.command = command;
//            if (command == null) {
//                if (clickHandlerRegistration != null) {
//                    clickHandlerRegistration.removeHandler();
//                }
//                clickHandlerRegistration = null;
//                getElement().getStyle().setCursor(Cursor.DEFAULT);
//            } else {
//                clickHandlerRegistration = addClickHandler(new ClickHandler() {
//                    @Override
//                    public void onClick(ClickEvent event) {
//                        command.execute();
//                    }
//                });
//                getElement().getStyle().setCursor(Cursor.POINTER);
//            }
//        }
//
//        void setSize(Size size) {
//            switch (size) {
//            case large:
//                setHeight("50px");
//                getElement().getStyle().setProperty("minWidth", "50px");
//                getElement().getStyle().setLineHeight(50, Unit.PX);
//                getElement().getStyle().setFontSize(1.5, Unit.EM);
//                getElement().getStyle().setProperty("margin", "0 1%");
//                getElement().getStyle().setProperty("borderRadius", "25px");
//                break;
//            case medium:
//                setHeight("32px");
//                getElement().getStyle().setProperty("minWidth", "32px");
//                getElement().getStyle().setLineHeight(32, Unit.PX);
//                getElement().getStyle().setFontSize(1, Unit.EM);
//                getElement().getStyle().setProperty("margin", "0 1%");
//                getElement().getStyle().setProperty("borderRadius", "16px");
//                break;
//            case small:
//                setHeight("26px");
//                getElement().getStyle().setProperty("minWidth", "26px");
//                getElement().getStyle().setLineHeight(26, Unit.PX);
//                getElement().getStyle().setFontSize(0.8, Unit.EM);
//                getElement().getStyle().setProperty("margin", "0 0.3%");
//                getElement().getStyle().setProperty("borderRadius", "13px");
//                break;
//
//            }
//        }
//
//        void setStatus(ApplicationStepDescriptor.Status status) {
//            this.status = status;
//            switch (status) {
//            case notVisited:
//                getElement().getStyle().setBackgroundColor("#999");
//                break;
//            case visited:
//                getElement().getStyle().setBackgroundColor("#179bdd");
//                break;
//            case complete:
//                getElement().getStyle().setBackgroundColor("#93c948");
//                break;
//            case invalid:
//                getElement().getStyle().setBackgroundColor("#ef372f");
//                break;
//            }
//        }
//
//        void doLayout(LayoutType layoutType) {
//            switch (layoutType) {
//            case phonePortrait:
//            case phoneLandscape:
//                setSize(Size.small);
//                break;
//            case tabletPortrait:
//            case tabletLandscape:
//                setSize(Size.medium);
//                break;
//            case monitor:
//            case huge:
//                setSize(Size.large);
//                break;
//            }
//        }
//
//        class CaptionPanel extends DropDownPanel {
//            public CaptionPanel(String caption) {
//                setWidget(new HTML(caption));
//                setStyleName(StepsTheme.StyleName.WizardStepHandlerCaption.name());
//            }
//        }
//    }

}
