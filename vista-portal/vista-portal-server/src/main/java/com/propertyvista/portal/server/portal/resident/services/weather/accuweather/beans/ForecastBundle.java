/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.weather.accuweather.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlRootElement
public class ForecastBundle {

    protected List<Forecast> forecast;

    public ForecastBundle() {
    }

    public List<Forecast> getForecast() {
        if (forecast == null) {
            forecast = new ArrayList<Forecast>();
        }
        return forecast;
    }

    public void setForecast(List<Forecast> forecast) {
        this.forecast = forecast;
    }

    @XmlAccessorType(XmlAccessType.NONE)
    public static class Forecast {

        @XmlElement(name = "DateTime")
        protected XMLGregorianCalendar dateTime;

        @XmlElement(name = "EpochDateTime")
        protected long epochDateTime;

        @XmlElement(name = "WeatherIcon")
        protected int weatherIcon;

        @XmlElement(name = "IconPhrase")
        protected String iconPhrase;

        @XmlElement(name = "Temperature")
        protected Temperature temperature;

        @XmlElement(name = "PrecipitationProbability")
        protected int precipitationProbability;

        @XmlElement(name = "MobileLink")
        protected String mobileLink;

        @XmlElement(name = "Link")
        protected String link;

        public Forecast() {
        }

        public XMLGregorianCalendar getDateTime() {
            return dateTime;
        }

        public void setDateTime(XMLGregorianCalendar dateTime) {
            this.dateTime = dateTime;
        }

        public long getEpochDateTime() {
            return epochDateTime;
        }

        public void setEpochDateTime(long epochDateTime) {
            this.epochDateTime = epochDateTime;
        }

        public int getWeatherIcon() {
            return weatherIcon;
        }

        public void setWeatherIcon(int weatherIcon) {
            this.weatherIcon = weatherIcon;
        }

        public String getIconPhrase() {
            return iconPhrase;
        }

        public void setIconPhrase(String iconPhrase) {
            this.iconPhrase = iconPhrase;
        }

        public Temperature getTemperature() {
            return temperature;
        }

        public void setTemperature(Temperature temperature) {
            this.temperature = temperature;
        }

        public int getPrecipitationProbability() {
            return precipitationProbability;
        }

        public void setPrecipitationProbability(int precipitationProbability) {
            this.precipitationProbability = precipitationProbability;
        }

        public String getMobileLink() {
            return mobileLink;
        }

        public void setMobileLink(String mobileLink) {
            this.mobileLink = mobileLink;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Temperature {

            @XmlElement(name = "Value")
            protected double temperatureValue;

            @XmlElement(name = "Unit")
            protected String unit;

            @XmlElement(name = "UnitType")
            protected int unitType;

            public Temperature() {
            }

            public double getTemperatureValue() {
                return this.temperatureValue;
            }

            public void setTemperatureValue(double temperatureValue) {
                this.temperatureValue = temperatureValue;
            }

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }

            public int getUnitType() {
                return this.unitType;
            }

            public void setUnitType(int unitType) {
                this.unitType = unitType;
            }
        }

    }
}
