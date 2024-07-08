package org.eu.smileyik.numericalrequirements.core.tag;

import org.eu.smileyik.numericalrequirements.core.item.tagold.service.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoreTagTest {

    LoreTagService service = new SimpleLoreTagService();

    @Test
    public void test1() {
        List<String> testStringList = Arrays.asList(
                "&4服用后将在未来&babc&4秒内减轻&b吃饭的效果！",
                "&4服用后将在未来&b吧唧吧唧&4秒内减轻&b吃饭的效果！",
                "&4服用后将在未来&b1&4秒内减轻&b啊吧啊吧的效果！",
                "&4将在未来&b吧唧吧唧&4秒内减轻&b吃饭的效果如果你服用的话！",
                "&4将在未来&b1&4秒内减轻&b吃饭的效果如果你服用的话！",
                "&4服用后将在未来&b1.2&4秒内减轻&b啊吧啊吧的效果！",
                "&4服用后将在未来&b.2&4秒内减轻&b啊吧啊吧的效果！",
                "&4服用后将在未来&b1.&4秒内减轻&b啊吧啊吧的效果！",
                "&4服用后将在未来&b.&4秒内减轻&b啊吧啊吧的效果！"
        );
        LoreTagPattern pattern = service.compile(
                "&4服用后将在未来&b<%:num>&4秒内减轻&b<%:str>的效果！");
        for (String test : testStringList) {
            if (pattern.matches(test)) {
                LoreTagValue valueList = pattern.getValue(test);
                List<String> strings = new ArrayList<>();
                for (LoreTagTypeValue value : valueList) {
                    strings.add(value.getValueString());
                }
                System.out.println(strings);
            } else {
                System.out.println("Not Matches!");
            }
        }
        System.out.println(pattern.buildLoreByStringList(
                Arrays.asList("2", "吧唧吧唧")
        ));
    }

    @Test
    public void test2() {
        List<String> testStringList = Arrays.asList(
                "12345111111111",
                "123451.11111111",
                "12345.11111111",
                "12345.111111111",
                "123451.111111111",
                "12.545.111111111",
                "121.545.111111111",
                "121.45.111111111",
                "120450.111111111"
        );
        LoreTagPattern pattern = service.compile("12<%:int>45<%:num>11111111");
        for (String test : testStringList) {
            if (pattern.matches(test)) {
                LoreTagValue valueList = pattern.getValue(test);
                List<String> strings = new ArrayList<>();
                for (LoreTagTypeValue value : valueList) {
                    strings.add(value.getValueString());
                }
                System.out.println(strings);
            } else {
                System.out.println("Not Matches!");
            }
        }
    }

    @Test
    public void test3() {
        List<String> testStringList = Arrays.asList(
                "&4服用后将在未来&babc&4秒内减轻&b吃饭的效果！",
                "&4服用后将在未来&b吧唧吧唧合成时不被消耗&4秒内减轻&b吃饭的效果！",
                "&4服用后将在未来&b1合成时不被消耗&4秒内减轻&b啊吧啊吧的效果！",
                "&4将在未来&b吧唧吧唧&4秒内减轻&合成时不被消耗b吃饭的效果如果你服用的话！",
                "&4将在未来&b1&4秒内减轻&b吃饭的效果如果你服用的话！",
                "&4服用后将在未来&b1.2&4秒内减轻&b啊吧啊吧的效果！",
                "&4服用后将在未来&b.2&4秒内减轻&b啊吧啊吧的效果！",
                "&4服用合成时不被消耗后将在未来&b1.&4秒内减轻&b啊吧啊吧的效果！",
                "合成时不被消耗"
        );
        LoreTagPattern pattern = service.compile("合成时不被消耗");
        for (String test : testStringList) {
            if (pattern.matches(test)) {
                LoreTagValue valueList = pattern.getValue(test);
                List<String> strings = new ArrayList<>();
                for (LoreTagTypeValue value : valueList) {
                    strings.add(value.getValueString());
                }
                System.out.println(strings);
            } else {
                System.out.println("Not Matches!");
            }
        }
    }
}
