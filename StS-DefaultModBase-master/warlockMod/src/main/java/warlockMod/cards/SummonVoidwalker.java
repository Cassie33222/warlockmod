package warlockMod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Fission;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import warlockMod.WarlockMod;
import warlockMod.characters.TheWarlock;
import warlockMod.orbs.SoulShard;
import warlockMod.powers.Spellpower;
import warlockMod.powers.Voidwalker;

import java.util.ArrayList;

public class SummonVoidwalker extends AbstractDynamicCard {

    /*
     * Wiki-page: https://github.com/daviscook477/BaseMod/wiki/Custom-Cards
     *
     * Summon a Voidwalker Demon, which grants 4 Block at the end of your turn. Costs one Soul Shard. Demonology.
     */

    public static final String ID = WarlockMod.makeID(SummonVoidwalker.class.getSimpleName());
    public static final String IMG = WarlockMod.makeCardPath("summonvoidwalker.png");

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = TheWarlock.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int MAGIC = 4;
    private static final int UPGRADE_MAGIC = 1;


    public SummonVoidwalker() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        magicNumber = baseMagicNumber = MAGIC;
    }
    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {


        ArrayList<AbstractOrb> orbs=p.orbs;
        if(orbs==null){return;}
        for(int i=0; i<orbs.size(); i++){
            AbstractOrb orb=orbs.get(i);
            if(orb!=null){
                if(orb.ID.equalsIgnoreCase(SoulShard.ORB_ID)){
                    p.removeNextOrb();
                }
            }
        }

        TheWarlock.attack();
        TheWarlock.shadowcastsound();
        WarlockMod.playRandomSound(WarlockMod.summonvoidwalkersounds);

        //remove existing corruption stack
        WarlockMod.cleanseDemons(p);
        addToBot(new ApplyPowerAction(p, p,
                new Voidwalker(p, p, magicNumber), magicNumber));
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractOrb> orbs=p.orbs;
        if(orbs==null){return false;}
        for(int i=0; i<orbs.size(); i++){
            AbstractOrb orb=orbs.get(i);
            if(orb!=null){
                if(orb.ID.equalsIgnoreCase(SoulShard.ORB_ID)){
                    return true;
                }
            }
        }
        this.cantUseMessage = "Not enough Soul Shards.";
        return false;
    }
    //Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_MAGIC);
            rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}