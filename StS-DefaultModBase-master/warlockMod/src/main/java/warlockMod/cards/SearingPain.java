package warlockMod.cards;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
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
import warlockMod.powers.Immolate;
import warlockMod.powers.Spellpower;

public class SearingPain extends AbstractDynamicCard{

    //Searing Pain
    //Deal 2+1sp damage. Draw a card. If the target is affected by Immolate, add 5 damage and extend it by one turn.
    //Destruction. Attack. Costs 0 mana. Common. Upgrade: Add 5 more.

    public static final String ID = WarlockMod.makeID(SearingPain.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = WarlockMod.makeCardPath("searingpain.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int COST = 0;
    private static final int DAMAGE = 2;
    private static final int MAGIC2=5;
    private static final int UPGRADE_MAGIC2=5;
    private static final int SPELLPOWER_RATIO = 1;
    private static final boolean affliction=false, destruction=true;

    // Hey want a second damage/magic/block/unique number??? Great!
    // Go check out DefaultAttackWithVariable and theDefault.variable.DefaultCustomVariable
    // that's how you get your own custom variable that you can use for anything you like.
    // Feel free to explore other mods to see what variables they personally have and create your own ones.

    public SearingPain() {
        //super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);

        baseDamage = DAMAGE;
        baseMagicNumber=magicNumber=DAMAGE;
        damageTypeForTurn= DamageInfo.DamageType.NORMAL;

        this.defaultBaseSecondMagicNumber=this.defaultSecondMagicNumber=MAGIC2;
        this.rawDescription = "Deal !M! damage. If the target is affected by Immolate, add [#87ceeb]"+this.defaultSecondMagicNumber+"[] damage and extend it by 1 turn. warlockmod:Destruction.";
        initializeDescription();
    }
   /*@Override
    public void updateDescription(){
        this.description = "Deal [#87ceeb]"+this.magicNumber+"[] damage. If the target is affected by Immolate, add [#87ceeb]"+this.defaultSecondMagicNumber+"[] damage to Immolate and extend it by one turn. warlockmod:Destruction.";
    }*/
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
            this.isMagicNumberModified = true;
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
        this.rawDescription = "Deal !M! damage. If the target is affected by Immolate, add [#87ceeb]"+this.defaultSecondMagicNumber+"[] damage and extend it by 1 turn. warlockmod:Destruction.";
        initializeDescription();
    }
    @Override
    public void calculateCardDamage(AbstractMonster mo){
        damage=magicNumber;
    }
    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        //Create gif animation to replace STS animation
        WarlockMod.immolateimpactgif.playOnceOverCreature(m);
        TheWarlock.attack();
        TheWarlock.firecastsound();
        CardCrawlGame.sound.play(WarlockMod.immolatesound);

        p.draw();
        //lose a stack of conflagrate, if you have it
        AbstractPower conf=p.getPower(warlockMod.powers.Conflagrate.POWER_ID);
        if(conf!=null&&conf.amount>0){
            if(conf.amount<=1){
                WarlockMod.cleansePower(p, warlockMod.powers.Conflagrate.POWER_ID);
            }
            else{
                addToBot(new ReducePowerAction(p, p, conf, 1));
            }
        }

        //deal 2 damage or more
        int damagevalue=(int)Math.round(damage*DestructionCard.getDestructionRatio(p, m));
        addToBot(new DamageAction(m, new DamageInfo(p, damagevalue, damageTypeForTurn)));

        AbstractPower power=m.getPower(Immolate.POWER_ID);
        if(power!=null&&power.amount>0){
            //If the target is affected by Immolate, add 5 damage and extend it by one turn.
            power.stackPower(defaultSecondMagicNumber);
            ((Immolate)power).turnsremaining++;
            (power).updateDescription();
        }
    }

    // Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDefaultSecondMagicNumber(UPGRADE_MAGIC2);
            this.rawDescription = "Deal !M! damage. If the target is affected by Immolate, add [#87ceeb]"+this.defaultSecondMagicNumber+"[] damage and extend it by 1 turn. warlockmod:Destruction.";
            initializeDescription();
        }
    }
}
