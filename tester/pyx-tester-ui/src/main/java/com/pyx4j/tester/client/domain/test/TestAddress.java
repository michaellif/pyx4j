package com.pyx4j.tester.client.domain.test;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface TestAddress extends IEntity {

    @NotNull
    @ToString(index = 0)
    @Caption(name = "Street Address")
    IPrimitive<String> addressLine1();

    @ToString(index = 1)
    @Caption(name = "")
    IPrimitive<String> addressLine2();

    @NotNull
    @ToString(index = 2)
    IPrimitive<String> city();

    @NotNull
    @ToString(index = 3)
    @Caption(name = "Region/Province/State")
    IPrimitive<String> region();

    @NotNull
    @ToString(index = 4)
    @Editor(type = EditorType.combo)
    TestCountry country();

    @NotNull
    @ToString(index = 5)
    IPrimitive<String> postalCode();
}
