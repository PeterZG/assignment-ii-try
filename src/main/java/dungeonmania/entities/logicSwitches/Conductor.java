package dungeonmania.entities.logicSwitches;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.Boulder;
import dungeonmania.entities.Entity;
import dungeonmania.entities.OverlapAble;
import dungeonmania.entities.logicSwitches.LogicalEntities.LogicalEntity;
import dungeonmania.map.GameMap;
// import dungeonmania.entities.logicSwitches.LogicalEntities.LogicalEntity;
// import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Conductor extends LogicSwitch implements OverlapAble {
    private boolean prevActivated = false;
    private List<Wire> wires = new ArrayList<>();
    private List<LogicalEntity> logicEntities = new ArrayList<>();

    public Conductor(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            super.setActive(true);
            prevActivated = true;
            wires.stream().forEach(wire -> wire.onOverlap(map));
            activeAllLogicEntities(map);
        }
    }

    public void subscribe(LogicSwitch entity) {
        if (entity instanceof Wire) {
            wires = new LogicSwitchFormingList<Wire>().subscribe((Wire) entity, wires);
        } else if (entity instanceof LogicalEntity) {
            logicEntities = new LogicSwitchFormingList<LogicalEntity>().
                            subscribe((LogicalEntity) entity, logicEntities);
        }
    }

    public void setPrevActive(boolean prevActivated) {
        this.prevActivated = prevActivated;
    }

    public boolean isPrevActived() {
        return prevActivated;
    }

    public List<Conductor> getAdjacentConductors(GameMap map) {
        return super.getAdjacentConductors(map);
    }

    public void activeAllLogicEntities(GameMap map) {
        logicEntities.stream().forEach(e -> e.activate(map));
    }
}
