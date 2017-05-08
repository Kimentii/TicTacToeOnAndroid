/**
 * Игрок
 */

public class Player {
    /**
     * Имя игрока
     */
    private String name;

    /**
     * Конструктор
     */
    public Player(String name) {
        this.name = name;
    }

    /**
     * Функция получения имени игрока
     */
    public CharSequence getName() {
        return (CharSequence) name;
    }
}