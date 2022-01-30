package warlockMod.cards;

import com.megacrit.cardcrawl.core.AbstractCreature;
import warlockMod.WarlockMod;
import warlockMod.powers.Haunt;

public abstract class AfflictionCard{
    public static String[] TaggedCards=new String[]{
            "Corruption", "Curse of Agony", "Curse of Weakness", "Death Coil", "Drain Life", "Drain Soul", "Fear", "Haunt", "Unstable Affliction"
    };
    public static double getAfflictionRatio(AbstractCreature player, AbstractCreature target){
        double ratio=1;
        //WarlockMod.logger.info("Trying to apply haunt");
        if(target!=null){
            //WarlockMod.logger.info("Target available");
            if(target.hasPower(Haunt.POWER_ID)){
                ratio*=1.35;
                //WarlockMod.logger.info("Applied Haunt");
            }
        }
        return ratio;
    }
}
