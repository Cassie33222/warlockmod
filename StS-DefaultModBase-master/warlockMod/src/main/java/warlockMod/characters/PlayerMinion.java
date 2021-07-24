package warlockMod.characters;

import GifTheSpire.util.GifAnimation;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import warlockMod.WarlockMod;

public class PlayerMinion extends CharacterAnimations{
    String attachedpower;
    boolean hasattacks=false;
    public PlayerMinion(String p, GifAnimation i, GifAnimation... a){
        super(i, null, a);
        idle=i;
        attacks=a;
        alpha=0;
        hasattacks=true;
        attachedpower=p;
    }
    public PlayerMinion(String p, GifAnimation i) {
        super(i, null, null);
        idle = i;
        alpha=0;
        attachedpower=p;
    }
    public void tick(){
        //fade into and out of existence depending on if the player has a power
        //also hide if out of combat
        //if(AbstractDungeon.getCurrRoom().isBattleOver){alpha=0;}
        AbstractPlayer p=AbstractDungeon.player;
        if(p!=null) {
            //idle.playOnceOverCreature(AbstractDungeon.player); need?
            boolean fade = p.hasPower(attachedpower);
            if (lastupdate == 0) {
                lastupdate = System.nanoTime();
            }
            double diff=(((System.nanoTime() - lastupdate) / 1000000L) / 1000d);
            alpha += diff * (fade ? 1 : -1);
            alpha = Math.min(1, Math.max(0, alpha));
            lastupdate=System.nanoTime();
           /*if(alpha>0.0001){
                idle.ishidden=false;
            }
            else{
                idle.ishidden=true;
            }
            */
            if(alpha!=1)WarlockMod.logger.info("power is "+fade+", gif alpha "+alpha);

            idle.ishidden=false;
            //idle.setLoop(true);
        }
        idle.setAlpha(alpha);
        if(attacks!=null&&hasattacks){restoreIdleAnimation();}
    }
}
