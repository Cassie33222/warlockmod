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
import com.megacrit.cardcrawl.powers.WeakPower;
import warlockMod.WarlockMod;
import warlockMod.characters.TheWarlock;
import warlockMod.powers.Immolate;
import warlockMod.powers.Spellpower;

public class Incinerate extends CustomCard{

    //Incinerate
    //Deal 9+1sp damage. If the target is affected by Immolate, gain 1 Energy. Destruction. Attack. Costs 1 mana. Uncommon. Upgrade: +3 base damage.

    public static final String ID = WarlockMod.makeID(Incinerate.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = WarlockMod.makeCardPath("incinerate.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 9;
    private static final int UPGRADE_PLUS_DMG = 3;
    private static final int SPELLPOWER_RATIO = 1;
    private int energygain=1;

    // Hey want a second damage/magic/block/unique number??? Great!
    // Go check out DefaultAttackWithVariable and theDefault.variable.DefaultCustomVariable
    // that's how you get your own custom variable that you can use for anything you like.
    // Feel free to explore other mods to see what variables they personally have and create your own ones.

    public Incinerate() {
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

        //buff damage by 35%, if you have conflagrate
        AbstractPlayer p=AbstractDungeon.player;
        AbstractPower conf=p.getPower(warlockMod.powers.Conflagrate.POWER_ID);
        if(conf!=null&&conf.amount>0){
            this.magicNumber=Math.max(0, MathUtils.floor(1.35f*this.magicNumber));
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
        WarlockMod.incinerategif.playOnceOverCreature(m);
        TheWarlock.attack();
        TheWarlock.firecastsound();
        CardCrawlGame.sound.play(WarlockMod.incineratesound);

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
        int damagevalue=(int)Math.round(damage*DestructionCard.getDestructionRatio(p, m));
        //deal 9 damage or more
        addToBot(new DamageAction(m, new DamageInfo(p, damagevalue, damageTypeForTurn)));

        //gain 1 energy if target is affected by immolate
        AbstractPower power=m.getPower(Immolate.POWER_ID);
        if(power!=null&&power.amount>0){
            //p.gainEnergy(1);
            addToBot(
                    new GainEnergyAction(energygain)
            );
        }
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
