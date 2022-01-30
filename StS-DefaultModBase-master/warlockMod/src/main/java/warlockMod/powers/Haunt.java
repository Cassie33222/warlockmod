package warlockMod.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import warlockMod.WarlockMod;
import warlockMod.orbs.SoulShard;
import warlockMod.util.TextureLoader;

public class Haunt extends AbstractPower{
    public static final String POWER_ID = WarlockMod.makeID("Haunt");
    public int turnsremaining=1;
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex32 = TextureLoader.getTexture(WarlockMod.makePowerPath("haunt32.png"));
    private static final Texture tex84 = TextureLoader.getTexture(WarlockMod.makePowerPath("empty84.png"));


    public Haunt(AbstractCreature owner, int amount) {
        this.owner=owner;
        type = PowerType.DEBUFF;
        name = NAME;
        ID = POWER_ID;
        // We load those textures here.
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        turnsremaining=3;
        isTurnBased = true;

        updateDescription();
    }
    public void removeIfComplete(){
        //remove if duration complete
        if(turnsremaining==0){
            WarlockMod.cleansePower(this.owner, POWER_ID);
        }
    }
    @Override
    public void atStartOfTurn() {
        countDown();
        updateDescription();
        removeIfComplete();
    }
    public void countDown(){
        turnsremaining--;
        this.amount=turnsremaining;
    }
    @Override
    public void updateDescription() {
        this.description = ("Taking 35% increased damage from Affliction effects. "+turnsremaining+" turns remaining.");
        this.amount=turnsremaining;
    }
}
