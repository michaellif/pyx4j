/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.caledoneft;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.propertyvista.domain.financial.FundsTransferType;

public class CaledonFundsTransferDirectories {

    /**
     * This is the list of directories
     * 
     * dbp_in
     * dbp_out
     * 
     * 
     * iop_in
     * iop_out
     * 
     * pad_in
     * pad_out
     */

    public static final String postDst = "in";

    public static final String getSrc = "out";

    private static final Map<String, FundsTransferType> postDirectories = buildDirectories(postDst);

    private static final Map<String, FundsTransferType> getDirectories = buildDirectories(getSrc);

    private static Map<String, FundsTransferType> buildDirectories(String name) {
        Map<String, FundsTransferType> directories = new HashMap<String, FundsTransferType>();
        for (FundsTransferType fundsTransferType : FundsTransferType.values()) {
            directories.put(fundsTransferType.getDirectoryName(name), fundsTransferType);
        }
        return directories;
    }

    public static FundsTransferType getFundsTransferTypeByDirectory(String directoryName) {
        FundsTransferType fundsTransferType = getDirectories.get(directoryName);
        if (fundsTransferType == null) {
            fundsTransferType = postDirectories.get(directoryName);
        }
        return fundsTransferType;
    }

    public static String[] allPostDirectories() {
        Collection<String> directories = postDirectories.keySet();
        return directories.toArray(new String[directories.size()]);
    }

    public static String[] allGetDirectories() {
        Collection<String> directories = getDirectories.keySet();
        return directories.toArray(new String[directories.size()]);
    }
}
