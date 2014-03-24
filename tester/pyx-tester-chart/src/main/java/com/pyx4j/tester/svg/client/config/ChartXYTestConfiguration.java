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
 * Created on Mar 9, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.svg.client.config;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;

public interface ChartXYTestConfiguration extends ChartTestConfiguration {

    public enum ValuesType {
        Numbers, Duration, Time
    }

    public enum ChartType {
        Line, Bar
    }

    public enum PointsType {
        None, Circle
    }

    @NotNull
    IPrimitive<ChartType> chartType();

    @NotNull
    IPrimitive<PointsType> pointsType();

    @NotNull
    IPrimitive<Integer> points();

    @NotNull
    IPrimitive<ValuesType> xValuesType();

    @NotNull
    @Format("0.000")
    IPrimitive<Double> xFrom();

    @NotNull
    @Format("0.000")
    IPrimitive<Double> xTo();

    @NotNull
    @Format("0.000")
    IPrimitive<Double> xMultiplication();

    @NotNull
    IPrimitive<ValuesType> yValuesType();

    @NotNull
    @Format("0.000")
    IPrimitive<Double> yFrom();

    @NotNull
    @Format("0.000")
    IPrimitive<Double> yTo();

    @NotNull
    @Format("0.000")
    IPrimitive<Double> yMultiplication();
}
