/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.validators;

import java.util.ArrayList;

import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CEmailField;

public class EmailListTextBoxValidator implements EditableValueValidator<String> {

    public static class EmailAddress {

        public String message;

        public boolean valid;

        public String email;

        public String personal;

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("valid ").append(valid);
            b.append(" email ").append(email);
            b.append(" personal ").append(personal);
            b.append(" message ").append(message);
            return b.toString();
        }
    }

    public EmailListTextBoxValidator() {
    }

    @Override
    public String getValidationMessage(CEditableComponent<String, ?> component, String value) {
        EmailAddress[] adr = parse(value);
        if (adr == null) {
            return null;
        } else {
            for (EmailAddress a : adr) {
                if (!a.valid) {
                    return a.message;
                }
            }
            return null;
        }
    }

    @Override
    public boolean isValid(CEditableComponent<String, ?> component, String value) {
        EmailAddress[] adr = parse(value);
        if (adr == null) {
            return true;
        } else {
            for (EmailAddress a : adr) {
                if (!a.valid) {
                    return false;
                }
            }
            return true;
        }
    }

    private enum State {
        ERROR, EMPTY, PERSONAL, EMAIL, SPACE
    };

    private static class ParsState {

        State state = State.EMPTY;

        int start_personal = -1;

        int end_personal = -1;

        int start_email = -1;

        int end_email = -1;

        void setState(State newState) {
            if (state == State.ERROR) {
                return;
            }
            switch (newState) {
            case PERSONAL:
                if ((start_personal >= 0) && (end_personal > 0)) {
                    state = State.ERROR;
                    return;
                }
            case EMAIL:
                if ((start_email >= 0) && (end_email > 0)) {
                    state = State.ERROR;
                    return;
                }
                break;
            }
            state = newState;
        }

        EmailAddress createAddress(String address, int from, int to) {
            if ((state == State.EMPTY) || (state == State.ERROR)) {
                if (from >= to) {
                    return null;
                } else {
                    String value = address.substring(from, to).trim();
                    if (value.length() == 0) {
                        return null;
                    }

                    EmailAddress a = new EmailAddress();
                    a.valid = value.matches(CEmailField.EMAIL_REGEXPR);
                    if (a.valid) {
                        a.email = value;
                    } else {
                        a.message = "Address {" + value + "} is not valid";
                    }
                    return a;
                }
            }

            EmailAddress a = new EmailAddress();

            if ((start_email >= 0) && (end_email > 0)) {
                a.email = address.substring(start_email, end_email);
            }

            a.valid = true;

            if ((start_personal >= 0) && (end_personal > 0)) {
                a.personal = address.substring(start_personal, end_personal);
            }

            if (a.email != null) {
                a.valid = a.email.matches(CEmailField.EMAIL_REGEXPR);
                if (!a.valid) {
                    a.message = "{" + a.email + "} not a valid e-mail";
                }
            } else {
                a.message = "Address {" + address.substring(from, to) + "} is not valid";
                a.valid = false;
            }

            return a;
        }
    }

    public static EmailAddress[] parse(String addresslist) {
        if (addresslist == null) {
            return null;
        }
        int length = addresslist.length();
        if (length == 0) {
            return null;
        }
        ArrayList<EmailAddress> r = new ArrayList<EmailAddress>();

        ParsState parsState = new ParsState();

        int processes = 0;
        for (int index = 0; index < length; index++) {
            char c = addresslist.charAt(index);
            switch (c) {
            case '<':
                if (parsState.state == State.EMAIL) {
                    parsState.setState(State.ERROR);
                } else {
                    parsState.setState(State.EMAIL);
                    parsState.start_email = index + 1;
                }
                break;
            case '>':
                if (parsState.state == State.EMAIL) {
                    parsState.end_email = index;
                    parsState.setState(State.SPACE);
                }
                break;

            case '"':
                if (parsState.state == State.PERSONAL) {
                    parsState.setState(State.SPACE);
                    parsState.end_personal = index;
                } else {
                    parsState.start_personal = index + 1;
                    parsState.setState(State.PERSONAL);
                }
                break;

            case ';':
            case ',':
                if ((parsState.state == State.EMPTY) || (parsState.state == State.SPACE) || (parsState.state == State.ERROR)) {
                    EmailAddress a = parsState.createAddress(addresslist, processes, index);
                    if (a != null) {
                        r.add(a);
                    }
                    parsState = new ParsState();
                    processes = index + 1;
                }
                break;

            // Ignore spaces
            case ' ':
            case '\t':
            case '\r':
            case '\n':
                break;
            }
        }
        EmailAddress last = parsState.createAddress(addresslist, processes, length);
        if (last != null) {
            r.add(last);
        }

        return r.toArray(new EmailAddress[r.size()]);
    }
}
