package warlockMod.cards;

import com.megacrit.cardcrawl.core.AbstractCreature;
import warlockMod.powers.CurseOfTheElements;

public abstract class DestructionCard {
    public static String[] TaggedCards=new String[]{
            "Chaos Bolt", "Conflagrate", "Immolate", "Incinerate", "Searing Pain", "Shadow Bolt", "Shadowburn", "Soul Fire"
    };
    public static double getDestructionRatio(AbstractCreature player, AbstractCreature target){
        double ratio=1;
        if(target!=null){
            if(target.hasPower(CurseOfTheElements.POWER_ID)){
                ratio*=1.2;
            }
        }
        return ratio;
    }
}
