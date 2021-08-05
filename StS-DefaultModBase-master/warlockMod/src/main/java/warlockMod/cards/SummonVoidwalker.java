package warlockMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Fission;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import warlockMod.WarlockMod;
import warlockMod.characters.TheWarlock;
import warlockMod.orbs.SoulShard;
import warlockMod.powers.Spellpower;
import warlockMod.powers.Voidwalker;

import java.util.ArrayList;

public class SummonVoidwalker extends SoulCard {

    //"Summon a Voidwalker Demon, which grants 5 Block each turn. Costs 1 Soul Shard. Demonology."

    public static final String ID = WarlockMod.makeID(SummonVoidwalker.class.getSimpleName());
    public static final String IMG = WarlockMod.makeCardPath("summonvoidwalker.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    //public static final String UPGRADE_DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int MAGIC = 5;
    private static final int UPGRADE_MAGIC = 2;
    private static final int DAMAGE = 5;
    private static final int UPGRADE_DAMAGE = 2;

    public SummonVoidwalker() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber=magicNumber=MAGIC;
        ignoreEnergyOnUse=false;
    }

    @Override
    public void applyPowers() {
        //Assuming you're using magic number to store your damage
        this.magicNumber = this.baseMagicNumber;
        this.isMagicNumberModified = false;
    }
    @Override
    public void calculateCardDamage(AbstractMonster mo){
        int value=magicNumber;
        damage=value;
    }
    //Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_MAGIC);
            initializeDescription();
        }
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

        TheWarlock.attack();
        TheWarlock.shadowcastsound();
        WarlockMod.playRandomSound(WarlockMod.summonvoidwalkersounds);

        //remove existing corruption stack
        WarlockMod.cleanseDemons(p);
        addToBot(new ApplyPowerAction(p, p,
                new Voidwalker(p, magicNumber), magicNumber));
    }
}