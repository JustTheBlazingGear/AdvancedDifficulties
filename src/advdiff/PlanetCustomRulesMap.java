package advdiff;

import arc.struct.*;
import mindustry.content.Planets;
import mindustry.type.Planet;

import static arc.Core.settings;
import static mindustry.Vars.content;

public class PlanetCustomRulesMap {
    Seq<PlanetCustomCampaignRules> maps = new Seq<>();

    public void load(){
        for(var p : content.planets()) if (p != Planets.sun) {
            String name = p.name + "advdiff-";
            AdvDiffCampaignRules r = new AdvDiffCampaignRules(p);

            //Log.info("Shit's loading for " + p.name);
            //Log.info(r.tierIncrease);
            //Log.info(settings.getInt(name + "EUTI", 0));
            r.enemy.blockDamageMultiplier = settings.getFloat(name + "EBD", 100f);
            r.enemy.blockHealthMultiplier = settings.getFloat(name + "EBH", 100f);
            r.enemy.unitDamageMultiplier = settings.getFloat(name + "EUD", 100f);
            r.enemy.unitHealthMultiplier = settings.getFloat(name + "EUH", 100f);
            r.enemy.unitCostMultiplier = settings.getFloat(name + "EUC", 100f);
            r.enemy.unitBuildSpeedMultiplier = settings.getFloat(name + "EUBS", 100f);
            r.enemy.oneShotOneKill = settings.getBool(name + "EOSOK", false);

            r.player.blockDamageMultiplier = settings.getFloat(name + "PBD", 100f);
            r.player.blockHealthMultiplier = settings.getFloat(name + "PBH", 100f);
            r.player.unitDamageMultiplier = settings.getFloat(name + "PUD", 100f);
            r.player.unitHealthMultiplier = settings.getFloat(name + "PUH", 100f);
            r.player.unitCostMultiplier = settings.getFloat(name + "PUC", 100f);
            r.player.unitBuildSpeedMultiplier = settings.getFloat(name + "PUBS", 100f);
            r.player.oneShotOneKill = settings.getBool(name + "POSOK", false);

            settings.put(name + "PALLDef", p.allowLaunchLoadout);
            settings.put(name + "PLCMDef", p.launchCapacityMultiplier);
            settings.put(name + "PALSDef", p.allowLaunchSchematics);
            r.allowLaunchLoadout = settings.getBool(name + "PALL", p.allowLaunchLoadout);
            r.launchCapacityMultiplier = settings.getFloat(name + "PLCM", p.launchCapacityMultiplier*100);
            r.allowLaunchSchematics = settings.getBool(name + "PALS", p.allowLaunchSchematics);
            r.buildCostMultiplier = settings.getFloat(name + "PBCM", 100f);
            r.buildSpeedMultiplier = settings.getFloat(name + "PBSM", 100f);
            r.deconstructRefundMultiplier = settings.getFloat(name + "PDRM", 50f);

            r.enemySpawnMultiplier = settings.getFloat(name + "ESM", 100f);
            r.waveTimeMultiplier = settings.getFloat(name + "WTM", 100f);
            r.initialWaveSpacingMultiplier = settings.getFloat(name + "IWSM", 100f);
            r.captureWaveMultiplier = settings.getFloat(name + "CWM", 100f);
            r.dropZoneRadiusMultiplier = settings.getFloat(name + "DZRM", 100f);

            r.wavesPerWave = settings.getInt(name + "WPW", 1);
            r.tierIncrease = settings.getInt(name + "EUTI", 0);
            r.moddedUnitsAsExtraTiers = settings.getInt(name + "MUAET", 0);

            r.forceRadiance = settings.getInt(name + "FR", 0);

            r.advDiff = AdvDiffDifficulty.all[settings.getInt(name + "D", 2)];

            r.shieldsToRadiance = settings.getBool(name + "STR", false);
            r.enemyTypeRandomizer = settings.getBool(name + "ETR", false);
            r.enrageOnMaxUnits = settings.getBool(name + "EOMU", false);

            r.allowEditRules = settings.getBool(name + "CAER", false);

            r.sectorInvasion = settings.getBool(name + "SI", p.campaignRules.sectorInvasion);
            r.fog = settings.getBool(name + "FOG", p.campaignRules.fog);
            r.hideSpawns = settings.getBool(name + "HS", p.campaignRules.hideSpawns);
            r.randomWaveAI = settings.getBool(name + "RWAI", p.campaignRules.randomWaveAI);
            r.rtsAI = settings.getBool(name + "RTSAI", p.campaignRules.randomWaveAI);
            r.clearSectorOnLose = settings.getBool(name + "CSOL", p.clearSectorOnLose);

            put(new PlanetCustomCampaignRules(p, r));

            if(settings.getBool("advdiff-forceShowDifficultyButton")){
                p.allowCampaignRules = true;
            }
            //Log.info("Shit loaded for " + p.name);
            //Log.info(r.tierIncrease);
            //Log.info(settings.getInt(name + "EUTI", 0));
        }
    }

