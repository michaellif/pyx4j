/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-05
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.themes;

import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public interface VistaStyles {

    public static interface ApartmentUnits {

        public final static String StylePrefix = "ApartmentViewForm";

        public static enum StyleSuffix implements IStyleSuffix {
            UnitListHeader, SelectedUnit, unitRowPanel, unitDetailPanel
        }

        public static enum StyleDependent implements IStyleDependent {
            selected, disabled, hover
        }

    }

}
