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
 * Created on Jan 6, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.svg;

public class Coordinate {

    private final Length x;

    private final Length y;

    public Coordinate(int x, int y) {
        this.x = new Length(x, null);
        this.y = new Length(y, null);
    }

    public Coordinate(Length x, Length y) {
        this.x = x;
        this.y = y;
    }

    public Length getX() {
        return x;
    }

    public Length getY() {
        return y;
    }

}
