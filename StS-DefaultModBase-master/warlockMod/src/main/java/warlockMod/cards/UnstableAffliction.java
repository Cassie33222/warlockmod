package warlockMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import warlockMod.WarlockMod;
import warlockMod.characters.TheWarlock;
import warlockMod.powers.Spellpower;

public class UnstableAffliction extends SoulCard{

    //Remove all Artifact from the target. Deal 40+4sp damage over 6 turns. Costs 1 Soul Shard. Affliction. Curse. Costs 2 mana. Skill. Rare. Upgrade: Deal 50+4sp damage.
    // TEXT DECLARATION

    public static final String ID = WarlockMod.makeID(UnstableAffliction.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = WarlockMod.makeCardPath("unstableaffliction.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;


    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int DAMAGE = 40;
    private static final int UPGRADE_PLUS_DMG = 10;
    private static final int SPELLPOWER_RATIO = 4;
    private static final boolean affliction=true, destruction=false;

    public UnstableAffliction() {
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
        //Create gif animation to replace STS animation
        WarlockMod.corruptionimpactgif.playOnceOverCreature(m);
        TheWarlock.attack();
        TheWarlock.shadowcastsound();
        CardCrawlGame.sound.play(WarlockMod.shadowimpactsound);

        //remove artifact
        WarlockMod.cleansePower(m, ArtifactPower.POWER_ID);

        //remove existing corruption stack
        WarlockMod.cleansePower(m, warlockMod.powers.UnstableAffliction.POWER_ID);
        addToBot(new ApplyPowerAction(m, p, new warlockMod.powers.UnstableAffliction(m, p, damage), 0));
    }
    // Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            //upgradeBaseCost(UPGRADED_COST);
            upgradeDamage(UPGRADE_PLUS_DMG);
            upgradeMagicNumber(UPGRADE_PLUS_DMG);
            initializeDescription();
        }
    }
}
