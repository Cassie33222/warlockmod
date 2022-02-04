package warlockMod.cards;

import com.badlogic.gdx.math.MathUtils;
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

public class ChaosBolt extends SoulCard{

    //Deal 60+10sp damage. If you have Conflagrate, gain 3 energy. Costs 3 Soul Shards. Destruction. Attack. Costs 3 mana. Rare. Upgrade: Costs 2 mana.

    public static final String ID = WarlockMod.makeID(ChaosBolt.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = WarlockMod.makeCardPath("chaosbolt.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;


    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int COST = 3;
    private static final int UPGRADED_COST = 2;
    private static final int DAMAGE = 60;
    private static final int SPELLPOWER_RATIO = 10;
    private static final int SOUL_COST = 3;
    private static final boolean affliction=false, destruction=true;

    public ChaosBolt() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);

        baseDamage = DAMAGE;
        baseMagicNumber=magicNumber=DAMAGE;

        this.SOULS=SOUL_COST;
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
        //buff damage by 35%, if you have conflagrate
        AbstractPlayer p=AbstractDungeon.player;
        AbstractPower conf=p.getPower(warlockMod.powers.Conflagrate.POWER_ID);
        if(conf!=null&&conf.amount>0){
            this.magicNumber=Math.max(0, MathUtils.floor(1.35f*this.magicNumber));
            this.isMagicNumberModified = true;
        }
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
        //eat souls or cancel
        if(!hasSoulShards(p, this.SOULS)){
            return;
        }else{
            eatSouls(p, this.SOULS);
        }
        int damagevalue=(int)Math.round(damage*DestructionCard.getDestructionRatio(p, m));

        //Create gif animation to replace STS animation
        WarlockMod.chaosboltimpact.playOnceOverCreature(m);
        TheWarlock.attack();
        TheWarlock.firecastsound();
        CardCrawlGame.sound.play(WarlockMod.fireblast1sound);

        addToBot(
                new DamageAction(m, new DamageInfo(p, damagevalue, damageTypeForTurn))
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
