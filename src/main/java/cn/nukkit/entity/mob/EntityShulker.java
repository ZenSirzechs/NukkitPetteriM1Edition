package cn.nukkit.entity.mob;

import cn.nukkit.entity.Entity;
import cn.nukkit.utils.EntityUtils;
import cn.nukkit.entity.mob.EntityWalkingMob;
import cn.nukkit.entity.projectile.EntityShulkerBullet;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityShulker extends EntityWalkingMob {

    public static final int NETWORK_ID = 54;

    public EntityShulker(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1f;
    }

    @Override
    public float getHeight() {
        return 1f;
    }
    
    @Override
    public double getSpeed() {
        return 0;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.fireProof = true;
        this.setMaxHealth(15);
    }

    @Override
    public void attackEntity(Entity player) {
    if (this.attackDelay > 23 && EntityUtils.rand(1, 32) < 4 && this.distanceSquared(player) <= 55) {
            this.attackDelay = 0;

            double f = 0.5;
            double yaw = this.yaw + EntityUtils.rand(-220, 220) / 10;
            double pitch = this.pitch + EntityUtils.rand(-120, 120) / 10;
            Location pos = new Location(this.x - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.5, this.y + this.getHeight() - 0.18,
                    this.z + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.5, yaw, pitch, this.level);
            Entity k = EntityUtils.create("ShulkerBullet", pos, this);
            if (!(k instanceof EntityShulkerBullet)) {
                return;
            }

            EntityShulkerBullet bullet = (EntityShulkerBullet) k;
            bullet.setMotion(new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f,
                    Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f));

            ProjectileLaunchEvent launch = new ProjectileLaunchEvent(bullet);
            this.server.getPluginManager().callEvent(launch);
            bullet.spawnToAll();
            this.level.addSound(this, "mob.shulker.shoot");
        }
    }

    @Override
    public boolean attack(EntityDamageEvent ev) {
        super.attack(ev);

        if (!ev.isCancelled()) {
            if (EntityUtils.rand(1, 12) == 6) {
                this.move(EntityUtils.rand(-10, 10), 0, EntityUtils.rand(-10, 10));
            }
        }

        return true;
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent && !this.isBaby() && EntityUtils.rand(1, 2) == 1) {
            return new Item[]{Item.get(Item.SHULKER_SHELL, 0, 1)};
        } else {
            return new Item[0];
        }
    }

    @Override
    public int getKillExperience() {
        return 5;
    }

    @Override
    public void knockBack(Entity attacker, double damage, double x, double z, double base) {
    }
}
