package advdiff;

import advdiff.content.AdvDiffStatusEffects;
import arc.Events;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.*;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.Planet;
import mindustry.type.UnitType;

import static advdiff.content.AdvDiffUnitTypes.*;
import static arc.math.Mathf.rand;
import static arc.math.Mathf.random;
import static mindustry.Vars.spawner;
import static mindustry.content.UnitTypes.*;

public class AdvDiffCampaignRules extends CampaignRules {
    public CustomTeamRules enemy;
    public CustomTeamRules player;
    public AdvDiffDifficulty advDiff;

    public boolean allowLaunchLoadout;
    public float launchCapacityMultiplier;
    public boolean allowLaunchSchematics;
    public float buildCostMultiplier;
    public float buildSpeedMultiplier;
    public float deconstructRefundMultiplier;

    public float enemySpawnMultiplier;
    public float waveTimeMultiplier;
    public float initialWaveSpacingMultiplier;
    public float captureWaveMultiplier;
    public float dropZoneRadiusMultiplier;

    public int wavesPerWave;
    public int tierIncrease;
    public int moddedUnitsAsExtraTiers;
    public int forceRadiance;

    public boolean shieldsToRadiance;
    public boolean enemyTypeRandomizer;
    public boolean enrageOnMaxUnits;

    public boolean allowEditRules;

    public int unitClass;
    public int unitTier;
    UnitType[][] species = {
            {null},
            {butterKnife, dagger, mace, fortress, scepter, reign},
            {null, crawler, atrax, spiroct, arkyid, toxopid},
            {null, nova, pulsar, quasar, vela, corvus},
            {null, flare, horizon, zenith, antumbra, eclipse},
            {null, mono, poly, mega, quad, oct},
            {null, risso, minke, bryde, sei, omura},
            {null, retusa, oxynoe, cyerce, aegires, navanax},
            {alpha, beta, gamma},

            {null, stell, locus, precept, vanquish, conquer}, //9
            {null, elude, avert, obviate, quell, disrupt},
            {null, merui, cleroi, anthicus, tecta, collaris},
            {evoke, incite, emanate},

            //New Horizon, 13
            {NHUnit("origin"), NHUnit("thynomo"), NHUnit("aliotiat"), NHUnit("tarlidor"), NHUnit("annihilation"), NHUnit("sin")},
            {NHUnit("histone"), NHUnit("restriction-enzyme"), NHUnit("lymph"), NHUnit("laugra")},
            {NHUnit("macrophage"), null, NHUnit("pester"), NHUnit("nucleoid")},
            {NHUnit("assaulter"), null, NHUnit("apparition"), null, null, NHUnit("anvil"), NHUnit("collapser")},
            {NHUnit("sharp"), NHUnit("branch"), NHUnit("warper"), NHUnit("striker"), NHUnit("naxos"),
                    NHUnit("destruction"), NHUnit("longinus"), NHUnit("hurricane")},
            {NHUnit("relay"), NHUnit("ghost"), null, NHUnit("zarkov"), null, NHUnit("declining")},

            //Asthosus
            {ASTHUnit("14n-01-shiv"), ASTHUnit("14n-02-bayonet"), ASTHUnit("14n-03-cleaver"), ASTHUnit("14n-04-apex"), ASTHUnit("14n-05-crest")},
            {ASTHUnit("15o-01-shennong"), ASTHUnit("15o-02-trigon"), ASTHUnit("15o-03-aprotodon"), ASTHUnit("15o-04-aphelops"), ASTHUnit("15o-05-annectodon")},
            {ASTHUnit("16a-01-parave"), ASTHUnit("16a-02-teraze"), ASTHUnit("16a-03-decredence"), ASTHUnit("16a-04-tequant"), ASTHUnit("16a-05-everse")},
            {ASTHUnit("16p-01-luminary"), ASTHUnit("16p-02-enlighten"), ASTHUnit("16p-03-illuminate"), ASTHUnit("16p-04-elucidate"), ASTHUnit("16p-05-expound")},
            {ASTHUnit("arcana-blob"), ASTHUnit("arcana-raynoid")},
            {ASTHUnit("barite-crawler"), ASTHUnit("barite-stalker"), ASTHUnit("barite-scourge"), null, ASTHUnit("barite-elder")},

            //Exoprosopa
            {EXPUnit("15o-01-mason"), EXPUnit("15o-02-oktarav"), EXPUnit("15o-03-vicient"), EXPUnit("15o-04-siphon"), EXPUnit("15o-05-rancor")},
            {EXPUnit("16p-01-ares"), EXPUnit("16p-02-rhitle"), EXPUnit("16p-03-sender"), EXPUnit("16p-04-carragher"), EXPUnit("16p-05-xenoct")},
            {EXPUnit("17q-01-codex"), EXPUnit("17q-02-calaust"), EXPUnit("17q-03-syntaxe"), EXPUnit("17q-04-noya"), EXPUnit("17q-05-archangel")},
            {EXPUnit("18r-01-byte"), EXPUnit("18r-02-syntax"), EXPUnit("18r-03-decode"), EXPUnit("18r-04-matrix"), EXPUnit("18r-05-program")},
            {EXPUnit("17q-05-bulbhead"), EXPUnit("17q-06-shark"), EXPUnit("17q-07-narhwal"), EXPUnit("17q-08-freesia")},

            //Frozen Farlands
            {FFLUnit("cryomech1"), FFLUnit("cryomech2"), FFLUnit("cryomech3"), FFLUnit("cryomech4"), FFLUnit("cryomech5")},
            {FFLUnit("cryospider1"), FFLUnit("cryospider2"), FFLUnit("cryospider3"), FFLUnit("cryospider4"), FFLUnit("cryospider5")},
            {FFLUnit("axoflare1"), FFLUnit("axoflare2"), FFLUnit("axoflare3"), FFLUnit("axoflare4"), FFLUnit("axoflare5")},
            {FFLUnit("cryoflare1"), FFLUnit("cryoflare2"), FFLUnit("cryoflare3"), FFLUnit("cryoflare4"), FFLUnit("cryoflare5")},
    };

