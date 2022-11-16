package com.wingedtech.common.sensitivity;

import com.google.common.collect.Lists;
import com.wingedtech.common.sensitivity.annotation.SensitivityMark;
import com.wingedtech.common.sensitivity.annotation.SensitivityProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.Test;

import java.util.List;

/**
 * @author 6688SUN
 */
public class SensitivityUtilsTest {

    @Test
    public void desensitizer() {
        // doc1
        GenericA subDoc1 = new GenericA();
        subDoc1.setUsername("王二");
        subDoc1.setPhone("18100989980");
        subDoc1.setAddress("四川天府新区1001号");


        GenericA subDoc2 = new GenericA();
        subDoc2.setUsername("李二");
        subDoc2.setPhone("18200989980");
        subDoc2.setAddress("四川天府新区1002号");

        Doc doc1 = new Doc();
        doc1.setUsername("大熊猫");
        doc1.setPhone("18366886688");
        doc1.setAddress("四川天府新区0001号");
        doc1.setGenericDocList(Lists.newArrayList(subDoc1, subDoc2));

        // doc2
        GenericA subDoc11 = new GenericA();
        subDoc11.setUsername("王小二");
        subDoc11.setPhone("19100989980");
        subDoc11.setAddress("四川天府新区1001号");

        GenericA subDoc12 = new GenericA();
        subDoc12.setUsername("李小二");
        subDoc12.setPhone("19200989980");
        subDoc12.setAddress("四川天府新区1002号");

        Doc doc2 = new Doc();
        doc2.setUsername("小熊猫");
        doc2.setPhone("19366886688");
        doc2.setAddress("四川天府新区0001号");
        doc2.setGenericDocList(Lists.newArrayList(subDoc11, subDoc12));

        List<Doc> content = Lists.newArrayList(doc1, doc2);

//        SensitivityUtils.desensitizer(content);
//
//        content.forEach(System.out::println);
        SensitivityUtils.desensitizer(doc1);
        System.out.println(doc1);

    }

    @Data
    static class Custom implements SensitivityCustomStrategy<Object> {

        @Override
        public Object desensitizer(Object t) {
            return String.valueOf(t).replaceAll("(\\S{2})\\S{2}(\\S*)\\S{2}", "$1*自定义$2*");
        }
    }

    @Data
    static class GenericDoc {
        @SensitivityProperty(strategy = SensitivityStrategy.PHONE)
        private String phone;

        @SensitivityProperty(useCustom = true, custom = Custom.class)
        private String address;
    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    static class Doc extends GenericDoc {
        @SensitivityProperty(strategy = SensitivityStrategy.USERNAME)
        private String username;

        @SensitivityMark
        private List<GenericDoc> genericDocList;
    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    static class GenericA extends GenericDoc {
        @SensitivityProperty
        private String username;
    }
}
