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
 */
package com.pyx4j.gwt.commons.layout;

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