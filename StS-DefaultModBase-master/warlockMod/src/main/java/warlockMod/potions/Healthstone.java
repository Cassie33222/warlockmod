package warlockMod.potions;

import basemod.AutoAdd;
import basemod.abstracts.CustomPotion;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.FlashPotionEffect;
import warlockMod.WarlockMod;

import java.util.Iterator;

@AutoAdd.Ignore
public class Healthstone extends CustomPotion{

    public static final String POTION_ID = WarlockMod.makeID("Healthstone");
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(POTION_ID);

    public static final String NAME = potionStrings.NAME;
    public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;

    public Healthstone() {
        // The bottle shape and inside is determined by potion size and color. The actual colors are the main DefaultMod.java
        super(NAME, POTION_ID, PotionRarity.COMMON, PotionSize.M, PotionColor.SMOKE);

        // Initialize the Description
        description = DESCRIPTIONS[0];

        isThrown = false;
        // Initialize the on-hover name + description
        tips.add(new PowerTip(name, description));
    }
    @Override
    public void use(AbstractCreature target) {
        //heal for 10% of health on use
        AbstractCreature t=AbstractDungeon.player;
        int amount=(int)Math.round(t.maxHealth*(getHealCalc(t,10)/100d));
        t.heal(amount);
        CardCrawlGame.sound.play("SOUL_SHARD");
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
    @Override
    public AbstractPotion makeCopy() {
        return new Healthstone();
    }
    public void render(SpriteBatch sb) {
        sb.draw(WarlockMod.healthstonepotiontexture, this.posX - 23.0F, this.posY - 24.0F, 23.0F, 24.0F, 46.0F, 42.0F, this.scale, this.scale, 0, 0, 0, 32, 32, false, false);
        if (this.hb != null) {
            this.hb.render(sb);
        }
    }






    // This is your potency.
    @Override
    public int getPotency(final int potency) {
        return 1;
    }
    public void renderLightOutline(SpriteBatch sb) {
    }

    public void renderOutline(SpriteBatch sb) {
    }

    public void renderOutline(SpriteBatch sb, Color c) {
    }

    public void renderShiny(SpriteBatch sb) {
    }
    public void upgradePotion()
    {
        potency += 1;
    }
}
