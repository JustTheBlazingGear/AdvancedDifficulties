package advdiff;

import arc.*;
import mindustry.game.Difficulty;

public enum AdvDiffDifficulty {
    casual(0.5f, 0.5f, 2f),
    easy(1f, 0.75f, 1.5f),
    normal(1f, 1f, 1f),
    hard(1.25f, 1.5f, 0.8f),
    eradication(1.5f, 2f, 0.6f),
    unreasonable(1.75f, 2.5f, 0.4f);

    public static final AdvDiffDifficulty[] all = values();

    public float enemyHealthMultiplier, enemySpawnMultiplier, waveTimeMultiplier;

    AdvDiffDifficulty(float enemyHealthMultiplier, float enemySpawnMultiplier, float waveTimeMultiplier){
        this.enemySpawnMultiplier = enemySpawnMultiplier;
        this.waveTimeMultiplier = waveTimeMultiplier;
        this.enemyHealthMultiplier = enemyHealthMultiplier;
    }

    public String info(){
        String res =
                (Core.bundle.format("difficulty." + name() + "-info") + "\n") +
                (enemyHealthMultiplier == 1f ? "" : Core.bundle.format("difficulty.enemyHealthMultiplier", percentStat(enemyHealthMultiplier)) + "\n") +
                        (enemySpawnMultiplier == 1f ? "" : Core.bundle.format("difficulty.enemySpawnMultiplier", percentStat(enemySpawnMultiplier)) + "\n") +
                        (waveTimeMultiplier == 1f ? "" : Core.bundle.format("difficulty.waveTimeMultiplier", percentStatNeg(waveTimeMultiplier)) + "\n");

        return res.isEmpty() ? Core.bundle.get("difficulty.nomodifiers") : res;
    }

    public String localized(){
        return Core.bundle.get("difficulty." + name());
    }

    static String percentStat(float val){
        return ((int)(val * 100 - 100) > 0 ? "[negstat]+" : "[stat]") + (int)(val * 100 - 100) + "%[]";
    }

    static String percentStatNeg(float val){
        return ((int)(val * 100 - 100) > 0 ? "[stat]+" : "[negstat]") + (int)(val * 100 - 100) + "%[]";
    }
}
