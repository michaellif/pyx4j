/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 24, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import java.io.Serializable;

import com.propertyvista.pmsite.server.model.Province;

public class QuickSearchModel implements Serializable {

    private static final long serialVersionUID = 1L;

    enum BedroomChoice {
        br1("1 Bedroom"), br2("2 Bedroom"), br3("3 Bedroom"), br4("4 or more");

        private final String display;

        BedroomChoice(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
    }

    enum PriceChoice {

        ch1("Under $600"), ch2("$600-$700"), ch3("$700-$799"), ch4("$800-$899"), ch5("$900-$999"), ch6("Over $1000");

        private final String display;

        PriceChoice(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }

    }

    private Province province;

    private String city;

    private BedroomChoice bedrooms;

    private PriceChoice price;

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BedroomChoice getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(BedroomChoice bedrooms) {
        this.bedrooms = bedrooms;
    }

    public PriceChoice getPrice() {
        return price;
    }

    public void setPrice(PriceChoice price) {
        this.price = price;
    }

}
