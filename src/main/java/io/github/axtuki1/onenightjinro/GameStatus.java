package io.github.axtuki1.onenightjinro;

public enum GameStatus {
    Ready, Playing, Pause, End;

    private static GameStatus s = GameStatus.Ready;

    public static GameStatus getStatus(){
        return s;
    }

    public static void setStatus(GameStatus status) {
        s = status;
    }


    public enum Cycle {
        Ready, Night, Discussion, Vote, Execution;

        private static Cycle c = Cycle.Ready;

        public static Cycle getCycle(){
            return c;
        }

        public static void setCycle(Cycle cycle) {
            c = cycle;
        }
    }
}


