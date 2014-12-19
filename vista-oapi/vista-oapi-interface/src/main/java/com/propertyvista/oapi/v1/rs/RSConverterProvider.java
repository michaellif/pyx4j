/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 19, 2014
 * @author stanp
 */
package com.propertyvista.oapi.v1.rs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.dto.PropertySearchCriteria.BathroomChoice;
import com.propertyvista.dto.PropertySearchCriteria.BedroomChoice;

@Provider
public class RSConverterProvider implements ParamConverterProvider {

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (LogicalDate.class.isAssignableFrom(rawType)) {
            return (ParamConverter<T>) new LogicalDateConverter();
        } else if (BedroomChoice.class.isAssignableFrom(rawType)) {
            return (ParamConverter<T>) new BedroomChoiceConverter();
        } else if (BathroomChoice.class.isAssignableFrom(rawType)) {
            return (ParamConverter<T>) new BathroomChoiceConverter();
        }
        return null;
    }

    static class LogicalDateConverter implements ParamConverter<LogicalDate> {
        private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public LogicalDate fromString(String value) {
            Date date = null;
            try {
                date = format.parse(value);
            } catch (ParseException e) {
                date = new Date();
            }
            return new LogicalDate(date);
        }

        @Override
        public String toString(LogicalDate value) {
            return format.format(value);
        }
    }

    static class BedroomChoiceConverter implements ParamConverter<BedroomChoice> {

        @Override
        public BedroomChoice fromString(String value) {
            try {
                return BedroomChoice.getChoice(Integer.valueOf(value));
            } catch (Throwable t) {
                return BedroomChoice.Any;
            }
        }

        @Override
        public String toString(BedroomChoice value) {
            return value.getBeds().toString();
        }
    }

    static class BathroomChoiceConverter implements ParamConverter<BathroomChoice> {

        @Override
        public BathroomChoice fromString(String value) {
            try {
                return BathroomChoice.getChoice(Integer.valueOf(value));
            } catch (Throwable t) {
                return BathroomChoice.Any;
            }
        }

        @Override
        public String toString(BathroomChoice value) {
            return value.getBaths().toString();
        }

    }
}
