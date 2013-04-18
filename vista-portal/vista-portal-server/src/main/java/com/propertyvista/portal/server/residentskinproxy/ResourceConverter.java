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
package com.propertyvista.portal.server.residentskinproxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.regex.Pattern;

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
            if (typeAndEncoding[1].startsWith("charset=")) {
                encoding = typeAndEncoding[1].substring(8);
            }
        }

        InputStream in = null;
        try {
            String host = method.getURI().getHost();
            in = method.getResponseBodyAsStream();

            if (contentType.equalsIgnoreCase("text/css")) {
                String rcPath = method.getURI().getPath();
                rcPath = rcPath.substring(0, rcPath.lastIndexOf("/"));
                return convertCSS(in, encoding, host, rcPath);
            }
        } catch (IOException ex) {
        }

        return in;
    }

    public static InputStream convertCSS(InputStream in, String encoding, String host, String rcPath) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(in, writer, encoding);
        String out = writer.toString();

        // relative urls should be prefixed with the css resource path
        // replace1: url("<path>") -> url("/<rcPath>/<path>")
        String regex = "url *\\( *([\"']?)([^/])";
        String replace = "url($1" + rcPath + "/$2";
        out = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(out).replaceAll(replace);

        // replace2: url("/<path>") -> url("<proxyPrefix>/<resourceHost>/<path>")
        regex = "url *\\( *([\"']?)/";
        replace = "url($1" + DeploymentConsts.portalInectionProxy + host + "/";
        out = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(out).replaceAll(replace);

        return new ByteArrayInputStream(out.getBytes(encoding));
    }
}
