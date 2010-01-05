/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 1, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.site.client.themes.classic;

public class ClassicThemeProperty {

    private String background;

    private int contentPanelWidth = 600;

    private int contentPanelTopMargin = 0;

    private int contentPanelBottomMargin = 0;

    private int headerHeight = 200;

    private int footerHeight = 100;

    private String headerBackground = "white";

    private String footerBackground = "white";

    private String mainPanelBackground = "white";

    private int headerCaptionsTop = 0;

    private int headerCaptionsLeft = 0;

    public int getContentPanelWidth() {
        return contentPanelWidth;
    }

    public void setContentPanelWidth(int width) {
        this.contentPanelWidth = width;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getContentPanelTopMargin() {
        return contentPanelTopMargin;
    }

    public void setContentPanelTopMargin(int contentPanelTopMargin) {
        this.contentPanelTopMargin = contentPanelTopMargin;
    }

    public int getContentPanelBottomMargin() {
        return contentPanelBottomMargin;
    }

    public void setContentPanelBottomMargin(int contentPanelBottomMargin) {
        this.contentPanelBottomMargin = contentPanelBottomMargin;
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }

    public int getFooterHeight() {
        return footerHeight;
    }

    public void setFooterHeight(int footerHeight) {
        this.footerHeight = footerHeight;
    }

    public String getHeaderBackground() {
        return headerBackground;
    }

    public void setHeaderBackground(String headerBackground) {
        this.headerBackground = headerBackground;
    }

    public String getFooterBackground() {
        return footerBackground;
    }

    public void setFooterBackground(String footerBackground) {
        this.footerBackground = footerBackground;
    }

    public String getMainPanelBackground() {
        return mainPanelBackground;
    }

    public void setMainPanelBackground(String mainPanelBackground) {
        this.mainPanelBackground = mainPanelBackground;
    }

    public int getHeaderCaptionsTop() {
        return headerCaptionsTop;
    }

    public void setHeaderCaptionsTop(int headerCaptionsTop) {
        this.headerCaptionsTop = headerCaptionsTop;
    }

    public int getHeaderCaptionsLeft() {
        return headerCaptionsLeft;
    }

    public void setHeaderCaptionsLeft(int headerCaptionsLeft) {
        this.headerCaptionsLeft = headerCaptionsLeft;
    }
}
