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
 * Created on 2011-06-04
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.shared;

import java.io.Serializable;

public class Dimension implements Serializable {

    private static final long serialVersionUID = 1L;

    public int width;

    public int height;

    public Dimension() {
        this(0, 0);
    }

    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Dimension(Dimension d) {
        this(d.width, d.height);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

}
