package advdiff;

import arc.*;

public enum AdvDiffDifficulty {
    casual(1F,0.5f, 0.5f, 2f),
    //1*0.5*0.875*0.25      = 0.109375
    easy(1F,1f, 0.75f, 1.5f),
    //1*1*0.75*0.44         = 0.33
    normal(1F,1f, 1f, 1f),
    //1*1*1*1               = 1
    hard(1F,1.25f, 1.5f, 0.8f),
    //1*1.25*1.25*1.5625    = 2.44140625
    eradication(1F,1.5f, 2f, 0.6f),
    //1*1.5*1.5*2.777       = 6.24825
    unreasonable(1.5F,1.5f, 3f, 0.5f),
    //1.5*1.5*2*4           = 18
    custom(1f,1f,1f,1f);

    public static final AdvDiffDifficulty[] all = values();

    public float enemyDamageMultiplier, enemyHealthMultiplier, enemySpawnMultiplier, waveTimeMultiplier;

    AdvDiffDifficulty(float enemyDamageMultiplier, float enemyHealthMultiplier, float enemySpawnMultiplier, float waveTimeMultiplier){
        this.enemyDamageMultiplier = enemyDamageMultiplier;
        this.enemySpawnMultiplier = enemySpawnMultiplier;
        this.waveTimeMultiplier = waveTimeMultiplier;
        this.enemyHealthMultiplier = enemyHealthMultiplier;
    }

    public String info(){
        String res =
                (Core.bundle.format("difficulty." + name() + "-info") + "\n") +
                        (enemyHealthMultiplier == 1f ? "" : Core.bundle.format("difficulty.enemyHealthMultiplier", percentStat(enemyHealthMultiplier)) + "\n") +
                        (enemyDamageMultiplier == 1f ? "" : Core.bundle.format("difficulty.enemyDamageMultiplier", percentStat(enemyDamageMultiplier)) + "\n") +
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
