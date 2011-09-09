/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 8, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.model;

import java.util.Date;

public class NewsDataModel {
    private Date date;

    private String headline;

    private String text;

    public Date getDate() {
        return date;
    }

    public NewsDataModel setDate(Date date) {
        this.date = (date == null ? new Date() : date);
        return this;
    }

    public String getHeadline() {
        return headline;
    }

    public NewsDataModel setHeadline(String headline) {
        this.headline = headline;
        return this;
    }

    public String getText() {
        return text;
    }

    public NewsDataModel setText(String text) {
        this.text = text;
        return this;
    }

}
