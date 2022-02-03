package warlockMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import warlockMod.WarlockMod;
import warlockMod.util.TextureLoader;

public class ShadowAndFlame extends CustomRelic {

    //At the start of combat, gain 1 Spellpower.

    // ID, images, text.
    public static final String ID = WarlockMod.makeID("ShadowAndFlame");

    private static final Texture IMG = TextureLoader.getTexture(WarlockMod.makeRelicPath("shadowandflame.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(WarlockMod.makeRelicOutlinePath("soulstone.png"));

    public ShadowAndFlame() {
        super(ID, IMG, OUTLINE, RelicTier.BOSS, LandingSound.MAGICAL);
    }

    // Description
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
