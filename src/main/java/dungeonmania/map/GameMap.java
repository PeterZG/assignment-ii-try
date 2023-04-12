package dungeonmania.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.DestoryAble;
import dungeonmania.entities.Entity;
import dungeonmania.entities.MoveAwayAble;
import dungeonmania.entities.OverlapAble;
import dungeonmania.entities.Player;
import dungeonmania.entities.Portal;
import dungeonmania.entities.Switch;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.ZombieToastSpawner;
import dungeonmania.util.Direction;
import dungeonmania.util.PathAlgorithm;
import dungeonmania.util.Position;

public class GameMap {
  private Game game;
  private Map<Position, GraphNode> nodes = new HashMap<>();
  private Player player;

  /**
   * Initialise the game map
   * 1. pair up portals
   * 2. register all movables
   * 3. register all spawners
   * 4. register bombs and switches
   * 5. more...
   */
  public void init() {
    initPairPortals();
    initRegisterMovables();
    initRegisterSpawners();
    initRegisterBombsAndSwitches();
  }

  private void initRegisterBombsAndSwitches() {
    List<Bomb> bombs = getEntities(Bomb.class);
    List<Switch> switchs = getEntities(Switch.class);
    for (Bomb b : bombs) {
      for (Switch s : switchs) {
        if (Position.isAdjacent(b.getPosition(), s.getPosition())) {
          b.subscribe(s);
          s.subscribe(b);
        }
      }
    }
  }

  // Pair up portals if there's any
  private void initPairPortals() {
    Map<String, Portal> portalsMap = new HashMap<>();
    nodes.forEach((k, v) -> {
      v.getEntities().stream().filter(Portal.class::isInstance).map(Portal.class::cast).forEach(portal -> {
        String color = portal.getColor();
        if (portalsMap.containsKey(color)) {
          portal.bind(portalsMap.get(color));
        } else {
          portalsMap.put(color, portal);
        }
      });
    });
  }

  private void initRegisterMovables() {
    List<Enemy> enemies = getEntities(Enemy.class);
    enemies.forEach(e -> {
      game.register(() -> e.move(game), Game.AI_MOVEMENT, e.getId());
    });
  }

  private void initRegisterSpawners() {
    List<ZombieToastSpawner> zts = getEntities(ZombieToastSpawner.class);
    zts.forEach(e -> {
      game.register(() -> e.spawn(game), Game.AI_MOVEMENT, e.getId());
    });
    game.register(() -> game.getEntityFactory().spawnSpider(game), Game.AI_MOVEMENT, "spawnSpiders");
  }

  public void moveTo(Entity entity, Position position) {
    if (!canMoveTo(entity, position))
      return;

    triggerMovingAwayEvent(entity);
    removeNode(entity);
    entity.setPosition(position);
    addEntity(entity);
    triggerOverlapEvent(entity);
  }

  public void moveTo(Entity entity, Direction direction) {
    if (!canMoveTo(entity, Position.translateBy(entity.getPosition(), direction)))
      return;
    triggerMovingAwayEvent(entity);
    removeNode(entity);
    entity.translate(direction);
    addEntity(entity);
    triggerOverlapEvent(entity);
  }

  private void triggerMovingAwayEvent(Entity entity) {
    List<Runnable> callbacks = new ArrayList<>();
    getEntities(entity.getPosition()).forEach(e -> {
      if (e != entity && e instanceof MoveAwayAble)
        callbacks.add(() -> ((MoveAwayAble) e).onMovedAway(this, entity));
    });
    callbacks.forEach(callback -> {
      callback.run();
    });
  }

  private void triggerOverlapEvent(Entity entity) {
    List<Runnable> overlapCallbacks = new ArrayList<>();
    getEntities(entity.getPosition()).forEach(e -> {
      if (e != entity && e instanceof OverlapAble)
        overlapCallbacks.add(() -> ((OverlapAble) e).onOverlap(this, entity));
    });
    overlapCallbacks.forEach(callback -> {
      callback.run();
    });
  }

  public boolean canMoveTo(Entity entity, Position position) {
    return !nodes.containsKey(position) || nodes.get(position).canMoveOnto(this, entity);
  }

  public Position dijkstraPathFind(Position src, Position dest, Entity entity) {
    return new PathAlgorithm(this, nodes).dijkstraPathFind(src, dest, entity);
  }

  public void removeNode(Entity entity) {
    Position p = entity.getPosition();
    if (nodes.containsKey(p)) {
      nodes.get(p).removeEntity(entity);
      if (nodes.get(p).size() == 0) {
        nodes.remove(p);
      }
    }
  }

  public void destroyEntity(Entity entity) {
    removeNode(entity);
    if (entity instanceof DestoryAble) {
      ((DestoryAble) entity).onDestroy(this);
    }
  }

  public void addEntity(Entity entity) {
    addNode(new GraphNode(entity));
  }

  public void addNode(GraphNode node) {
    Position p = node.getPosition();

    if (!nodes.containsKey(p))
      nodes.put(p, node);
    else {
      GraphNode curr = nodes.get(p);
      curr.mergeNode(node);
      nodes.put(p, curr);
    }
  }

  public Entity getEntity(String id) {
    Entity res = null;
    for (Map.Entry<Position, GraphNode> entry : nodes.entrySet()) {
      List<Entity> es = entry.getValue().getEntities().stream().filter(e -> e.getId().equals(id))
          .collect(Collectors.toList());
      if (es != null && es.size() > 0) {
        res = es.get(0);
        break;
      }
    }
    return res;
  }

  public List<Entity> getEntities(Position p) {
    GraphNode node = nodes.get(p);
    return (node != null) ? node.getEntities() : new ArrayList<>();
  }

  public List<Entity> getEntities() {
    List<Entity> entities = new ArrayList<>();
    nodes.forEach((k, v) -> entities.addAll(v.getEntities()));
    return entities;
  }

  public <T extends Entity> List<T> getEntities(Class<T> type) {
    return getEntities().stream().filter(type::isInstance).map(type::cast).collect(Collectors.toList());
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }
}
