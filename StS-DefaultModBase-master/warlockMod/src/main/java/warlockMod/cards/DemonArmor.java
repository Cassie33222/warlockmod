package warlockMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import warlockMod.WarlockMod;
import warlockMod.characters.TheWarlock;

public class DemonArmor extends CustomCard {

    //Demon Armor
    //Gain 1 Artifact. At the end of each turn, gain 2 block. Demonology. Costs 1 mana. Power. Uncommon. Upgrade: Costs 0.

    public static final String ID = WarlockMod.makeID(DemonArmor.class.getSimpleName());
    public static final String IMG = WarlockMod.makeCardPath("demonarmor.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int ARTIFACT_GAIN=1;
    private static final int MAGIC = 2;
    private static final int COST = 1;
    private static final int UPGRADED_COST=0;

    public DemonArmor() {
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
        WarlockMod.demonarmorgif.playOnceOverCreature(p);
        TheWarlock.attack();
        CardCrawlGame.sound.play(WarlockMod.demonarmorsound);

        //gain 1 artifact
        addToBot(new ApplyPowerAction(p, p,
                new ArtifactPower(p, ARTIFACT_GAIN), ARTIFACT_GAIN));

        AbstractPower newdemonarmor=new warlockMod.powers.DemonArmor(p, magicNumber);
        addToBot(new ApplyPowerAction(p, p,
                newdemonarmor, magicNumber));
    }
}