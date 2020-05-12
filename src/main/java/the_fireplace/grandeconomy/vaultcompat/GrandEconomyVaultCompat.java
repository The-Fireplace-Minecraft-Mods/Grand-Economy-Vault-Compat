package the_fireplace.grandeconomy.vaultcompat;

import com.google.common.collect.Lists;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import the_fireplace.grandeconomy.GrandEconomy;

public final class GrandEconomyVaultCompat extends JavaPlugin {
    @Override
    public void onEnable() {
        super.onEnable();
        if(!Lists.newArrayList("bukkit", "vault").contains(GrandEconomy.globalConfig.economyBridge.toLowerCase()))
            Bukkit.getServer().getServicesManager().register(Economy.class, new OtherEconHandler(this), this, ServicePriority.High);
        else
            //noinspection deprecation
            GrandEconomy.setEconomy(new VaultEconHandler());
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Bukkit.getServer().getServicesManager().unregisterAll(this);
    }
}
