/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-12-22
 * @author vlads
 */
package com.pyx4j.svg.chart;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ChartColorsCollection implements ChartColors {

    private final Collection<String> colors;

    public ChartColorsCollection(Collection<String> colors) {
        this.colors = colors;
    }

    public ChartColorsCollection(String... colors) {
        this.colors = Arrays.asList(colors);
    }

    @Override
    public Iterator<String> iterator() {
        return colors.iterator();
    }

}