    boolean shitRan = false;
    int loop = 0;

    public AdvDiffCampaignRules(Planet planet){
        enemy = new CustomTeamRules();
        player = new CustomTeamRules();
        advDiff = AdvDiffDifficulty.normal;

        allowLaunchLoadout = planet.allowLaunchLoadout;
        launchCapacityMultiplier = planet.launchCapacityMultiplier * 100;
        allowLaunchSchematics = planet.allowLaunchSchematics;
        buildCostMultiplier = 100f;
        buildSpeedMultiplier = 100f;
        deconstructRefundMultiplier = 50f;

        enemySpawnMultiplier = 100f;
        waveTimeMultiplier = 100f;
        initialWaveSpacingMultiplier = 100f;
        captureWaveMultiplier = 100f;
        dropZoneRadiusMultiplier = 100f;

        wavesPerWave = 1;
        tierIncrease = 0;
        moddedUnitsAsExtraTiers = 0;
        forceRadiance = 0;

        shieldsToRadiance = false;
        enemyTypeRandomizer = false;
        enrageOnMaxUnits = false;

        allowEditRules = false;

        fog = planet.campaignRuleDefaults.fog;
        hideSpawns = planet.campaignRuleDefaults.hideSpawns;
        sectorInvasion = planet.campaignRuleDefaults.sectorInvasion;
        randomWaveAI = planet.campaignRuleDefaults.randomWaveAI;
        rtsAI = planet.campaignRuleDefaults.rtsAI;
        pauseDisabled = planet.campaignRuleDefaults.pauseDisabled;
        clearSectorOnLose = planet.campaignRuleDefaults.clearSectorOnLose;

    }

