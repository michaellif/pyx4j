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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

public class CLayoutConstraints {

    public static enum Anchor {

        UPPER_LEFT, UPPER_CENTER, UPPER_RIGHT,

        CENTER_LEFT, CENTER, CENTER_RIGHT,

        LOWER_LEFT, LOWER_CENTER, LOWER_RIGHT

    }

    public static enum Stretch {
        NONE, HORIZONTAL, VERTICAL, BOTH
    }

    public static class Padding {

        public int top;

        public int left;

        public int bottom;

        public int right;

        public Padding() {
        }

        public Padding(int top, int right, int bottom, int left) {
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.left = left;
        }
    }

    public static class Margin {

        public int top;

        public int left;

        public int bottom;

        public int right;

        public Margin() {
        }

        public Margin(int top, int right, int bottom, int left) {
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.left = left;
        }
    }

    public Stretch stretch = Stretch.NONE;

    public Anchor anchor;

    public Padding padding;

    public Margin margin;

    public int rowSpan = 1;

    public int colSpan = 1;

}
