/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import static com.pyx4j.commons.HtmlUtils.h3;

import java.util.Map;
import java.util.TreeMap;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.common.client.ui.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.resources.SiteResources;
import com.propertyvista.portal.domain.MarketRent;
import com.propertyvista.portal.domain.pt.ApartmentUnit;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupInteger;

public class ApartmentUnitDetailsPanel extends FlowPanel implements HasHandlers {

    private static I18n i18n = I18nFactory.getI18n(ApartmentUnitDetailsPanel.class);

    public static final Type<AnimationCompleteEventHandler> TYPE = new Type<AnimationCompleteEventHandler>();

    private final HandlerManager handlerManager;

    public ApartmentUnitDetailsPanel() {
        handlerManager = new HandlerManager(this);
    }

    public class FadeInAnimation extends Animation {
        private final FlowPanel panel;

        FadeInAnimation(final FlowPanel panel) {
            this.panel = panel;
        }

        @Override
        protected void onUpdate(final double progress) {
            panel.getElement().getStyle().setOpacity(progress);
        }
    }

    public class GrowAnimation extends Animation {
        private final FlowPanel panel;

        private final int height;

        GrowAnimation(final FlowPanel panel) {
            this.panel = panel;
            this.height = panel.getOffsetHeight();
        }

        @Override
        protected void onUpdate(final double progress) {
            panel.setHeight(String.valueOf((int) (progress * this.height)) + "px");
        }
    }

    public class ShrinkAnimation extends Animation {
        private final FlowPanel panel;

        private final FlowPanel parent;

        private final int height;

        ShrinkAnimation(final FlowPanel parent, final FlowPanel panel) {
            this.parent = parent;
            this.panel = panel;
            this.height = panel.getOffsetHeight();
        }

        @Override
        protected void onUpdate(final double progress) {
            panel.setHeight(String.valueOf((int) ((1 - progress) * this.height)) + "px");
        }

        @Override
        protected void onComplete() {
            parent.clear();
            handlerManager.fireEvent(new AnimationCompleteEvent());
        }
    }

    public class AnimationCompleteEvent extends GwtEvent<AnimationCompleteEventHandler> {
        @Override
        public GwtEvent.Type<AnimationCompleteEventHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(AnimationCompleteEventHandler handler) {
            handler.onAnimationComplete(this);
        }
    }

    public interface AnimationCompleteEventHandler extends EventHandler {
        void onAnimationComplete(AnimationCompleteEvent event);
    }

    public HandlerRegistration addAnimationCompleteEventHandler(AnimationCompleteEventHandler handler) {
        return handlerManager.addHandler(TYPE, handler);
    }

    public void showUnitDetails(final ApartmentUnit unit, final MarketRent selectedmarketRent,
            final ValueChangeHandler<MarketRent> selectedMarketRentChangeHandler, IDebugId debugId) {

        this.clear();

        FlowPanel unitDetailPanel = new FlowPanel();
        unitDetailPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);

        FlowPanel infoPanel = new FlowPanel();
        infoPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        infoPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        infoPanel.add(new HTML(h3(i18n.tr("Info"))));
        infoPanel.add(new HTML(unit.infoDetails().getStringView()));
        infoPanel.getElement().getStyle().setMarginRight(3, Unit.PCT);
        infoPanel.setWidth("30%");
        unitDetailPanel.add(infoPanel);

        FlowPanel amenitiesPanel = new FlowPanel();
        amenitiesPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        amenitiesPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        amenitiesPanel.add(new HTML(h3(i18n.tr("Amenities/Utilities"))));
        amenitiesPanel.add(new HTML(unit.amenities().getStringView()));
        amenitiesPanel.add(new HTML(unit.utilities().getStringView()));
        amenitiesPanel.getElement().getStyle().setMarginRight(3, Unit.PCT);
        amenitiesPanel.setWidth("30%");
        unitDetailPanel.add(amenitiesPanel);

        FlowPanel concessionPanel = new FlowPanel();
        concessionPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        concessionPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        concessionPanel.add(new HTML(h3(i18n.tr("Concession"))));
        concessionPanel.add(new HTML(unit.concessions().getStringView()));
        concessionPanel.getElement().getStyle().setMarginRight(3, Unit.PCT);
        concessionPanel.setWidth("30%");
        unitDetailPanel.add(concessionPanel);

        Widget sp = new ViewLineSeparator(98, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
        sp.getElement().getStyle().setPaddingLeft(0, Unit.EM);
        unitDetailPanel.add(sp);

        FlowPanel addonsPanel = new FlowPanel();
        addonsPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        addonsPanel.add(new HTML(h3(i18n.tr("Available add-ons"))));
        addonsPanel.setWidth("33%");
        unitDetailPanel.add(addonsPanel);
        addonsPanel.add(new HTML(unit.addOns().getStringView()));

        sp = new ViewLineSeparator(98, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
        sp.getElement().getStyle().setPaddingLeft(0, Unit.EM);
        unitDetailPanel.add(sp);

        // lease term:
        unitDetailPanel.add(new HTML());
        unitDetailPanel.add(new HTML(h3(i18n.tr("Lease Terms"))));

        Map<Integer, String> options = new TreeMap<Integer, String>();
        for (final MarketRent mr : unit.marketRent()) {
            options.put(mr.leaseTerm().getValue(), mr.leaseTerm().getStringView() + i18n.tr(" month ") + mr.rent().getStringView());
        }

        CRadioGroupInteger mrg = new CRadioGroupInteger(CRadioGroup.Layout.VERTICAL, options);
        mrg.setDebugId(new CompositeDebugId(debugId, "leaseTerm"));

        if (unit.marketRent().contains(selectedmarketRent)) {
            mrg.populate(selectedmarketRent.leaseTerm().getValue());
        }
        mrg.addValueChangeHandler(new ValueChangeHandler<Integer>() {

            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                for (final MarketRent mr : unit.marketRent()) {
                    if (event.getValue().equals(mr.leaseTerm().getValue())) {
                        selectedMarketRentChangeHandler.onValueChange(new ValueChangeEvent<MarketRent>(mr) {
                        });
                        break;
                    }
                }
            }
        });

        mrg.asWidget().getElement().getStyle().setFloat(Float.LEFT);
        unitDetailPanel.add(mrg);

        HTML availabilityAndPricing = new HTML(SiteResources.INSTANCE.availabilityAndPricing().getText());
        availabilityAndPricing.getElement().getStyle().setFloat(Float.RIGHT);
        unitDetailPanel.add(availabilityAndPricing);
        availabilityAndPricing.setWidth("70%");

        unitDetailPanel.getElement().getStyle().setPadding(1, Unit.EM);
        unitDetailPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
        unitDetailPanel.getElement().getStyle().setBackgroundColor("white");
        this.add(unitDetailPanel);

        new GrowAnimation(unitDetailPanel).run(500);
        // new FadeInAnimation(unitDetailPanel).run(250);
    }

    public void hideUnitDetails() {
        //this.clear();
        if (this.getWidgetCount() > 0) {
            FlowPanel unitDetailPanel = (FlowPanel) this.getWidget(0);
            new ShrinkAnimation(this, unitDetailPanel).run(500);
        } else {
            handlerManager.fireEvent(new AnimationCompleteEvent());
        }
    }

}
