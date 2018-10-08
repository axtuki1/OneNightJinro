package io.github.axtuki1.onenightjinro;

public enum JinroConfig {
    // 夜の間の時間
    NightTime(){
        @Override
        public String getPath() {
            return "NightTime";
        }
    },
    // 議論時間
    DiscussionTime(){
        @Override
        public String getPath() {
            return "DiscussionTime";
        }
    },
    // サイドバーの内部名
    InfoObjectiveName(){
        @Override
        public String getPath() {
            return "InfoObjectiveName";
        }
    }
    ;

    /**
     * Configのパスを返します。
     * @return
     */
    public abstract String getPath();

    /**
     * Configを再読込します。
     * pluginのreloadConfig()と同じです。
     */
    public void reload(){
        MConJinro.getMain().reloadConfig();
    }
}
