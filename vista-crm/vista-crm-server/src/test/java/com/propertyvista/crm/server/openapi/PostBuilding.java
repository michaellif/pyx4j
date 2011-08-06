/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 5, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.crm.server.openapi;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.gwt.server.IOUtils;

public class PostBuilding {
    private final static Logger log = LoggerFactory.getLogger(PostBuilding.class);

    public void testPost() {
        try {

            String xml = IOUtils.getTextResource("building.xml", BuildingsResource.class);

            log.info("Posting");
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://localhost:8888/vista/rest/buildings");
            request.addHeader("Content-Type", "application/xml;charset=utf-8");

            HttpEntity entitySent;
            entitySent = new StringEntity(xml);
            request.setEntity(entitySent);
            client.execute(request);
        } catch (Exception e) {
            log.error("Failure to post", e);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        PostBuilding post = new PostBuilding();
        post.testPost();
    }

}
