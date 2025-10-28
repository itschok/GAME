public class Skill {
    public String name;
    public String description;
    public Runnable effect; // โค้ดที่จะทำเมื่อเลือกสกิล

    public Skill(String name, String description, Runnable effect) {
        this.name = name;
        this.description = description;
        this.effect = effect;
    }

    public void apply() {
        if (effect != null) effect.run();
    }
}