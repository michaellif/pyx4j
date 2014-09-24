/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 31, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.rpc.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ConnectionTestResultDTO implements Serializable {

    private int progressPct;

    private String progressMessage = "";

    private String resultMessage = "";

    public static final String CR = "</br>\n";

    public static final String SP = "&nbsp;";

    public static final String OK = " <span style=\"color:#00CC33\">OK</span>";

    public static String htmlError(String text) {
        return "<span style=\"color:red\"> " + ((text != null) ? text.replace("\n", CR) : "") + " </span>";
    }

    public static String htmlWarn(String text) {
        return "<span style=\"color:#FF9933\"> " + text + " </span>";
    }

    public ConnectionTestResultDTO append(String message) {
        resultMessage += message;
        return this;
    }

    public ConnectionTestResultDTO ok() {
        return append(OK);
    }

    public ConnectionTestResultDTO sp() {
        return append(SP);
    }

    public ConnectionTestResultDTO cr() {
        return append(CR);
    }

    public ConnectionTestResultDTO error(String message) {
        return append(htmlError(message));
    }

    public ConnectionTestResultDTO warn(String message) {
        return append(htmlWarn(message));
    }

    public void setProgressPct(int pct) {
        progressPct = pct;
    }

    public int getProgressPct() {
        return progressPct;
    }

    public void setProgressMessage(String message) {
        progressMessage = message;
    }

    public String getProgressMessage() {
        return progressMessage;
    }

    @Override
    public String toString() {
        return resultMessage;
    }
}
