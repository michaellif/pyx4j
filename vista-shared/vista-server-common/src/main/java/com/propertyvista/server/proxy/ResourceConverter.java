/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 13, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.server.proxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.io.IOUtils;

import com.propertyvista.portal.rpc.DeploymentConsts;

public class ResourceConverter {

    private static final String ContentType = "text";

    private static final String Encoding = "UTF-8";

    public static InputStream convert(HttpMethodBase method) {
        Header contentTypeHdr = method.getResponseHeader("Content-Type");
        String contentType = contentTypeHdr == null ? ContentType : contentTypeHdr.getValue();
        String[] typeAndEncoding = contentType.split(";");
        String encoding = Encoding;
        if (typeAndEncoding.length > 1) {
            contentType = typeAndEncoding[0];
            encoding = typeAndEncoding[1];
        }

        InputStream in = null;
        try {
            String host = method.getURI().getHost();
            in = method.getResponseBodyAsStream();

            if (contentType.equals("text/css")) {
                return convertCSS(in, encoding, host);
            }
        } catch (IOException ex) {
        }

        return in;
    }

    public static InputStream convertCSS(InputStream in, String encoding, String host) throws IOException {
        // replace: url("/<path>") -> url("<proxyPrefix>/<resourceHost>/<path>")
        String regex = "url *\\( *([\"']?)/";
        String replace = "url($1" + DeploymentConsts.portalInectionProxy + host + "/";

        StringWriter writer = new StringWriter();
        IOUtils.copy(in, writer, encoding);
        String out = writer.toString();
        out = out.toLowerCase().replaceAll(regex, replace);
        return new ByteArrayInputStream(out.getBytes(encoding));
    }
}
