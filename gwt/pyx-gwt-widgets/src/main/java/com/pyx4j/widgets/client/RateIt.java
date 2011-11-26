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

public class RateIt extends FocusPanel implements HasValueChangeHandlers<Integer> {

    private int userRating;

    private boolean rated = false;

    private double rating;

    public RateIt(double rating, int maxRating) {
        this(rating, maxRating, ImageFactory.getImages().rateEmptyStar(), ImageFactory.getImages().rateFullStar());
    }

    public RateIt(final double rating, final int maxRating, ImageResource emptyStarImage, ImageResource fullStarImage) {

        FlowPanel container = new FlowPanel();
        setWidget(container);

        SimplePanel ratingBar = new SimplePanel();
        container.add(ratingBar);
        ratingBar.getElement().getStyle().setFloat(Float.LEFT);
        ratingBar.getElement().getStyle().setProperty("background", "url('" + emptyStarImage.getSafeUri().asString() + "') repeat-x 0%");

        final int starWidth = emptyStarImage.getWidth();
        int starHeight = emptyStarImage.getHeight();

        ratingBar.setWidth(starWidth * maxRating + "px");
        ratingBar.setHeight(starHeight + "px");

        final SimplePanel ratingStars = new SimplePanel();
        ratingStars.getElement().getStyle().setFloat(Float.LEFT);
        ratingStars.getElement().getStyle().setProperty("background", "url('" + fullStarImage.getSafeUri().asString() + "') repeat-x 0%");
        ratingStars.setWidth(0 + "px");
        ratingStars.setHeight(starHeight + "px");
        ratingBar.setWidget(ratingStars);

        ratingBar.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                if (rated) {
                    ratingStars.setWidth(userRating * starWidth + "px");
                } else {
                    ratingStars.setWidth(rating * starWidth + "px");
                }
            }
        }, MouseOutEvent.getType());

        ratingBar.addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                ratingStars.setWidth(((int) ((float) (event.getX() - 1) / starWidth) + 1) * starWidth + "px");
            }
        }, MouseMoveEvent.getType());

        ratingBar.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                userRating = ((int) ((float) event.getX() / starWidth) + 1);
                rated = true;
            }
        }, ClickEvent.getType());

        SimplePanel resetRating = new SimplePanel();
        container.add(resetRating);

    }

    public void setUserRating(int rating) {
        setUserRating(rating, false);
    }

    public int getUserRating() {
        return userRating;
    }

    public boolean isRated() {
        return rated;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    protected void setUserRating(int rating, boolean fireEvents) {
        this.userRating = rating;
        if (fireEvents) {
            this.rated = true;
            ValueChangeEvent.fire(this, userRating);
        }
    }

}