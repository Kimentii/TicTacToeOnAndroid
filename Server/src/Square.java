public class Square {
    private Player player = null;                               //��������� �� �����, ������������ ������

    /**
     * ���������� ������
     *
     * @param player - �����, ������� ��������� ������
     */
    public void fill(Player player) {
        this.player = player;
    }

    /**
     * ��������� �� ������?
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