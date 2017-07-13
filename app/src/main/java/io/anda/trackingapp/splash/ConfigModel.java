package io.anda.trackingapp.splash;

public class ConfigModel {
    private int themes;
    private int balance;

    public ConfigModel(int themes, int balance) {
        this.themes = themes;
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public int getThemes() {
        return themes;
    }
}
