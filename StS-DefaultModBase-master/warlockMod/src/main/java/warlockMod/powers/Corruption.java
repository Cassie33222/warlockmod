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

public class Corruption extends AbstractPower implements CloneablePowerInterface, HealthBarRenderPower {
    public AbstractCreature source;

    public static final String POWER_ID = WarlockMod.makeID("Corruption");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // We create 2 new textures *Using This Specific Texture Loader* - an 84x84 image and a 32x32 one.
    // There's a fallback "missing texture" image, so the game shouldn't crash if you accidentally put a non-existent file.
    private static final Texture tex32 = TextureLoader.getTexture(WarlockMod.makePowerPath("corruption32.png"));
    private static final Texture tex84 = TextureLoader.getTexture(WarlockMod.makePowerPath("empty84.png"));

    int turnsremaining=5;

    public Corruption(AbstractCreature owner, AbstractCreature source, int amount) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;
        this.source = source;

        type = PowerType.BUFF;
        isTurnBased = false;

        // We load those txtures here.
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
        //description = DESCRIPTIONS[0];
        //this.description = DESCRIPTIONS[0]+this.amount+".[]";
        //this.description = String.format(DESCRIPTIONS[0], this.amount);
        this.description = ("Dealing [#87ceeb]"+this.amount+"[] [#EFC851]Affliction[] damage over [#87ceeb]"+turnsremaining+"[] remaining turns.");
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) { // At the end of your turn

        //load turns from corruption timer
        //int turnsremaining=this.owner.getPower(CorruptionCounter.ID).amount;
        int damagethisturn=0;
        boolean remove=false;
        if(turnsremaining>1){
            //even distribution of damage
            damagethisturn=this.amount/turnsremaining;
        }
        else{
            damagethisturn = this.amount;
            remove=true;
        }

        if(damagethisturn>0){
            addToBot(new DamageAction(this.owner,
                    new DamageInfo(AbstractDungeon.player, damagethisturn, DamageInfo.DamageType.HP_LOSS)
            ));

            //TODO: possibly add dot ticking gif here?

            //this.owner.getPower(CorruptionCounter.ID).reducePower(1);
            turnsremaining--;
            //this.owner.getPower(Corruption.ID).reducePower(damagethisturn);
            this.amount-=damagethisturn;
        }

        this.description = ("Dealing [#87ceeb]"+this.amount+"[] remaining [#EFC851]Affliction[] damage over "+turnsremaining+" remaining turns.");
    }
    public int damageThisTurn() { // At the end of your turn
        //load turns from corruption timer
        int damagethisturn=0;
        if(turnsremaining>1){
            //even distribution of damage
            damagethisturn=this.amount/turnsremaining;
        }
        else{
            damagethisturn = this.amount;
        }
        return damagethisturn;
    }

    @Override
    public void updateDescription() {

        //this.description = ("Dealing [#87ceeb]"+this.amount+"[] remaining [#EFC851]Affliction[] damage over "+turnsremaining+" remaining turns.");
    }

    @Override
    public AbstractPower makeCopy() {
        return new Corruption(owner, source, amount);
    }

    @Override
    public int getHealthBarAmount() {
        return damageThisTurn();
    }
    @Override
    public Color getColor() {
        return WarlockMod.DOT_PURPLE;
    }
}
