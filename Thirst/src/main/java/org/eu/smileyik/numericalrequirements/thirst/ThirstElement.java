package org.eu.smileyik.numericalrequirements.thirst;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.element.AbstractElement;
import org.eu.smileyik.numericalrequirements.core.element.Element;
import org.eu.smileyik.numericalrequirements.core.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.element.handler.ElementHandler;
import org.eu.smileyik.numericalrequirements.core.extension.placeholderapi.ElementPlaceholder;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataValue;
import org.eu.smileyik.numericalrequirements.core.util.Pair;

public class ThirstElement extends AbstractElement {
    private final ConfigurationSection config;
    private final double upperBound;
    private final double naturalDepletion;
    private final ElementHandler handler;

    public ThirstElement(ThirstExtension extension, ConfigurationSection config, ElementHandler handler) {
        super(
                "ThirstElement",
                I18N.tr("extension.thirst.element.name"),
                extension.getInfo().getAuthor(),
                extension.getInfo().getVersion(),
                I18N.tr("extension.thirst.element.description")
        );
        this.config = config;
        this.upperBound = config.getDouble("thirst.max-value");
        this.naturalDepletion = config.getDouble("thirst.natural-depletion");
        this.handler = handler;
    }

    @Override
    protected void register() {
    }

    @Override
    protected void unregister() {

    }

    @Override
    public ElementData newElementData() {
        ThirstData thirstData = new ThirstData();
        thirstData.setRate(1);
        thirstData.setBounds(Pair.newPair(0D, upperBound));
        thirstData.setNaturalDepletionValue(naturalDepletion);
        thirstData.setValue(upperBound);
        return thirstData;
    }

    @Override
    public void handlePlayer(NumericalPlayer player, PlayerDataValue value) {
        handler.handlePlayer(player, value);
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

//papi parse SmileYik 属性值范围：[%nreq_ThirstElement_lower_bound%, %nreq_ThirstElement_upper_bound%] 自然流失：%nreq_ThirstElement_natural_depletion%每秒 上一值/现值：[%nreq_ThirstElement_previous%/%nreq_ThirstElement%(%nreq_ThirstElement_percentage%%)]
}
