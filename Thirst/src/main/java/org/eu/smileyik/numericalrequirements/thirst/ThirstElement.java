package org.eu.smileyik.numericalrequirements.thirst;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.element.AbstractElement;
import org.eu.smileyik.numericalrequirements.core.element.Element;
import org.eu.smileyik.numericalrequirements.core.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.extension.placeholderapi.ElementPlaceholder;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataValue;
import org.eu.smileyik.numericalrequirements.core.util.Pair;

public class ThirstElement extends AbstractElement {
    public ThirstElement() {
        super("ThirstElement", "Thirst", "SmileYik", "1.0", "aaa");
    }

    @Override
    protected void register() {
        System.out.println("口渴要素被启用！");
    }

    @Override
    protected void unregister() {

    }

    @Override
    public ElementData newElementData() {
        ThirstData thirstData = new ThirstData();
        thirstData.setRate(1);
        thirstData.setBounds(Pair.newPair(0D, 200D));
        thirstData.setNaturalDepletionValue(1.0);
        thirstData.setValue(100D);
        return thirstData;
    }

    @Override
    public void handlePlayer(NumericalPlayer player, PlayerDataValue value) {
        ThirstData thirstData = (ThirstData) value;
//        player.getPlayer().sendMessage(String.format(
//                "%s: %.2f/%.2f%n",
//                player.getPlayer().getName(),
//                thirstData.getValue(),
//                thirstData.getUpperBound()
//        ));
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

//    @Override
//    public String onRequestPlaceholder(ElementData data) {
//        return String.format("%.2f", ((ThirstData) data).getValue());
//    }
//papi parse SmileYik 属性值范围：[%nreq_ThirstElement_lower_bound%, %nreq_ThirstElement_upper_bound%] 自然流失：%nreq_ThirstElement_natural_depletion%每秒 上一值/现值：[%nreq_ThirstElement_previous%/%nreq_ThirstElement%(%nreq_ThirstElement_percentage%%)]
//    @Override
//    public String onRequestPlaceholder(ElementData data, String args) {
//        if (args.equalsIgnoreCase("percentage")) {
//            return String.format("%.2f", ((ThirstData) data).getValueOfUpperBound() * 100);
//        }
//        return onRequestPlaceholder(data);
//    }
}