    public void applyPlanet(Planet planet) {
        planet.allowLaunchLoadout = allowLaunchLoadout;
        planet.launchCapacityMultiplier = launchCapacityMultiplier / 100;
        planet.allowLaunchSchematics = allowLaunchSchematics;

        planet.campaignRules.sectorInvasion = sectorInvasion;
        planet.campaignRules.clearSectorOnLose = clearSectorOnLose;
    }
    @Override
    public void apply(Planet planet, Rules rules){

        rules.staticFog = rules.fog = fog;
        rules.hideSpawns = hideSpawns;
        rules.randomWaveAI = randomWaveAI;
        rules.pauseDisabled = pauseDisabled;
        if(planet.showRtsAIRule){
            boolean enabled = rtsAI && rules.attackMode;
            boolean swapped = rules.teams.get(rules.waveTeam).rtsAi != enabled;

            rules.teams.get(rules.waveTeam).rtsAi = enabled;
            rules.teams.get(rules.waveTeam).rtsMaxSquad = 15;

            if(swapped && Vars.state.isGame()){
                Groups.unit.each(u -> {
                    if(u.team == rules.waveTeam && !u.isPlayer()){
                        u.resetController();
                    }
                });
            }
        }
        planet.campaignRules.sectorInvasion = sectorInvasion;
        planet.campaignRules.clearSectorOnLose = clearSectorOnLose;

        rules.teams.get(rules.waveTeam).blockDamageMultiplier *= enemy.blockDamageMultiplier / 100;
        rules.teams.get(rules.waveTeam).blockHealthMultiplier *= enemy.blockHealthMultiplier / 100;
        rules.teams.get(rules.waveTeam).unitDamageMultiplier *= enemy.unitDamageMultiplier / 100;
        rules.teams.get(rules.waveTeam).unitHealthMultiplier *= enemy.unitHealthMultiplier / 100;
        rules.teams.get(rules.waveTeam).unitCostMultiplier *= enemy.unitCostMultiplier / 100;
        rules.teams.get(rules.waveTeam).unitBuildSpeedMultiplier *= enemy.unitBuildSpeedMultiplier / 100;
        if (enemy.oneShotOneKill) {
            rules.teams.get(rules.waveTeam).blockHealthMultiplier = 1000000f;
            rules.teams.get(rules.waveTeam).unitHealthMultiplier = 1000000f;
            rules.teams.get(Vars.player.team()).blockDamageMultiplier = 1/1000000f;
            rules.teams.get(Vars.player.team()).unitDamageMultiplier = 1/1000000f;
        }

        rules.teams.get(Vars.player.team()).blockDamageMultiplier *= player.blockDamageMultiplier / 100;
        rules.teams.get(Vars.player.team()).blockHealthMultiplier *= player.blockHealthMultiplier / 100;
        rules.teams.get(Vars.player.team()).unitDamageMultiplier *= player.unitDamageMultiplier / 100;
        rules.teams.get(Vars.player.team()).unitHealthMultiplier *= player.unitHealthMultiplier / 100;
        rules.teams.get(Vars.player.team()).unitCostMultiplier *= player.unitCostMultiplier / 100;
        rules.teams.get(Vars.player.team()).unitBuildSpeedMultiplier *= player.unitBuildSpeedMultiplier / 100;
        if (player.oneShotOneKill) {
            rules.teams.get(rules.waveTeam).blockDamageMultiplier = 1000000;
            rules.teams.get(rules.waveTeam).unitDamageMultiplier = 1000000;
            rules.teams.get(Vars.player.team()).blockHealthMultiplier = 1/1000000f;
            rules.teams.get(Vars.player.team()).unitHealthMultiplier = 1/1000000f;
        }

        rules.buildCostMultiplier *= buildCostMultiplier / 100;
        rules.buildSpeedMultiplier *= buildSpeedMultiplier / 100;
        rules.deconstructRefundMultiplier = deconstructRefundMultiplier / 100;

        rules.spawns.each((s) -> {
            s.unitAmount *= (int) (enemySpawnMultiplier / 100f);
            s.unitScaling *= enemySpawnMultiplier / 100f;
            s.max *= (int) (enemySpawnMultiplier / 100f);
        });
        //Idk what the fuck is this
        //Time.run(1f, () ->
        if (initialWaveSpacingMultiplier != 100 || waveTimeMultiplier != 100)  {
            if (rules.initialWaveSpacing == 0)
                Vars.state.wavetime -= rules.waveSpacing * 2 - rules.waveSpacing * 2 * (initialWaveSpacingMultiplier / 100) * (waveTimeMultiplier / 100);
            if (rules.initialWaveSpacing != 0)
                Vars.state.wavetime -= rules.initialWaveSpacing - rules.initialWaveSpacing * (initialWaveSpacingMultiplier / 100) * (waveTimeMultiplier / 100);
        }
        rules.waveSpacing *= waveTimeMultiplier / 100f;
        rules.objectiveTimerMultiplier *= waveTimeMultiplier / 100f;
        rules.teams.get(rules.waveTeam).unitFactoryActivationDelay *= waveTimeMultiplier / 100f;
        //if (initialWaveSpacingMultiplier != 100 && rules.initialWaveSpacing == 0) rules.initialWaveSpacing = rules.waveSpacing * 2;
        //rules.initialWaveSpacing *= initialWaveSpacingMultiplier / 100f;
        //if (initialWaveSpacingMultiplier != 100 && rules.initialWaveSpacing == 0) rules.initialWaveSpacing = 1;

        if(rules.winWave > 0){
            rules.winWave = (int) (rules.winWave * captureWaveMultiplier / 100f);
        }
        if (captureWaveMultiplier>100f || wavesPerWave>1) {
            rules.spawns.each((s) -> {
                if (s.unitScaling<1 && s.max<10) {
                    s.max = 10;
                }
                if (s.unitScaling<1) {
                    s.unitScaling = 1;
                }
                if (s.begin==s.end) {
                    s.spacing = 20;
                }
                if (s.end==rules.winWave-2) {
                    s.end = (int) Double.POSITIVE_INFINITY;
                }
            });
        }
        rules.dropZoneRadius *= dropZoneRadiusMultiplier / 100f;
        if (!rules.allowEditRules) rules.allowEditRules = allowEditRules;

        //Instead of UnitSpawnEvent replace Vars.state.rules.spawns
        //but apply degradation and radiance via a function
        if (tierIncrease!=0 || enemyTypeRandomizer) {
            rules.spawns.each((e) -> {
                //Log.info("Changing a spawn");
                unitClass = 0;
                unitTier = 0;
                for (int i = 0; i < species.length; i++) {
                    for (int i2 = 0; i2 < species[i].length; i2++) {
                        if (species[i][i2] == e.type) {
                            unitClass = i;
                            unitTier = i2;
                            break;
                        }
                    }
                }

                EnemyTypeRandomizer(species, true);

                if (tierIncrease==0) {
                    if (species[unitClass][unitTier] != null) {
                        e.type = species[unitClass][unitTier];
                    }
                }

                //This is turning into spaghetti that I can't understand
                //Leaving useful tipes for me and other people in my source code? Nah.
                //Leaving random bullshit? ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅
                //Holy shit the emojis are colored

                //Code rewritten
                //Holy glowup, it became 3 times smaller
                if (tierIncrease>0) for (int i = 0; tierIncrease - i >= 0; i++) {
                    //Log.info("Tier increasing loop started");
                    if (unitTier + tierIncrease - i >= 0 && unitTier + tierIncrease - i < species[unitClass].length) {
                        //Log.info("Tier and class are in range");
                        if (species[unitClass][unitTier + tierIncrease - i] != null) {
                            //Log.info("Tier and class is not null. Increasing tier");
                            e.type = species[unitClass][unitTier + tierIncrease - i];
                            //Log.info("Apply " + i + " radiance");
                            if (i > 0) e.shields += (float) (species[unitClass][unitTier + tierIncrease - i].health * 2 * Math.pow(2, i-1));
                            break;
                        } else if (unitClass == 0 && unitTier + tierIncrease - i == 0) {
                            if (i > 0) e.shields += (float) (e.type.health * 2 * Math.pow(2, i-1));
                        }
                    }
                }
                if (tierIncrease<0) for (int i = 0; tierIncrease + i <= 0; i++) {
                    if (unitTier + tierIncrease + i >= 0 && unitTier + tierIncrease + i < species[unitClass].length) {
                        if (species[unitClass][unitTier + tierIncrease + i] != null) {
                            e.type = species[unitClass][unitTier + tierIncrease + i];
                            for (int j = 0; i > j; j++) if (e.shields % 1 < 0.07) e.shields += 0.01F;
                            break;
                        } else if (unitClass == 0 && unitTier + tierIncrease + i == 0) {
                            for (int j = 0; i > j; j++) if (e.shields % 1 < 0.07) e.shields += 0.01F;
                        }
                    }
                }
            });
        }

        //Hm, I can't get modifiers like Waves Per Wave, Force Radiance and Tier Increase to not change
        //on the map if I start a game on a different map.
        //While changes from Tier Increase's spawns change remains.
        //So yeah, I'm using a spawn to store that data.
        rules.spawns.add(new SpawnGroup(dagger));
        rules.spawns.get(rules.spawns.size-1).end = 0; //So they won't spawn
        rules.spawns.get(rules.spawns.size-1).begin = wavesPerWave;
        rules.spawns.get(rules.spawns.size-1).spacing = forceRadiance+1;
        rules.spawns.get(rules.spawns.size-1).unitAmount = tierIncrease;
        //rules.spawns.get(rules.spawns.size-1).max = 5; //Will be later used for player tier increase
        rules.spawns.get(rules.spawns.size-1).shields = shieldsToRadiance ? 1 : 0;
        //Log.info(rules.spawns.get(rules.spawns.size-1));
    }
    public void applyEvents(){
        if (!shitRan) {
            Events.on(EventType.UnitSpawnEvent.class, e -> {
                if (Vars.state.rules.spawns.get(Vars.state.rules.spawns.size-1).type == dagger && Vars.state.rules.spawns.get(Vars.state.rules.spawns.size-1).end == 0) {
                    forceRadiance = Vars.state.rules.spawns.get(Vars.state.rules.spawns.size - 1).spacing-1;
                    tierIncrease = Vars.state.rules.spawns.get(Vars.state.rules.spawns.size - 1).unitAmount;
                    shieldsToRadiance = Vars.state.rules.spawns.get(Vars.state.rules.spawns.size - 1).shields == 1;
                } else {
                    forceRadiance = 0;
                    tierIncrease = 0;
                    shieldsToRadiance = false;
                }
                //Log.info("Yo sum shit spawned");
                if (e.unit.team == Vars.state.rules.waveTeam) {
                    //Suffering was unnecessary and all I needed was 1 frame delay
                    Time.run(0.001f, () -> {
                        if (shieldsToRadiance || tierIncrease > 0) {
                            //Log.info("Radihhiance");
                            int extraRadiance = 0;
                            while (e.unit.shield >= e.unit.health() * 2 * Math.pow(2, extraRadiance)) {
                                extraRadiance++;
                            }
                            //Shield amount has to be greatly decreased, because if unit has T3 Radiance he would take 8 times less damage
                            //meaning 1000 shields would act like 8000 shields. If the spawn gains about 10 shields per wave it would feel like 80 per wave.
                            //It would take the same amount of waves to increase Radiance tier, but they would feel even tougher than vanilla wave 500 mfs would
                            if (extraRadiance > 0) {
                                e.unit.shield -= (float) (e.unit.health() * Math.pow(2, extraRadiance));
                                e.unit.shield /= (float) Math.pow(2, extraRadiance);
                            }
                            if (extraRadiance + forceRadiance > 7) extraRadiance = 7 - forceRadiance;
                            if (extraRadiance + forceRadiance < -7) extraRadiance = -7 - forceRadiance;
                            radiance(e.unit, extraRadiance);
                            //Log.info("applied " + extraRadiance + " radiance");
                        }
                        if (shieldsToRadiance || tierIncrease < 0) {
                            int extraRadiance = 0;
                            extraRadiance -= (int) ((e.unit.shield() % 1) / 0.01F);
                            if (extraRadiance + forceRadiance > 7) extraRadiance = 7 - forceRadiance;
                            if (extraRadiance + forceRadiance < -7) extraRadiance = -7 - forceRadiance;
                            radiance(e.unit, extraRadiance);
                            //Log.info("applied " + extraRadiance + " degrad");
                        }
                    });
                }
            });
            //Keep using UnitUnloadEvent to spawn a new unit and remove the deployed one
            //because they will have default stats
            //apply degradation and radiance via a function
            Events.on(EventType.UnitCreateEvent.class, e -> {
                if (Vars.state.rules.spawns.get(Vars.state.rules.spawns.size-1).type == dagger && Vars.state.rules.spawns.get(Vars.state.rules.spawns.size-1).end == 0) {
                    tierIncrease = Vars.state.rules.spawns.get(Vars.state.rules.spawns.size - 1).unitAmount;
                } else {
                    tierIncrease = 0;
                }
                if (e.unit.team == Vars.state.rules.waveTeam && tierIncrease != 0 && e.unit.x != 0) {
                    unitClass = 0;
                    unitTier = 0;
                    for (int i = 0; i < species.length; i++) {
                        for (int i2 = 0; i2 < species[i].length; i2++) {
                            if (species[i][i2] == e.unit.type) {
                                unitClass = i;
                                unitTier = i2;
                                break;
                            }
                        }
                    }
                    EnemyTypeRandomizer(species, false);
                    if (tierIncrease > 0) for (int i = 0; tierIncrease - i >= 0; i++) {
                        if (unitTier + tierIncrease - i >= 0 && unitTier + tierIncrease - i < species[unitClass].length) {
                            if (species[unitClass][unitTier + tierIncrease - i] != null) {
                                Unit newUnit = species[unitClass][unitTier + tierIncrease - i].spawn(Vars.state.rules.waveTeam, e.unit.x, e.unit.y, e.unit.rotation);
                                radiance(newUnit, i);
                                e.unit.remove();
                                break;
                            }
                        }
                    }
                    if (tierIncrease < 0) for (int i = 0; tierIncrease + i <= 0; i++) {
                        if (unitTier + tierIncrease + i >= 0 && unitTier + tierIncrease + i < species[unitClass].length) {
                            if (species[unitClass][unitTier + tierIncrease + i] != null) {
                                Unit newUnit = species[unitClass][unitTier + tierIncrease + i].spawn(Vars.state.rules.waveTeam, e.unit.x, e.unit.y, e.unit.rotation);
                                radiance(newUnit, -i);
                                e.unit.remove();
                                break;
                            }
                        }
                    }
                }
            });
            /*
            Events.on(EventType.UnitUnloadEvent.class, e -> {
                if (Vars.state.rules.spawns.get(Vars.state.rules.spawns.size-1).type == dagger && Vars.state.rules.spawns.get(Vars.state.rules.spawns.size-1).end == 0) {
                    tierIncrease = Vars.state.rules.spawns.get(Vars.state.rules.spawns.size - 1).unitAmount;
                } else {
                    tierIncrease = 0;
                }
                if (e.unit.team == Vars.state.rules.waveTeam && tierIncrease != 0) {
                    unitClass = 0;
                    unitTier = 0;
                    for (int i = 0; i < species.length; i++) {
                        for (int i2 = 0; i2 < species[i].length; i2++) {
                            if (species[i][i2] == e.unit.type) {
                                unitClass = i;
                                unitTier = i2;
                                break;
                            }
                        }
                    }

                    EnemyTypeRandomizer(species, false);

                    if (tierIncrease > 0) for (int i = 0; tierIncrease - i >= 0; i++) {
                        if (unitTier + tierIncrease - i >= 0 && unitTier + tierIncrease - i < species[unitClass].length) {
                            if (species[unitClass][unitTier + tierIncrease - i] != null) {
                                //All this cuz ground units will die when spawned on top of the unit factory
                                float dir = e.unit.rotation;
                                int ex = 0;
                                int ey = 0;
                                if (dir == 0) ex = 4;
                                if (dir == 90) ey = 4;
                                if (dir == 180) ex = -4;
                                if (dir == 270) ey = -4;
                                Unit newUnit = species[unitClass][unitTier + tierIncrease - i].spawn(Vars.state.rules.waveTeam, e.unit.x + ex, e.unit.y + ey, e.unit.rotation);
                                radiance(newUnit, i);
                                e.unit.remove();
                                break;
                            }
                        }
                    }
                    if (tierIncrease < 0) for (int i = 0; tierIncrease + i <= 0; i++) {
                        if (unitTier + tierIncrease + i >= 0 && unitTier + tierIncrease + i < species[unitClass].length) {
                            if (species[unitClass][unitTier + tierIncrease + i] != null) {
                                float dir = e.unit.rotation;
                                int ex = 0;
                                int ey = 0;
                                if (dir == 0) ex = 4;
                                if (dir == 90) ey = 4;
                                if (dir == 180) ex = -4;
                                if (dir == 270) ey = -4;
                                Unit newUnit = species[unitClass][unitTier + tierIncrease + i].spawn(Vars.state.rules.waveTeam, e.unit.x + ex, e.unit.y + ey, e.unit.rotation);
                                radiance(newUnit, -i);
                                e.unit.remove();
                                break;
                            }
                        }
                    }
                }
            });
             */
            Events.on(EventType.WaveEvent.class, e -> {
                //if (loop % 2 == 0) {
                    //Units get enraged if their amount per spawn reaches the max amount
                /*
                if (config.enrageOnMaxUnits) {
                    Vars.state.rules.spawns.each((s) -> {
                        if (s.spawn >= s.max && s.effect != AdvDiffStatusEffects.radiance) {
                            s.effect = AdvDiffStatusEffects.enraged;
                        }
                    });
                }
                 */
                    if (Vars.state.rules.spawns.get(Vars.state.rules.spawns.size - 1).type == dagger) {
                        wavesPerWave = Vars.state.rules.spawns.get(Vars.state.rules.spawns.size - 1).begin;
                    } else {
                        wavesPerWave = 1;
                    }
                    for (int i = 1; wavesPerWave > i; i++) {
                        spawner.spawnEnemies();
                        Vars.state.wave++;
                    }
                //}
                //loop++;
            });
            shitRan = true;
        }
    }
    public UnitType ASTHUnit(String unit) {
        return Vars.content.unit("asthosus-" + unit);
    }
    public UnitType EXPUnit(String unit) {
        return Vars.content.unit("exoprosopa-" + unit);
    }
    public UnitType FFLUnit(String unit) {
        return Vars.content.unit("moon-mod-" + unit);
    }
    public UnitType EGOldUnit(String unit) {
        return Vars.content.unit("exogenesisold-" + unit) != null ? Vars.content.unit("exogenesisold-" + unit) : Vars.content.unit("exogenesis-" + unit);
    }
    public UnitType EGUnit(String unit) {
        return Vars.content.unit("exogenesis-" + unit);
    }
    public UnitType NHUnit(String unit) {
        return Vars.content.unit("new-horizon-" + unit);
    }
    public void EnemyTypeRandomizer(UnitType[][] species, boolean enableRandom){
        if (enemyTypeRandomizer && enableRandom) {
            if (unitClass >= 1 && unitClass <= 4) unitClass = random(1,4); //Serpulo Ground and non-support Air random
            species[4][4] = rand.chance(0.5) ? quad : antumbra; //The Antumbruh or the Quahh
            if (unitClass == 6 || unitClass == 7) unitClass = random(6,7); //Serpulo Naval random
        }
        //Exogenesis old
        if (moddedUnitsAsExtraTiers == 1) {
            species[1] = new UnitType[]{butterKnife, dagger, mace, fortress, scepter, reign, EGOldUnit("anvil"), EGOldUnit("fornax")};
            species[2] = new UnitType[]{null, crawler, atrax, spiroct, arkyid, toxopid, EGOldUnit("toxicity"), EGOldUnit("xenoct"), null, EGOldUnit("saggitarius")};
            species[3] = new UnitType[]{null, nova, pulsar, quasar, vela, corvus, EGOldUnit("stella"), EGOldUnit("virgo"), null, EGOldUnit("saggitarius")};

            species[4] = new UnitType[]{null, flare, horizon, zenith, antumbra, eclipse, EGOldUnit("twilight"), EGOldUnit("nadir")};
            species[5] = new UnitType[]{null, mono, poly, mega, quad, oct, EGOldUnit("hex"), EGOldUnit("colossus")};

            species[6] = new UnitType[]{null, risso, minke, bryde, sei, omura, EGOldUnit("orca"), EGOldUnit("balaenoptera"), null, EGOldUnit("apotheosis")};
            species[7] = new UnitType[]{null, retusa, oxynoe, cyerce, aegires, navanax, EGOldUnit("mariana"), EGOldUnit("magnapinna"), null, EGOldUnit("apotheosis")};

            species[9] = new UnitType[]{null, stell, locus, precept, vanquish, conquer, EGOldUnit("t-prometheus")};
            species[10] = new UnitType[]{null, elude, avert, obviate, quell, disrupt, EGOldUnit("t-atlas")};
            species[11] = new UnitType[]{null, merui, cleroi, anthicus, tecta, collaris, EGOldUnit("t-nemesis")};
        }
        //Exogenesis Java
        if (moddedUnitsAsExtraTiers == 2 && !enemyTypeRandomizer) {
            species[1] = new UnitType[]{butterKnife, dagger, mace, fortress, scepter, reign, EGUnit("empire"), EGUnit("apophis")};
            species[2] = new UnitType[]{null, crawler, atrax, spiroct, arkyid, toxopid, EGUnit("vidar")};
            species[3] = new UnitType[]{null, nova, pulsar, quasar, vela, corvus, EGUnit("ursa"), EGUnit("artemis")};

            species[4] = new UnitType[]{null, flare, horizon, zenith, antumbra, eclipse, EGUnit("twilight"), EGUnit("odin")};

            species[6] = new UnitType[]{null, risso, minke, bryde, sei, omura, EGUnit("orca"), EGUnit("tyr")};
            species[7] = new UnitType[]{null, retusa, oxynoe, cyerce, aegires, navanax, EGUnit("notodoris"), EGUnit("thor")};

            species[9] = new UnitType[]{null, stell, locus, precept, vanquish, conquer, EGUnit("prometheus")};
            species[10] = new UnitType[]{null, elude, avert, obviate, quell, disrupt, EGUnit("atlas")};
            species[11] = new UnitType[]{null, merui, cleroi, anthicus, tecta, collaris, EGUnit("nemesis")};
        }
        if (moddedUnitsAsExtraTiers == 2 && enemyTypeRandomizer) {
            species[1] = new UnitType[]{butterKnife, dagger, mace, fortress,
                    rand.chance(0.5) ? EGUnit("anvil") : scepter, rand.chance(0.5) ? EGUnit("smith") : reign, EGUnit("empire"), EGUnit("apophis")};
            species[2] = new UnitType[]{null, crawler, atrax, spiroct, arkyid, toxopid, EGUnit("vidar")};
            species[3] = new UnitType[]{null, nova, pulsar, quasar, vela,
                    rand.chance(0.5) ? EGUnit("neutron") : corvus, EGUnit("ursa"), EGUnit("artemis")};

            species[4] = new UnitType[]{null, flare, horizon, zenith, antumbra, eclipse, EGUnit("twilight"), EGUnit("odin")};

            species[6] = new UnitType[]{null, risso, minke, bryde, sei, omura, EGUnit("orca"), EGUnit("tyr")};
            species[7] = new UnitType[]{null, retusa, oxynoe, cyerce, aegires, navanax, EGUnit("notodoris"), EGUnit("thor")};

            species[9] = new UnitType[]{null, stell, locus, precept, vanquish, conquer, EGUnit("prometheus")};
            species[10] = new UnitType[]{null, elude, avert, obviate, quell, disrupt, EGUnit("atlas")};
            species[11] = new UnitType[]{null, merui, cleroi, anthicus, tecta, collaris, EGUnit("nemesis")};
        }
        //New Horizon
        if (moddedUnitsAsExtraTiers == 3) {
            species[1] = new UnitType[]{butterKnife, dagger, mace, fortress, scepter, reign, NHUnit("annihilation"), NHUnit("sin")};
            species[2] = new UnitType[]{null, crawler, atrax, spiroct, arkyid, toxopid, NHUnit("laugra")};
            species[3] = new UnitType[]{null, nova, pulsar, quasar, vela, corvus, NHUnit("laugra")};

            species[4] = new UnitType[]{null, flare, horizon, zenith, antumbra, eclipse, rand.chance(0.5) ? NHUnit("saviour") : null,
                    rand.chance(0.667) ? NHUnit("anvil") : rand.chance(0.5) ? NHUnit("hurricane") : NHUnit("guardian"),
                    rand.chance(0.5) ? NHUnit("collapser") : NHUnit("pester"),
                    NHUnit("nucleoid")};
            species[5] = new UnitType[]{null, mono, poly, mega, quad, oct, NHUnit("saviour")};

            species[6] = new UnitType[]{null, risso, minke, bryde, sei, omura, null, NHUnit("declining")};
            species[7] = new UnitType[]{null, retusa, oxynoe, cyerce, aegires, navanax, null, NHUnit("declining")};
        }
    }
    public void radiance(Unit unit, int extraRadiance){
        forceRadiance = Vars.state.rules.spawns.get(Vars.state.rules.spawns.size-1).spacing-1;
        //Log.info(extraRadiance + forceRadiance);
        switch (extraRadiance + forceRadiance) {
            case -6: unit.apply(AdvDiffStatusEffects.highDegradation); unit.apply(AdvDiffStatusEffects.mediumDegradation); break;
            case -5: unit.apply(AdvDiffStatusEffects.highDegradation); unit.apply(AdvDiffStatusEffects.lowDegradation); break;
            case -4: unit.apply(AdvDiffStatusEffects.highDegradation); break;
            case -3: unit.apply(AdvDiffStatusEffects.mediumDegradation); unit.apply(AdvDiffStatusEffects.lowDegradation); break;
            case -2: unit.apply(AdvDiffStatusEffects.mediumDegradation); break;
            case -1: unit.apply(AdvDiffStatusEffects.lowDegradation); break;

            case 1: unit.apply(AdvDiffStatusEffects.radiance); break;
            case 2: unit.apply(AdvDiffStatusEffects.greaterRadiance); break;
            case 3: unit.apply(AdvDiffStatusEffects.greaterRadiance); unit.apply(AdvDiffStatusEffects.radiance); break;
            case 4: unit.apply(AdvDiffStatusEffects.supremeRadiance); break;
            case 5: unit.apply(AdvDiffStatusEffects.supremeRadiance); unit.apply(AdvDiffStatusEffects.radiance); break;
            case 6: unit.apply(AdvDiffStatusEffects.supremeRadiance); unit.apply(AdvDiffStatusEffects.greaterRadiance); break;
        }
        if (extraRadiance + forceRadiance <= -7) {
            unit.apply(AdvDiffStatusEffects.highDegradation);
            unit.apply(AdvDiffStatusEffects.mediumDegradation);
            unit.apply(AdvDiffStatusEffects.lowDegradation);
        }
        if (extraRadiance + forceRadiance >= 7) {
            unit.apply(AdvDiffStatusEffects.supremeRadiance);
            unit.apply(AdvDiffStatusEffects.greaterRadiance);
            unit.apply(AdvDiffStatusEffects.radiance);
        }
    }

