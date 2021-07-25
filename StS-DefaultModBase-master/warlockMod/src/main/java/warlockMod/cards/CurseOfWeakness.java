package warlockMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
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
public class CurseOfWeakness extends CustomCard{

    //Apply 2 Weak, and reduce the target's Strength by 6, but not below 0. Affliction.

    // TEXT DECLARATION

    public static final String ID = WarlockMod.makeID(CurseOfWeakness.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = WarlockMod.makeCardPath("curseofweakness.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;


    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int STRENGTHLOSS=6;
    private static final int WEAKNESS=2;
    private static final int UPGRADED_COST = 0;

    public CurseOfWeakness() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber=magicNumber=WEAKNESS;
    }
    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        //Create gif animation to replace STS animation
        //TODO: animate properly
        WarlockMod.curseofweaknessgif.playOnceOverCreature(m);
        TheWarlock.attack();
        TheWarlock.shadowcastsound();
        CardCrawlGame.sound.play(WarlockMod.cursesound);

        //TODO: if their strength is 0 or less, don't do this
        int strengthdown=STRENGTHLOSS;
        if(strengthdown>0) {
            addToBot( // The action managed queues all the actions a card should do.
                    new ReducePowerAction(m, p, "Strength", strengthdown)
                    //new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn))
            );
        }

        addToBot( // The action managed queues all the actions a card should do.
                new ApplyPowerAction(m, p, new WeakPower(m, WEAKNESS, false), WEAKNESS)
                //new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn))
        );
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
