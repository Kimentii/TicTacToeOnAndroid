public class Square {
    private Player player = null;                               //”казатель на ирока, заполневшего €чейку

    /**
     * «аполнение €чейки
     *
     * @param player - игрок, который заполн€ет €чейку
     */
    public void fill(Player player) {
        this.player = player;
    }

    /**
     * «аполнена ли €чейка?
     *
     * @return
     */
    public boolean isFilled() {
        if (player != null) {
            return true;
        }
        return false;
    }

    public Player getPlayer() {
        return player;
    }
}