    public void set(AdvDiffDifficulty diff){
        waveTimeMultiplier = diff.waveTimeMultiplier * 100f;
        enemySpawnMultiplier = diff.enemySpawnMultiplier * 100f;

        enemy.unitCostMultiplier = 1/diff.enemySpawnMultiplier * 100f;
        enemy.unitBuildSpeedMultiplier = diff.enemySpawnMultiplier * 100f;
        enemy.blockDamageMultiplier = diff.enemyDamageMultiplier * 100f;
        enemy.unitDamageMultiplier = diff.enemyDamageMultiplier * 100f;
        enemy.blockHealthMultiplier = diff.enemyHealthMultiplier * 100f;
        enemy.unitHealthMultiplier = diff.enemyHealthMultiplier * 100f;

        player.unitCostMultiplier = 100f;
        player.unitBuildSpeedMultiplier = 100f;
        player.unitDamageMultiplier = 100f;
        player.blockDamageMultiplier = 100f;
        player.blockHealthMultiplier = 100f;
        player.unitHealthMultiplier = 100f;

        advDiff = diff;
    }

    public CustomTeamRules team(RuleTeam t){
        if(t == RuleTeam.enemy){
            return enemy;
        }
        else if(t == RuleTeam.player) {
            return player;
        }
        return null;
    }

    public static class CustomTeamRules{
        public float blockDamageMultiplier;
        public float blockHealthMultiplier;
        public float unitDamageMultiplier;
        public float unitHealthMultiplier;
        public float unitCostMultiplier;
        public float unitBuildSpeedMultiplier;
        public boolean oneShotOneKill;
        public CustomTeamRules(){
            blockDamageMultiplier = 100f;
            blockHealthMultiplier = 100f;
            unitDamageMultiplier = 100f;
            unitHealthMultiplier = 100f;
            unitCostMultiplier = 100f;
            unitBuildSpeedMultiplier = 100f;
            oneShotOneKill = false;
        }
    }
}
