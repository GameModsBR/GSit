package dev.geco.gsit.objects;

import java.util.*;
import java.util.stream.*;

import org.bukkit.entity.*;
import org.bukkit.scheduler.*;

import dev.geco.gsit.GSitMain;

public class GEmote {

    protected final String id;

    protected final List<GEmotePart> parts;

    protected final long loop;

    protected final boolean head;

    protected final HashMap<Long, List<GEmotePart>> setParts = new HashMap<>();

    protected HashMap<Entity, BukkitRunnable> tasks = new HashMap<>();

    protected long range = 250;

    public GEmote(String Id, List<GEmotePart> Parts, long Loop, boolean Head) {
        id = Id;
        parts = new ArrayList<>(Parts);
        loop = Loop;
        head = Head;

        long i = 0;

        for(GEmotePart part : parts) {

            i += part.getDelay();

            List<GEmotePart> sParts = setParts.containsKey(i) ? setParts.get(i) : new ArrayList<>();

            sParts.add(part);

            setParts.put(i, sParts);
        }
    }

    public void start(LivingEntity Entity) {

        if(parts.size() == 0) return;

        boolean pe = Entity instanceof Player;

        BukkitRunnable task = new BukkitRunnable() {

            long i = 0;
            long l = 0;
            final long t = Collections.max(setParts.keySet());

            @Override
            public void run() {

                if(setParts.containsKey(i)) {

                    for(GEmotePart part : setParts.get(i)) {

                        if(pe) {

                            Player p = (Player) Entity;

                            for(Player t : Entity.getWorld().getPlayers().stream().filter(o -> Entity.getLocation().distance(o.getLocation()) <= range && o.canSee(p)).collect(Collectors.toSet())) {
                                part.start(t, Entity, isFromHead());
                            }
                        } else {

                            for(Player t : Entity.getWorld().getPlayers().stream().filter(o -> Entity.getLocation().distance(o.getLocation()) <= range).collect(Collectors.toSet())) {
                                part.start(t, Entity, isFromHead());
                            }
                        }
                    }
                }

                i++;

                if(i >= t) {

                    if(getLoop() > 0 && getLoop() <= l) GSitMain.getInstance().getEmoteManager().stopEmote(Entity);
                    else {
                        i = 0;
                        l++;
                    }
                }
            }
        };

        task.runTaskTimer(GSitMain.getInstance(), 0, 1);

        tasks.put(Entity, task);
    }

    public void stop(Entity Entity) {

        if(!tasks.containsKey(Entity)) return;

        tasks.get(Entity).cancel();
    }

    public String getId() { return id; }

    public List<GEmotePart> getParts() { return parts; }

    public long getLoop() { return loop; }

    public boolean isFromHead() { return head; }

    public String toString() { return getId(); }

}