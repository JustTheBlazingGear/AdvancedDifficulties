package advdiff;

import arc.Core;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.game.Difficulty;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.mod.*;
import arc.Events;

import static arc.math.Mathf.random;

public class AdvancedDifficulties extends Mod{
    public static AdvDiffDifficultySelectionDialog AdvDiffSettingsDialog;
    public static AdvDiffDifficulty difficulty = AdvDiffDifficulty.normal;
    public PlanetParams state = new PlanetParams();
    /*
    TODO
    Actual fcuking damn redesign of difficulty select
    Would be cool to add every single custom game rule in here
    Drop Zone Radius
    Core resources
     */

    public void loadContent() {
        AdvDiffStatusEffects.load();
    }

    public AdvancedDifficulties() {

        Events.on(EventType.WaveEvent.class, e -> {
            //Units get enraged if their amount per spawn reaches the max amount
            if (Core.settings.getBool("advdiff-enrageOnMaxUnits")) {
                Vars.state.rules.spawns.each((s) -> {
                    if (s.spawn >= s.max && s.effect != AdvDiffStatusEffects.radiance) {
                        s.effect = AdvDiffStatusEffects.enraged;
                    }
                });
            }
        });
        Events.on(EventType.ClientLoadEvent.class, e -> {

            Vars.ui.campaignRules = new AdvDiffDifficultySelectionDialog();

            //Your next line is "This code is shit".
            //But bear with me. I know java too little (none) so would absolutely appreciate help or feedback.
            //No idea how to override a vanilla dialog yet.
            //Vars.ui.research.buttons.button("advdiff-settings", Icon.units, AdvDiffSettingsDialog::show).size(210, 64);
            //Vars.ui.planet.titleTable.button("advdiff-settings", Icon.units, AdvDiffSettingsDialog::show).size(210, 64);
            //Vars.ui.planet.expandTable.button(Icon.units, AdvDiffSettingsDialog::show).size(210, 64);

            Vars.ui.settings.addCategory(Core.bundle.get("advdiff-settings"), Icon.units, t -> {
                //this.rules = rules;
                //t.number("advdiff-enemyHealthMultiplier", f -> rules.blockHealthMultiplier = f, () -> rules.blockHealthMultiplier);
                t.label(() -> "modifiers-info").row();
                t.sliderPref("advdiff-enemyHealthMultiplier", 100, 0, 500, 5, s -> s + "%");
                t.sliderPref("advdiff-enemyDamageMultiplier", 100, 0, 500, 5, s -> s + "%");
                t.sliderPref("advdiff-enemySpawnMultiplier", 100, 0, 2500, 25, s -> s + "%");
                t.sliderPref("advdiff-waveTimeMultiplier", 100, 0, 500, 5, s -> s + "%");
                t.sliderPref("advdiff-initialWaveSpacingMultiplier", 100, 0, 500, 5, s -> s + "%");
                t.sliderPref("advdiff-captureWaveMultiplier", 100, 0, 500, 5, s -> s + "%");
                t.sliderPref("advdiff-tierIncrease", 0, 0, 5, 1, s -> s + " Tiers");
                t.checkPref("advdiff-oneShotOneKill", false);
                t.checkPref("advdiff-enemyTypeRandomizer", false);
                t.checkPref("advdiff-enrageOnMaxUnits", false);
                t.checkPref("advdiff-forceRadiance", false);
                //t.button("advdiff-difficultySelect", Icon.units, AdvDiffSettingsDialog::show).size(210, 64);
            });
            //Vars.ui.planet.fill(t -> {
            //    t.button("settings", Icon.units, AdvDiffSettingsDialog::show).size(210, 64).center().left();
            //});
        });

        Events.run(EventType.Trigger.newGame, () -> {

            //Applying changes from new difficulties

            if (Core.settings.getInt("advdiff-captureWaveMultiplier")>100) {
                Vars.state.rules.spawns.each((s) -> {
                    if (s.unitScaling<1 && s.max<10) {
                        s.max = 10;
                    }
                    if (s.unitScaling<1) {
                        s.unitScaling = 1;
                    }
                    if (s.begin==s.end) {
                        s.spacing = 20;
                    }
                    if (s.end==Vars.state.rules.winWave-2) {
                        s.end = (int) Double.POSITIVE_INFINITY;
                    }
                });
            }
            //This (int) (double) shi looks ugly, but whatever you say, Intellij.
            //Btw I think it might crash.
            //Nope, it didn't + worked perfectly, so cool.
            Vars.state.rules.winWave *= (int) ((double) Core.settings.getInt("advdiff-captureWaveMultiplier") / 100);
            Vars.state.rules.waveSpacing *= (float) Core.settings.getInt("advdiff-initialWaveSpacingMultiplier") / 100;
            Vars.state.rules.initialWaveSpacing *= (float) Core.settings.getInt("advdiff-initialWaveSpacingMultiplier") / 100;
            Vars.state.rules.teams.get(Vars.state.rules.waveTeam).blockHealthMultiplier *= (int) ((double) Core.settings.getInt("advdiff-enemyHealthMultiplier") / 100);
            Vars.state.rules.teams.get(Vars.state.rules.waveTeam).blockDamageMultiplier *= (int) ((double) Core.settings.getInt("advdiff-enemyDamageMultiplier") / 100);
            Vars.state.rules.teams.get(Vars.state.rules.waveTeam).unitHealthMultiplier *= (int) ((double) Core.settings.getInt("advdiff-enemyHealthMultiplier") / 100);
            Vars.state.rules.teams.get(Vars.state.rules.waveTeam).unitDamageMultiplier *= (int) ((double) Core.settings.getInt("advdiff-enemyDamageMultiplier") / 100);
            Vars.state.rules.teams.get(Vars.state.rules.waveTeam).unitCostMultiplier *= 1f / Core.settings.getInt("advdiff-waveTimeMultiplier") / 100;
            Vars.state.rules.teams.get(Vars.state.rules.waveTeam).unitBuildSpeedMultiplier *= (int) ((double) Core.settings.getInt("advdiff-waveTimeMultiplier") / 100);

            Vars.state.rules.spawns.each((s) -> {
                s.unitAmount *= (int) ((double) Core.settings.getInt("advdiff-enemySpawnMultiplier") / 100);
                s.unitScaling *= (int) ((double) Core.settings.getInt("advdiff-enemySpawnMultiplier") / 100);
                s.max *= (int) ((double) Core.settings.getInt("advdiff-enemySpawnMultiplier") / 100);
            });

            //Niko One Shot - One Kill
            if (Core.settings.getBool("advdiff-oneShotOneKill")) {
                Vars.state.rules.teams.get(Vars.state.rules.waveTeam).blockHealthMultiplier /= 1000000;
                Vars.state.rules.teams.get(Vars.state.rules.waveTeam).blockDamageMultiplier *= 1000000;
                Vars.state.rules.teams.get(Vars.state.rules.waveTeam).unitHealthMultiplier /= 1000000;
                Vars.state.rules.teams.get(Vars.state.rules.waveTeam).unitDamageMultiplier *= 1000000;
                Vars.state.rules.teams.get(Vars.state.rules.defaultTeam).blockHealthMultiplier /= 1000000;
                Vars.state.rules.teams.get(Vars.state.rules.defaultTeam).blockDamageMultiplier *= 1000000;
                Vars.state.rules.teams.get(Vars.state.rules.defaultTeam).unitHealthMultiplier /= 1000000;
                Vars.state.rules.teams.get(Vars.state.rules.defaultTeam).unitDamageMultiplier *= 1000000;

            }

            //Force Radiance
            if (Core.settings.getBool("advdiff-forceRadiance")) {
                Vars.state.rules.spawns.each((s) -> {
                    s.effect = AdvDiffStatusEffects.radiance;
                });
            }

            //Enemy unit type randomizer
            //bulky, wish I could make it more compact
            if (Core.settings.getBool("advdiff-enemyTypeRandomizer")) {
                Vars.state.rules.spawns.each((s) -> {
                    int rngType = random(0,3);
                    if (s.type == UnitTypes.dagger ||
                            s.type == UnitTypes.crawler ||
                            s.type == UnitTypes.nova ||
                            s.type == UnitTypes.flare) {
                        switch (rngType) {
                            case 0: s.type = UnitTypes.dagger; break;
                            case 1: s.type = UnitTypes.crawler; break;
                            case 2: s.type = UnitTypes.nova; break;
                            case 3: s.type = UnitTypes.flare; break;
                        }
                    } else if (s.type == UnitTypes.mace ||
                            s.type == UnitTypes.atrax ||
                            s.type == UnitTypes.pulsar ||
                            s.type == UnitTypes.horizon) {
                        switch (rngType) {
                            case 0: s.type = UnitTypes.mace; break;
                            case 1: s.type = UnitTypes.atrax; break;
                            case 2: s.type = UnitTypes.pulsar; break;
                            case 3: s.type = UnitTypes.horizon; break;
                        }
                    } else if (s.type == UnitTypes.fortress ||
                            s.type == UnitTypes.spiroct ||
                            s.type == UnitTypes.quasar ||
                            s.type == UnitTypes.zenith) {
                        switch (rngType) {
                            case 0: s.type = UnitTypes.fortress; break;
                            case 1: s.type = UnitTypes.spiroct; break;
                            case 2: s.type = UnitTypes.quasar; break;
                            case 3: s.type = UnitTypes.zenith; break;
                        }
                    } else if (s.type == UnitTypes.scepter ||
                            s.type == UnitTypes.arkyid ||
                            s.type == UnitTypes.vela ||
                            s.type == UnitTypes.antumbra) {
                        switch (rngType) {
                            case 0: s.type = UnitTypes.scepter; break;
                            case 1: s.type = UnitTypes.arkyid; break;
                            case 2: s.type = UnitTypes.vela; break;
                            case 3: s.type = UnitTypes.antumbra; break;
                        }
                    } else if (s.type == UnitTypes.reign ||
                            s.type == UnitTypes.toxopid ||
                            s.type == UnitTypes.corvus ||
                            s.type == UnitTypes.eclipse) {
                        switch (rngType) {
                            case 0: s.type = UnitTypes.reign; break;
                            case 1: s.type = UnitTypes.toxopid; break;
                            case 2: s.type = UnitTypes.corvus; break;
                            case 3: s.type = UnitTypes.eclipse; break;
                        }
                    }
                });
            }
            
            
            //Enemy unit tier increase
            if (Core.settings.getInt("advdiff-tierIncrease")!=0) {
                Vars.state.rules.teams.get(Vars.state.rules.waveTeam).unitCostMultiplier /= Core.settings.getInt("advdiff-tierIncrease") * 3F;
                Vars.state.rules.teams.get(Vars.state.rules.waveTeam).unitBuildSpeedMultiplier *= Core.settings.getInt("advdiff-tierIncrease") * 3F;
            }
            //Behold, the block
            for (int i = 0; Core.settings.getInt("advdiff-tierIncrease")>i; i++) {
                Events.on(EventType.UnitCreateEvent.class, e -> {
                    if (e.unit.type == UnitTypes.scepter) {
                        e.unit.type = UnitTypes.reign;
                    } else if (e.unit.type == UnitTypes.fortress) {
                        e.unit.type = UnitTypes.scepter;
                    } else if (e.unit.type == UnitTypes.mace) {
                        e.unit.type = UnitTypes.fortress;
                    } else if (e.unit.type == UnitTypes.dagger) {
                        e.unit.type = UnitTypes.mace;
                    } else if (e.unit.type == UnitTypes.vela) {
                        e.unit.type = UnitTypes.corvus;
                    } else if (e.unit.type == UnitTypes.quasar) {
                        e.unit.type = UnitTypes.vela;
                    } else if (e.unit.type == UnitTypes.pulsar) {
                        e.unit.type = UnitTypes.quasar;
                    } else if (e.unit.type == UnitTypes.nova) {
                        e.unit.type = UnitTypes.pulsar;
                    } else if (e.unit.type == UnitTypes.arkyid) {
                        e.unit.type = UnitTypes.toxopid;
                    } else if (e.unit.type == UnitTypes.spiroct) {
                        e.unit.type = UnitTypes.arkyid;
                    } else if (e.unit.type == UnitTypes.atrax) {
                        e.unit.type = UnitTypes.spiroct;
                    } else if (e.unit.type == UnitTypes.crawler) {
                        e.unit.type = UnitTypes.atrax;

                    } else if (e.unit.type == UnitTypes.antumbra) {
                        e.unit.type = UnitTypes.eclipse;
                    } else if (e.unit.type == UnitTypes.zenith) {
                        e.unit.type = UnitTypes.antumbra;
                    } else if (e.unit.type == UnitTypes.horizon) {
                        e.unit.type = UnitTypes.zenith;
                    } else if (e.unit.type == UnitTypes.flare) {
                        e.unit.type = UnitTypes.horizon;
                    } else if (e.unit.type == UnitTypes.quad) {
                        e.unit.type = UnitTypes.oct;
                    } else if (e.unit.type == UnitTypes.mega) {
                        e.unit.type = UnitTypes.quad;
                    } else if (e.unit.type == UnitTypes.poly) {
                        e.unit.type = UnitTypes.mega;
                    } else if (e.unit.type == UnitTypes.mono) {
                        e.unit.type = UnitTypes.poly;

                    } else if (e.unit.type == UnitTypes.sei) {
                        e.unit.type = UnitTypes.omura;
                    } else if (e.unit.type == UnitTypes.bryde) {
                        e.unit.type = UnitTypes.sei;
                    } else if (e.unit.type == UnitTypes.minke) {
                        e.unit.type = UnitTypes.bryde;
                    } else if (e.unit.type == UnitTypes.risso) {
                        e.unit.type = UnitTypes.minke;
                    } else if (e.unit.type == UnitTypes.aegires) {
                        e.unit.type = UnitTypes.navanax;
                    } else if (e.unit.type == UnitTypes.cyerce) {
                        e.unit.type = UnitTypes.aegires;
                    } else if (e.unit.type == UnitTypes.oxynoe) {
                        e.unit.type = UnitTypes.cyerce;
                    } else if (e.unit.type == UnitTypes.retusa) {
                        e.unit.type = UnitTypes.oxynoe;

                    } else if (e.unit.type == UnitTypes.beta) {
                        e.unit.type = UnitTypes.gamma;
                    } else if (e.unit.type == UnitTypes.alpha) {
                        e.unit.type = UnitTypes.beta;

                    } else if (e.unit.type == UnitTypes.vanquish) {
                        e.unit.type = UnitTypes.conquer;
                    } else if (e.unit.type == UnitTypes.precept) {
                        e.unit.type = UnitTypes.vanquish;
                    } else if (e.unit.type == UnitTypes.locus) {
                        e.unit.type = UnitTypes.precept;
                    } else if (e.unit.type == UnitTypes.stell) {
                        e.unit.type = UnitTypes.locus;

                    } else if (e.unit.type == UnitTypes.quell) {
                        e.unit.type = UnitTypes.disrupt;
                    } else if (e.unit.type == UnitTypes.obviate) {
                        e.unit.type = UnitTypes.quell;
                    } else if (e.unit.type == UnitTypes.avert) {
                        e.unit.type = UnitTypes.obviate;
                    } else if (e.unit.type == UnitTypes.elude) {
                        e.unit.type = UnitTypes.avert;

                    } else if (e.unit.type == UnitTypes.tecta) {
                        e.unit.type = UnitTypes.collaris;
                    } else if (e.unit.type == UnitTypes.anthicus) {
                        e.unit.type = UnitTypes.tecta;
                    } else if (e.unit.type == UnitTypes.cleroi) {
                        e.unit.type = UnitTypes.anthicus;
                    } else if (e.unit.type == UnitTypes.merui) {
                        e.unit.type = UnitTypes.cleroi;

                    } else if (e.unit.type == UnitTypes.incite) {
                        e.unit.type = UnitTypes.emanate;
                    } else if (e.unit.type == UnitTypes.evoke) {
                        e.unit.type = UnitTypes.incite;

                    } else {
                        if (e.unit.hasEffect(StatusEffects.none)) {
                            e.unit.hasEffect(StatusEffects.overclock);
                        }
                    }
                });
                Log.info("Thy end is now.");
            }
        });
    }
}