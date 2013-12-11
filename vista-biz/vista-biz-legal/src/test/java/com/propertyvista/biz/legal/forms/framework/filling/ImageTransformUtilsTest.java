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
 * Created on 2013-12-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal.forms.framework.filling;

import org.junit.Assert;
import org.junit.Test;

public class ImageTransformUtilsTest {

    @Test
    public void testScaleProportionallyToFit() {
        int[] scaled1 = ImageTransformUtils.scaleProportionallyToFit(dim(10, 10), dim(200, 100));
        Assert.assertArrayEquals(dim(100, 100), scaled1);

        int[] scaled2 = ImageTransformUtils.scaleProportionallyToFit(dim(10, 10), dim(100, 200));
        Assert.assertArrayEquals(dim(100, 100), scaled2);

        int[] scaled3 = ImageTransformUtils.scaleProportionallyToFit(dim(20, 10), dim(200, 100));
        Assert.assertArrayEquals(dim(200, 100), scaled3);

        int[] scaled4 = ImageTransformUtils.scaleProportionallyToFit(dim(20, 10), dim(100, 200));
        Assert.assertArrayEquals(dim(100, 50), scaled4);

        int[] scaled5 = ImageTransformUtils.scaleProportionallyToFit(dim(10, 20), dim(200, 100));
        Assert.assertArrayEquals(dim(50, 100), scaled5);
    }

    @Test
    public void testCenter() {
        int[] centered1 = ImageTransformUtils.center(dim(50, 50), rect(1, 1, 100, 100));
        Assert.assertArrayEquals(upperLeftCorner(25, 25), centered1);

        int[] centered2 = ImageTransformUtils.center(dim(50, 50), rect(1, 1, 50, 100));
        Assert.assertArrayEquals(upperLeftCorner(1, 25), centered2);

        int[] centered3 = ImageTransformUtils.center(dim(100, 50), rect(1, 1, 100, 60));
        Assert.assertArrayEquals(upperLeftCorner(1, 5), centered3);

        int[] centered4 = ImageTransformUtils.center(dim(50, 100), rect(1, 1, 60, 100));
        Assert.assertArrayEquals(upperLeftCorner(5, 1), centered4);
    }

    private static int[] dim(int... dimensions) {
        if (dimensions.length != 2) {
            throw new IllegalArgumentException("array of length 2 expected");
        }
        return dimensions;
    }

    private static int[] upperLeftCorner(int... dimensions) {
        if (dimensions.length != 2) {
            throw new IllegalArgumentException("array of length 2 expected");
        }
        return dimensions;
    }

    private static int[] rect(int... rectCoordinates) {
        if (rectCoordinates.length != 4) {
            throw new IllegalArgumentException("in form {ulx, uly, lrx, lry} expected");
        }
        return rectCoordinates;
    }

}
