package warlockMod.characters;

import GifTheSpire.util.GifAnimation;
import com.megacrit.cardcrawl.core.AbstractCreature;
import warlockMod.WarlockMod;

public class CharacterAnimations{
    GifAnimation idle, dead;
    GifAnimation[] attacks;
    int attackcycle;
    int currentattack=-1;
    public CharacterAnimations(GifAnimation i, GifAnimation d, GifAnimation... a){
        idle=i;
        dead=d;
        attacks=a;
    }
    public void restoreIdleAnimation(){
        if(currentattack>=0&&currentattack<attacks.length&&attacks[currentattack].ishidden&&idle.ishidden){
            idle.ishidden=false;
            WarlockMod.logger.info("Reached the good bit 2 time:"+System.currentTimeMillis());
        }
    }
    public void attack(AbstractCreature animated){
        WarlockMod.logger.info("Reached the good bit time:"+System.currentTimeMillis());
        idle.ishidden=true;
        //attacks[attackcycle].playOnceOverCreature(animated);
        attacks[attackcycle].playOnce();
        currentattack=attackcycle;
        attackcycle++;
        if(attackcycle>attacks.length-1){attackcycle=0;}
    }
}
