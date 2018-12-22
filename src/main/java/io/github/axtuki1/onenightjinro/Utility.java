package io.github.axtuki1.onenightjinro;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class Utility {
    public static Player getPlayer(String Name) {
        if( Name == null ){
            return null;
        }
        return Bukkit.getPlayerExact(Name);
    }

    public static Player getUUIDPlayer(String UUID) {
        if( UUID == null ){
            return null;
        }
        return Bukkit.getPlayer(UUID);
    }
    /**
     * 大文字小文字を区別せずにreplaceAllします
     *
     * @param regex 置き換えたい文字列
     * @param reql  置換後文字列
     * @param text  置換対象文字列
     */
    public static String myReplaceAll(String regex, String reql, String text) {
        String retStr = "";
        retStr = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text).replaceAll(reql);
        return retStr;
    }

    /**
     * コマンド引数を文字列に変換します。
     * @param args コマンド引数
     * @param start 開始するindex []に指定する数字でOK
     * @return
     */
    public static String CommandText(String[] args, int start){
        StringBuilder out = new StringBuilder();
        for(int i = start; i < args.length ; i++){
            out.append(args[i]).append(" ");
        }
        if( (out.length() - 1) <= 0 ){
            return out.toString();
        } else {
            return out.toString().substring(0,out.length() - 1);
        }
    }

    public static void sendCmdHelp(Player p, String cmd, String help, boolean Prefix){
        String send = " ";
        if(Prefix){
            send = MConJinro.getPrefix();
        }
        p.sendMessage(send + CmdColor(cmd));
        p.sendMessage(send + ChatColor.GREEN + "   " + help);
    }

    public static void sendCmdHelp(Player p, String cmd, String help){
        sendCmdHelp(p, cmd, help, false);
    }

    public static void sendCmdHelp(CommandSender p, String cmd, String help, boolean Prefix){
        sendCmdHelp((Player)p, cmd, help, Prefix);
    }

    public static void sendCmdHelp(CommandSender p, String cmd, String help){
        sendCmdHelp((Player)p, cmd, help, false);
    }

    public static String CmdColor(String s){
        return ChatColor.AQUA+s;
    }

    public static ChatColor getColor(float per){
        if( per <= 0.25 ){
            return ChatColor.RED;
        }
        if( per <= 0.5 ){
            return ChatColor.YELLOW;
        }
        return ChatColor.GREEN;
    }

    public static ChatColor getColor(float now, float max){
        float per = now / max;
        if( per <= 0.25 ){
            return ChatColor.RED;
        }
        if( per <= 0.5 ){
            return ChatColor.YELLOW;
        }
        return ChatColor.GREEN;
    }

    /**
     * 乱数を生成します 0 から max
     * @param max 生成される最大数値 - 1
     * @return 生成された整数
     */
    public static int generateRandom( int max ) {
        return (int)(Math.random() * max);
    }

}
