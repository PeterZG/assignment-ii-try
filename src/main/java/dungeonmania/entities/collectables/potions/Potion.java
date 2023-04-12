package dungeonmania.entities.collectables.potions;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.collectables.CollectableEntity;
import dungeonmania.util.Position;

public abstract class Potion extends CollectableEntity {
    private int duration;
    private int durability = 1;

    public Potion(Position position, int duration) {
        super(position);
        this.duration = duration;
    }

    public void use(Game game) {
        return;
    }

    public int getDuration() {
        return duration;
    }

    public abstract BattleStatistics applyBuff(BattleStatistics origin);

    public int getDurability() {
        return durability;
    }
}
