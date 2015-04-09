/*
 * Copyright 2008 Google Inc.
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
 */
package com.pyx4j.widgets.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * This is copy of com.google.gwt.widgetideas.client.ProgressBar from GWT incubator
 * 
 * A widget that displays progress on an arbitrary scale.
 * 
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-ProgressBar-shell { primary style }</li>
 * <li>.gwt-ProgressBar-shell .gwt-ProgressBar-bar { the actual progress bar }</li>
 * <li>.gwt-ProgressBar-shell .gwt-ProgressBar-text { text on the bar }</li>
 * <li>.gwt-ProgressBar-shell .gwt-ProgressBar-text-firstHalf { applied to text when progress is less than 50 percent }</li>
 * <li>.gwt-ProgressBar-shell .gwt-ProgressBar-text-secondHalf { applied to text when progress is greater than 50 percent }</li>
 * </ul>
 */
public class ProgressBar extends Widget {

    /**
     * A formatter used to format the text displayed in the progress bar widget.
     */
    public abstract static class TextFormatter {
        /**
         * Generate the text to display in the ProgressBar based on the current value.
         * 
         * Override this method to change the text displayed within the ProgressBar.
         * 
         * @param bar
         *            the progress bar
         * @param curProgress
         *            the current progress
         * @return the text to display in the progress bar
         */
        protected abstract String getText(ProgressBar bar, double curProgress);
    }

    /**
     * The bar element that displays the progress.
     */
    private final Element barElement;

    /**
     * The current progress.
     */
    private double curProgress;

    /**
     * The maximum progress.
     */
    private double maxProgress;

    /**
     * The minimum progress.
     */
    private double minProgress;

    /**
     * A boolean that determines if the text is visible.
     */
    private boolean textVisible = true;

    /**
     * The element that displays text on the page.
     */
    private final Element textElement;

    /**
     * The current text formatter.
     */
    private TextFormatter textFormatter;

    private final int maxBarWidth = 80;

    /**
     * Create a progress bar with default range of 0 to 100.
     */
    public ProgressBar() {
        this(0.0, 100.0, 0.0);
    }

    /**
     * Create a progress bar with an initial progress and a default range of 0 to 100.
     * 
     * @param curProgress
     *            the current progress
     */
    public ProgressBar(double curProgress) {
        this(0.0, 100.0, curProgress);
    }

    /**
     * Create a progress bar within the given range.
     * 
     * @param minProgress
     *            the minimum progress
     * @param maxProgress
     *            the maximum progress
     */
    public ProgressBar(double minProgress, double maxProgress) {
        this(minProgress, maxProgress, 0.0);
    }

    /**
     * Create a progress bar within the given range starting at the specified progress
     * amount.
     * 
     * @param minProgress
     *            the minimum progress
     * @param maxProgress
     *            the maximum progress
     * @param curProgress
     *            the current progress
     */
    public ProgressBar(double minProgress, double maxProgress, double curProgress) {
        this(minProgress, maxProgress, curProgress, null);
    }

    /**
     * Create a progress bar within the given range starting at the specified progress
     * amount.
     * 
     * @param minProgress
     *            the minimum progress
     * @param maxProgress
     *            the maximum progress
     * @param curProgress
     *            the current progress
     * @param textFormatter
     *            the text formatter
     */
    public ProgressBar(double minProgress, double maxProgress, double curProgress, TextFormatter textFormatter) {
        this.minProgress = minProgress;
        this.maxProgress = maxProgress;
        this.curProgress = curProgress;
        setTextFormatter(textFormatter);

        // Create the outer shell
        setElement(DOM.createDiv());
        DOM.setStyleAttribute(getElement(), "width", "100%");
        setStyleName("gwt-ProgressBar-shell");

        // Create the bar element
        barElement = DOM.createDiv();
        DOM.appendChild(getElement(), barElement);
        DOM.setElementProperty(barElement, "className", "gwt-ProgressBar-bar");
        DOM.setStyleAttribute(barElement, "width", maxBarWidth + "%");

        // Create the text element
        textElement = DOM.createDiv();
        DOM.appendChild(getElement(), textElement);
        DOM.setElementProperty(textElement, "className", "gwt-ProgressBar-text-firstHalf");

        // Set the current progress
        setProgress(curProgress);
    }

