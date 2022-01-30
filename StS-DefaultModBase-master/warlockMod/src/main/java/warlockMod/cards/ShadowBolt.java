package warlockMod.cards;

import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.WeakPower;
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
public class ShadowBolt extends CustomCard{

    //Deal 6+1sp damage. Destruction.

    public static final String ID = WarlockMod.makeID(ShadowBolt.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = WarlockMod.makeCardPath("shadowbolt.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPGRADE_PLUS_DMG = 3;
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

    public ShadowBolt() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);

        baseDamage = DAMAGE;
        baseMagicNumber=magicNumber=DAMAGE;
        damageTypeForTurn= DamageInfo.DamageType.NORMAL;
    }
    @Override
    public void applyPowers() {
        //Assuming you're using magic number to store your damage
        this.magicNumber = this.baseMagicNumber;
        this.isMagicNumberModified = false;
        AbstractPower yourModifierPower = AbstractDungeon.player.getPower(Spellpower.POWER_ID); //usually defined as a constant in power classes
        if (yourModifierPower != null) {
            this.magicNumber += yourModifierPower.amount*SPELLPOWER_RATIO;
            this.isMagicNumberModified = true;
        }

        AbstractPower weak = AbstractDungeon.player.getPower(WeakPower.POWER_ID); //usually defined as a constant in power classes
        if (weak != null) {
            this.magicNumber = Math.max(0, MathUtils.floor(AbstractDungeon.player.hasRelic("Paper Crane") ? this.magicNumber * 0.6F : this.magicNumber * 0.75F));
            this.isMagicNumberModified = true;
        }
        damage=magicNumber;
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
        WarlockMod.shadowboltimpactgif.playOnceOverCreature(m);
        TheWarlock.attack();
        TheWarlock.shadowcastsound();
        CardCrawlGame.sound.play(WarlockMod.shadowimpactsound);

        int damagevalue=(int)Math.round(damage*DestructionCard.getDestructionRatio(p, m));

        addToBot(
                new DamageAction(m, new DamageInfo(p, damagevalue, damageTypeForTurn)
                )
    );
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
