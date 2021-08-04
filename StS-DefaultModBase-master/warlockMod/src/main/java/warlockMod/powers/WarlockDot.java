package warlockMod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import warlockMod.WarlockMod;
import warlockMod.util.TextureLoader;

public class WarlockDot extends AbstractPower implements CloneablePowerInterface, HealthBarRenderPower{
    public AbstractCreature source;
    public int turnsremaining=5;
    public static String POWER_ID;
    private static final Texture tex32 = TextureLoader.getTexture(WarlockMod.makePowerPath("corruption32.png"));
    private static final Texture tex84 = TextureLoader.getTexture(WarlockMod.makePowerPath("empty84.png"));
    public WarlockDot(AbstractCreature owner, AbstractCreature source, int amount) {
        this.owner = owner;
        this.amount = amount;
        this.source = source;

        type = PowerType.DEBUFF;
        isTurnBased = true;
    }

    @Override
    public void atStartOfTurn() {
        int damagethisturn=damageThisTurn();

        if(damagethisturn>0){
            damage(damagethisturn);
            animate();
            countDown(damagethisturn);
        }
        updateDescription();
        removeIfComplete();
    }
    public int damageThisTurn() {
        //load turns from timer
        int damagethisturn;
        if (turnsremaining > 1) {
            //even distribution of damage
            damagethisturn = this.amount / turnsremaining;
        } else {
            damagethisturn = this.amount;
        }
        return damagethisturn;
    }
    public void damage(int damagethisturn){
        addToBot(new DamageAction(this.owner,
                new DamageInfo(AbstractDungeon.player, damagethisturn, DamageInfo.DamageType.HP_LOSS)
        ));
    }
    public void countDown(int damagethisturn){
        turnsremaining--;
        this.amount-=damagethisturn;
    }
    public void removeIfComplete(){
        //remove if duration complete
        if(turnsremaining==0){
            WarlockMod.cleansePower(this.owner, POWER_ID);
        }
    }
    public void animate(){
        WarlockMod.corruptiontickgif.playCopyOnceOverCreature(owner);
        //WarlockMod.corruptiontickgif.playOnceOverCreature(owner);
    }
    @Override
    public void updateDescription() {
        this.description = ("Dealing [#87ceeb]"+this.amount+"[] [#EFC851]Affliction[] damage over [#87ceeb]"+turnsremaining+"[] remaining turns.");
    }
    @Override
    public int getHealthBarAmount() {
        return damageThisTurn();
    }
    @Override
    public Color getColor() {
        return WarlockMod.DOT_PURPLE;
    }
    @Override
    public AbstractPower makeCopy() {
        return new WarlockDot(owner, source, amount);
    }
}
