package vanya.uff;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public final class Uff extends JavaPlugin implements Listener {
    Map<String, String> sexreqests = new HashMap<>();
    Map<String, Boolean> Cooldown = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);



    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Cooldown.put(e.getPlayer().getName(), false);

    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return this.filter(args);
        } else return null;
    }

    private List<String> filter(String[] args) {
        List<String> list = Arrays.asList("call", "cancel", "help");

        String last = args[args.length - 1];
        List<String> result = new ArrayList();
        Iterator var5 = list.iterator();

        while (var5.hasNext()) {
            String arg = (String) var5.next();
            if (arg.toLowerCase().startsWith(last.toLowerCase())) {
                result.add(arg);
            }
        }

        return result;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("sex")) {

            if (!(sender instanceof Player)) return false;

            switch (args.length) {
                case 0: //0 аргументов
                    sender.sendMessage(getConfig().getString("HelpMessage").replace("/n", "\n"));
                    return true;

                case 1: //1 аргумент
                    if (args[0].equalsIgnoreCase("help")) {
                        sender.sendMessage(getConfig().getString("HelpMessage").replace("/n", "\n"));
                        return true;
                    }

                    if (args[0].equalsIgnoreCase("cancel")) {
                        sender.sendMessage(getConfig().getString("CancelMessage"));
                        String name = sender.getName();
                        sexreqests.put(name, "null");
                        return true;
                    }

                case 2: //2 аргумента
                    if (args[0].equalsIgnoreCase("call")) {
                        if (!(Cooldown.get(sender.getName()))) {
                            if (!(args[1].equalsIgnoreCase(sender.getName()))) {
                                if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1]))) {
                                    if (((Player) sender).getLocation().distance(Bukkit.getPlayer(args[1]).getLocation()) < 3) {

                                        sender.sendMessage(getConfig().getString("AskMessage").replace("{player}", args[1]));

                                        Player p = Bukkit.getPlayer(args[1]);
                                        p.sendMessage(getConfig().getString("ReceivingMessage").replace("{player}", sender.getName()));

                                        p.spigot().sendMessage(new ComponentBuilder("§a✔")
                                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sex accept " + sender.getName()))
                                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(getConfig().getString("Click"))))
                                                .create());

                                        p.spigot().sendMessage(new ComponentBuilder("§c❌")
                                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sex reject " + sender.getName()))
                                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(getConfig().getString("Click"))))
                                                .create());


                                        String name = sender.getName();
                                        sexreqests.put(name, args[1]);

                                    } else sender.sendMessage(getConfig().getString("ErrorFar")); // if player too far
                                } else sender.sendMessage(getConfig().getString("ErrorOffline")); // if player offline
                            } else sender.sendMessage(getConfig().getString("SelfRequest")); // if request to himself
                        } else sender.sendMessage(getConfig().getString("Cooldown")); // if cooldown
                        return true;
                    }

                    if (args[0].equalsIgnoreCase("accept")) {
                        if (!(Cooldown.get(sender.getName()))) {
                            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1]))) {
                                String name = args[1]; // the name of the one from whom they accept
                                if (sexreqests.getOrDefault(name, "null").equalsIgnoreCase(sender.getName())) {
                                    if (((Player) sender).getLocation().distance(Bukkit.getPlayer(args[1]).getLocation()) < 3) {

                                        sexreqests.put(name, "null");
                                        sender.sendMessage(getConfig().getString("AcceptMessage").replace("{player}", name)); // to sender
                                        Bukkit.getPlayer(name).sendMessage(getConfig().getString("AcceptMessageReceiving").replace("{player}", sender.getName())); // to the player of the one from whom they accept

                                        //asynchronous message loop
                                        int i = 0;
                                        int max = (int) (Math.random() * 10 + 5);
                                        int delay = 0;

                                        ((Player) sender).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, max * 20 + 20, 0));
                                        Bukkit.getPlayer(name).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, max * 20 + 20, 0));

                                        ((Player) sender).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, max * 20 + 20, 9));
                                        Bukkit.getPlayer(name).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, max * 20 + 20, 9));

                                        while (i < max) {
                                            i++;
                                            delay += (int) Math.random() * 10 + 10;
                                            new BukkitRunnable() {
                                                public void run() {

                                                    String ProcMessage = getConfig().getStringList("ProcMessages").get((int) (Math.random() * getConfig().getStringList("ProcMessages").size()));
                                                    String AdditionalMessage = getConfig().getStringList("AdditionalMessages").get((int) (Math.random() * getConfig().getStringList("AdditionalMessages").size()));

                                                    sender.sendMessage(ProcMessage + " " + AdditionalMessage);
                                                    ((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_GUARDIAN_FLOP, 1, 1);

                                                    Bukkit.getPlayer(name).sendMessage(ProcMessage + " " + AdditionalMessage);
                                                    Bukkit.getPlayer(name).playSound(Bukkit.getPlayer(name).getLocation(), Sound.ENTITY_GUARDIAN_FLOP, 1, 1);

                                                    Cooldown.put(name, true);
                                                    Cooldown.put(sender.getName(), true);

                                                }
                                            }.runTaskLater(this, delay);
                                        }

                                        new BukkitRunnable() {
                                            public void run() {
                                                List CumMessages = getConfig().getStringList("CumMessages");
                                                Integer x = (int) (Math.random() * (CumMessages.size()));

                                                ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_ROOTED_DIRT_STEP, 1, 1);
                                                Bukkit.getPlayer(name).playSound(Bukkit.getPlayer(name).getLocation(), Sound.BLOCK_ROOTED_DIRT_STEP, 1, 1);
                                                sender.sendMessage(CumMessages.get(x).toString().replace("{player1}", name).replace("{player2}", sender.getName()));
                                                Bukkit.getPlayer(name).sendMessage(CumMessages.get(x).toString().replace("{player1}", name).replace("{player2}", sender.getName()));

                                                Cooldown.put(name, false);
                                                Cooldown.put(sender.getName(), false);
                                            }
                                        }.runTaskLater(this, delay + 20);
                                    } else sender.sendMessage(getConfig().getString("ErrorFar")); // if player too far
                                } else sender.sendMessage(getConfig().getString("ErrorLate")); // if player did`t send a request or late
                            } else sender.sendMessage(getConfig().getString("ErrorOffline")); // if offline
                        } else sender.sendMessage(getConfig().getString("Cooldown")); // if cooldown
                        return true;
                    }

                    if (args[0].equalsIgnoreCase("reject")) {
                        String name = args[1]; // the name of the one from whom they accept
                        if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1]))) {
                            if (sexreqests.getOrDefault(name, "null").equalsIgnoreCase(sender.getName())) {
                                sexreqests.put(name, "null");
                                sender.sendMessage(getConfig().getString("RejectMessage").replace("{player}", name)); // to sender
                                Bukkit.getPlayer(name).sendMessage(getConfig().getString("RejectMessageReceiving").replace("{player}", sender.getName())); // to the player of the one from whom they accept
                            } else sender.sendMessage(getConfig().getString("ErrorLate")); // if player did`t send a request or late
                        } else sender.sendMessage(getConfig().getString("ErrorOffline")); // if offline
                        return true;

                    }

            }

        }


        return super.onCommand(sender, command, label, args);

    }
}
