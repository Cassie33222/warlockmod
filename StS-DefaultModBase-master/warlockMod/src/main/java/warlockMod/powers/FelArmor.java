package warlockMod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import warlockMod.WarlockMod;
import warlockMod.util.TextureLoader;

//Gain 1 dex for the turn for each card played.

public class FelArmor extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = WarlockMod.makeID("FelArmor");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex32 = TextureLoader.getTexture(WarlockMod.makePowerPath("felarmor32.png"));
    private static final Texture tex84 = TextureLoader.getTexture(WarlockMod.makePowerPath("empty84.png"));

    public FelArmor(final AbstractCreature owner, final int amount) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;
        type = PowerType.BUFF;
        isTurnBased = false;

        // We load those txtures here.
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
        //description = DESCRIPTIONS[0];
        //this.description = DESCRIPTIONS[0]+this.amount+".[]";
        //this.description = String.format(DESCRIPTIONS[0], this.amount);

        //this.description = "Granting [#87ceeb]"+this.amount+"%[] increased self-healing. [#EFC851]Demonology[].";
        this.updateDescription();
    }

    @Override
    public void updateDescription(){
        description="Granting [#87ceeb]"+this.amount+"%[] increased self-healing. [#EFC851]Demonology[].";
    }
    @Override
    public AbstractPower makeCopy() {
        return new FelArmor(owner, amount);
    }
}
