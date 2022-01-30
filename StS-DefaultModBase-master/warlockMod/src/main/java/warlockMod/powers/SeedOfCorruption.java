package warlockMod.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import warlockMod.WarlockMod;
import warlockMod.cards.AfflictionCard;
import warlockMod.util.TextureLoader;

public class SeedOfCorruption extends WarlockDot{
    public static final String POWER_ID = WarlockMod.makeID("SeedOfCorruption");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex32 = TextureLoader.getTexture(WarlockMod.makePowerPath("seedofcorruption32.png"));
    private static final Texture tex84 = TextureLoader.getTexture(WarlockMod.makePowerPath("empty84.png"));

    boolean exploded=false;

    public SeedOfCorruption(AbstractCreature owner, AbstractCreature source, int amount) {
        super(owner, source, amount);
        name = NAME;
        ID = POWER_ID;
        // We load those textures here.
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        turnsremaining=6;
        affliction=true;
        //this.description = ("Dealing [#87ceeb]"+this.amount+"[] [#EFC851]Affliction[] damage over [#87ceeb]"+turnsremaining+"[] remaining turns.");
        updateDescription();
    }
    @Override
    public void removeIfComplete(){
        //remove if duration complete
        if(turnsremaining==0){
            explode();
            WarlockMod.cleansePower(this.owner, POWER_ID);
        }
    }
    public void onDeath(){
        if(owner==null||owner.currentHealth>0||owner.halfDead){
            return;
        }
    }
    public void explode(){
        if(exploded) {
            return;
        }
        exploded=true;
        int damagevalue=(int)Math.round(originalamount*AfflictionCard.getAfflictionRatio(AbstractDungeon.player, owner));

        WarlockMod.shadowboltimpactgif.playOnceOverCreature(this.owner);
        CardCrawlGame.sound.play(WarlockMod.shadowfurysound);
        CardCrawlGame.sound.play(WarlockMod.seedofcorruptionsound);

        addToBot(new DamageAllEnemiesAction(AbstractDungeon.player, damagevalue, DamageInfo.DamageType.HP_LOSS, AbstractGameAction.AttackEffect.NONE));
    }
    @Override
    public void updateDescription() {
        this.description = ("Dealing [#87ceeb]"+this.amount+"[] [#EFC851]Affliction[] damage over [#87ceeb]"+turnsremaining+"[] remaining turns." +
                " Deals [#87ceeb]"+this.originalamount+"[] [#EFC851]Affliction[] damage to enemies on expiration or death."
                );
    }
    @Override
    public AbstractPower makeCopy() {
        return new SeedOfCorruption(owner, source, amount);
    }
}