    public void save(Planet planet, AdvDiffCampaignRules rules){
        //Log.info("Shit's about to save");
        PlanetCustomCampaignRules customrules = new PlanetCustomCampaignRules(planet, rules);
        saveCustomSetting(customrules);
        boolean found = false;
        for(int i = 0; i < maps.size; i++){
            if(maps.get(i).planet == planet){
                maps.set(i, customrules);
                found = true;
                break;
            }
        }
        if(!found){
            maps.add(customrules);
        }
    }

    public void put(PlanetCustomCampaignRules rules){
        maps.add(rules);
    }

    public AdvDiffCampaignRules get(Planet planet){
        //Log.info("Gotta get get");
        for(var rules : maps){
            if(rules.planet == planet) return rules.rules;
        }
        return new AdvDiffCampaignRules(planet);
    }

    private void saveCustomSetting(PlanetCustomCampaignRules customrules){
        AdvDiffCampaignRules r = customrules.rules;
        String name = customrules.planet.name + "advdiff-";
        //Log.info("Shit's saving for " + customrules.planet.name);
        //Log.info(r.tierIncrease);
        //Log.info(settings.getInt(name + "EUTI", 0));

        settings.put(name + "EBD", r.enemy.blockDamageMultiplier);
        settings.put(name + "EBH", r.enemy.blockHealthMultiplier);
        settings.put(name + "EUD", r.enemy.unitDamageMultiplier);
        settings.put(name + "EUH", r.enemy.unitHealthMultiplier);
        settings.put(name + "EUC", r.enemy.unitCostMultiplier);
        settings.put(name + "EUBS", r.enemy.unitBuildSpeedMultiplier);
        settings.put(name + "EOSOK", r.enemy.oneShotOneKill);

        settings.put(name + "PBD", r.player.blockDamageMultiplier);
        settings.put(name + "PBH", r.player.blockHealthMultiplier);
        settings.put(name + "PUD", r.player.unitDamageMultiplier);
        settings.put(name + "PUH", r.player.unitHealthMultiplier);
        settings.put(name + "PUC", r.player.unitCostMultiplier);
        settings.put(name + "PUBS", r.player.unitBuildSpeedMultiplier);
        settings.put(name + "POSOK", r.player.oneShotOneKill);

        settings.put(name + "PALL", r.allowLaunchLoadout);
        settings.put(name + "PLCM", r.launchCapacityMultiplier);
        settings.put(name + "PALS", r.allowLaunchSchematics);
        settings.put(name + "PBCM", r.buildCostMultiplier);
        settings.put(name + "PBSM", r.buildSpeedMultiplier);
        settings.put(name + "PDRM", r.deconstructRefundMultiplier);

        settings.put(name + "ESM", r.enemySpawnMultiplier);
        settings.put(name + "WTM", r.waveTimeMultiplier);
        settings.put(name + "IWSM", r.initialWaveSpacingMultiplier);
        settings.put(name + "CWM", r.captureWaveMultiplier);
        settings.put(name + "DZRM", r.dropZoneRadiusMultiplier);

        settings.put(name + "WPW", r.wavesPerWave);
        settings.put(name + "EUTI", r.tierIncrease);
        settings.put(name + "MUAET", r.moddedUnitsAsExtraTiers);
        settings.put(name + "FR", r.forceRadiance);

        for(int i = 0; i < AdvDiffDifficulty.all.length; i++){
            if(r.advDiff == AdvDiffDifficulty.all[i]){
                settings.put(name + "D", i);
            }
        }

        settings.put(name + "STR", r.shieldsToRadiance);
        settings.put(name + "ETR", r.enemyTypeRandomizer);
        settings.put(name + "EOMU", r.enrageOnMaxUnits);

        settings.put(name + "CAER", r.allowEditRules);

        settings.put(name + "SI", r.sectorInvasion);
        settings.put(name + "FOG", r.fog);
        settings.put(name + "HS", r.hideSpawns);
        settings.put(name + "RWAI", r.randomWaveAI);
        settings.put(name + "RTSAI", r.rtsAI);
        settings.put(name + "CSOL", r.clearSectorOnLose);

        //Log.info("Shit saved for " + customrules.planet.name);
        //Log.info(r.tierIncrease);
        //Log.info(settings.getInt(name + "EUTI", 0));
    }

    public static class PlanetCustomCampaignRules{
        public Planet planet;
        public AdvDiffCampaignRules rules;

        public PlanetCustomCampaignRules(Planet planet, AdvDiffCampaignRules rules){
            this.planet = planet;
            this.rules = rules;
        }
    }
}
