package GifTheSpire.util;

import GifTheSpire.GifTheSpireLib;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class GifAnimation implements ApplicationListener {
    private Animation<TextureRegion> GifAnimation;
    Texture TextureReg;
    float stateTime;
    private float currentx;
    private float currenty;
    private float widthmodfier;
    private float heightmodifier;
    private int clms;
    private int rows;
    private boolean loop = true;
    private String txt;
    private float framedur = 0.025F;
    private float flatadjustx=0;
    private float flatadjusty=0;
    public boolean ishidden;
    public static SpriteBatch getSpritebatch = null;
    public static final Logger logger = LogManager.getLogger(GifTheSpireLib.class.getName());
    private int emptyFrames;

    public float alpha=1f;
    public boolean screenbackground =false;
    ArrayList<GifCopy> copies=new ArrayList<GifCopy>();
    public boolean isTemp=false, disableAnimating=false;
    public boolean usescopies=false, relativetoplayer=false;
    class GifCopy{
        GifAnimation parent;
        boolean ishidden;
        float currentx, currenty;
        boolean isTemp=true;
        float stateTime=0;
        public GifCopy(GifAnimation p){
            parent=p;
            ishidden=p.ishidden;
            currentx=p.currentx;
            currenty=p.currenty;
        }
        public void moveOverCreature(AbstractCreature m, float xadjust, float yadjust)
        {
            //get the frame so we can move it
            TextureRegion currentFrame = GifAnimation.getKeyFrame(0, loop);
            this.currentx=(float)(
                    xadjust+flatadjustx
                            +(m.drawX+m.animX)/Settings.scale
                            -((float) (currentFrame.getTexture().getWidth()/clms)*widthmodfier*0.5F)
            );
            this.currenty=(float)(
                    yadjust+flatadjusty
                            +((m.hb_h*0.5f+m.drawY+m.animY))/Settings.scale
                            -(float)(currentFrame.getTexture().getHeight()/rows)*heightmodifier*0.5f
            );
        }
        public void playOnce()
        {
            this.stateTime = 0;
            this.isTemp=true;
            this.ishidden=false;
        }
    }

    public GifAnimation(String imgurl, int columns, int rows, float x, float y, float stretchx, float stretchy, float flatx, float flaty, boolean ishiddeninitially, int emptyFrames)
    {
        currentx = x;
        currenty = y;
        txt = imgurl;
        clms = columns;
        this.rows = rows;
        ishidden = ishiddeninitially;
        heightmodifier = stretchy;
        widthmodfier = stretchx;
        this.emptyFrames = emptyFrames;
        flatadjustx=flatx;
        flatadjusty=flaty;
    }

    public GifAnimation(String imgurl, int columns, int rows, float x, float y, float stretchx, float stretchy, boolean ishiddeninitially)
    {
       this(imgurl, columns, rows,  x,  y,  stretchx,  stretchy, 0, 0, ishiddeninitially, 0);
    }
    public GifAnimation(String imgurl, int columns, int rows, float x, float y, float stretchx, float stretchy, float flatx, float flaty, boolean ishiddeninitially)
    {
        this(imgurl, columns, rows,  x,  y,  stretchx,  stretchy, flatx, flaty, ishiddeninitially, 0);
    }
    @Override
    public void create() {
        TextureReg = ImageMaster.loadImage(txt);
        TextureRegion[][] tmp = TextureRegion.split(TextureReg,
                TextureReg.getWidth() / clms,
                TextureReg.getHeight() / rows);

        TextureRegion[] Frames = new TextureRegion[clms * rows - emptyFrames];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < clms; j++) {
                if(i == rows - 1 && j == clms - emptyFrames)
                {
                    break;
                }
                Frames[index++] = tmp[i][j];
            }
        }

        GifAnimation = new Animation<TextureRegion>(0.025f, Frames);
        stateTime = 0f;
        GifTheSpireLib.TickThis.add(this);
    }
    public void tick()
    {
        stateTime += Gdx.graphics.getDeltaTime();
        completeTempAnimation();

        //tick copies
        if(!copies.isEmpty()){
            for(int i=0; i<copies.size(); i++) {
                if (i < 0 || copies.size() == 0) {
                    break;
                }
                GifCopy copy = copies.get(i);
                copy.stateTime += Gdx.graphics.getDeltaTime();
            }
        }
    }

    public void setAnimationspeed(float frameDuration)
    {
        GifAnimation.setFrameDuration(frameDuration);
    }
    public void setAlpha (float a){
        alpha=a;
    }
    public void renderanimation(SpriteBatch sb) {
        if(!ishidden) {
            TextureRegion currentFrame = GifAnimation.getKeyFrame(disableAnimating ? 0 : stateTime, loop);
            //sb.setColor(Color.WHITE);
            sb.setColor(1, 1, 1, alpha);
            float xm=0;
            float ym=0;
            if(relativetoplayer){
                xm= AbstractDungeon.player.drawX;
                ym=AbstractDungeon.player.drawY;
            }
            sb.draw(currentFrame, xm+currentx * Settings.scale, ym+currenty * Settings.scale, (currentFrame.getTexture().getWidth() / clms) * widthmodfier * Settings.scale, (currentFrame.getTexture().getHeight() / rows) * heightmodifier * Settings.scale /*Settings.WIDTH, Settings.HEIGHT*/);
        }
        renderanimationcopies(sb);
    }
    public void renderanimationcopies(SpriteBatch sb){
        if(!usescopies||copies.isEmpty()){return;}

            for(int i=0; i<copies.size(); i++) {
                if(i<0||copies.size()==0){
                    break;
                }
                GifCopy copy = copies.get(i);
                if (copy.isTemp && animationFinished(copy.stateTime)) {
                    copies.remove(copy);
                    i--;
                    //WarlockMod.logger.info("Removed a finished gif copy");
                }else{
                    TextureRegion currentFrame2 = GifAnimation.getKeyFrame(disableAnimating?0:copy.stateTime, loop);
                    sb.setColor(Color.WHITE);
                    sb.draw(currentFrame2, copy.currentx*Settings.scale, copy.currenty*Settings.scale, (currentFrame2.getTexture().getWidth()/clms)*widthmodfier*Settings.scale,
                            (currentFrame2.getTexture().getHeight()/rows)*heightmodifier*Settings.scale);

                    //WarlockMod.logger.info("Rendering gif copy");
                }
            }
    }
    public void renderAsBackground(SpriteBatch sb) {
        if(!screenbackground){renderanimation(sb); return;}
        TextureRegion currentFrame = GifAnimation.getKeyFrame(stateTime, loop);
        sb.setColor(Color.WHITE);
        sb.draw(currentFrame, 0, 0, Settings.WIDTH, Settings.HEIGHT);
    }
    public void renderAsPortrait(SpriteBatch sb, float angle, float sizex, float sizey, float posx, float posy, float originx, float originy, float scalex, float scaley, AbstractCard card) {
        TextureRegion currentFrame = GifAnimation.getKeyFrame(CardTimerField.CardTimer.get(card), loop);
        sb.setColor(Color.WHITE);
        sb.draw(currentFrame, posx, posy,originx, originy, sizex, sizey, scalex, scaley, angle);
    }
    public void renderAsEvent(SpriteBatch sb) {
        TextureRegion currentFrame = GifAnimation.getKeyFrame(stateTime, loop);
        sb.setColor(Color.WHITE);
        sb.draw(currentFrame, (460.0F*Settings.scale + (600.0F-(currentFrame.getTexture().getWidth()/clms)*widthmodfier*Settings.scale)/2.0F) - 300.0F,  (Settings.EVENT_Y - 300.0F + 16.0F * Settings.scale)+(600.0F -(currentFrame.getTexture().getHeight()/rows*heightmodifier*Settings.scale))/2.0F, (currentFrame.getTexture().getWidth()/clms)*widthmodfier*Settings.scale, (currentFrame.getTexture().getHeight()/rows)*heightmodifier*Settings.scale);
    }
    public void renderOverCharacter(SpriteBatch sb, AbstractPlayer a) {
        TextureRegion currentFrame = GifAnimation.getKeyFrame(stateTime, loop);
        getSpritebatch.setColor(Color.WHITE);
        getSpritebatch.draw(currentFrame, a.drawX - (currentFrame.getTexture().getWidth()/clms)*widthmodfier*Settings.scale*0.5F + a.animX, a.drawY, (currentFrame.getTexture().getWidth()/clms)*widthmodfier*Settings.scale, (currentFrame.getTexture().getHeight()/rows)*heightmodifier*Settings.scale);
    }
    public void renderOverCreature(SpriteBatch sb, AbstractMonster m)
    {
        TextureRegion currentFrame = GifAnimation.getKeyFrame(stateTime, loop);
        sb.setColor(Color.WHITE);
        sb.draw(currentFrame,m.drawX - ((float) (currentFrame.getTexture().getWidth()/clms)*widthmodfier*Settings.scale*0.5F) + m.animX,(float) m.drawY + m.animY, (float)(currentFrame.getTexture().getWidth()/clms)*widthmodfier* Settings.scale, (float)(currentFrame.getTexture().getHeight()/rows)*heightmodifier*Settings.scale);
    }
    public void playOnceOverCreature(AbstractCreature m){
        playOnceOverCreature(m, 0, 0);
    }
    public void playOnceOverCreature(AbstractCreature m, float adjustx, float adjusty){
        moveOverCreature(m, adjustx, adjusty);
        ishidden=false;
        playOnce();
    }
    public void playCopyOnceOverCreature(AbstractCreature m){
        playCopyOnceOverCreature(m, 0, 0);
    }
    public void playCopyOnceOverCreature(AbstractCreature m, float adjustx, float adjusty){
        usescopies=true;
        GifCopy copy=new GifCopy(this);
        copy.moveOverCreature(m, adjustx, adjusty);
        copy.ishidden=false;
        copy.playOnce();
        copies.add(copy);
    }
    public void moveOverCreature(AbstractCreature m)
    {
        moveOverCreature(m, 0, 0);
    }
    public void moveOverCreature(AbstractCreature m, float xadjust, float yadjust)
    {
        //get the frame so we can move it
        TextureRegion currentFrame = GifAnimation.getKeyFrame(0, loop);
        //this is the function previously used by the mod maker to draw over a creature, we reference it here for new features
       /*sb.draw(
                currentFrame,
                m.drawX - ((float) (currentFrame.getTexture().getWidth()/clms)*widthmodfier*Settings.scale*0.5F) + m.animX,
                (float) m.drawY + m.animY,
                (float)(currentFrame.getTexture().getWidth()/clms)*widthmodfier* Settings.scale,
                (float)(currentFrame.getTexture().getHeight()/rows)*heightmodifier*Settings.scale
                );*/
        //adjust the texture by user-defined amounts (for uncentered effects on the sprite),
        //move them into position on the screen, accounting for scale by using real screen position of the character instead of scaled position,
        //also move the vertical center up half the monster's height (may add user option later),
        //lastly, center the gif frame on itself, again may user-define later
        currentx=(float)(
                xadjust+flatadjustx
                +(m.drawX+m.animX)/Settings.scale
                -((float) (currentFrame.getTexture().getWidth()/clms)*widthmodfier*0.5F)
        );
        currenty=(float)(
                yadjust+flatadjusty
                +((m.hb_h*0.5f+m.drawY+m.animY))/Settings.scale
                -(float)(currentFrame.getTexture().getHeight()/rows)*heightmodifier*0.5f
        );
        //debug lines for testing scaling issues
        //WarlockMod.logger.info("Scale Setting: "+Settings.scale+", Texture Width: "+currentFrame.getTexture().getWidth());
        //WarlockMod.logger.info("Draw x: "+m.drawX+", Anim x: "+m.animX);
        //WarlockMod.logger.info("Draw y: "+m.drawY+", Anim y: "+m.animY);
        //WarlockMod.logger.info("Current x: "+currentx+", Current y: "+currenty);
        //WarlockMod.logger.info("Monster width: "+m.hb_w+", Monster height: "+m.hb_h);
    }
    public void setLoop(boolean loop)
    {
        this.loop = loop;
    }

    public void playOnce()
    {
        this.stateTime = 0;
        this.isTemp=true;
        this.ishidden=false;
    }
    public void playOnce(boolean t)
    {
        this.stateTime = 0;
        this.isTemp=t;
        this.ishidden=false;
    }

    public boolean setTemp(boolean b){
        this.isTemp=b;
        return this.isTemp;
    }
    private void completeTempAnimation(){
        if(this.isTemp&&animationFinished(stateTime)){
            this.ishidden=true;
        }
    }
    public boolean animationFinished(float state){
        return GifAnimation.isAnimationFinished(state);
    }
    public boolean animationFinished(){
        return GifAnimation.isAnimationFinished(stateTime);
    }

    public void playOnceOnSpecificCard(AbstractCard card)
    {
        CardTimerField.CardTimer.set(card, 0.0f);
    }

    public void addAsBackgroundAnimation()
    {
        if(GifTheSpireLib.Animations.containsKey("Background"))
        {
            GifTheSpireLib.Animations.get("Background").add(this);
        }
        else
        {
            GifTheSpireLib.Animations.put("Background", new ArrayList<>());
            GifTheSpireLib.Animations.get("Background").add(this);
        }
    }
    public void addAsCardAnimation(String Card)
    {
        if(GifTheSpireLib.Animations.containsKey(Card))
        {
            GifTheSpireLib.Animations.get(Card).add(this);
        }
        else
        {
            GifTheSpireLib.Animations.put(Card, new ArrayList<>());
            GifTheSpireLib.Animations.get(Card).add(this);
        }
    }
    public void addAsEventAnimation(String Event)
    {
        if(GifTheSpireLib.Animations.containsKey(Event))
        {
            GifTheSpireLib.Animations.get(Event).add(this);
        }
        else
        {
            GifTheSpireLib.Animations.put(Event, new ArrayList<>());
            GifTheSpireLib.Animations.get(Event).add(this);
        }
    }
    public void addAsForeGroundAnimation()
    {
        if(GifTheSpireLib.Animations.containsKey("Foreground"))
        {
            GifTheSpireLib.Animations.get("Foreground").add(this);
        }
        else
        {
            GifTheSpireLib.Animations.put("Foreground", new ArrayList<>());
            GifTheSpireLib.Animations.get("Foreground").add(this);
        }
    }
    public void addAsCharacterAnimation(String ChosenClass)
    {
        if(GifTheSpireLib.Animations.containsKey(ChosenClass))
        {
            GifTheSpireLib.Animations.get(ChosenClass).add(this);
        }
        else
        {
            GifTheSpireLib.Animations.put(ChosenClass, new ArrayList<>());
            GifTheSpireLib.Animations.get(ChosenClass).add(this);
        }
    }
    public void addAsMonsterAnimation(String MonsterID)
    {
        if(GifTheSpireLib.Animations.containsKey(MonsterID))
        {
            GifTheSpireLib.Animations.get(MonsterID).add(this);
        }
        else
        {
            GifTheSpireLib.Animations.put(MonsterID, new ArrayList<>());
            GifTheSpireLib.Animations.get(MonsterID).add(this);
        }
    }



    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
