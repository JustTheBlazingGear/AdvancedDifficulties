package advdiff;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.type.StatusEffect;

public class AdvDiffStatusEffects {
    public static StatusEffect enraged, radiance, supremeRadiance;

    public static void load() {
        enraged = new StatusEffect("enraged"){{
            color = Color.white;
            outline = false;
            permanent = true;
            speedMultiplier = 1.5f;
            damageMultiplier = 1.5f;
            reloadMultiplier = 1.5f;
            effectChance = 0.07F;
            effect = Fx.pulverizeRed;
        }};
        radiance = new StatusEffect("radiance"){{
            color = Color.white;
            permanent = true;
            healthMultiplier = 2f;
            speedMultiplier = 1.5f;
            damageMultiplier = 2f;
            reloadMultiplier = 2f;
            effectChance = 0.25F;
            effect = AdvDiffFx.radiance;
        }};
        supremeRadiance = new StatusEffect("supreme-radiance"){{
            color = Color.white;
            permanent = true;
            healthMultiplier = 3.0f;
            speedMultiplier = 1.25f;
            damageMultiplier = 4f;
            reloadMultiplier = 2f;
            effectChance = 0.5F;
            effect = AdvDiffFx.radiance;
        }};
    }
}