package warlockMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.green.PiercingWail;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import warlockMod.WarlockMod;
import warlockMod.characters.TheWarlock;

import java.util.Iterator;
// "How come this card extends CustomCard and not DynamicCard like all the rest?"
// Skip this question until you start figuring out the AbstractDefaultCard/AbstractDynamicCard and just extend DynamicCard
// for your own ones like all the other cards.

// Well every card, at the end of the day, extends CustomCard.
// Abstract Default Card extends CustomCard and builds up on it, adding a second magic number. Your card can extend it and
// bam - you can have a second magic number in that card (Learn Java inheritance if you want to know how that works).
// Abstract Dynamic Card builds up on Abstract Default Card even more and makes it so that you don't need to add
// the NAME and the DESCRIPTION into your card - it'll get it automatically. Of course, this functionality could have easily
// Been added to the default card rather than creating a new Dynamic one, but was done so to deliberately to showcase custom cards/inheritance a bit more.
public class HowlOfTerror extends CustomCard{

    //ALL enemies lose 3 Strength this turn, and gain 1 Weak. Affliction. Costs 1 mana. Skill. Common. Upgrade: Costs 0 mana.

    // TEXT DECLARATION

    public static final String ID = WarlockMod.makeID(HowlOfTerror.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = WarlockMod.makeCardPath("howlofterror.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;


    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int STRENGTHLOSS=3;
    private static final int WEAKNESS=1;

    private static final int UPGRADED_COST = 0;

    public HowlOfTerror() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber=magicNumber=STRENGTHLOSS;
    }
    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        //Create gif animation to replace STS animation
        TheWarlock.attack();
        TheWarlock.shadowcastsound();
        CardCrawlGame.sound.play(WarlockMod.fearsound);

        //if strength lower than debuff effect, only go to strengthfloor
        int strengthchange=-STRENGTHLOSS;

        Iterator var3 = AbstractDungeon.getCurrRoom().monsters.monsters.iterator();
        while(var3.hasNext()) {
            AbstractMonster mo = (AbstractMonster) var3.next();
            this.addToBot(new ApplyPowerAction(mo, p, new StrengthPower(mo, -this.magicNumber), -this.magicNumber, true, AbstractGameAction.AttackEffect.NONE));
            this.addToBot(new ApplyPowerAction(mo, p, new GainStrengthPower(mo, this.magicNumber), this.magicNumber, true, AbstractGameAction.AttackEffect.NONE));
                addToBot( // The action managed queues all the actions a card should do.
                        new ApplyPowerAction(m, p, new WeakPower(m, WEAKNESS, false), WEAKNESS)
                );
        }

    }
    // Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADED_COST);
            rawDescription="All enemies lose 3 Strength this turn, and gain 1 Weak. warlockmod:Affliction.";
            initializeDescription();
        }
    }
}
