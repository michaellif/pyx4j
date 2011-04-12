/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 8, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.tester;

import com.pyx4j.commons.IDebugId;

public enum TesterDebugId implements IDebugId {

    TesterMainMenu,

    StartTestSufix,

    ComponentUnderTest,

    //TODO vadims: format and rename to Humanly readable names. NO need to flow  All Capitals rules
    BTN, TXTBOX, CHK, LBL, DTBOX, MANDATORY_CHK, VISITED_CHK, READONLY_CHK, EDITABLE_CHK, PANEL, MENU, VALUE, RAWVALUE, PRINTREPORT_BTN, DISABLED_CHK, WATERMARK_TXT, TONUM_TXT, FROMNUM_TXT, NOPOSTDATE_TXT, MAXLENGTH_TXT, CCOMP_STACK, FORM_STACK, F1_HREF,

    TestMessage,

    TestMessageClear;

    @Override
    public String debugId() {
        return this.name();
    }

}
