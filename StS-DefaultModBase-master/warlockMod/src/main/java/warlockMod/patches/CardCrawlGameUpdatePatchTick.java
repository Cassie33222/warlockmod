package warlockMod.patches;

import GifTheSpire.GifTheSpireLib;
import GifTheSpire.util.GifAnimation;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import warlockMod.WarlockMod;

@SpirePatch(clz = CardCrawlGame.class, method = "update")
public class CardCrawlGameUpdatePatchTick {
    @SpirePostfixPatch
    public static void patch(CardCrawlGame __instance) {
            WarlockMod.tick();
    }
}
