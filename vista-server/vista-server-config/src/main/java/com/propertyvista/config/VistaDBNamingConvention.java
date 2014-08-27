/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config;

import com.pyx4j.entity.rdb.dialect.NamingConventionOracle;
import com.pyx4j.entity.rdb.dialect.ShortWords;

public class VistaDBNamingConvention extends NamingConventionOracle {

    public VistaDBNamingConvention() {
        this(false);
    }

    public VistaDBNamingConvention(boolean hsql) {
        super(63, shortWords(), false, hsql ? true : false, hsql ? '_' : '$');
    }

    private static ShortWords shortWords() {
        ShortWords shortWords = new ShortWords();
        shortWords.add("discriminator", "discr");

        shortWords.add("billing_billing_cycle_billing_cycle_start_date_building_billing_type_idx", "billing_cycle_start_date_building_type_idx");
        shortWords.add("billing_billing_type_payment_frequency_billing_cycle_start_day_idx", "billing_type_payment_frequency_billing_cycle_start_day_idx");
        shortWords.add("payment_information_payment_method_billing_address_street_type_e_ck", "payment_information_payment_method_billing_addr_str_type_e_ck");
        shortWords.add("payment_information_payment_method_billing_address_street_direction_e_ck",
                "payment_information_payment_method_billing_addr_str_dir_e_ck");
        shortWords.add("funds_transfer_file_file_creation_number_funds_transfer_type_company_id_idx", "funds_transfer_file_creation_number_company_id_idx");
        shortWords.add("dev_card_service_simulation_merchant_account_terminal_id_company_idx", "dev_card_service_simulation_merchant_account_term_comp_idx");
        return shortWords;
    }

}