    /**
     * Get the maximum progress.
     * 
     * @return the maximum progress
     */
    public double getMaxProgress() {
        return maxProgress;
    }

    /**
     * Get the minimum progress.
     * 
     * @return the minimum progress
     */
    public double getMinProgress() {
        return minProgress;
    }

    /**
     * Get the current percent complete, relative to the minimum and maximum values. The
     * percent will always be between 0.0 - 1.0.
     * 
     * @return the current percent complete
     */
    public double getPercent() {
        // If we have no range
        if (maxProgress <= minProgress) {
            return 0.0;
        }

        // Calculate the relative progress
        double percent = (curProgress - minProgress) / (maxProgress - minProgress);
        return Math.max(0.0, Math.min(1.0, percent));
    }

    /**
     * Get the current progress.
     * 
     * @return the current progress
     */
    public double getProgress() {
        return curProgress;
    }

    /**
     * Get the text formatter.
     * 
     * @return the text formatter
     */
    public TextFormatter getTextFormatter() {
        return textFormatter;
    }

    /**
     * Check whether the text is visible or not.
     * 
     * @return true if the text is visible
     */
    public boolean isTextVisible() {
        return textVisible;
    }

    /**
     * Set the maximum progress. If the minimum progress is more than the current
     * progress, the current progress is adjusted to be within the new range.
     * 
     * @param maxProgress
     *            the maximum progress
     */
    public void setMaxProgress(double maxProgress) {
        this.maxProgress = maxProgress;
        curProgress = Math.min(curProgress, maxProgress);
        resetProgress();
    }

    /**
     * Set the minimum progress. If the minimum progress is more than the current
     * progress, the current progress is adjusted to be within the new range.
     * 
     * @param minProgress
     *            the minimum progress
     */
    public void setMinProgress(double minProgress) {
        this.minProgress = minProgress;
        curProgress = Math.max(curProgress, minProgress);
        resetProgress();
    }

    /**
     * Set the current progress.
     * 
     * @param curProgress
     *            the current progress
     */
    public void setProgress(double curProgress) {
        this.curProgress = Math.max(minProgress, Math.min(maxProgress, curProgress));

        DOM.setStyleAttribute(barElement, "width", (int) (maxBarWidth * getPercent()) + "%");

        DOM.setElementProperty(textElement, "innerHTML", generateText(curProgress));

        // Set the style depending on the size of the bar
        if (getPercent() < 0.5) {
            DOM.setElementProperty(textElement, "className", "gwt-ProgressBar-text gwt-ProgressBar-text-firstHalf");
        } else {
            DOM.setElementProperty(textElement, "className", "gwt-ProgressBar-text gwt-ProgressBar-text-secondHalf");
        }

    }

    /**
     * Set the text formatter.
     * 
     * @param textFormatter
     *            the text formatter
     */
    public void setTextFormatter(TextFormatter textFormatter) {
        this.textFormatter = textFormatter;
    }

    /**
     * Sets whether the text is visible over the bar.
     * 
     * @param isVisible
     *            True to show text, false to hide it
     */
    public void setTextVisible(boolean isVisible) {
        this.textVisible = isVisible;
        if (this.textVisible) {
            DOM.setStyleAttribute(textElement, "display", "");
        } else {
            DOM.setStyleAttribute(textElement, "display", "none");
        }
    }

    /**
     * Generate the text to display within the progress bar. Override this function to
     * change the default progress percent to a more informative message, such as the
     * number of kilobytes downloaded.
     * 
     * @param curProgress
     *            the current progress
     * @return the text to display in the progress bar
     */
    protected String generateText(double curProgress) {
        if (textFormatter != null) {
            return textFormatter.getText(this, curProgress);
        } else {
            return (int) (100 * getPercent()) + "%";
        }
    }

    /**
     * Get the bar element.
     * 
     * @return the bar element
     */
    protected Element getBarElement() {
        return barElement;
    }

    /**
     * Get the text element.
     * 
     * @return the text element
     */
    protected Element getTextElement() {
        return textElement;
    }

    /**
     * Reset the progress text based on the current min and max progress range.
     */
    protected void resetProgress() {
        setProgress(getProgress());
    }
}
