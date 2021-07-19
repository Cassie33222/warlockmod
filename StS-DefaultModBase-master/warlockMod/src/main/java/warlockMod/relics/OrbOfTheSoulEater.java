package warlockMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import warlockMod.WarlockMod;
import warlockMod.orbs.SoulShard;
import warlockMod.util.TextureLoader;

public class OrbOfTheSoulEater extends CustomRelic {

    /*
     * https://github.com/daviscook477/BaseMod/wiki/Custom-Relics
     *
     * Orb of the Soul-Eater
     * "At the start of each combat, gain 1 Soul Shard. Gain 1 Soul Shard whenever a non-minion enemy dies."
     *
     */

    // ID, images, text.
    public static final String ID = WarlockMod.makeID("OrbOfTheSoulEater");

    private static final Texture IMG = TextureLoader.getTexture(WarlockMod.makeRelicPath("soulstone.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(WarlockMod.makeRelicOutlinePath("soulstone.png"));

    public OrbOfTheSoulEater() {
        super(ID, IMG, OUTLINE, RelicTier.STARTER, LandingSound.MAGICAL);
    }

    // Flash at the start of Battle.
    @Override
    public void atBattleStartPreDraw() {
        createSoulShard();
    }
    public void createSoulShard(){
        flash();
        AbstractDungeon.player.channelOrb(new SoulShard());
    }
    public void onMonsterDeath(AbstractMonster m) {
        boolean isminion=m.hasPower("Minion");
        if(!isminion) {
            createSoulShard();
        }
    }

    // Description
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
