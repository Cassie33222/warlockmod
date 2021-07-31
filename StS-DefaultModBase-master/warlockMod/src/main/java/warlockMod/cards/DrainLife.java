package warlockMod.cards;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.BandageUp;
import com.megacrit.cardcrawl.cards.red.Reaper;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import warlockMod.WarlockMod;
import warlockMod.characters.TheWarlock;
import warlockMod.powers.Spellpower;
// "How come this card extends CustomCard and not DynamicCard like all the rest?"
// Skip this question until you start figuring out the AbstractDefaultCard/AbstractDynamicCard and just extend DynamicCard
// for your own ones like all the other cards.

// Well every card, at the end of the day, extends CustomCard.
// Abstract Default Card extends CustomCard and builds up on it, adding a second magic number. Your card can extend it and
// bam - you can have a second magic number in that card (Learn Java inheritance if you want to know how that works).
// Abstract Dynamic Card builds up on Abstract Default Card even more and makes it so that you don't need to add
// the NAME and the DESCRIPTION into your card - it'll get it automatically. Of course, this functionality could have easily
// Been added to the default card rather than creating a new Dynamic one, but was done so to deliberately to showcase custom cards/inheritance a bit more.
public class DrainLife extends CustomCard{

    //Deal "Deal 3+1sp damage and self-healing. Affliction."

    // TEXT DECLARATION

    public static final String ID = WarlockMod.makeID(DrainLife.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = WarlockMod.makeCardPath("drainlife.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    // /TEXT DECLARATION/


    // STAT DECLARATION

    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 3;
    private static final int UPGRADE_PLUS_DMG = 2;
    private static final int SPELLPOWER_RATIO = 1;

    // Hey want a second damage/magic/block/unique number??? Great!
    // Go check out DefaultAttackWithVariable and theDefault.variable.DefaultCustomVariable
    // that's how you get your own custom variable that you can use for anything you like.
    // Feel free to explore other mods to see what variables they personally have and create your own ones.

    // /STAT DECLARATION/

    // IMPORTANT NOTE: If you add parameters to your constructor, you'll crash the auto-add cards with a
    // `NoSuchMethodException` because it except a constructor with no params.
    // (If you don't know what a constructor or params are or what not pls google, java questions = java study)
    // You have two option:
    // 1. Create a new constructor with empty parameters call your custom one with default params in it
    // 2. Mark the card with @AutoAdd.NotSeen (https://github.com/daviscook477/BaseMod/wiki/AutoAdd) to prevent it from
    // being auto-add it, and then load it manually with
    // BaseMod.addCard(new DefaultCommonAttack());
    // UnlockTracker.unlockCard(DefaultCommonAttack.ID);
    // in your main class, in the receiveEditCards() method

    public DrainLife() {
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
            this.isMagicNumberModified = true; //Causes magicNumber to be displayed for the variable rather than baseMagicNumber
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
        TheWarlock.attack();
        TheWarlock.shadowcastsound();
        int damagevalue=damage;
        int healvalue=getHealCalc(p, damage);
        //CardCrawlGame.sound.play(WarlockMod.shadowimpactsound);

        addToBot(
                new DamageAction(m, new DamageInfo(p, damagevalue, damageTypeForTurn)
                )
        );
        addToBot(new HealAction(p, p, healvalue));
    }

    // Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_PLUS_DMG);
            upgradeMagicNumber(UPGRADE_PLUS_DMG);
            initializeDescription();
        }
    }
}
