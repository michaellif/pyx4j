/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 26, 2011
 * @author kostya
 * @version $Id$
 */
package com.propertyvista.payment.caledon;
import com.propertyvista.payment.IToken;

public class CaledonToken implements IToken{
	public static final int STATUS_UNKNOWN=0;
	public static final int STATUS_ACTIVE=1;
	public static final int STATUS_INACTIVE=2;
//	public static final int STATUS_INACTIVE=2;
	
	
	private String token_id=null;
	private int state;
	
	public CaledonToken(String token_id, int state){
		setToken_id(token_id);
		setState(state);
	}

	public void setToken_id(String token_id) {
		this.token_id = token_id;
	}

	public String getToken_id() {
		return token_id;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}
	
	

}
