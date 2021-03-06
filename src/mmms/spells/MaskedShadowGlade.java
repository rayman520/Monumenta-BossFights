package mmms.spells;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import mmbf.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class MaskedShadowGlade
{

	private Plugin plugin;
	
	public MaskedShadowGlade(mmbf.main.Main plugin2)
	{	
		plugin = plugin2;
	}
	
	Random rand = new Random();
	Utils utils = new Utils(plugin);
	int Ccount = 0;
	
	public boolean onSpell(CommandSender sender, String[] arg)
	{
		if (arg.length != 2)
		{
			System.out.println(ChatColor.RED + "wrong number of parameters given!\n" + ChatColor.GREEN + "Usage: " + ChatColor.DARK_GREEN + "/mobspell Melee_Minions_1 <Count> <Scope> <Repeats>");
			return (true);
		}
		int count = Integer.parseInt(arg[1]);
		if (count < 0 || count > 4)
		{
			System.out.println(ChatColor.RED + "Count must be between 0 and 4");
			return (false);
		}
		Ccount = count;
		boolean[] isQuadrantDone = new boolean[4];
		Location[] possibleLocs = new Location[4];
		Location sendLoc = utils.calleeEntity(sender).getLocation();
		int j = 0;
		for (int x = 0; x < 2; x++)
		{
			for (int y = 0; y < 2; y++)
			{
				possibleLocs[j] = new Location(sendLoc.getWorld(), sendLoc.getX() - 8.25 + x * 12.5, sendLoc.getY() - 5, sendLoc.getZ() - 8.25 + y * 12.5);
				j++;
			}
		}
		int chosen;
		while (count > 0)
		{
			chosen = rand.nextInt(4);
			if (!isQuadrantDone[chosen])
			{
				count--;
				isQuadrantDone[chosen] = true;
				spell(possibleLocs[chosen]);
			}
		}
		return true;
	}
	
	public void spell(Location zoneStart)
	{
		System.out.println(zoneStart.toString());
		animation(zoneStart);
		damage(zoneStart);
	}
	
	int j = 0;
	public void animation(Location zoneStart)
	{
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		List<Player> pList = utils.playersInRange(zoneStart, 40);
		
		Runnable anim_loop = new Runnable() {
            @Override
            public void run() {
            		for (Player player : pList)
            		{
            			Location pPos = player.getLocation();
            			if (pPos.getX() > zoneStart.getX() - 8.25 && pPos.getX() < zoneStart.getX() + 8.25 && pPos.getZ() > zoneStart.getZ() - 8.25 && pPos.getZ() < zoneStart.getZ() + 8.25)
            			{
            				pPos.getWorld().playSound(pPos, Sound.ENTITY_PLAYER_HURT_ON_FIRE, 1f, 0.5f);
            			}
            		}
            		zoneStart.getWorld().spawnParticle(Particle.FLAME, zoneStart, (j / Ccount) * 10, 4, 0, 4, 0.01);
            		if (j / Ccount >= 24)
            		{
            			for (Player player : pList)
                		{
                			Location pPos = player.getLocation();
                			pPos.getWorld().playSound(pPos, Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 1f, 0.8f);
                		}
            			zoneStart.getWorld().spawnParticle(Particle.LAVA, zoneStart, (j / Ccount) * 10, 4, 0, 4, 0.01);
            		}
            		j++;
            }
        };
        
        for (int i = 0; i < 25; i++)
        		scheduler.scheduleSyncDelayedTask(this.plugin, anim_loop , (i * 4));
	}
	
	public void damage(Location zoneStart)
	{
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		List<Player> pList = utils.playersInRange(zoneStart, 40);
		
		Runnable burst = new Runnable() {
            @Override
            public void run() {
            		for (Player player : pList)
            		{
            			Location pPos = player.getLocation();
            			if (pPos.getX() > zoneStart.getX() - 8.25 && pPos.getX() < zoneStart.getX() + 8.25 && pPos.getZ() > zoneStart.getZ() - 8.25 && pPos.getZ() < zoneStart.getZ() + 8.25)
                		{
            				pPos.getWorld().playSound(pPos, Sound.ENTITY_GHAST_HURT, 1f, 0.7f);
        					player.addPotionEffect((new PotionEffect(PotionEffectType.WITHER, 7 * 20, 3)));
        					player.addPotionEffect((new PotionEffect(PotionEffectType.BLINDNESS, 7 * 20, 1)));
                         player.setFireTicks(20 * 7);
            			}
            		}
            }
        };
        scheduler.scheduleSyncDelayedTask(this.plugin, burst , 100L);
	}
}
