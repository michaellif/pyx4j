/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.blob;

import org.apache.commons.codec.digest.DigestUtils;

import com.propertyvista.domain.File;

public class ETag {

    public static String getEntityTag(File file, Object variation) {
        StringBuilder b = new StringBuilder();
        b.append(file.blobKey().getValue()).append("-");
        b.append(file.fileSize().getValue()).append("-");
        b.append(file.timestamp().getValue()).append("-");
        b.append(variation);
        return "\"" + DigestUtils.md5Hex(b.toString()) + "\"";
    }

    public static boolean checkIfNoneMatch(String currentToken, String headerValue) {
        if (headerValue == null) {
            return false;
        }
        if (headerValue.equals("*")) {
            return true;
        }
        String[] tokens = headerValue.split(",");
        for (String token : tokens) {
            if (token.trim().equals(currentToken)) {
                return true;
            }
        }

        return false;
    }
}
