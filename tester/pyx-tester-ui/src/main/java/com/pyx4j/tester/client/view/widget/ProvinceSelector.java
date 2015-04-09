package com.pyx4j.tester.client.view.widget;

import com.pyx4j.tester.client.domain.test.TestCountry;
import com.pyx4j.tester.client.domain.test.TestProvince;

public class ProvinceSelector extends CAddressStateComboBox<String, TestProvince> {

    public ProvinceSelector() {
        super(TestProvince.class);
    }

    public void setCountry(TestCountry country) {
        setTextMode(false);
        setOptions(country.provinces());
    }

    @Override
    public String convertOption(TestProvince prov) {
        return prov == null ? "" : prov.name().getValue();
    }

    @Override
    public String formatValue(String value) {
        return value == null ? "" : value;
    }

    @Override
    public String parseValue(String text) {
        return text == null ? "" : text;
    }
}
