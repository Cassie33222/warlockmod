package warlockMod.cards;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
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

public class Immolate extends CustomCard{

    //Deal 5+1sp damage immediately, and 10+1sp more over 5 turns. Destruction. Curse. Costs 1 mana. Attack. Common. Upgrade: +3 base damage.

    // TEXT DECLARATION

    public static final String ID = WarlockMod.makeID(Immolate.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = WarlockMod.makeCardPath("immolate.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;


    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 5;
    private static final int UPGRADE_PLUS_DMG = 3;
    private static final int SPELLPOWER_RATIO = 1;
    private static final boolean affliction=false, destruction=true;

    public Immolate() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);

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
    @Override
    public void calculateCardDamage(AbstractMonster mo){
        int value=magicNumber;
        damage=value;
    }
    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        //Create gif animation to replace STS animation
        WarlockMod.immolateimpactgif.playOnceOverCreature(m);
        TheWarlock.attack();
        TheWarlock.firecastsound();
        CardCrawlGame.sound.play(WarlockMod.immolatesound);
        int damagevalue=(int)Math.round(damage*DestructionCard.getDestructionRatio(p, m));
        //remove existing corruption stack
        WarlockMod.cleansePower(m, warlockMod.powers.Immolate.POWER_ID);
        addToBot(
                new DamageAction(m, new DamageInfo(p, damagevalue, damageTypeForTurn))
        );
        addToBot( // The action managed queues all the actions a card should do.
                new ApplyPowerAction(m, p, new warlockMod.powers.Immolate(m, p, damage+5), 0)
                //new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn))
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
