/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Nov 25, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.DefaultWidgetsTheme.StyleName;

public class RateIt extends FocusPanel implements HasValueChangeHandlers<Integer> {

    private static I18n i18n = I18n.get(RateIt.class);

    private int rating;

    private final int maxRating;

    private final SimplePanel ratingBar;

    private final SimplePanel ratingStars;

    private final int starWidth;

    public RateIt(int maxRating) {
        this(maxRating, ImageFactory.getImages().rateEmptyStar(), ImageFactory.getImages().rateFullStar());
    }

    public RateIt(final int maxRating, ImageResource emptyStarImage, ImageResource fullStarImage) {

        this.maxRating = maxRating;

        setStyleName(StyleName.RateIt.name());

        FlowPanel container = new FlowPanel();
        setWidget(container);

        ratingBar = new SimplePanel();
        ratingBar.setStyleName(StyleName.RateItBar.name());

        container.add(ratingBar);
        ratingBar.getElement().getStyle().setFloat(Float.LEFT);
        ratingBar.getElement().getStyle().setProperty("background", "url('" + emptyStarImage.getSafeUri().asString() + "') repeat-x 0%");

        starWidth = emptyStarImage.getWidth();
        int starHeight = emptyStarImage.getHeight();

        ratingBar.setWidth(starWidth * maxRating + "px");
        ratingBar.setHeight(starHeight + "px");

        ratingStars = new SimplePanel();
        ratingStars.getElement().getStyle().setFloat(Float.LEFT);
        ratingStars.getElement().getStyle().setProperty("background", "url('" + fullStarImage.getSafeUri().asString() + "') repeat-x 0%");
        ratingStars.setWidth("0px");
        ratingStars.setHeight(starHeight + "px");
        ratingBar.setWidget(ratingStars);

        ratingBar.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                ratingStars.setWidth(rating * starWidth + "px");
            }
        }, MouseOutEvent.getType());

        ratingBar.addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                showRating(convertCoordinateToRating(event.getX()));
            }
        }, MouseMoveEvent.getType());

        ratingBar.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                rating = convertCoordinateToRating(event.getX());
                ValueChangeEvent.fire(RateIt.this, rating);
            }
        }, ClickEvent.getType());

        SimplePanel resetRating = new SimplePanel();
        container.add(resetRating);

    }

    private int convertCoordinateToRating(int x) {
        return (int) ((float) (x - 1) / starWidth) + 1;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
        showRating(rating);
    }

    protected void showRating(int rating) {
        ratingStars.setWidth(rating * starWidth + "px");
        ratingBar.setTitle(rating + i18n.tr(" out of ") + maxRating);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}