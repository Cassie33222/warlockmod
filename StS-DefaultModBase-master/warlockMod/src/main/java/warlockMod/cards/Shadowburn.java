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
import warlockMod.orbs.SoulShard;
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
public class Shadowburn extends SoulCard{

    //Deal 20+2sp damage. Costs 1 Soul Shard. If the target dies this turn, gain 1 Soul Shard. Does not stack. Destruction. Attack. Costs 2 mana. Uncommon. Upgrade: +5 damage.

    // TEXT DECLARATION

    public static final String ID = WarlockMod.makeID(Shadowburn.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = WarlockMod.makeCardPath("shadowburn.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    // /TEXT DECLARATION/


    // STAT DECLARATION

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int DAMAGE = 20;
    private static final int UPGRADE_PLUS_DMG = 5;
    private static final int SPELLPOWER_RATIO = 2;
    private static final boolean affliction=false, destruction=true;

    public Shadowburn() {
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
    public void createSoulShard(){
        AbstractDungeon.player.channelOrb(new SoulShard());
    }
    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        if(!hasSoulShards(p, this.SOULS)){
            return;
        }else{
            eatSouls(p, this.SOULS);
        }

        //Create gif animation to replace STS animation
        WarlockMod.incinerategif.playOnceOverCreature(m);
        TheWarlock.attack();
        TheWarlock.firecastsound();
        CardCrawlGame.sound.play(WarlockMod.incineratesound);

        int damagevalue=(int)Math.round(damage*DestructionCard.getDestructionRatio(p, m));
        //CardCrawlGame.sound.play(WarlockMod.shadowimpactsound);

        WarlockMod.cleansePower(m, warlockMod.powers.Shadowburn.POWER_ID);
        addToBot(new ApplyPowerAction(m, p, new warlockMod.powers.Shadowburn(m, p, 1), 0));
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
