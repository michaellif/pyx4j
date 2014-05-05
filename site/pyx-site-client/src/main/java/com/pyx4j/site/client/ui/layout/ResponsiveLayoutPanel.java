/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on May 5, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.layout;

import com.google.gwt.dom.client.Document;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

import com.pyx4j.site.client.AppSite;

public abstract class ResponsiveLayoutPanel extends ComplexPanel implements RequiresResize, ProvidesResize, LayoutChangeRerquestHandler {

    public static final int ANIMATION_TIME = 500;

    private LayoutType layoutType;

    private final Layout layout;

    public enum LayoutType {

        phonePortrait(0, 320), phoneLandscape(321, 480), tabletPortrait(481, 768), tabletLandscape(769, 1024), monitor(1025, 1200), huge(1201,
                Integer.MAX_VALUE);

        private final int minWidth;

        private final int maxWidth;

        LayoutType(int minWidth, int maxWidth) {
            this.minWidth = minWidth;
            this.maxWidth = maxWidth;
        }

        public static LayoutType getLayoutType(int width) {
            for (LayoutType segment : LayoutType.values()) {
                if (width >= segment.minWidth && width <= segment.maxWidth)
                    return segment;
            }
            throw new Error("No ResponseSegment found for width " + width);
        }
    }

    public ResponsiveLayoutPanel() {
        setElement(Document.get().createDivElement());
        layout = new Layout(getElement());

        layoutType = LayoutType.getLayoutType(Window.getClientWidth());
    }

    public LayoutType getLayoutType() {
        return layoutType;
    }

    public Layout getLayout() {
        return layout;
    }

    @Override
    public void onResize() {

        LayoutType previousLayoutType = layoutType;
        layoutType = LayoutType.getLayoutType(Window.getClientWidth());

        if (previousLayoutType != layoutType) {
            forceLayout(0);
        } else {
            resizeComponents();
        }

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        forceLayout(0);
    }

    public final void forceLayout(int animationTime) {
        doLayout();
        layout.layout(animationTime);
        AppSite.getEventBus().fireEvent(new LayoutChangeEvent(getLayoutType()));
        resizeComponents();
    }

    protected abstract void resizeComponents();

    protected abstract void doLayout();
}
