package warlockMod.powers;

import com.badlogic.gdx.graphics.Color;
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

public class Shadowburn extends AbstractPower{
    public static final String POWER_ID = WarlockMod.makeID("Shadowburn");
    public int turnsremaining=1;
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex32 = TextureLoader.getTexture(WarlockMod.makePowerPath("shadowburn32.png"));
    private static final Texture tex84 = TextureLoader.getTexture(WarlockMod.makePowerPath("empty84.png"));


    public Shadowburn(AbstractCreature owner, AbstractCreature source, int amount) {
        this.owner=owner;
        type = PowerType.BUFF;
        name = NAME;
        ID = POWER_ID;
        // We load those textures here.
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        turnsremaining=1;
        isTurnBased = true;
        //this.description = ("Dealing [#87ceeb]"+this.amount+"[] [#EFC851]Affliction[] damage over [#87ceeb]"+turnsremaining+"[] remaining turns.");
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
    @Override
    public void onDeath(){
        AbstractDungeon.player.channelOrb(new SoulShard());
    }
    public void countDown(){
        turnsremaining--;
    }
    @Override
    public void updateDescription() {
        this.description = ("Grants 1 [#EFC851]Soul[] [#EFC851]Shard[] if it dies this turn.");
    }
}
