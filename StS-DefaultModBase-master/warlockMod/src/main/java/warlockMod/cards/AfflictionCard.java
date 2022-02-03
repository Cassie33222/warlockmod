package warlockMod.cards;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import warlockMod.powers.Haunt;
import warlockMod.relics.ShadowMastery;

public abstract class AfflictionCard{
    public static String[] TaggedCards=new String[]{
            "Corruption", "Curse of Agony", "Curse of Weakness", "Death Coil", "Drain Life", "Drain Soul", "Fear", "Haunt", "Unstable Affliction"
    };
    public static double getAfflictionRatio(AbstractCreature player, AbstractCreature target){
        double ratio=1;
        if(target!=null){
            if(target.hasPower(Haunt.POWER_ID)){
                ratio*=1.35;
            }
            if(AbstractDungeon.player.hasRelic(ShadowMastery.ID)){
                ratio*=1.4;
            }
        }
        return ratio;
    }
    public static double getAfflictionBaseRatio(){
        double ratio=1;
            if(AbstractDungeon.player.hasRelic(ShadowMastery.ID)){
                ratio*=1.4;
            }
        return ratio;
    }
}
