package advdiff.content;

import advdiff.visual.DegradationStatus;
import advdiff.visual.RadianceStatus;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class AdvDiffStatusEffects {
    public static StatusEffect enraged,
            lowDegradation, mediumDegradation, highDegradation,
            radiance, greaterRadiance, supremeRadiance, plotArmor;

    public static void load() {
        enraged = new StatusEffect("enraged"){{
            alwaysUnlocked = true;
            color = Color.white;
            outline = false;
            permanent = true;
            speedMultiplier = 1.5f;
            damageMultiplier = 1.5f;
            reloadMultiplier = 1.5f;
            effectChance = 0.07F;
            effect = Fx.pulverizeRed;
        }
            //@Override
            //public boolean isHidden() {
            //    return false;
            //}

            @Override
            public void update(Unit unit, StatusEntry entry) {
                super.update(unit, entry);
                if (damage > 0) {
                    unit.damageContinuousPierce(damage);
                } else if (damage < 0) { //heal unit
                    unit.heal(-1f * damage * Time.delta);
                }

                if (Mathf.chanceDelta(effectChance)) {
                    Tmp.v1.trns(unit.rotation, -unit.type.engineOffset).add(unit);
                    //NHFunc.randFadeLightningEffect(Tmp.v1.x, Tmp.v1.y, unit.hitSize * 1.7f, Mathf.random(8f, 18f), unit.team.color, Mathf.chance(0.5));

                    effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, parentizeEffect ? unit : null);
                }
            }
        };
        lowDegradation = new DegradationStatus("low-degradation"){{
            alwaysUnlocked = true;
            outline = false;
            glintAlpha = 0.4F;
            baseSize = 1F;
            color = Color.white;
            permanent = true;
            healthMultiplier = 0.5f;
            speedMultiplier = 0.9f;
            damageMultiplier = 0.5f;
            reloadMultiplier = 0.75f;
        }};
        mediumDegradation = new DegradationStatus("medium-degradation"){{
            alwaysUnlocked = true;
            outline = false;
            glintAlpha = 0.6F;
            baseSize = 1.15F;
            color = Color.white;
            permanent = true;
            healthMultiplier = 0.25f;
            speedMultiplier = 0.8f;
            damageMultiplier = 0.25f;
            reloadMultiplier = 0.67f;
        }};
        highDegradation = new DegradationStatus("high-degradation"){{
            alwaysUnlocked = true;
            outline = false;
            glintAlpha = 0.8F;
            baseSize = 1.3F;
            color = Color.white;
            permanent = true;
            healthMultiplier = 1/16f;
            speedMultiplier = 0.7f;
            damageMultiplier = 1/16f;
            reloadMultiplier = 0.5f;
        }};
        radiance = new RadianceStatus("radiance"){{
            alwaysUnlocked = true;
            outline = false;
            glintAlpha = 0.3F;
            baseSize = 1.25F;
            color = Color.white;
            permanent = true;
            healthMultiplier = 2f;
            speedMultiplier = 1.1f;
            damageMultiplier = 2f;
            reloadMultiplier = 1.25f;
            effectChance = 0.2F;
            effect = AdvDiffFx.radiance;
        }};
        greaterRadiance = new RadianceStatus("greater-radiance"){{
            alwaysUnlocked = true;
            outline = false;
            glintAlpha = 0.5F;
            baseSize = 1.45F;
            color = Color.white;
            permanent = true;
            healthMultiplier = 4.0f;
            speedMultiplier = 1.25f;
            damageMultiplier = 4f;
            reloadMultiplier = 1.5f;
            effectChance = 0.4F;
            effect = AdvDiffFx.radiance;
        }};
        supremeRadiance = new RadianceStatus("supreme-radiance"){{
            alwaysUnlocked = true;
            outline = false;
            glintAlpha = 0.7F;
            baseSize = 1.75F;
            color = Color.white;
            permanent = true;
            healthMultiplier = 16f;
            speedMultiplier = 1.5f;
            damageMultiplier = 16f;
            reloadMultiplier = 2f;
            effectChance = 0.6F;
            effect = AdvDiffFx.radiance;
        }};
        /*
        plotArmor = new RadianceStatus("plot-armor"){{
            alwaysUnlocked = true;
            outline = false;
            glintAlpha = 0.9F;
            baseSize = 2F;
            color = Color.white;
            permanent = true;
            healthMultiplier = 256f;
            speedMultiplier = 2.5f;
            damageMultiplier = 256f;
            reloadMultiplier = 4f;
            effectChance = 0.8F;
            effect = AdvDiffFx.radiance;
        }};
         */
        plotArmor = new StatusEffect("plot-armor"){{
            alwaysUnlocked = true;
            outline = false;
            color = Color.white;
            permanent = true;
            healthMultiplier = Float.MAX_VALUE;
        }
            @Override
            public boolean isHidden() {
                return false;
            }

            //@Override
            //public void setStats() {
            //    this.stats.addMultModifier(Stat.healthMultiplier, Float.MAX_VALUE);
            //}

            @Override
            public void update(Unit unit, StatusEntry entry) {
                unit.health = unit.maxHealth();
            }
        };
    }
}