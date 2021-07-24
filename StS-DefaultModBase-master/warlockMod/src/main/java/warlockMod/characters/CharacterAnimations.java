package warlockMod.characters;

import GifTheSpire.util.GifAnimation;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import warlockMod.WarlockMod;

public class CharacterAnimations{
    GifAnimation idle, dead;
    GifAnimation[] attacks;
    int attackcycle;
    int currentattack=-1;
    boolean died=false;
    float alpha=1;
    long lastupdate;
    public CharacterAnimations(GifAnimation i, GifAnimation d, GifAnimation... a){
        idle=i;
        dead=d;
        attacks=a;
    }
    public void restoreIdleAnimation(){
        if(currentattack>=0&&currentattack<attacks.length&&(attacks[currentattack].ishidden||attacks[currentattack].animationFinished())&&idle.ishidden){
            idle.ishidden=false;
            attacks[currentattack].ishidden=true;
            //WarlockMod.logger.info("Reached the good bit 2 time:"+System.currentTimeMillis());
        }
        if(died&&(dead.ishidden||dead.animationFinished())&&idle.ishidden){
            idle.ishidden=false;
            dead.ishidden=true;
            //WarlockMod.logger.info("Reached the good bit 2 time:"+System.currentTimeMillis());
        }
    }
    public void attack(){
        //WarlockMod.logger.info("Reached the good bit time:"+System.currentTimeMillis());
        idle.ishidden=true;
        //attacks[attackcycle].playOnceOverCreature(animated);
        attacks[attackcycle].playOnce(false);
        currentattack=attackcycle;
        attackcycle++;
        if(attackcycle>attacks.length-1){attackcycle=0;}
    }
    public void die(){
        died=true;
        idle.ishidden=true;
        dead.playOnce(false);
    }
    public void tick(){
        idle.setAlpha(alpha);
        restoreIdleAnimation();
    }
}
