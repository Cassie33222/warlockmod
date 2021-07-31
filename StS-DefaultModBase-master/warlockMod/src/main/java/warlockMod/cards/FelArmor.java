package warlockMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
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
import warlockMod.powers.Voidwalker;

import java.util.ArrayList;

public class FelArmor extends CustomCard {

    //Fel Armor
    //Increase your self-healing by 40%. Demonology. Costs 1 mana. Power. Uncommon. Upgrade: Costs 0 mana.

    public static final String ID = WarlockMod.makeID(FelArmor.class.getSimpleName());
    public static final String IMG = WarlockMod.makeCardPath("felarmor.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int MAGIC = 40;
    private static final int COST = 1;
    private static final int UPGRADED_COST=0;

    public FelArmor() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber=magicNumber=MAGIC;
        //this.isMagicNumberModified = true;
    }

    @Override
    public void applyPowers() {
        this.magicNumber = this.baseMagicNumber;
    }
    //Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADED_COST);
            initializeDescription();
        }
    }
    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        WarlockMod.felarmorgif.playOnceOverCreature(p);
        TheWarlock.attack();
        CardCrawlGame.sound.play(WarlockMod.felarmorsound);

        //count the total for later tooltip
        int count=this.magicNumber;
        //make the new copy to work with in advance
        AbstractPower newfelarmor=new warlockMod.powers.FelArmor(p, count);
        addToBot(new ApplyPowerAction(p, p,
                newfelarmor, count));
    }
}