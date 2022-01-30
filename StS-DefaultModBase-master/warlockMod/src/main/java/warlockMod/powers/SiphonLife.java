package warlockMod.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import warlockMod.WarlockMod;
import warlockMod.util.TextureLoader;

public class SiphonLife extends WarlockDot{
    public static final String POWER_ID = WarlockMod.makeID("Siphon Life");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex32 = TextureLoader.getTexture(WarlockMod.makePowerPath("siphonlife32.png"));
    private static final Texture tex84 = TextureLoader.getTexture(WarlockMod.makePowerPath("empty84.png"));


    public SiphonLife(AbstractCreature owner, AbstractCreature source, int amount) {
        super(owner, source, amount);
        name = NAME;
        ID = POWER_ID;
        // We load those textures here.
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        turnsremaining=10;
        affliction=true;
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
    public int getHealCalc(AbstractCreature p, int d){
        float calc=d;

        int felarmorcount=0;
        //fel armor id
        String power=warlockMod.powers.FelArmor.POWER_ID;

        //if already has fel armor, add the amount to the count
        if(p.getPower(power)!=null) {
            felarmorcount += p.getPower(power).amount;
        }

        //apply fel armor percent to total
        calc*=(1+(felarmorcount/100f));

        return MathUtils.floor(calc);
    }
    public void damage(int damagethisturn){
        int damagevalue=damagethisturn;
        if(affliction){
            damagevalue=(int)Math.round(damagevalue*getSpecializationRatio());
        }
        int healvalue=getHealCalc(AbstractDungeon.player, damagevalue);

        addToBot(new DamageAction(this.owner,
                new DamageInfo(AbstractDungeon.player, damagevalue, DamageInfo.DamageType.HP_LOSS)
        ));
        addToBot(new HealAction(AbstractDungeon.player, AbstractDungeon.player, healvalue));
    }
    public void animate(){
        WarlockMod.drainlifeimpactgif.playCopyOnceOverCreature(owner);
    }
    @Override
    public void updateDescription() {
        this.description = ("Dealing [#87ceeb]"+this.amount+"[] [#EFC851]Affliction[] damage over [#87ceeb]"+turnsremaining+"[] remaining turns, and healing you for that amount.");
    }
    @Override
    public AbstractPower makeCopy() {
        return new SiphonLife(owner, source, amount);
    }
}
