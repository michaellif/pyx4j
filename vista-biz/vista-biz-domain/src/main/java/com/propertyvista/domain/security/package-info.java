/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 10, 2012
 * @author vlads
 * @version $Id$
 *
 * <pre>
 
Behaviors are objects instances that are comparable. There are single instance of each Behavior.
The simplest Java implementation for this are Strings or Enums
Hare are Behavior enums used in vista

1 - VistaApplication identify application  e.g. CRM, Portal ...
      Assigned to session on session start
      Block session access from one application to another.  e.g. Tenant access to CRM.
      No other permissions (CRUD or Service) are assigned to this Behavior.
      The same enum used Identify different vista applications in URLS and email templates.
    
2.  VistaBasicBehavior  (this also has all vista applications).
     One of VistaBasicBehavior assigned on session start.  but may be not application Behavior.
     Some basic permissions are assigned to this Behavior.
     VistaBasicBehavio.${application}  assigned to session on session approval e.g. after  PasswordChangeRequired was completed.
     VistaBasicBehavior is not attached to Roles.

3.A.  VistaCrmBehavior drive all permission in CRM, The only one shown to user.
        VistaCrmBehavior is attached to Roles.  And is shown in Roles UI as permissions .

3.B
      There are dynamic part of it :  VistaDataAccessBehavior
      Assigned base on user properties.

4.  VistaOperationsBehavior  drive all permission in Operations
     Assigned to users directly.   Act as hard-coded Role  in Operations
5.A  VistaCustomerBehavior  derives Portal and Prospect
     VistaCustomerBehavior  is not attached to Roles sine there are no configurable roles in portal.
     VistaCustomerBehavior is dynamically assigned  base on customer roles e.g. Tenant, CoTenant ....

5.B
      VistaCustomerPaymentTypeBehavior  adds  different permissions for UI in portal base on PMC configuration
</pre>
 * 
 */
package com.propertyvista.domain.security;