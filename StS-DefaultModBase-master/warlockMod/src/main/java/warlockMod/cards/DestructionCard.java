package warlockMod.cards;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import warlockMod.powers.CurseOfTheElements;
import warlockMod.relics.ShadowAndFlame;
import warlockMod.relics.ShadowMastery;

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
    public static double getDestructionBaseRatio(){
        double ratio=1;
        if(AbstractDungeon.player.hasRelic(ShadowAndFlame.ID)){
            ratio*=1.4;
        }
        return ratio;
    }
}
