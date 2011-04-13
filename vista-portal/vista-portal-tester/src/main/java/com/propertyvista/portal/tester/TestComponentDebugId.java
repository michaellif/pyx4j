/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 11, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.tester;

import com.pyx4j.commons.IDebugId;

public enum TestComponentDebugId implements IDebugId {

    CButton,

    CCheckBox,

    CComboBox,

    CDataPicker,

    CDoubleField,

    CEmailField,

    CHyperLink,

    CIntegerField,

    CLabel,

    CListBox,

    CLongField,

    CMonthYearPicker,

    CPasswordTextField,

    CRadioGroupInteger,

    CRichTextArea,

    CSuggestBox,

    CTextArea,

    CTextField,

    CTimeField;

    @Override
    public String debugId() {
        return this.name();
    }

}
