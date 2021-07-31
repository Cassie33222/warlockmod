package warlockMod.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import warlockMod.WarlockMod;
import warlockMod.util.TextureLoader;

public class Immolate extends WarlockDot{
    public static final String POWER_ID = WarlockMod.makeID("Immolate");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex32 = TextureLoader.getTexture(WarlockMod.makePowerPath("immolate32.png"));
    private static final Texture tex84 = TextureLoader.getTexture(WarlockMod.makePowerPath("empty84.png"));


    public Immolate(AbstractCreature owner, AbstractCreature source, int amount) {
        super(owner, source, amount);
        name = NAME;
        ID = POWER_ID;
        // We load those textures here.
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        turnsremaining=5;
        //this.description = ("Dealing [#87ceeb]"+this.amount+"[] [#EFC851]Affliction[] damage over [#87ceeb]"+turnsremaining+"[] remaining turns.");
        updateDescription();
    }
    public void animate(){
        WarlockMod.immolatetickgif.playCopyOnceOverCreature(owner);
    }
    @Override
    public void updateDescription() {
        this.description = ("Dealing [#87ceeb]"+this.amount+"[] [#EFC851]Destruction[] damage over [#87ceeb]"+turnsremaining+"[] remaining turns.");
    }
    @Override
    public Color getColor() {
        return WarlockMod.DOT_ORANGE;
    }

    @Override
    public AbstractPower makeCopy() {
        return new Immolate(owner, source, amount);
    }
}
