package warlockMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import warlockMod.WarlockMod;
import warlockMod.characters.TheWarlock;
import warlockMod.orbs.SoulShard;
import warlockMod.powers.Voidwalker;

import java.util.ArrayList;

public abstract class SoulCard extends AbstractDefaultCard {
    public int SOULS=1;

    public SoulCard(final String id,
                    final String name,
                    final String img,
                    final int cost,
                    final String rawDescription,
                    final CardType type,
                    final CardColor color,
                    final CardRarity rarity,
                    final CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
    }
    public void eatSouls(AbstractPlayer p, int souls){
        for(int i=0; i<souls; i++) {
            WarlockMod.consumeSpecificOrb(p, SoulShard.ORB_ID);
        }
    }
    public void createSoulShard(){
        AbstractDungeon.player.channelOrb(new SoulShard());
    }
    public boolean hasSoulShards(AbstractPlayer p, int souls){
        ArrayList<AbstractOrb> orbs=p.orbs;
        int foundshard=0;
        if(orbs==null){return false;}
        for(int i=0; i<orbs.size(); i++){
            AbstractOrb orb=orbs.get(i);
            if(orb!=null){
                if(orb.ID!=null&&orb.ID.equalsIgnoreCase(SoulShard.ORB_ID)){
                    foundshard++;
                    if(foundshard>=souls){break;}
                }
            }
        }
        if(foundshard<souls){
            return false;
        }

        return true;
    }
    @Override
    public boolean hasEnoughEnergy(){
        if(hasSoulShards(AbstractDungeon.player, this.SOULS)){
            return super.hasEnoughEnergy();
        }
        this.cantUseMessage = "Not enough [#ff334c]Soul[] [#ff334c]Shards[].";
        return false;
    }
}