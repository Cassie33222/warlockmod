package warlockMod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import warlockMod.WarlockMod;
import warlockMod.cards.DefaultRareAttack;
import warlockMod.util.TextureLoader;

public class Corruption extends WarlockDot{
    public static final String POWER_ID = WarlockMod.makeID("Corruption");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex32 = TextureLoader.getTexture(WarlockMod.makePowerPath("corruption32.png"));
    private static final Texture tex84 = TextureLoader.getTexture(WarlockMod.makePowerPath("empty84.png"));


    public Corruption(AbstractCreature owner, AbstractCreature source, int amount) {
        super(owner, source, amount);
        name = NAME;
        ID = POWER_ID;
        // We load those textures here.
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        turnsremaining=6;
        //this.description = ("Dealing [#87ceeb]"+this.amount+"[] [#EFC851]Affliction[] damage over [#87ceeb]"+turnsremaining+"[] remaining turns.");
        updateDescription();
    }
    @Override
    public void removeIfComplete(){
        //remove if duration complete
        if(turnsremaining==0){
            WarlockMod.cleansePower(this.owner, POWER_ID);
        }
    }
    @Override
    public AbstractPower makeCopy() {
        return new Corruption(owner, source, amount);
    }
}
