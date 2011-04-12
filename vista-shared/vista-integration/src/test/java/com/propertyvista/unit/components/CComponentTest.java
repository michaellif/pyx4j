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
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.unit.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.gwt.user.client.ui.UIObject;
import com.propertyvista.portal.tester.TestComponentDebugId;
import com.propertyvista.portal.tester.TesterDebugId;
import com.propertyvista.portal.tester.util.Constants;
import com.propertyvista.unit.VistaDevLogin;
import com.propertyvista.unit.config.ApplicationId;
import com.propertyvista.unit.config.VistaSeleniumTestConfiguration;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.selenium.BaseSeleniumTestCase;
import com.pyx4j.selenium.ISeleniumTestConfiguration;

public class CComponentTest extends BaseSeleniumTestCase {

    //TODO vadims: Remove this 
    private final String PYX_DEBUG_PRFX = UIObject.DEBUG_ID_PREFIX + Constants.DEBUG_ID_PRFX;

    //TODO vadims: Remove this, See all oveloaded methods we have in class SeleniumExtended
    private final String TESTED_COMPONENT = TesterDebugId.ComponentUnderTest.debugId();

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new VistaSeleniumTestConfiguration(ApplicationId.tester);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        VistaDevLogin.login(selenium);
        selenium.waitFor(new CompositeDebugId(TesterDebugId.TesterMainMenu, "text0"));
        selenium.click(new CompositeDebugId(TesterDebugId.TesterMainMenu, "text0"));

    }

    public void testCButton() throws Exception {
        //TODO finish it up
        //final String el = "CButton-";
        //selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
        selenium.click(TestComponentDebugId.CButton, TesterDebugId.StartTestSufix);
    }

    public void testCCheckBox() throws Exception {
        final String el = "CCheckBox-";
        //WAS: selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
        //I think this is better, because more consistend with all other use of debugIds.
        selenium.click(TestComponentDebugId.CCheckBox, TesterDebugId.StartTestSufix);

        //Enable/Disable
        //tstEnableDisable(TESTED_COMPONENT + "-input", PYX_DEBUG_PRFX + TesterDebugId.DISABLED_CHK.debugId() + "-input");
        //NOW: How I(VladS) see the same function
        assertEnabled(TesterDebugId.ComponentUnderTest);
        selenium.click(TesterDebugId.DISABLED_CHK);
        assertNotEnabled(TesterDebugId.ComponentUnderTest);
        selenium.click(TesterDebugId.DISABLED_CHK);

        //Read-only
        //tstReadOnly(TESTED_COMPONENT + "-input", PYX_DEBUG_PRFX + TesterDebugId.READONLY_CHK.debugId() + "-input");

    }

    public void testCComboBox() throws Exception {
        final String el = "CComboBox-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
        //Enable/Disable
        tstEnableDisable(TESTED_COMPONENT, PYX_DEBUG_PRFX + TesterDebugId.DISABLED_CHK.debugId() + "-input");
        //TODO mandatory
        //Read-only
        tstReadOnly(TESTED_COMPONENT, PYX_DEBUG_PRFX + TesterDebugId.READONLY_CHK.debugId() + "-input");

    }

    public void testCDatePicker() throws Exception {
        final String el = "CDatePicker-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void testCDoubleField() throws Exception {
        final String el = "CDoubleField-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void testCEmailField() throws Exception {
        final String el = "CEmailField-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void testCHyperlink() throws Exception {
        final String el = "CHyperlink-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void testCIntegerField() throws Exception {
        final String el = "CIntegerField-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void testCLabel() throws Exception {
        final String el = "CLabel-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void testCListBox() throws Exception {
        final String el = "CListBox-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void testCLongField() throws Exception {
        final String el = "CLongField-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void testCMonthYearPicker() throws Exception {
        final String el = "CMonthYearPicker-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void testCPasswordTextField() throws Exception {
        final String el = "CPasswordTextField-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void testCRadioGroupInteger() throws Exception {
        final String el = "CRadioGroupInteger-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void testCRichTextArea() throws Exception {
        final String el = "CRichTextArea-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void ttestCSuggestBox() throws Exception {
        final String el = "CSuggestBox-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void testCTextArea() throws Exception {
        final String el = "CTextArea-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void ttestCTextField() throws Exception {
        final String el = "CTextField-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    public void testCTimeField() throws Exception {
        final String el = "CTimeField-";
        selenium.click(By.id(PYX_DEBUG_PRFX + el + TesterDebugId.StartTestSufix.debugId()));
    }

    private void tstEnableDisable(String testedcId, String testedfId) {
        assertEnabled(testedcId);
        selenium.click(By.id(testedfId));
        assertFalse(selenium.isEnabled(testedcId));
        selenium.click(By.id(testedfId));
    }

    /**
     * TODO finish
     * 
     * @param testedcId
     * @param testedfId
     */
    private void tstMandatory(String testedcId, String testedfId) {
        selenium.click(By.id(testedfId));
        WebElement wel = selenium.findElement(By.id(testedcId));
        // selenium.get(paramString)
        // assertFalse(selenium.isEnabled(testedcId));
        selenium.click(By.id(testedfId));
    }

    private void tstReadOnly(String testedcId, String testedfId) {
        assertEditable(testedcId);
        selenium.click(By.id(testedfId));
        assertFalse(selenium.isEditable(testedcId));
        selenium.click(By.id(testedfId));
    }

    private void ttstReadOnly(String testedcId, String testedfId, String value) {
        String tstvalue = String.valueOf(Math.random());
        assertEditable(testedcId);
        selenium.setValue(testedcId, value);
        selenium.click(By.id(testedfId));
        selenium.setValue(testedcId, tstvalue);
        WebElement wel = selenium.findElement(By.id(testedcId));
        assertTrue(wel.getValue().equals(value));
        selenium.click(By.id(testedfId));
    }

}
