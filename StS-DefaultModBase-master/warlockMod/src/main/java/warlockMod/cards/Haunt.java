package warlockMod.cards;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PenNibPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import warlockMod.WarlockMod;
import warlockMod.characters.TheWarlock;
import warlockMod.powers.Spellpower;

public class Haunt extends CustomCard{

    //Deal 6+1sp damage and self-healing. The target takes 35% increased Affliction damage for 3 turns. Affliction. Curse. Costs 2 mana. Skill. Rare. Upgrade: Costs 1 mana.

    // TEXT DECLARATION

    public static final String ID = WarlockMod.makeID(Haunt.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = WarlockMod.makeCardPath("haunt.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    // /TEXT DECLARATION/


    // STAT DECLARATION

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int DAMAGE = 6;
    private static final int UPGRADED_COST = 1;
    private static final int SPELLPOWER_RATIO = 1;
    private static final boolean affliction=true, destruction=false;

    public Haunt() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);

        // Aside from baseDamage/MagicNumber/Block there's also a few more.
        // Just type this.base and let intelliJ auto complete for you, or, go read up AbstractCard
        baseDamage = DAMAGE;
        baseMagicNumber=magicNumber=DAMAGE;
    }
    @Override
    public void applyPowers() {
        //Assuming you're using magic number to store your damage
        this.magicNumber = this.baseMagicNumber;
        this.isMagicNumberModified = false;
        AbstractPower yourModifierPower = AbstractDungeon.player.getPower(Spellpower.POWER_ID); //usually defined as a constant in power classes
        if (yourModifierPower != null) {
            this.magicNumber += yourModifierPower.amount*SPELLPOWER_RATIO;
            if(affliction)this.magicNumber=(int)Math.round(this.magicNumber*AfflictionCard.getAfflictionBaseRatio());
            if(destruction)this.magicNumber=(int)Math.round(this.magicNumber*DestructionCard.getDestructionBaseRatio());
            this.isMagicNumberModified = true; //Causes magicNumber to be displayed for the variable rather than baseMagicNumber
        }
        AbstractPlayer p=AbstractDungeon.player;
        AbstractPower nib=p.getPower(warlockMod.powers.Conflagrate.POWER_ID);
        if(p.hasPower(PenNibPower.POWER_ID)){
            this.magicNumber=Math.max(0, MathUtils.round(2f*this.magicNumber));
            this.isMagicNumberModified = true;
        }
        AbstractPower weak = AbstractDungeon.player.getPower(WeakPower.POWER_ID); //usually defined as a constant in power classes
        if (weak != null) {
            this.magicNumber = Math.max(0, MathUtils.floor(this.magicNumber * 0.75F));
            this.isMagicNumberModified = true;
        }
        damage=magicNumber;
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
    public void calculateCardDamage(AbstractMonster mo){
        int value=magicNumber;
        damage=value;
    }
    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        //Create gif animation to replace STS animation
        WarlockMod.drainlifeimpactgif.playOnceOverCreature(m);
        WarlockMod.shadowboltimpactgif.playOnceOverCreature(m);
        TheWarlock.attack();
        TheWarlock.shadowcastsound();
        CardCrawlGame.sound.play(WarlockMod.shadowimpactsound);

        int damagevalue=(int)Math.round(damage*AfflictionCard.getAfflictionRatio(p, m));
        int healvalue=getHealCalc(p, damagevalue);
        //CardCrawlGame.sound.play(WarlockMod.shadowimpactsound);

        addToBot(
                new DamageAction(m, new DamageInfo(p, damagevalue, damageTypeForTurn)
                )
        );
        addToBot(new HealAction(p, p, healvalue));

        WarlockMod.cleansePower(m, warlockMod.powers.Haunt.POWER_ID);
        addToBot(new ApplyPowerAction(m, p, new warlockMod.powers.Haunt(m, 0), 0));

    }

    // Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADED_COST);
            initializeDescription();
        }
    }
}
