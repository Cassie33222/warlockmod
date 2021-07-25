package warlockMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Fission;
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

public class SummonVoidwalker extends CustomCard {

    //"Summon a Voidwalker Demon, which grants 4 Block each turn. Costs 1 Soul Shard. Demonology."


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
    private static final int MAGIC = 4;
    private static final int UPGRADE_MAGIC = 1;
    private static final int DAMAGE = 4;
    private static final int UPGRADE_DAMAGE = 1;

    public SummonVoidwalker() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber=magicNumber=MAGIC;
    }

    @Override
    public void applyPowers() {
        //Assuming you're using magic number to store your damage
        this.magicNumber = this.baseMagicNumber;
        this.isMagicNumberModified = false;
        /*AbstractPower yourModifierPower = AbstractDungeon.player.getPower(Spellpower.POWER_ID); //usually defined as a constant in power classes
        if (yourModifierPower != null) {
            this.magicNumber += yourModifierPower.amount*SPELLPOWER_RATIO;
            this.isMagicNumberModified = true; //Causes magicNumber to be displayed for the variable rather than baseMagicNumber
        }*/
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


        ArrayList<AbstractOrb> orbs=p.orbs;
        boolean foundshard=false;
        if(orbs==null){return;}
        for(int i=0; i<orbs.size(); i++){
            AbstractOrb orb=orbs.get(i);
            if(orb!=null){
                if(orb.ID!=null&&orb.ID.equalsIgnoreCase(SoulShard.ORB_ID)){
                    WarlockMod.consumeSpecificOrb(p, SoulShard.ORB_ID);
                    foundshard=true;
                    break;
                }
            }
        }
        if(!foundshard){
            //WarlockMod.logger.info("Warning! Summon voidwalker was playable despite having no soul shard!");
            return;
        }

        TheWarlock.attack();
        TheWarlock.shadowcastsound();
        WarlockMod.playRandomSound(WarlockMod.summonvoidwalkersounds);

        //remove existing corruption stack
        WarlockMod.cleanseDemons(p);
        addToBot(new ApplyPowerAction(p, p,
                new Voidwalker(p, magicNumber), magicNumber));
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractOrb> orbs=p.orbs;
        if(orbs==null){return false;}
        for(int i=0; i<orbs.size(); i++){
            AbstractOrb orb=orbs.get(i);
            if(orb!=null){
                if(orb.ID!=null&&orb.ID.equalsIgnoreCase(SoulShard.ORB_ID)){
                    return true;
                }
            }
        }
        this.cantUseMessage = "Not enough Soul Shards.";
        return false;
    }
}