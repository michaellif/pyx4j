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

public class ImageTransformUtils {

    /**
     * Will find dimensions that fit the best into given rectangle.
     * 
     * @param origDimensions
     *            original dimensions that needs scaling {width, height}
     * @param dimensionsToFitIn
     *            dimensions of the rectangle to fit in
     * @return scaled dimension array {width, height}
     */
    public static int[] scaleProportionallyToFit(int[] origDimensions, int[] dimensionsToFitIn) {
        int fitWidth = dimensionsToFitIn[0];
        int fitHeight = dimensionsToFitIn[1];

        double fitProportions = (double) fitWidth / (double) fitHeight;

        int origWidth = origDimensions[0];
        int origHeight = origDimensions[1];

        double origProportions = (double) origWidth / (double) origHeight;

        if (fitProportions >= 1 && origProportions >= 1) {
            if (fitProportions > origProportions) {
                double scale = (double) fitHeight / (double) origHeight;
                return new int[] { (int) (origWidth * scale), fitHeight };
            } else {
                double scale = (double) fitWidth / (double) origWidth;
                return new int[] { fitWidth, (int) (origHeight * scale) };
            }
        } else if (fitProportions >= 1 && origProportions <= 1) {
            double scale = (double) fitHeight / (double) origHeight;
            return new int[] { (int) (origWidth * scale), fitHeight };
        } else if (fitProportions <= 1 && origProportions <= 1) {
            if (fitProportions > origProportions) {
                double scale = (double) fitHeight / (double) origHeight;
                return new int[] { (int) (origWidth * scale), fitHeight };
            } else {
                double scale = (double) fitWidth / (double) origWidth;
                return new int[] { fitWidth, (int) (origHeight * scale) };
            }
        } else if (fitProportions <= 1 && origProportions >= 1) {
            double scale = (double) fitWidth / (double) origWidth;
            return new int[] { fitWidth, (int) (origHeight * scale) };
        } else {

            return new int[] { origWidth, origHeight };
        }

    }

    /**
     * Tries to find coordinates of top left corner for rectangle of given dimensions so that it fits into center of another rectangle
     * 
     * @param dimensions
     *            dimensions of rectangle that is to be centered
     * @param rect
     *            rectangle to center in (defined as array {leftUpperX, leftUpperY, rightLowerX, rightLowerY})
     * @return {leftUpperX, leftUpperY}
     * 
     * @throws IllegalArgumentException
     *             when it's impossible to place in given dimensions inside the given rectangle
     */
    public static int[] center(int[] dimensions, int[] rect) throws IllegalArgumentException {
        int leftUpperX = rect[0];
        int leftUpperY = rect[1];
        int rightLowerX = rect[2];
        int rightLowerY = rect[3];

        int rectWidth = rightLowerX - leftUpperX;
        int rectHeight = rightLowerY - leftUpperY;

        int itemWidth = dimensions[0];
        int itemHeight = dimensions[1];

        if (rectWidth < itemWidth || rectHeight < itemHeight) {
            throw new IllegalArgumentException("the rectangle to is too small to contain the an item of given dimensions (rect: " + rect + ", item dimensions:"
                    + dimensions + ")");
        }

        int offsetX = (rectWidth - itemWidth) / 2;
        int offsetY = (rectHeight - itemHeight) / 2;

        return new int[] { leftUpperX + offsetX, leftUpperY + offsetY };
    }
}
