package warlockMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import warlockMod.WarlockMod;
import warlockMod.powers.Spellpower;
import warlockMod.util.TextureLoader;

public class ShadowMastery extends CustomRelic {

    //At the start of combat, gain 1 Spellpower.

    // ID, images, text.
    public static final String ID = WarlockMod.makeID("ShadowMastery");

    private static final Texture IMG = TextureLoader.getTexture(WarlockMod.makeRelicPath("shadowmastery.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(WarlockMod.makeRelicOutlinePath("soulstone.png"));

    public ShadowMastery() {
        super(ID, IMG, OUTLINE, RelicTier.BOSS, LandingSound.MAGICAL);
    }

    // Description
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
