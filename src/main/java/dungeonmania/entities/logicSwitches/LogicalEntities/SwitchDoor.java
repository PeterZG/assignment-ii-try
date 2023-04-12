package dungeonmania.entities.logicSwitches.LogicalEntities;

import dungeonmania.entities.Entity;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.logicSwitches.ConnectAble;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwitchDoor extends LogicalEntity {
    public SwitchDoor(Position pos, ConnectAble logicRule) {
        super(pos.asLayer(Entity.ITEM_LAYER), logicRule);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        if (isActivated() || entity instanceof Spider)
            return true;
        return false;
    }
}
