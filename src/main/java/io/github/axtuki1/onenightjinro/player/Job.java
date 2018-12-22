package io.github.axtuki1.onenightjinro.player;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.Utility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum Job {

    murabito(){
        @Override
        public void Action(Player p, String[] args) {
            // この役職には特有の行動はありません。
        }

        @Override
        public String getJobID() {
            return "murabito";
        }

        @Override
        public String getJobName() {
            return "村人";
        }

        @Override
        public String getJobName2Moji() {
            return "村人";
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.WHITE;
        }

        @Override
        public String[] getDescription() {
            return new String[]{
                    getColor() + "特に能力は持っていない役職です。",
                    getColor() + "持ち前のトークスキルと推察力で人狼を処刑しましょう。",
                    getColor() + "目標: 人狼を村から一人でも処刑する [村人の勝利]"
            };
        }

        @Override
        public String[] getDescription(boolean option) {
            return getDescription();
        }

        @Override
        public boolean isActionable() {
            return false;
        }
    },
    jinro(){
        @Override
        public void Action(Player p, String[] args) {
            // この役職には特有の行動はありません。
        }

        @Override
        public String getJobID() {
            return "jinro";
        }

        @Override
        public String getJobName() {
            return "人狼";
        }

        @Override
        public String getJobName2Moji() {
            return "人狼";
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.RED;
        }

        @Override
        public String[] getDescription() {
            return getDescription(false);
        }

        @Override
        public String[] getDescription(boolean isPlayerList) {
            ArrayList<String> s = new ArrayList<String>(){
                {
                    add(getColor() + "人狼以外を処刑するように他のプレイヤーを騙して生き残りましょう。");
                    add(getColor() + "目標: 一人も処刑されずに生き残る [人狼の勝利]");
                }
            };
            if( isPlayerList ){
                s.add(getColor() + "=========[今回の人狼]=========");
                for( Player p : JinroPlayers.getPlayers(jinro) ){
                    s.add(getColor() + "[ " + p.getName() + " ]");
                }
            }
            return s.toArray(new String[s.size()]);
        }

        @Override
        public boolean isActionable() {
            return false;
        }
    },
    kaitou(){
        @Override
        public void Action(Player p, String[] args) {
            PlayerData pd = JinroPlayers.getData(p);
            if(!GameStatus.getStatus().equals(GameStatus.Playing) || !GameStatus.Cycle.getCycle().equals(GameStatus.Cycle.Night) ){
                p.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "現在は使用できません。");
                return;
            } else if( !JinroPlayers.equalsFirstJob( p, this ) ){
                p.sendMessage(MConJinro.getPrefix() + ChatColor.RED + this.getJobName() + "ではありません。");
                return;
            } else if( pd.isAction() ){
                p.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "あなたは行動済みです。");
                return;
            }
            if( args.length <= 1 ){
                Utility.sendCmdHelp(p, "/jinro change <Player>", "指定プレイヤーと役を交換します。");
                Utility.sendCmdHelp(p, "/jinro change nope##", "交換しない場合はこちら");
                return;
            }
            if( args[1].equalsIgnoreCase("nope##") ){
                p.sendMessage(MConJinro.getPrefix() + ChatColor.GREEN + "行動済みにしました。これ以降の交換はできません。");
                pd.setAction(true);
                JinroPlayers.setData(p, pd);
                return;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if( target == null ){
                p.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "指定プレイヤーは存在しません。");
                return;
            }
            if( !JinroPlayers.getPlayers().containsKey( target.getUniqueId() ) ){
                p.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "指定プレイヤーは参加していません。");
                return;
            }
            Job j = JinroPlayers.getData(target).getFirstJob();
            Bukkit.broadcast(MConJinro.getPrefix() + getColor() + "[" + getJobName() + ":交換] "+ p.getName() +" <=-=> " +
                    target.getName() + j.getColor() + " ["+ j.getJobName() +"]","Jinro.GameMaster");
            p.sendMessage(MConJinro.getPrefix() +
                    ChatColor.GREEN + target.getName() + " とすり替えた結果、あなたは " +
                    j.getColor() + "["+j.getJobName()+"] " +
                    ChatColor.GREEN + "になりました。");
            pd.setJob(j);
            pd.setAction(true);
            pd.setAcitonTarget(target.getUniqueId());
            JinroPlayers.setData(p, pd);

            pd = JinroPlayers.getData(target);
            pd.setJob(Job.kaitou);
            JinroPlayers.setData(target, pd);
        }

        @Override
        public String getJobID() {
            return "kaitou";
        }

        @Override
        public String getJobName() {
            return "怪盗";
        }

        @Override
        public String getJobName2Moji() {
            return "怪盗";
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.AQUA;
        }

        @Override
        public String[] getDescription() {
            return new String[]{
                    getColor() + "「/jinro change <Player>」で一人と役職を交換できます。",
                    getColor() + "交換されたプレイヤーは交換されたことはわかりません。",
                    getColor() + "交換した後の勝利条件は交換された役の勝利条件になります。",
                    getColor() + "交換しない場合は時間までコマンドを実行しないでください。",
                    getColor() + "目標: 人狼を村から一人でも処刑する [村人の勝利]"
            };
        }

        @Override
        public String[] getDescription(boolean option) {
            return getDescription();
        }

        @Override
        public boolean isActionable() {
            return true;
        }
    },
    uranai(){
        @Override
        public void Action(Player p, String[] args) {
            PlayerData pd = JinroPlayers.getData(p);
            if(!GameStatus.getStatus().equals(GameStatus.Playing) || !GameStatus.Cycle.getCycle().equals(GameStatus.Cycle.Night)){
                p.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "現在は使用できません。");
                return;
            } else if( !JinroPlayers.equalsFirstJob( p, this ) ){
                p.sendMessage(MConJinro.getPrefix() + ChatColor.RED + this.getJobName() + "ではありません。");
                return;
            } else if( pd.isAction() ){
                p.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "あなたは行動済みです");
                return;
            }
            if( args.length <= 1 ){
                Utility.sendCmdHelp(p, "/jinro uranai <Player>", "指定プレイヤーの役を占います。");
                Utility.sendCmdHelp(p, "/jinro uranai amari##", "余っている役を占います。");
                return;
            }
            if( args[1].equalsIgnoreCase("amari##") ){
                StringBuilder s = new StringBuilder();
                for( Job j : JinroPlayers.getNotJob() ){
                    s.append(j.getColor())
                            .append(j.getJobName())
                            .append(ChatColor.GREEN)
                            .append(", ");
                }
                p.sendMessage(MConJinro.getPrefix() + ChatColor.GREEN + "余っている役を占った結果はこちらです:");
                String ss = null;
                if( (s.length() - 2) > 0 ){
                    ss = s.substring(0, s.length() - 2);
                }
                p.sendMessage(MConJinro.getPrefix() + ChatColor.GREEN + ss);
            } else {
                Player target = Bukkit.getPlayerExact(args[1]);
                if( target == null ){
                    p.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "指定プレイヤーは存在しません。");
                    return;
                }
                if( !JinroPlayers.getPlayers().containsKey( target.getUniqueId() ) ){
                    p.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "指定プレイヤーは参加していません。");
                    return;
                }
                Job j = JinroPlayers.getData(target).getFirstJob();
                Bukkit.broadcast(MConJinro.getPrefix() + getColor() + "[" + getJobName() + ":占い] "+ p.getName() +" -> " +
                        target.getName() + j.getColor() + " ["+ j.getJobName() +"]","Jinro.GameMaster");
                p.sendMessage(MConJinro.getPrefix() +
                        ChatColor.GREEN + target.getName() + " は " +
                        j.getColor() + "["+j.getJobName()+"] " +
                        ChatColor.GREEN + "でした。");
                pd.setAction(true);
                pd.setAcitonTarget(target.getUniqueId());
                JinroPlayers.setData(p, pd);
            }
        }

        @Override
        public String getJobID() {
            return "uranai";
        }

        @Override
        public String getJobName() {
            return "占い師";
        }

        @Override
        public String getJobName2Moji() {
            return "占い";
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.GREEN;
        }

        @Override
        public String[] getDescription() {
            return new String[]{
                    getColor() + "一人を占う(役職を見る)ことが出来ます。",
                    getColor() + "「/jinro uranai <Player>」で占えます。",
                    getColor() + "目標: 人狼を村から一人でも処刑する [村人の勝利]"
            };
        }

        @Override
        public String[] getDescription(boolean option) {
            return getDescription();
        }

        @Override
        public boolean isActionable() {
            return true;
        }
    }
    ;

    public enum Marker {
        white {
            @Override
            public String getString() {
                return "○";
            }

            @Override
            public String getName() {
                return "白";
            }
        },
        black {
            @Override
            public String getString() {
                return "●";
            }

            @Override
            public String getName() {
                return "黒";
            }
        };

        public abstract String getString();
        public abstract String getName();
    }

    /**
     * "アクション"コマンド実行したときに呼ばれます。
     * 呼ばれるのは初期所属役職内のActionです。
     * 引数: /<cmd> <役職指定>[0] <...>
     * @param player コマンド実行したプレイヤー
     * @param args コマンド引数(すべて)
     */
    abstract public void Action(Player player, String[] args);

    /**
     * 役職の内部で使用するIDを返します。
     * @return 役職の内部ID
     */
    abstract public String getJobID();

    /**
     * 役職の表示名を返します。
     * @return 役職の表示名
     */
    abstract public String getJobName();

    /**
     * 役職の表示名(2文字)を返します。
     * @return 役職の表示名
     */
    abstract public String getJobName2Moji();

    /**
     * 役職の色を返します。
     * @return 役職の色
     */
    abstract public ChatColor getColor();

    /**
     * 役についての説明を返します。
     */
    abstract public String[] getDescription();

    /**
     * 役についての説明を返します。
     * 2通りのメッセージを返せます
     */
    abstract public String[] getDescription(boolean option);

    /**
     * この役がアクションを起こせるか返します。
     */
    abstract public boolean isActionable();

    /**
     * 役職リストを返します。
     * このリストは役職指定ヘルプに使用します。
     */
    public static String[] getJobHelp(){
        ArrayList<String> s = new ArrayList<String>(){
            {
                add(MConJinro.getPrefix() + ChatColor.GREEN + "役職指定には以下の文字が使用できます。");
            }
        };
        int i = 0;
        StringBuilder ss = new StringBuilder();
        for( Job j : Job.values() ){
            i++;
            ss.append(j.getJobName()).append(":").append(j.getJobID()).append(", ");
            if( i >= 3 ){
                s.add(MConJinro.getPrefix() + ChatColor.AQUA + ss.toString().substring(0, ss.length() - 2));
                ss = new StringBuilder();
            }
        }
        return s.toArray(new String[s.size()]);
    }

    public static Job[] getDefaultSort(){
        return new Job[]{
                murabito,
                jinro,
                kaitou,
                uranai,
        };
    }

}
