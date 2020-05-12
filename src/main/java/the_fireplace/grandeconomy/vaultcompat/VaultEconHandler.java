package the_fireplace.grandeconomy.vaultcompat;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import the_fireplace.grandeconomy.econhandlers.IEconHandler;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public final class VaultEconHandler implements IEconHandler {

    private Economy econ;

    private boolean shouldUsePlayerAccount(UUID uuid, Boolean isPlayer) {
        return isPlayer == null && Bukkit.getOfflinePlayer(uuid).hasPlayedBefore() || isPlayer == Boolean.TRUE || !getEcon().hasBankSupport();
    }

    @Override
    public double getBalance(UUID uuid, Boolean isPlayer) {
        if(shouldUsePlayerAccount(uuid, isPlayer))
            return getEcon().getBalance(Bukkit.getOfflinePlayer(uuid));
        else
            return getEcon().bankBalance(uuid.toString()).balance;
    }

    @Override
    public boolean addToBalance(UUID uuid, double amount, Boolean isPlayer) {
        if(shouldUsePlayerAccount(uuid, isPlayer))
            return getEcon().depositPlayer(Bukkit.getOfflinePlayer(uuid), amount).transactionSuccess();
        else
            return getEcon().bankDeposit(uuid.toString(), amount).transactionSuccess();
    }

    @Override
    public boolean takeFromBalance(UUID uuid, double amount, Boolean isPlayer) {
        if(shouldUsePlayerAccount(uuid, isPlayer))
            return getEcon().withdrawPlayer(Bukkit.getOfflinePlayer(uuid), amount).transactionSuccess();
        else
            return getEcon().bankWithdraw(uuid.toString(), amount).transactionSuccess();
    }

    @Override
    public boolean setBalance(UUID uuid, double amount, Boolean isPlayer) {
        if(shouldUsePlayerAccount(uuid, isPlayer)) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            if(getEcon().getBalance(p) > amount)
                return getEcon().withdrawPlayer(p, getEcon().getBalance(p)-amount).transactionSuccess();
            else
                return getEcon().depositPlayer(p, amount-getEcon().getBalance(p)).transactionSuccess();
        } else {
            if(getEcon().bankBalance(uuid.toString()).balance > amount)
                return getEcon().bankWithdraw(uuid.toString(), getEcon().bankBalance(uuid.toString()).balance-amount).transactionSuccess();
            else
                return getEcon().bankDeposit(uuid.toString(), amount-getEcon().bankBalance(uuid.toString()).balance).transactionSuccess();
        }
    }

    @Override
    public String getCurrencyName(double amount) {
        return amount == 1 ? getEcon().currencyNameSingular() : getEcon().currencyNamePlural();
    }

    @Override
    public String getFormattedCurrency(double amount) {
        return getEcon().format(amount);
    }

    @Override
    public String getId() {
        return "vault";
    }

    @Override
    public void init() {}

    public Economy getEcon() {
        if(econ == null) {
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
            econ = economyProvider.getProvider();
        }
        return econ;
    }
}
