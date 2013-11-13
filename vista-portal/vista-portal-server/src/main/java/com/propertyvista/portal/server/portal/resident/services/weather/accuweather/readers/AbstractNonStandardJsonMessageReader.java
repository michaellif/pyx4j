/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-31
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.weather.accuweather.readers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Scanner;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONUnmarshaller;

/**
 * Intended to fix the non standard JSON that doesn't enclose value names with double quotes.
 */
public class AbstractNonStandardJsonMessageReader<E> implements MessageBodyReader<E> {

    private static final Logger log = LoggerFactory.getLogger(AbstractNonStandardJsonMessageReader.class);

    private final Class<E> beanClazz;

    private String topLevelArrayFieldName;

    public AbstractNonStandardJsonMessageReader(Class<E> beanClazz) {
        this.beanClazz = beanClazz;
    }

    /**
     * Intended to be used with JSON lists: wraps an array literal into an object literal assigning it to the <code>fieldName</code>, for example:
     * 
     * <pre>
     * [ { foo: "1", bar : "bla-bla" }, { foo: "2", bar: "another bla-bla} ]
     * </pre>
     * 
     * will be turned into:
     * 
     * <pre>
     * {
     *   "fieldName" : [ { foo: "1", bar : "bla-bla" }, { foo: "2", bar: "another bla-bla} ]
     * }
     * </pre>
     */
    public AbstractNonStandardJsonMessageReader<E> assignTopLevelArrayTo(String fieldName) {
        this.topLevelArrayFieldName = fieldName;
        return this;
    }

    @Override
    public boolean isReadable(Class<?> arg0, Type arg1, Annotation[] arg2, MediaType arg3) {
        return beanClazz.equals(arg0);
    }

    @Override
    public E readFrom(Class<E> arg0, Type arg1, Annotation[] arg2, MediaType arg3, MultivaluedMap<String, String> arg4, InputStream in) throws IOException,
            WebApplicationException {

        E result = null;
        Scanner sc = new Scanner(in);
        sc.useDelimiter("\\A");
        String json = sc.next();
        StringReader fixedJsonReader = null;

        JSONJAXBContext jCtx;
        try {
            jCtx = new JSONJAXBContext(JSONConfiguration.mapped().build(), beanClazz);
            JSONUnmarshaller unmarshaller = jCtx.createJSONUnmarshaller();
            String fixedJson = json.replaceAll("\\s([a-zA-Z]+)\\s*:", "\"$1\" :");
            if (topLevelArrayFieldName != null) {
                fixedJson = "{ \"" + topLevelArrayFieldName + "\" : " + fixedJson + " }";
            }
            fixedJsonReader = new StringReader(fixedJson);
            result = unmarshaller.unmarshalFromJSON(fixedJsonReader, beanClazz);
        } catch (JAXBException e) {
            log.error("Failed to parse the following text into " + beanClazz.getSimpleName() + "\n" + json + "\n", e);
        } finally {
            IOUtils.closeQuietly(fixedJsonReader);
            IOUtils.closeQuietly(sc);
        }

        return result;
    }

}
