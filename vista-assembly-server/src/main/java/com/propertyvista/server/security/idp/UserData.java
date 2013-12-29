/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-24
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.security.idp;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserData implements Serializable {

    String userSelectedClaimedId;

    Boolean authenticatedAndApproved;

    String email;
}
